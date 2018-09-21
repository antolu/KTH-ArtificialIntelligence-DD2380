
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HMM {
    
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
    
    public static double probabilityOfSequenceScaled(double[][] A, double[][] B, double[] pi, List<Integer> emiSeq, int next) {
        emiSeq.add(next);
        double result = probabilityOfSequenceScaled(A, B, pi, emiSeq);
        emiSeq.remove(emiSeq.size() - 1);
        return result;
    }
    
    public static double probabilityOfSequenceScaled(double[][] A, double[][] B, double[] pi, List<Integer> emiSeq) {
        
        int T = emiSeq.size();
        int N = A.length;
        
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
        
        double logProb = 0;
        for(int t = 0; t < T; t++) {
            logProb = logProb + Math.log10(scales.get(t));
        }
        return -logProb;
    }
    
    
    public static double[] getCurrentStateDistribution(double[][] A, double[][] B, double[] pi, List<Integer> emiSeq) {
        
        int T = emiSeq.size();
        int N = A.length;
        
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
        
        return alphas.get(alphas.size() - 1);
    }
    
    
    public static double[][][] HMM(double[][] A, double[][] B, double[] pi, int maxIters, List<Integer> emiSeq) {
        
        A = Matrix.clone(A);
        B = Matrix.clone(B);
        pi = Matrix.clone(pi);
        
        int T = emiSeq.size();
        
        int N = A.length;
        int M = B[0].length;
        double oldLogProb = - Double.MAX_VALUE;
        
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
    
    public static double[][][] HMMMean(double[][] A, double[][] B, double[] pi, int maxIters, List<List<Integer>> emiSequences) {
        A = Matrix.clone(A);
        B = Matrix.clone(B);
        pi = Matrix.clone(pi);
        
        int N = A.length;
        int M = B[0].length;
        int K = emiSequences.size();
        
        double oldLogProb = - Double.MAX_VALUE;
        
        double tol = 0.01;
        
        for(int z = 0; z < maxIters; z++) {
            
            List<List<Double>> allScales = new ArrayList<>(emiSequences.size());
            List<List<double[]>> allGammas = new ArrayList<>(emiSequences.size());
            List<List<double[][]>> allDiGammas = new ArrayList<>(emiSequences.size());
            
            for (int k = 0; k < K; k++) {
                
                List<Integer> emiSeq = emiSequences.get(k);
                
                int T = emiSeq.size();

                List<double[]> alphas = new ArrayList<>(T);
                
                List<Double> scales = new ArrayList<>(T);
                allScales.add(scales);
                
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
                allDiGammas.add(digammas);
                List<double[]> gammas = new ArrayList<>();
                allGammas.add(gammas);

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
            }
            
            // Pi
            for (int i = 0; i < N; i++) {
                double sum = 0;
                for (int k = 0; k < K; k++)
                    sum += allGammas.get(k).get(0)[i];
                pi[i] = 1.0 / K * sum;
            }

            // A
            for (int m = 0; m < A.length; m++) {
                double denominator = 0;
                for (int k = 0; k < K; k++) {
                    for(int t = 0; t < emiSequences.get(k).size() - 1; t++){
                        denominator += allGammas.get(k).get(t)[m];
                    }
                }
                for(int n = 0; n < A.length; n++){
                    double nominator = 0;
                    for (int k = 0; k < K; k++) {
                        for(int t = 0; t < emiSequences.get(k).size() - 1; t++){
                            nominator += allDiGammas.get(k).get(t)[m][n];
                        }
                    }
                    A[m][n] = nominator * 1.0 / denominator;
                }
            }

            // B
            for (int n = 0; n < A.length; n++) {
                double denominator = 0;
                for (int k = 0; k < K; k++) {
                    for(int t = 0; t < emiSequences.get(k).size(); t++){
                        denominator += allGammas.get(k).get(t)[n];
                    }
                }
                for (int m = 0; m < B[0].length; m++){
                    double nominator = 0;
                    for (int k = 0; k < K; k++) {
                        for (int t = 0; t < emiSequences.get(k).size(); t++){
                            if (emiSequences.get(k).get(t) == m) {
                                nominator += allGammas.get(k).get(t)[n];
                            }
                        }
                    }
                    B[n][m] = nominator * 1.0 / denominator;
                }
            }

            double logProb = 0;
            for (int k = 0; k < allScales.size(); k++) {
                for(int t = 0; t < emiSequences.get(k).size(); t++) {
                    logProb = logProb + Math.log(allScales.get(k).get(t));
                }
            }
            logProb = -logProb;

            if (Math.abs(logProb - oldLogProb) < tol){
                break;
            }
            oldLogProb = logProb;
        }
        
        return new double[][][]{A, B, new double[][] {pi}};
    }
    
    public static int mostProbableCurrentState(double[][] A, double[][] B, double[] pi, List<Integer> sequence) {
        
        double[] previousDelta;
        double[] nextDelta = new double[A.length];
        for (int i = 0; i < nextDelta.length; i++)
            nextDelta[i] = pi[i] * B[i][sequence.get(0)];
        
        
        for (int k = 1; k < sequence.size(); k++) {
            previousDelta = nextDelta;
            nextDelta = new double[nextDelta.length];
            
            int obs = sequence.get(k);
            for (int i = 0; i < nextDelta.length; i++) {
                double[] temporaryValues = new double[previousDelta.length];
                
                double value = B[i][obs];
                for (int j = 0; j < A.length; j++) {
                    temporaryValues[j] = A[j][i] * previousDelta[j] * value;
                }
                
                nextDelta[i] = Matrix.getMax(temporaryValues);
            }
        }
        
        return Matrix.argMax(nextDelta);
    }
}
   
    
    
    
    