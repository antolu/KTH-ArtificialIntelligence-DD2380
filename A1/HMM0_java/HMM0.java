
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import matrix.Matrix;
import hmmtoolbox.Toolbox;

public class HMM0 {

    private Matrix A;
    private Matrix B;
    private Matrix pi;
    private Toolbox toolbox;

    public static void main(String[] args) {
        new HMM0();
    }

    public HMM0() {
        try {
            List<String> input = toolbox.getFileInput();

            A = new Matrix(toolbox.convertToInt(input.get(0)));
            B = new Matrix(toolbox.convertToInt(input.get(1)));
            pi = new Matrix(toolbox.convertToInt(input.get(2)));

            Matrix prod = pi.multiply(A);
            prod = prod.multiply(B);

            System.out.println(prod.toKattisString());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
