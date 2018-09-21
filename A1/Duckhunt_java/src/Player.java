
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


class Player {
    
    List<List<Integer>> observations;
    List<List<List<Integer>>> oldObservations;
    
    double[][][] oldA = new double[Constants.COUNT_SPECIES][][];
    double[][][] oldB = new double[Constants.COUNT_SPECIES][][];
    double[][] oldPi = new double[Constants.COUNT_SPECIES][];
    
    int round = -1;
    
    int states = 3;
    
    double hit = 0;
    double shoot = 0;
   
    public Player() {
        oldObservations = new ArrayList<>(Constants.COUNT_SPECIES);
        for (int i = 0; i < Constants.COUNT_SPECIES; i++)
            oldObservations.add(new ArrayList<>());
    }
    
    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */
        
        if (pState.getRound() != round) 
            newRound(pState);
        
        for (int bird = 0; bird < pState.getNumBirds(); bird++) {
            if (pState.getBird(bird).isAlive()){
                observations.get(bird).add(pState.getBird(bird).getLastObservation());
            }
        }
        
        if (observations.get(0).size() < 60 || round < 1 || oldA[Constants.SPECIES_BLACK_STORK] == null)
            return cDontShoot;
        
        int birdToShoot = -1;
        int moveToShoot = -1;
        double probabilityOfHit = - Double.MAX_VALUE;
        
        for (int bird = 0; bird < pState.getNumBirds(); bird++) {
            
            if (pState.getBird(bird).isDead())
                continue;
            
            int birdKind = -1;
            double kindProbability = - Double.MAX_VALUE;
            
            for (int kind = 0; kind < Constants.COUNT_SPECIES; kind++) {
                
                if (oldA[kind] == null)
                    continue;
                
                double prob = HMM.probabilityOfSequenceScaled(oldA[kind], oldB[kind], oldPi[kind], observations.get(bird));
                if (prob > kindProbability) {
                    birdKind = kind;
                    kindProbability = prob;
                }
            }
            
            if (kindProbability < -60 || birdKind == Constants.SPECIES_BLACK_STORK  )        
                continue;
            
            double[][][] matrices = HMM.HMM(
                    Matrix.startMatrix(states, states),
                    Matrix.startMatrix(states, Constants.COUNT_MOVE),
                    Matrix.startPi(states),
                    100,
                    observations.get(bird)
            );
            
            double[] currentStateDistribution = HMM.getCurrentStateDistribution(
                    matrices[0], 
                    matrices[1], 
                    matrices[2][0], 
                    observations.get(bird)
            );
            
            double[] nextMoveDistribution = Matrix.multiply(
                    Matrix.multiply(currentStateDistribution, matrices[0]),
                    matrices[1]
            );
            
            int mostProbableMove = Matrix.argMax(nextMoveDistribution);
            
            if (nextMoveDistribution[mostProbableMove] > probabilityOfHit) {
                birdToShoot = bird;
                moveToShoot = mostProbableMove;
                probabilityOfHit = nextMoveDistribution[mostProbableMove];
            }
        }
        
        if (probabilityOfHit > 0.85) {
            System.err.println("Shooting! " );
            shoot++;
            return new Action(birdToShoot, moveToShoot);
        }
        
        
        return new Action(-1, -1);
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */
        
        int[] lGuess = new int[pState.getNumBirds()];
        
        for (int bird = 0; bird < lGuess.length; bird++) {
            
            int mostProbableKind = Constants.SPECIES_PIGEON;
            double kindProbability = - Double.MAX_VALUE;
            
            for (int kind = 0; kind < Constants.COUNT_SPECIES; kind++) {
                
                if (oldA[kind] == null)
                    continue;
                
                double probability = HMM.probabilityOfSequence(oldA[kind], oldB[kind], oldPi[kind], observations.get(bird));
                
                if (probability > kindProbability){
                    mostProbableKind = kind;
                    kindProbability = probability;
                }
            }
            
            lGuess[bird] = mostProbableKind;
            
        }
        
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        hit++;
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
        for (int bird = 0; bird < pSpecies.length; bird++) {
            if (pSpecies[bird] == Constants.SPECIES_UNKNOWN)
                continue;
            
            oldObservations.get(pSpecies[bird]).add(observations.get(bird));
        }
        
        for (int kind = 0; kind < Constants.COUNT_SPECIES; kind++) {
            
            if (oldObservations.get(kind).size() == 0)
                continue;
            
            double[][][] matrices = HMM.HMMMean(
                    Matrix.startMatrix(states, states), 
                    Matrix.startMatrix(states, Constants.COUNT_MOVE), 
                    Matrix.startPi(states), 
                    50, 
                    oldObservations.get(kind)
            );
            
            oldA[kind] = matrices[0];
            oldB[kind] = matrices[1];
            oldPi[kind] = matrices[2][0];
            
        }
        System.err.println("Hit ratio: " + hit / shoot);
    }

    public static final Action cDontShoot = new Action(-1, -1);

    private void newRound(GameState pState) {
        
        System.err.println("New round " + pState.getRound() + " with " + pState.getNumBirds() + " birds.");
        
        round = pState.getRound();
        observations = new ArrayList<>(pState.getNumBirds());
        
        for (int i = 0; i < pState.getNumBirds(); i++){
            observations.add(new ArrayList<Integer>(100));
        }
    }
}
