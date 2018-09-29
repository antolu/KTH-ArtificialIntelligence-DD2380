import java.util.*;
import java.lang.*;
import java.util.stream.Collectors;

public class Player {
    private static final int MAX_DEPTH = 16;
    private static final int CAPACITY = (int) 5e6;
    private static final int TIME_LIMIT =  (int) 1e8;

    private Hashtable<Integer, Integer> lastScore = new Hashtable<>(CAPACITY);
    private Hashtable<Integer, Integer> newScore = new Hashtable<>(CAPACITY);
    private Hashtable<Integer, Integer> scoreDepth = new Hashtable<>(CAPACITY);

    private int currentDepth = 1;

    /**
     * Performs a move
     *
     * @param pState the current state of the board
     * @param pDue  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> nextStates = new Vector<GameState>();
        pState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        lastScore.clear();

        int bestScore = 0;

        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            currentDepth = depth;
            Hashtable<Integer, Integer> temp = lastScore;
            lastScore = newScore;
            // newScore = temp;
            newScore.clear();
            scoreDepth.clear();

            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            bestScore = alphaBeta(pState, depth, alpha, beta, pState.getNextPlayer());

            if (bestScore == Integer.MAX_VALUE) {
                break;
            }

            if (pDue.timeUntil() < TIME_LIMIT) {
                break;
            }
        }

        lastScore = newScore;
        final int finalBestScore = bestScore;
        return nextStates.stream()
                .filter(move -> lastScore.containsKey(hashState(move)))
                .filter(move -> lastScore.get(hashState(move)) == finalBestScore)
                .findFirst().get();
    }

    private int alphaBeta(GameState pState, int depth, int alpha, int beta, int player) {
        int hash = hashState(pState);
        if (newScore.containsKey(hash) && scoreDepth.get(hash) >= depth) {
            return newScore.get(hash);
        }

        int bestPossible = 0;

        if (pState.isEOG() || depth == 0) {
            bestPossible = eval(pState);
        } else {
            Vector<GameState> possibleMoves = new Vector<>();
            pState.findPossibleMoves(possibleMoves);

            List<GameState> exploredMoves;

            if (depth != 1) {
                exploredMoves = possibleMoves.stream()
                        .filter(move -> lastScore.containsKey(hashState(move)))
                        .collect(Collectors.toList());

                List<Integer> lastScores = exploredMoves.stream()
                        .mapToInt(move -> lastScore.get(hashState(move)))
                        .map(score -> player == Constants.CELL_RED ? -score : score)// reverse ordering for Red
                        .boxed().collect(Collectors.toList());

                keySort(lastScores, exploredMoves);

                List<GameState> restMoves = possibleMoves.stream()
                        .filter(move -> !exploredMoves.contains(move))
                        .collect(Collectors.toList());

                exploredMoves.addAll(restMoves);
            } else {
                exploredMoves = possibleMoves;
            }

            if (player == Constants.CELL_RED) {
                bestPossible = Integer.MIN_VALUE;

                for (GameState move : exploredMoves) {
                    bestPossible = Math.max(bestPossible, alphaBeta(move, depth - 1, alpha, beta, Constants.CELL_WHITE));
                    alpha = Math.max(alpha, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            } else if (player == Constants.CELL_WHITE) {
                bestPossible = Integer.MAX_VALUE;

                for (GameState move : exploredMoves) {
                    bestPossible = Math.min(bestPossible, alphaBeta(move, depth - 1, alpha, beta, Constants.CELL_RED));
                    beta = Math.min(beta, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        if (currentDepth != MAX_DEPTH || depth != 0) {
            newScore.put(hash, bestPossible);
            scoreDepth.put(hash, depth);
        }

        return bestPossible;
    }

    private int eval(GameState pState) {
        if (pState.isEOG()) {
            if (pState.isRedWin()) {
                return Integer.MAX_VALUE;
            } else if (pState.isWhiteWin()) {
                return Integer.MIN_VALUE;
            } else {
                return 0;
            }
        }

        int whiteMarkers = 0;
        int redMarkers = 0;
        int marker;

        for (int i = 0; i < 32; i++) {
            marker = pState.get(i);
            if (marker == Constants.CELL_WHITE) {
                whiteMarkers++;
            } else if (marker == Constants.CELL_RED) {
                redMarkers++;
            }
            
            if (marker  == (Constants.CELL_WHITE | Constants.CELL_KING)) {
                whiteMarkers++;
            } else if (marker == (Constants.CELL_RED | Constants.CELL_KING)) {
                redMarkers++;
            }
        }

        return redMarkers - whiteMarkers;
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