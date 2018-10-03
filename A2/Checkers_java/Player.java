import java.util.*;
import java.lang.*;
import java.util.stream.Collectors;

public class Player {
    private static final int MAX_DEPTH = 12;
    private static final int CAPACITY = (int) 5e7;
    private static final int TIME_LIMIT =  (int) 2e8;

    private Hashtable<Integer, Integer> lastScore = new Hashtable<Integer, Integer>(CAPACITY);
    private Hashtable<Integer, Integer> newScore = new Hashtable<Integer, Integer>(CAPACITY);
    private Hashtable<Integer, Integer> scoreDepth = new Hashtable<Integer, Integer>(CAPACITY);
    private Hashtable <Integer, Integer[]> scoreboards = new Hashtable<Integer, Integer[]>();

    private int currentDepth = 1;
    private int me = 0;
    private int opponent = 0;

    private final Integer[] scoreboardRed = {
        2, 0, 0, 2, 
      2, 0, 0, 1, 
        0, 0, 0, 2,
      2, 1, 1, 1, 
        1, 1, 1, 2, 
      2, 2, 2, 2, 
        2, 2, 2, 3,
      4, 3, 3, 4
  };

    private final Integer[] scoreboardWhite = {
        4, 3, 3, 4,
    3, 2, 2, 2, 
        2, 2, 2, 2, 
    2, 1, 1, 1, 
        1, 1, 1, 2, 
    2, 0, 0, 0,
        1, 0, 0, 2,
    2, 0, 0, 2
    };

    /**
     * Performs a move
     *
     * @param pState the current state of the board
     * @param pDue  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {

        me = pState.getNextPlayer();
        if (me == Constants.CELL_RED) {
            opponent = Constants.CELL_WHITE;
            scoreboards.put(me, scoreboardRed);
            scoreboards.put(opponent, scoreboardWhite);
        }
        else {
            opponent = Constants.CELL_RED;
            scoreboards.put(opponent, scoreboardRed);
            scoreboards.put(me, scoreboardWhite);
        }

        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            return new GameState(pState, new Move());
        }

        // lastScore.clear();

        int bestScore = 0;

        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            currentDepth = depth;
            lastScore = newScore;
            newScore.clear();
            scoreDepth.clear();

            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            bestScore = alphaBeta(pState, depth, alpha, beta, me);

            /* Check if win */
            if (bestScore == Integer.MAX_VALUE) {
                break;
            }

