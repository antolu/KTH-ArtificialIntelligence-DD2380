
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.lang.Double;


class Player {
   
    List<List<Integer>> emissionSequences;
    List<List<Integer>> AntonsEmissionSequences;
    
    List<List<Integer>> oldEmissionSequences;
    double[][][] oldA;
    double[][][] oldB;
    double[][] oldPi;
    
    int states = 9;
    private static final int FLY_PATTERNS = 3;
    private static final double PROB_TO_SHOOT = 0.80;
    private static final int TURN_TO_START_SHOOTING = 80;
    private HMM[] models;
    int possibleObservations = Constants.COUNT_MOVE;
    
    int round = -1;
    int turn = -1;
    int numBirds = 0;

    int hitShootRatio = 0;
    double nShoot = 0;
    double nHit = 0;

    int lastShotBird = 0;
    int lastShotBirdMove = 0;
    
    public Player() {
        oldEmissionSequences = new ArrayList<>();
        for (int i = 0; i < Constants.COUNT_SPECIES; i++) {
            oldEmissionSequences.add(new ArrayList<>());
        }
        
        oldA = new double[Constants.COUNT_SPECIES][][];
        oldB = new double[Constants.COUNT_SPECIES][][];
        oldPi = new double[Constants.COUNT_SPECIES][];
    }

    private void newRound(GameState pState){
        
        System.err.println("New round with " + pState.getNumBirds() + " birds!");
        
        emissionSequences = new ArrayList<>();
        AntonsEmissionSequences = new ArrayList<>();

        numBirds = pState.getNumBirds();
        
        for (int i = 0; i < numBirds; i++)
            emissionSequences.add(new ArrayList<>());

        for (int i = 0; i < numBirds; i++)
            AntonsEmissionSequences.add(new ArrayList<>());
        
        this.round = pState.getRound();
        turn = -1;

        /****************************/
        models = new HMM[numBirds];

        // Initialize HMMs
        for (int i = 0; i < numBirds; i++) {
            models[i] = new HMM(FLY_PATTERNS, Constants.COUNT_MOVE);
        }
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


        if (round != pState.getRound())
            newRound(pState);

        turn++;
       
        // Add observations
        for (int i = 0; i < numBirds; i++) {
            AntonsEmissionSequences.get(i).add(pState.getBird(i).getLastObservation());
            if (pState.getBird(i).isAlive())
                emissionSequences.get(i).add(pState.getBird(i).getLastObservation());
        }
        
        /* Collect observations until t = 80 */
        if (turn < TURN_TO_START_SHOOTING)
            return cDontShoot;

        /** Train all bird models */
        // if (turn == TURN_TO_START_SHOOTING)
            for (int i = 0; i < numBirds; i++) {
                if (pState.getBird(i).isAlive())
                    models[i].train(AntonsEmissionSequences.get(i));
            }

        // if (turn >= TURN_TO_START_SHOOTING)
        //     for (int i = 0; i < numBirds; i++) {
        //         if (pState.getBird(i).isAlive())
        //             models[i].retrain(pState.getBird(i).getLastObservation());
        //     }

        /** Calculate the most probable next moves of the birds */
        ArrayList<ArrayList<Double>> nextObservationProbabilities = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < numBirds; i++) {
            if (pState.getBird(i).isAlive()) {
                nextObservationProbabilities.add(models[i].nextMoves(AntonsEmissionSequences.get(i))); 
            }
            else {
                nextObservationProbabilities.add(new ArrayList<Double>(Collections.nCopies(Constants.COUNT_MOVE, 0.0)));
            }
        }

        /** Determine which bird and move has the highest probability */
        ArrayList<Double> probabilities = new ArrayList<Double>();
        ArrayList<Integer> observations = new ArrayList<Integer>();

        for (int i = 0; i < numBirds; i++) {
            probabilities.add(Collections.max(nextObservationProbabilities.get(i)));
            observations.add(nextObservationProbabilities.get(i).indexOf(probabilities.get(i)));
        }

        double probabilityOfObservation = Collections.max(probabilities);
        int birdToShoot = probabilities.indexOf(probabilityOfObservation);
        int whereToShoot = observations.get(birdToShoot);

        /** Decide whether to shoot or not */
        if (probabilityOfObservation < PROB_TO_SHOOT || Double.isNaN(probabilityOfObservation) || (birdToShoot == lastShotBird && whereToShoot == lastShotBirdMove)) {
            return cDontShoot;
        }
        else {
            System.err.println("SHOOTING BIRD " + birdToShoot + " WITH PROB " + probabilityOfObservation);
            nShoot++;
            lastShotBird = birdToShoot;
            lastShotBirdMove = whereToShoot;
            return new Action(birdToShoot, whereToShoot); 
        }
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
        
        Random rand = new Random();
        
        for (int i = 0; i < pState.getNumBirds(); i++) {
            
            // int bestGuess = rand.nextInt(Constants.COUNT_SPECIES);
            int bestGuess = Constants.SPECIES_PIGEON;
            double probability = 0;
            
            for (int j = 0; j < Constants.COUNT_SPECIES; j++) {
                if (oldA[j] == null)
                    continue;
                
                double prob = HMM.probabilityOfSequence(oldA[j], oldB[j], oldPi[j], emissionSequences.get(i));
                if (prob > probability) {
                    bestGuess = j;
                    probability = prob;
                }
            }
            
            lGuess[i] = bestGuess;
            
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
        nHit++;
        System.err.println("HIT BIRD ON TURN " + turn);
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
        List<Integer> lengthEmi = new ArrayList<>();
        for(int i = 0; i < oldEmissionSequences.size(); i++){
            lengthEmi.add(oldEmissionSequences.get(i).size());
        }
        
        for (int i = 0; i < pSpecies.length; i++) {
            oldEmissionSequences.get(pSpecies[i]).addAll(emissionSequences.get(i));
        }
        
        for (int i = 0; i < Constants.COUNT_SPECIES; i++) {
            
            if (oldEmissionSequences.get(i).size() == 0)
                continue;
            
            if(lengthEmi.get(i) != oldEmissionSequences.get(i).size()){
                double[][] A = Matrix.startMatrix(states, states);
                double[][] B = Matrix.startMatrix(states, possibleObservations);
                double[] pi = Matrix.startPi(states);


                double[][][] matrices = HMM.HMM(A, B, pi, oldEmissionSequences.get(i));

                oldA[i] = matrices[0];
                oldB[i] = matrices[1];
                oldPi[i] = matrices[2][0];
            }
        }
        System.err.println("Shoot and hit ratio: " + nHit / nShoot);
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
