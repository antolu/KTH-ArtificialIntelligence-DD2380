package baumwelch;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import matrix.Matrix;
import hmmtoolbox.Toolbox;

public class BaumWelch {
    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Matrix emiSeq;

    public BaumWelch(Matrix A, Matrix B, Matrix pi, Matrix emissionSequence) {
        this.A = A;
        this.B = B;
        this.pi = pi;
        this.emiSeq = emissionSequence;
    }

    public Hashtable<String, Matrix> baumWelch(List<String> input) {
        int T = emiSeq.m;
        int N = A.m;
        int M = B.n;
        int maxIters = 10000;
        double oldLogProb = Double.MIN_VALUE;

        double tol = 0.01;
        int z = 0;

        for (z = 0; z < maxIters; z++) {

            List<Matrix> alphas = new ArrayList<>();
            List<Double> scales = new ArrayList<>();

            double c0 = 0;

            Matrix a0 = new Matrix(N, 1);
            for (int i = 0; i < N; i++) {
                a0.set(i, 0, pi.get(0, i) * B.get(i, (int) emiSeq.get(0, 0)));
                c0 = c0 + a0.get(i, 0);
            }

            c0 = 1 / c0;
            a0.scalarMult(c0);

            scales.add(c0);
            alphas.add(a0);

            for (int t = 1; t < T; t++) {
                double ct = 0;

                Matrix at = new Matrix(N, 1);
                for (int i = 0; i < N; i++) {
                    double ati = 0;
                    for (int j = 0; j < N; j++) {
                        ati = ati + alphas.get(t - 1).get(j, 0) * A.get(j, i);
                    }
                    ati = ati * B.get(i, (int) emiSeq.get(t, 0));
                    at.set(i, 0, ati);
                    ct += at.get(i, 0);
                }

                ct = 1 / ct;
                at = at.scalarMult(ct);
                alphas.add(at);
                scales.add(ct);
            }

            List<Matrix> betas = new ArrayList<>();

            Matrix BTm1 = new Matrix(N, 1);
            for (int i = 0; i < N; i++)
                BTm1.set(i, 0, scales.get(scales.size() - 1));

            betas.add(BTm1);

            for (int t = T - 2; t >= 0; t--) {

                Matrix bt = new Matrix(N, 1);

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        bt.set(i, 0, bt.get(i, 0)
                                + A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1, 0)) * betas.get(0).get(j, 0));
                    }
                }

                bt = bt.scalarMult(scales.get(t));
                betas.add(0, bt);
            }

            List<Matrix> digammas = new ArrayList<>();
            List<Matrix> gammas = new ArrayList<>();

            for (int t = 0; t < T - 1; t++) {
                double denom = 0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        denom += alphas.get(t).get(i, 0) * A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1, 0))
                                * betas.get(t + 1).get(j, 0);
                    }
                }
                Matrix digamma = new Matrix(N, N);
                Matrix gamma = new Matrix(N, 1);
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        digamma.set(i, j, alphas.get(t).get(i, 0) * A.get(i, j) * B.get(j, (int) emiSeq.get(t + 1, 0))
                                * betas.get(t + 1).get(j, 0) / denom);
                        gamma.set(i, 0, gamma.get(i, 0) + digamma.get(i, j));
                    }
                }

                digammas.add(digamma);
                gammas.add(gamma);
            }

            double denom = 0;
            for (int i = 0; i < N; i++) {
                denom += alphas.get(T - 1).get(i, 0);
            }

            Matrix gamTm1 = new Matrix(N, 1);
            for (int i = 0; i < N; i++) {
                gamTm1.set(i, 0, alphas.get(T - 1).get(i, 0) / denom);
            }

            gammas.add(gamTm1);

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
                        if (emiSeq.get(t, 0) == j) {
                            numer = numer + gammas.get(t).get(i, 0);
                        }
                        denom2 = denom2 + gammas.get(t).get(i, 0);
                    }
                    B.set(i, j, numer / denom2);
                }
            }

            double logProb = 0;
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

        System.out.println("Iterations: " + z);

        Hashtable<String, Matrix> retMap = new Hashtable<String, Matrix>();
        retMap.put("A", A);
        retMap.put("B", B);
        retMap.put("pi", pi);

        return retMap;
    }
}