
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import matrix.Matrix;
import hmmtoolbox.Toolbox;

public class HMM1 {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Matrix emiSeq;
    private Toolbox toolbox;

    public HMM1() {
        toolbox = new Toolbox();
        try {
            List<String> input = toolbox.getFileInput();

            A = new Matrix(toolbox.convertToInt(input.get(0)));
            B = new Matrix(toolbox.convertToInt(input.get(1)));
            pi = new Matrix(toolbox.convertToInt(input.get(2)));

            List<Double> inO = toolbox.convertToInt(input.get(3));
            int mO = inO.get(0).intValue();
            List<Double> sublist = inO.subList(1, mO + 1);

            emiSeq = new Matrix(mO, 1, sublist);
            List<Matrix> alphas = alphaPass(mO);
            double alpha = alphas.get(alphas.size() - 1).sum();

            System.out.println(alpha);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        new HMM1();
    }

    public List<Matrix> alphaPass(int t) {
        List<Matrix> alphas = new ArrayList<Matrix>();
        Matrix alpha = B.getColumn((int) emiSeq.get(0, 0)).elementWiseMult(pi.transpose());
        alphas.add(alpha);
        for (int i = 1; i < t; i++) {
            alpha = A.transpose().multiply(alpha).elementWiseMult(B.getColumn((int) emiSeq.get(i, 0)));
            alphas.add(alpha);
        }
        return alphas;
    }
}