            /* Abort if there's no time less */
            if (pDue.timeUntil() < TIME_LIMIT) {
                break;
            }
        }

        lastScore = newScore; // Reset for next round
        final int finalBestScore = bestScore; // To optimize memory
        return nextStates.stream().filter(move -> lastScore.containsKey(hashState(move))).filter(move -> lastScore.get(hashState(move)) == finalBestScore).findFirst().get();
    }

    /**
     * Minimax algorithm with alpha/beta pruning, computes the best move for
     * the player based on the current game state
     * 
     * @param pState The gamestate to be analyzed.
     * @param depth The current search depth.
     * @param alpha The previous alpha
     * @param beta The previous beta
     * @param player The player to maximize for
     * 
     * @return Returns the minimax value for the gamestate and player
     */
    private int alphaBeta(GameState pState, int depth, int alpha, int beta, int player) {
        /* If the state has already been visited, return the result from the previous search */
        int hash = hashState(pState);
        if (newScore.containsKey(hash) && scoreDepth.get(hash) >= depth) {
            return newScore.get(hash);
        }

        int bestPossible = 0;
        if (pState.isEOG() || depth == 0) {
            bestPossible = eval(pState);
        } else {
            /* Get the next possible states */
            Vector<GameState> possibleMoves = new Vector<>();
            pState.findPossibleMoves(possibleMoves);
            int numMoves = possibleMoves.size();

            List<GameState> exploredMoves;

            /* Move ordering algorithm, only effective for depth > 1 */
            if (depth != 1) {
                /* Retreieve previously visited states */
                exploredMoves = possibleMoves.stream()
                        .filter(move -> lastScore.containsKey(hashState(move)))
                        .collect(Collectors.toList());

                /* Retrieve the corresponting scores for the previously visited states */
                List<Integer> scores = exploredMoves.stream()
                        .mapToInt(move -> lastScore.get(hashState(move)))
                        .map(score -> player == me ? -score : score) 
                        .boxed().collect(Collectors.toList());

                /* Sort moves for move ordering */
                keySort(scores, exploredMoves);

                /* Retrieve non visited states (the rest) */
                List<GameState> nonExploredMoves = possibleMoves.stream()
                        .filter(move -> !exploredMoves.contains(move))
                        .collect(Collectors.toList());

                /* Merge all next states into one list */
                exploredMoves.addAll(nonExploredMoves);
            } else {
                exploredMoves = possibleMoves;
            }
            int res;
            if (player == me) {
                bestPossible = Integer.MIN_VALUE;
                for (int i = 0; i < numMoves; i++) {
                    res = alphaBeta(exploredMoves.get(i), depth - 1, alpha, beta, opponent);
                    bestPossible = Math.max(res, bestPossible);
                    alpha = Math.max(alpha, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            } else if (player == opponent) {
                bestPossible = Integer.MAX_VALUE;
                for (int i = 0; i < numMoves; i++) {
                    res = alphaBeta(exploredMoves.get(i), depth - 1, alpha, beta, me);
                    bestPossible = Math.min(res, bestPossible);
                    beta = Math.min(beta, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        /* Save the score if not at the end of the tree */
        if (currentDepth != MAX_DEPTH || depth != 0) {
            newScore.put(hash, bestPossible);
            scoreDepth.put(hash, depth);
        }

        return bestPossible;
    }

    /**
     * Computes the utility value of the given gamestate
     * using the naive method of simly computing the differing
     * amount of markers between the two players.
     * 
     * @param pState The gamestate to be analyzed.
     * 
     * @return Returns the utility value of the state
     */
    private int eval(GameState pState) {
        if (pState.isEOG()) {
            if ((pState.isRedWin() && me == Constants.CELL_RED) || (pState.isWhiteWin() && me == Constants.CELL_WHITE)) {
                return Integer.MAX_VALUE;
            } else if ((pState.isRedWin() && me == Constants.CELL_WHITE) || (pState.isWhiteWin() && me == Constants.CELL_RED)) {
                return Integer.MIN_VALUE;
            } else {
                return 0;
            }
        }

        int opponentMarkers = 0;
        int myMarkers = 0;
        int marker;

        for (int i = 0; i < 32; i++) {
            marker = pState.get(i);
            if (marker == opponent) {
                opponentMarkers += 2;
                opponentMarkers += scoreboards.get(opponent)[i];
            } else if (marker == me) {
                myMarkers += 2;
                myMarkers += scoreboards.get(me)[i];
            }
            
            if (marker  == (Constants.CELL_WHITE | Constants.CELL_KING)) {
                if (me == Constants.CELL_WHITE) {
                    myMarkers += 5;
                    myMarkers += scoreboardWhite[i];
                }
                else 
                {
                    opponentMarkers += 5;
                    opponentMarkers += scoreboardWhite[i];
                }
            } else if (marker == (Constants.CELL_RED | Constants.CELL_KING)) {
                if (me == Constants.CELL_RED) {
                    myMarkers += 5;
                    myMarkers += scoreboardRed[i];
                }
                else 
                {
                    opponentMarkers += 5;
                    opponentMarkers += scoreboardRed[i];
                }
            }
        }

        return myMarkers - opponentMarkers;
    }

    public static <T extends Comparable<T>> void keySort(final List<T> key, List<?>... lists) {
        // Create a List of indices
        List<Integer> indices = new ArrayList<Integer>();
        for(int i = 0; i < key.size(); i++)
            indices.add(i);

        // Sort the indices list based on the key
        Collections.sort(indices, new Comparator<Integer>(){
            @Override public int compare(Integer i, Integer j) {
                return key.get(i).compareTo(key.get(j));
            }
        });

        // Create a mapping that allows sorting of the List by N swaps.
        Map<Integer,Integer> swapMap = new HashMap<Integer, Integer>(indices.size());

        // Only swaps can be used b/c we cannot create a new List of type <?>
        for(int i = 0; i < indices.size(); i++){
            int k = indices.get(i);
            while(swapMap.containsKey(k))
                k = swapMap.get(k);

            swapMap.put(i, k);
        }

        // for each list, swap elements to sort according to key list
        for(Map.Entry<Integer, Integer> e : swapMap.entrySet())
            for(List<?> list : lists)
                Collections.swap(list, e.getKey(), e.getValue());
    }

    /**
     * Computes a hash code for the given gamestate.
     * 
     * @param pState The gamestate to be hashed
     * 
     * @return Returns the hash value of the game state
     */
    private int hashState(GameState pState) {
        int hash = pState.getNextPlayer();
        int marker;

        for (int i = 0; i < 32; i++) {
            marker = pState.get(i);
            hash *= 31;
            hash += marker;
        }

        return hash;
    }
}