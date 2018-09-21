
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HMM {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private List<Integer> emiSeq;

    private int T;
    private ArrayList<Matrix> alphas;
    private ArrayList<Matrix> betas;
    private ArrayList<Matrix> gammas;
    private ArrayList<Matrix> digammas;
    private ArrayList<Double> scales;

    private static final int MAX_ITERS = 100;
    private static final double tol = 0.001;
    private final int numberOfStates;
    private final int numberOfEmissions;
    private final int N;
    private final int M;

    public HMM(int numberOfStates, int numberOfEmissions) {
        this.numberOfStates = numberOfStates;
        this.numberOfEmissions = numberOfEmissions;

        A = Matrix.createRandomMatrix(numberOfStates, numberOfStates);
        B = Matrix.createRandomMatrix(numberOfStates, numberOfEmissions);
        pi = Matrix.createRandomMatrix(1, numberOfStates);

        N = A.m;
        M = B.n;
    }

    // public void retrain(List<Integer> emissionSequence) {
    //     A = Matrix.createRandomMatrix(numberOfStates, numberOfStates);
    //     B = Matrix.createRandomMatrix(numberOfStates, numberOfEmissions);
    //     pi = Matrix.createRandomMatrix(1, numberOfStates);

    //     train(emissionSequence);
    // }

    public void retrain(int observation) {
        emiSeq.add(observation);
        estimateModel();
        // System.err.println(A.toKattisString());
    }

    public void train(List<Integer> emissionSequence) {
        this.emiSeq = emissionSequence;
        T = emiSeq.size();

        A = Matrix.createRandomMatrix(numberOfStates, numberOfStates);
        B = Matrix.createRandomMatrix(numberOfStates, numberOfEmissions);
        pi = Matrix.createRandomMatrix(1, numberOfStates);

        estimateModel();
    }

    public void estimateModel() {
        double oldLogProb = Double.MIN_VALUE;

        int z = 0;

        for (z = 0; z < MAX_ITERS; z++) {

            alphapass();
            betapass();
            gamma();
            updateMatrices();

            double logProb = 0.0;
            for (int t = 0; t < T; t++) {
                logProb = logProb + Math.log10(scales.get(t));
            }
            logProb = -logProb;

            if (Math.abs(logProb - oldLogProb) < tol) {
                break;
            }
            oldLogProb = logProb;
            // System.out.println(oldLogProb);
        }
    }

    public ArrayList<Double> nextMoves(List<Integer> emissionSequence) {
        this.emiSeq = emissionSequence;
        Matrix currentState = alphapass();
        Matrix nextState = currentState.transpose().multiply(A);
        Matrix nextMoves = nextState.multiply(B);

        return nextMoves.toArray();
    }

    private Matrix alphapass() {
        alphas = new ArrayList<Matrix>();
        scales = new ArrayList<Double>();

        double c0 = 0.0;

        Matrix a0 = new Matrix(N, 1);
        for (int i = 0; i < N; i++) {
            a0.set(i, 0, pi.get(0, i) * B.get(i, (int) emiSeq.get(0)));
            c0 = c0 + a0.get(i, 0);
        }

        c0 = 1.0 / c0;
        a0.scalarMult(c0);

        scales.add(c0);
        alphas.add(a0);

        for (int t = 1; t < T; t++) {
            double ct = 0.0;

            Matrix at = new Matrix(N, 1);
            for (int i = 0; i < N; i++) {
                double ati = 0.0;
                for (int j = 0; j < N; j++) {
                    ati = ati + alphas.get(t - 1).get(j, 0) * A.get(j, i);
                }
                ati = ati * B.get(i, (int) emiSeq.get(t));
                at.set(i, 0, ati);
                ct += at.get(i, 0);
            }

            ct = 1.0 / ct;
            at = at.scalarMult(ct);
            alphas.add(at);
            scales.add(ct);
        }

        return alphas.get(alphas.size() - 1);
    }
    
    private void betapass() {
        betas = new ArrayList<Matrix>();

        Matrix BTm1 = new Matrix(N, 1);
        for (int i = 0; i < N; i++)
            BTm1.set(i, 0, scales.get(scales.size() - 1));

        betas.add(BTm1);

        for (int t = T - 2; t >= 0; t--) {

            Matrix bt = new Matrix(N, 1);

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    bt.set(i, 0, bt.get(i, 0)
                            + A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1)) * betas.get(0).get(j, 0));
                }
            }

            bt = bt.scalarMult(scales.get(t));
            betas.add(0, bt);
        }
    }

    private void gamma() {
        digammas = new ArrayList<Matrix>();
        gammas = new ArrayList<Matrix>();

        for (int t = 0; t < T - 1; t++) {
            double denom = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    denom += alphas.get(t).get(i, 0) * A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1))
                            * betas.get(t + 1).get(j, 0);
                }
            }
            Matrix digamma = new Matrix(N, N);
            Matrix gamma = new Matrix(N, 1);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    digamma.set(i, j, alphas.get(t).get(i, 0) * A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1))
                            * betas.get(t + 1).get(j, 0) / denom);
                    gamma.set(i, 0, gamma.get(i, 0) + digamma.get(i, j));
                }
            }

            digammas.add(digamma);
            gammas.add(gamma);
        }

        double denom = 0.0;
        for (int i = 0; i < N; i++) {
            denom += alphas.get(T - 1).get(i, 0);
        }

        Matrix gamTm1 = new Matrix(N, 1);
        for (int i = 0; i < N; i++) {
            gamTm1.set(i, 0, alphas.get(T - 1).get(i, 0) / denom);
        }

        gammas.add(gamTm1);
    }

    private void updateMatrices() {
        for (int i = 0; i < N; i++) {
            pi.set(0, i, gammas.get(0).get(i, 0));
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double numer = 0;
                double denom2 = 0;
                for (int t = 0; t < T - 1; t++) {
                    numer = numer + digammas.get(t).get(i, j);
                    denom2 = denom2 + gammas.get(t).get(i, 0);
                }
                A.set(i, j, numer / denom2);
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                double numer = 0;
                double denom2 = 0;
                for (int t = 0; t < T; t++) {
                    if (emiSeq.get(t) == j) {
                        numer = numer + gammas.get(t).get(i, 0);
                    }
                    denom2 = denom2 + gammas.get(t).get(i, 0);
                }
                B.set(i, j, numer / denom2);
            }
        }

    }
    
    public static double probabilityOfSequence(double[][] A, double[][] B, double[] pi, List<Integer> emiSeq) {

        double[] oldAlpha;
        double[] alpha = new double[pi.length];
        
        for(int i = 0; i < pi.length; i++){
            alpha[i] = B[i][emiSeq.get(0)] * pi[i];
        }
        
        for(int k = 1; k < emiSeq.size(); k++) {
            
            oldAlpha = alpha;
            alpha = new double[pi.length];
            
            for(int i = 0; i < A.length; i++){
                for(int j = 0; j < A.length; j++){
                    alpha[i] += oldAlpha[j] * A[j][i];
                }
                alpha[i] *= B[i][emiSeq.get(k)];
            }
        }
        
        double sum = 0;
        for(int i = 0; i < alpha.length; i++){
            sum += alpha[i];
        }
        return sum;
    }
    
    
        
    public static double[][][] HMM(double[][] A, double[][] B, double[] pi, List<Integer> emiSeq) {
        
        int T = emiSeq.size();
        int N = A.length;
        int M = B[0].length;
        int maxIters = 15;
        double oldLogProb = Double.MIN_VALUE;
        
        double tol = 0.001;
        
        for(int z = 0; z < maxIters; z++) {
            
            List<double[]> alphas = new ArrayList<>();
            List<Double> scales = new ArrayList<>();

            double c0 = 0;

            double[] a0 = new double[N];
            for (int i = 0; i < N; i++) {
                a0[i] = pi[i] * B[i][emiSeq.get(0)];
                c0 = c0 + a0[i];
            }
            
            c0 = 1 / c0;
            for (int i = 0; i < N; i++) {
                a0[i] = c0 * a0[i];
            }

            scales.add(c0);
            alphas.add(a0);

            for (int t = 1; t < T; t++) {
                double ct = 0;

                double[] at = new double[N];
                for (int i = 0; i < N; i++) {
                    double ati = 0;
                    for (int j = 0; j < N; j++) {
                        ati = ati + alphas.get(t-1)[j] * A[j][i];
                    }
                    at[i] = ati*B[i][emiSeq.get(t)];
                    ct = ct + at[i];
                }

                ct = 1 / ct;
                for (int i = 0; i < N; i++) {
                    at[i] = at[i] * ct;
                }
                
                alphas.add(at);
                scales.add(ct);
            }

            List<double[]> betas = new ArrayList<>();

            double[] BTm1 = new double[N];
            for (int i = 0; i < N; i++)
                BTm1[i] = scales.get(scales.size() - 1);

            betas.add(BTm1);

            for (int t = T-2; t >= 0; t--) {

                double[] bt = new double[N];

                for (int i = 0; i < N; i++) {
                    double bti = 0;
                    for (int j = 0; j < N; j++) {
                        bti = bti + A[i][j] * B[j][emiSeq.get(t+1)] * betas.get(0)[j];
                    }
                    bt[i] = bti * scales.get(t);
                }
                betas.add(0, bt);
            }

            List<double[][]> digammas = new ArrayList<>();
            List<double[]> gammas = new ArrayList<>();

            for (int t = 0; t < T - 1; t++) {
                double denom = 0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        denom += alphas.get(t)[i] * A[i][j] * B[j][emiSeq.get(t+1)] * betas.get(t+1)[j];
                    }
                }
                double[][] digamma = new double[N][N];
                double[] gamma = new double[N];
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        digamma[i][j] = (alphas.get(t)[i] * A[i][j] * B[j][emiSeq.get(t+1)] * betas.get(t+1)[j]) / denom;
                        gamma[i] = gamma[i] + digamma[i][j];
                    }
                }

                digammas.add(digamma);
                gammas.add(gamma);
            }

            // Special case for T-1
            double denom = 0;
            for (int i = 0; i < N; i++) {
                denom += alphas.get(T-1)[i];
            }
            double[] gamTm1 = new double[N];
            for(int i = 0; i < N; i++) {
                gamTm1[i] = alphas.get(T-1)[i] / denom;
            }

            gammas.add(gamTm1);
            
            for (int i = 0; i < N; i++) {
                pi[i] = gammas.get(0)[i];
            }
        
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    double numer = 0;
                    double denom2 = 0;
                    for(int t = 0; t < T-1; t++) {
                        numer = numer + digammas.get(t)[i][j];
                        denom2 = denom2 + gammas.get(t)[i];
                    }
                    A[i][j] = numer/denom2;
                }
            }
            
            for( int i = 0; i < N; i++){
                for(int j = 0; j < M; j++) {
                    double numer = 0;
                    double denom2 = 0; 
                    for(int t = 0; t < T; t++) {
                        if(emiSeq.get(t) == j) {
                            numer = numer + gammas.get(t)[i];
                        }
                        denom2 = denom2 + gammas.get(t)[i];
                    }
                    B[i][j] = numer/denom2;
                }
            }

            double logProb = 0;
            for(int t = 0; t < T; t++) {
                logProb = logProb + Math.log10(scales.get(t));
            }
            logProb = -logProb;

            if (Math.abs(logProb - oldLogProb) < tol){
                break;
            }
            oldLogProb = logProb;
        }
        
        return new double[][][]{A, B, new double[][] {pi}};  
    }
}
   
    
    
    
    