
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HMM {
    
    private static Matrix A;
    private static Matrix B;
    private static Matrix pi;
    private static Matrix emiSeq;
    
    public static void main(String[] args) throws IOException {
        List<String> input = getFileInput();
        
        A = new Matrix(convertToInt(input.get(0)));
        B = new Matrix(convertToInt(input.get(1)));
        pi = new Matrix(convertToInt(input.get(2)));
        
        HMM3b(input);
        //HMM2(input);
        //HMM1(input);
        //HMM0();
    }
    
    public static void HMM3b(List<String> input) {
        List<Double> inO = convertToInt(input.get(3));
        int mO = inO.get(0).intValue();
        List<Double> sublist = inO.subList(1, mO+1);
        
        emiSeq = new Matrix(mO, 1, sublist);
        
        int T = mO;
        int N = A.m;
        int M = B.n;
        int maxIters = 100;
        double oldLogProb = Double.MIN_VALUE;
        
        double tol = 0.001;
        
        for(int z = 0; z < maxIters; z++) {
            
            List<Matrix> alphas = new ArrayList<>();
            List<Double> scales = new ArrayList<>();

            double c0 = 0;

            Matrix a0 = new Matrix(N, 1);
            for (int i = 0; i < N; i++) {
                a0.set(i, 0, pi.get(0, i) * B.get(i, (int)emiSeq.get(0, 0)));
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
                        ati = ati + alphas.get(t-1).get(j, 0) * A.get(j, i);
                    }
                    ati = ati*B.get(i, (int)emiSeq.get(t, 0));
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

            for (int t = T-2; t >= 0; t--) {

                Matrix bt = new Matrix(N, 1);

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        bt.set(i, 0, bt.get(i, 0) + A.get(i, j) * B.get(j, (int)emiSeq.get(t+1, 0)) * betas.get(0).get(j, 0));
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
                        denom += alphas.get(t).get(i, 0) * A.get(i,j) * B.get(j, (int)emiSeq.get(t+1, 0)) * betas.get(t+1).get(j, 0);
                    }
                }
                Matrix digamma = new Matrix(N, N);
                Matrix gamma = new Matrix(N, 1);
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        digamma.set(i,j, alphas.get(t).get(i, 0) * A.get(i,j) * B.get(j, (int)emiSeq.get(t+1, 0)) * betas.get(t+1).get(j, 0) / denom);
                        gamma.set(i, 0, gamma.get(i, 0) + digamma.get(i, j));
                    }
                }

                digammas.add(digamma);
                gammas.add(gamma);
            }

            double denom = 0;
            for (int i = 0; i < N; i++) {
                denom += alphas.get(T-1).get(i, 0);
            }

            Matrix gamTm1 = new Matrix(N, 1);
            for(int i = 0; i < N; i++) {
                gamTm1.set(i, 0, alphas.get(T-1).get(i, 0) / denom);
            }

            gammas.add(gamTm1);
            
            for (int i = 0; i < N; i++) {
                pi.set(0, i, gammas.get(0).get(i, 0));
            }
        
        
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    double numer = 0;
                    double denom2 = 0;
                    for(int t = 0; t < T-1; t++) {
                        numer = numer + digammas.get(t).get(i, j);
                        denom2 = denom2 + gammas.get(t).get(i, 0);
                    }
                    A.set(i, j, numer/denom2);
                }
            }

            for( int i = 0; i < N; i++){
                for(int j = 0; j < M; j++) {
                    double numer = 0;
                    double denom2 = 0; 
                    for(int t = 0; t < T; t++) {
                        if(emiSeq.get(t, 0) == j) {
                            numer = numer + gammas.get(t).get(i, 0);
                        }
                        denom2 = denom2 + gammas.get(t).get(i, 0);
                    }
                    B.set(i, j, numer/denom2);
                }
            }

            double logProb = 0;
            for(int t = 0; t < T; t++) {
                logProb = logProb + Math.log10(scales.get(t));
            }
            logProb = -logProb;

            //if (Math.abs(logProb - oldLogProb) < tol){
              //  break;
            //}
            oldLogProb = logProb;
            //System.out.println(oldLogProb);
        }
        
        System.out.println("3 3 0.7 0.05 0.25 0.1 0.8 0.1 0.2 0.3 0.5");
        System.out.println("3 4 0.7 0.2 0.1 0.0 0.1 0.4 0.3 0.2 0.0 0.1 0.2 0.7");
        System.out.println(A.toKattisString());
        System.out.println(B.toKattisString());
    }
    
    
    
    public static void HMM3(List<String> input) {
        List<Double> inO = convertToInt(input.get(3));
        int mO = inO.get(0).intValue();
        List<Double> sublist = inO.subList(1, mO+1);
        
        emiSeq = new Matrix(mO, 1, sublist);
        
        
        for(int i = 0; i < 100; i++){
            List<Matrix> betas = betaPass(mO);
            List<Matrix> alphas = alphaPass(mO);
            double alphaTot = alphas.get(alphas.size() - 1).sum();
            List<Matrix> diGamma = diGamma(alphas, betas, alphaTot);

            List<Matrix> gamma = gamma(diGamma);
            
            setA(diGamma, gamma);
            setB(diGamma, gamma, mO);
            setPI(gamma);
        }
        
        System.out.println(A.toKattisString());
        System.out.println(B.toKattisString());
    }
    
    public static void HMM2(List<String> input) {
        List<Double> inO = convertToInt(input.get(3));
        int mO = inO.get(0).intValue();
        List<Double> sublist = inO.subList(1, mO+1);
        
        emiSeq = new Matrix(mO, 1, sublist);
        List totDelta = viterbi(mO);
        
        for(int i = 0; i<totDelta.size();i++){
            //System.out.println(totDelta.get(i).toString());
        }
        String output = backtrack(totDelta);
        System.out.println(output);
    }
    
    
    public static void HMM1(List<String> input) {
        List<Double> inO = convertToInt(input.get(3));
        int mO = inO.get(0).intValue();
        List<Double> sublist = inO.subList(1, mO+1);
        
        emiSeq = new Matrix(mO, 1, sublist);
        List<Matrix> alphas = alphaPass(mO);
        double alpha = alphas.get(alphas.size() - 1).sum();
        System.out.println(alpha);
    }
    
    public static void HMM0() {
        Matrix prod = pi.multiply(A);
        prod = prod.multiply(B);
        System.out.println(prod.toKattisString());
    }
        
    public static List<String> getStandardInput(){    
        Scanner br = new Scanner(System.in);
        
        List<String> input = new ArrayList<>();
        
        while (br.hasNextLine()) {
            input.add(br.nextLine());
        }
        
        return input;
    }
    
    public static List<String> getFileInput() throws FileNotFoundException, IOException{    
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        
        List<String> input = new ArrayList<>();
        String line = br.readLine();
        
        while (line != null) {
            input.add(line);
            line = br.readLine();
        }
        return input;
    }
    
    public static List<Double> convertToInt(String list){
        String[] el = list.split(" ");
        List<Double> coeff = new ArrayList<>();
        for (int i = 0; i < el.length; i++){
            coeff.add(Double.parseDouble((el[i])));
        }
        return coeff;
    }
    
    public static List<Matrix> alphaPassScaled(int T) {
        
        List<Matrix> alphas = new ArrayList<>();
        
        double c0 = 0;
        
        Matrix a0 = new Matrix(A.m, 1);
        for (int i = 0; i < A.m; i++) {
            a0.set(i, 0, pi.get(0, i) * B.get(i, (int)emiSeq.get(0, 0)));
            c0 += a0.get(i, 0);
        }
        c0 = 1 / c0;
        a0.scalarMult(c0);
        
        alphas.add(a0);
        
        for (int t = 1; t < T; t++) {
            double ct = 0;
            
            Matrix at = new Matrix(A.m, 1);
            for (int i = 0; i < A.m; i++) {
                for (int j = 0; j < A.n; j++) {
                    at.set(i, 0, at.get(i, 0) + alphas.get(t-1).get(i, 0) * A.get(j, i));
                }
                at.set(i, 0, at.get(i, 0) * B.get(i, (int)emiSeq.get(t, 0)));
                ct += at.get(i, 0);
            }
            
            ct = 1 / ct;
            at = at.scalarMult(ct);
            alphas.add(at);
        }
        
        return alphas;
    }
    
    public static List<Matrix> alphaPass(int t) {
        List<Matrix> alphas = new ArrayList<>();
        Matrix alpha = B.getColumn((int)emiSeq.get(0, 0)).elementWiseMult(pi.transpose());
        alphas.add(alpha);
        for(int i = 1; i < t; i++) {
            alpha = A.transpose().multiply(alpha).elementWiseMult(B.getColumn((int)emiSeq.matrix[i][0]));
            alphas.add(alpha);
        }
        return alphas;
    }
    
    
    
    public static List<Matrix> betaPass(int t) {
        List<Matrix> betas = new ArrayList<>();
        Matrix beta = new Matrix(A.m, 1);
        for(int i = 0; i < A.m; i++){
            beta.matrix[i][0] = 1;
        }
        betas.add(beta.clone());
        
        /*Matrix betat = beta;
        
        for (int k = t-1; k >= 0; k--) {
            Matrix betatm1 = new Matrix(A.m,1);
            
            for (int i = 0; i < A.m; i++) {
                double sum = 0;
                for (int j = 0; j < A.n; j++) {
                    sum += A.get(i,j) * B.get(j, (int)emiSeq.get(k, 0)) * betat.get(j, 0);
                }
                betatm1.set(i, 0, sum);
            }
            
            betas.add(0, betatm1.clone());
            betat = betatm1;
        }*/
        
        // Tid
        for(int k = t-1; k >= 0;k--) {
            // beta
            for(int i = 0; i < A.m; i++) {
                double sum = 0;
                // Summera A
                for(int j = 0; j < A.n; j++) {
                    sum += beta.matrix[i][0]*B.getColumn((int)emiSeq.matrix[k][0]).matrix[i][0]* A.matrix[i][j];
                }
                beta.matrix[i][0] = sum;
            }
            betas.add(0,beta.clone());
        }
        return betas;
    }
    
    
    
    
    public static List<Matrix> diGamma(List<Matrix> alpha, List<Matrix> beta, double alphaTot) {
        List<Matrix> diGamma = new ArrayList<>();
        for(int t = 0; t < alpha.size()-1; t++) {
            Matrix diGam = new Matrix(A.m,A.n);

            for (int i = 0; i < A.m; i++) {
                for (int j = 0; j < A.n; j++) {
                    diGam.matrix[i][j] = alpha.get(t).get(i,0) * A.get(i,j) * B.getColumn((int)emiSeq.matrix[t+1][0]).get(j, 0) * beta.get(t+1).get(i,0);
                }
            }
            diGam.scalarMult(1/alphaTot);
            diGamma.add(diGam);
        }
        return diGamma;
    }
    
    public static List<Matrix> gamma(List<Matrix> diGamma) {
        List<Matrix> gamma = new ArrayList<>();
        for(Matrix matrix : diGamma) {
            Matrix gam = new Matrix(matrix.m, 1);
            for(int i = 0; i < diGamma.get(0).m; i++) {
                gam.set(i, 0, matrix.getRow(i).sum());
            }
            gamma.add(gam);
        }
        return gamma;
    }
    
    
    public static void setA(List<Matrix> diGamma, List<Matrix> gam) {
        Matrix sumDiGamma = diGamma.get(0);
        for (int i = 1; i < diGamma.size(); i++){
            sumDiGamma = sumDiGamma.add(diGamma.get(i));
        }
        
        Matrix sumGamma = gam.get(0);
        for (int i = 1; i < gam.size(); i++) {
            sumGamma = sumGamma.add(gam.get(i));
        }
        
        for (int i = 0; i < A.m; i++) {
            double denominator = sumGamma.get(i, 0);
            for (int j = 0; j < A.n; j++) {
                A.set(i, j, sumDiGamma.get(i, j) / denominator);
            }
        }
    }
    
    public static void setB(List<Matrix> diGamma, List<Matrix> gam, int T) {
        Matrix sumGamma = gam.get(0);
        for (int i = 1; i < gam.size(); i++) {
            sumGamma = sumGamma.add(gam.get(i));
        }
        
        for (int j = 0; j < B.m; j++) {
            double denominator = sumGamma.get(j, 0);
            for (int k = 0; k < B.n; k++) {
                double sum = 0;
                for (int t = 0; t < T - 1; t++) {
                    if (emiSeq.get(t, 0) == k){
                        sum += gam.get(t).get(j, 0);
                    }
                }
                B.set(j, k, sum / denominator);
            }
        }
    }
    
    public static void setPI(List<Matrix> gamma) {
        pi = gamma.get(0).transpose();
    }
    
    
    public static List viterbi(int t) {
        List<Matrix> totDelta = new ArrayList<>();
        Matrix deltaTemp;
        
        deltaTemp = B.getColumn((int)emiSeq.matrix[0][0]).elementWiseMult(pi.transpose());
        //System.out.println(deltaTemp.toString());
        for (int k = 1; k < t; k++) {
            Matrix BColumn = B.getColumn((int)emiSeq.matrix[k][0]);
            Matrix delta = new Matrix(A.m,2);
            Matrix delta2 = null;
            for (int j = 0; j< A.n; j++) {
                double value = BColumn.matrix[j][0];
                Matrix AColumn = A.getColumn(j);
                delta2 = AColumn.elementWiseMult(deltaTemp);
                delta2 = delta2.scalarMult(value);
                delta.matrix[j][0] = delta2.getMax();
                if (delta2.getMax() == 0){
                    delta.matrix[j][1] = -1;
                }
                else{
                    delta.matrix[j][1] = (int)delta2.indexOf(delta2.getMax())[0];
                }
            }
            //System.out.println(delta.toString());
            totDelta.add(0,delta);
            deltaTemp = delta;
        }
        return totDelta;
    }
    
    
    public static String backtrack(List<Matrix> delta) {
        Matrix lastMatrix = delta.get(0).getColumn(0);
        String output = "";
        int lastIndex;
        lastIndex = lastMatrix.indexOf(lastMatrix.getMax())[0];
        output += Integer.toString(lastIndex);
                
        for(Matrix matrix : delta) {
            lastIndex = (int)matrix.matrix[lastIndex][1];
            
            output = Integer.toString(lastIndex) + " " + output;
            
        }
        return output;
    }
    
    
    
}

