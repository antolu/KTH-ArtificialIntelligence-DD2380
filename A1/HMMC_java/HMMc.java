import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import matrix.Matrix;
import hmmtoolbox.Toolbox;
import baumwelch.BaumWelch;

public class HMMc {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Matrix emiSeq;
    private Toolbox toolbox;
    private BaumWelch baumwelch;

    public HMMc() {
        toolbox = new Toolbox();
        try {
            List<String> input = toolbox.getStandardInput();
            A = new Matrix(toolbox.convertToInt(input.get(0)));
            B = new Matrix(toolbox.convertToInt(input.get(1)));

            pi = new Matrix(toolbox.convertToInt(input.get(2)));

            List<Double> inO = toolbox.convertToInt(input.get(3));
            int mO = inO.get(0).intValue();
            List<Double> sublist = inO.subList(1, mO + 1);
            emiSeq = new Matrix(mO, 1, sublist);

            System.out.println(A.toKattisString());
            System.out.println(B.toKattisString());

            baumwelch = new BaumWelch(A, B, pi, emiSeq);
            Hashtable<String, Matrix> table = baumwelch.baumWelch(input);

            System.out.println(table.get("A").toKattisString());
            System.out.println(table.get("B").toKattisString());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new HMMc();
    }
}
