
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import matrix.Matrix;
import hmmtoolbox.Toolbox;

public class HMM2 {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Matrix emiSeq;
    private Toolbox toolbox;

    public HMM2() {
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
            List<Matrix> totDelta = viterbi(mO);

            for (int i = 0; i < totDelta.size(); i++) {
                // System.out.println(totDelta.get(i).toString());
            }
            String output = backtrack(totDelta);
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        new HMM2();
    }

    public List<Matrix> viterbi(int t) {
        List<Matrix> totDelta = new ArrayList<>();
        Matrix deltaTemp;

        deltaTemp = B.getColumn((int) emiSeq.get(0, 0)).elementWiseMult(pi.transpose());
        // System.out.println(deltaTemp.toString());
        for (int k = 1; k < t; k++) {
            Matrix BColumn = B.getColumn((int) emiSeq.get(k, 0));
            Matrix delta = new Matrix(A.m, 2);
            Matrix delta2 = null;
            for (int j = 0; j < A.n; j++) {
                double value = BColumn.get(j, 0);
                Matrix AColumn = A.getColumn(j);
                delta2 = AColumn.elementWiseMult(deltaTemp);
                delta2 = delta2.scalarMult(value);
                delta.set(j, 0, delta2.getMax());
                if (delta2.getMax() == 0) {
                    delta.set(j, 1, -1);
                } else {
                    delta.set(j, 1, (int) delta2.indexOf(delta2.getMax())[0]);
                }
            }
            // System.out.println(delta.toString());
            totDelta.add(0, delta);
            deltaTemp = delta;
        }
        return totDelta;
    }

    public String backtrack(List<Matrix> delta) {
        Matrix lastMatrix = delta.get(0).getColumn(0);
        String output = "";
        int lastIndex;
        lastIndex = lastMatrix.indexOf(lastMatrix.getMax())[0];
        output += Integer.toString(lastIndex);

        for (Matrix matrix : delta) {
            lastIndex = (int) matrix.get(lastIndex, 1);

            output = Integer.toString(lastIndex) + " " + output;

        }
        return output;
    }

}
