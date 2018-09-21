import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import matrix.Matrix;
import hmmtoolbox.Toolbox;
import baumwelch.BaumWelch;

public class HMM3 {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Matrix emiSeq;
    private Toolbox toolbox;
    private BaumWelch baumwelch;

    public HMM3() {
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

            baumwelch = new BaumWelch(A, B, pi, emiSeq);
            Hashtable<String, Matrix> table = baumwelch.baumWelch(input);
            Matrix newA = table.get("A");
            Matrix newB = table.get("B");

            // System.out.println(A);
            // System.out.println(B);
            System.out.println(newA.toKattisString());
            System.out.println(newB.toKattisString());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new HMM3();
    }
}
