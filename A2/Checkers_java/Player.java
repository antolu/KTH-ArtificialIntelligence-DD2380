import java.util.*;
import java.lang.*;
import java.util.stream.Collectors;

public class Player {
    private static final int MAX_DEPTH = 16;

    private static final int CAPACITY = 32000000;

    private static final int TIME_LEFT_THRESHOLD =  300000000;

    private Hashtable<Integer, Integer> lastScore = new Hashtable<>(CAPACITY);
    private Hashtable<Integer, Integer> newScore = new Hashtable<>(CAPACITY);
    private Hashtable<Integer, Integer> scoreDepth = new Hashtable<>(CAPACITY);

    /**
     * Performs a move
     *
     * @param gameState the current state of the board
     * @param pDue  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline pDue) {

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        lastScore.clear();

        int bestScore = 0;

        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            Hashtable<Integer, Integer> temp = lastScore;
            lastScore = newScore;
            newScore = temp;
            newScore.clear();
            scoreDepth.clear();

            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            bestScore = alphaBeta(gameState, depth, alpha, beta, gameState.getNextPlayer(), depth);

            if (bestScore == Integer.MAX_VALUE) {
                break;
            }

            if (pDue.timeUntil() < TIME_LEFT_THRESHOLD) {
                break;
            }
        }

        lastScore = newScore;
        final int finalBestScore = bestScore;
        return nextStates.stream()
                .filter(move -> lastScore.containsKey(hashState(move)))
                .filter(move -> getLastScore(move) == finalBestScore)
                .findFirst().get();
    }

    private int getLastScore(GameState gameState) {
        return lastScore.get(hashState(gameState));
    }

    private int alphaBeta(GameState gameState, int depth, int alpha, int beta, int player, int totalDepth) {
        int h = hashState(gameState);
        if (newScore.containsKey(h) && scoreDepth.get(h) >= depth) {
            return newScore.get(h);
        }

        int bestPossible = 0;

        if (gameState.isEOG()) {
            if (gameState.isRedWin()) {
                bestPossible = Integer.MAX_VALUE;
            } else if (gameState.isWhiteWin()) {
                bestPossible = Integer.MIN_VALUE;
            } else {
                bestPossible = 0;
            }
        } else if (depth == 0) {
            bestPossible = eval(gameState);
        } else {
            Vector<GameState> possibleMoves = new Vector<>();
            gameState.findPossibleMoves(possibleMoves);

            List<GameState> exploredMoves;

            if (depth != 1) {
                exploredMoves = possibleMoves.stream()
                        .filter(move -> lastScore.containsKey(new HashableGameState(move)))
                        .collect(Collectors.toList());

                List<Integer> lastScores = exploredMoves.stream()
                        .mapToInt(move -> getLastScore(move))
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
                    bestPossible = Math.max(bestPossible, alphaBeta(move, depth - 1, alpha, beta, Constants.CELL_WHITE, totalDepth));
                    alpha = Math.max(alpha, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            } else if (player == Constants.CELL_WHITE) {
                bestPossible = Integer.MAX_VALUE;

                for (GameState move : exploredMoves) {
                    bestPossible = Math.min(bestPossible, alphaBeta(move, depth - 1, alpha, beta, Constants.CELL_RED, totalDepth));
                    beta = Math.min(beta, bestPossible);

                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        if (totalDepth != MAX_DEPTH || depth != 0) {
            newScore.put(h, bestPossible);
            scoreDepth.put(h, depth);
        }

        return bestPossible;
    }

    private int eval(GameState pState) {
        int whiteSum = 0;
        int redSum = 0;

        for (int i = 0; i < 32; i++) {

            if (pState.get(i) == Constants.CELL_WHITE) {
                whiteSum += 1;
            } else if (pState.get(i) == Constants.CELL_RED) {
                redSum += 1;
            }
            
            if (pState.get(i)  == (Constants.CELL_WHITE | Constants.CELL_KING)) {
                whiteSum += 1;
            } else if (pState.get(i) == (Constants.CELL_RED | Constants.CELL_KING)) {
                redSum += 1;
            }
        }

        return redSum - whiteSum;
    }

    // This algorithm is due to stackoverflow.com user "bcorso"
    public static <T extends Comparable<T>> void keySort(
            final List<T> key, List<?>... lists){
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