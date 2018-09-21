package hmmtoolbox;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Toolbox {

    public List<String> getStandardInput() {
        Scanner br = new Scanner(System.in);

        List<String> input = new ArrayList<>();

        while (br.hasNextLine()) {
            input.add(br.nextLine());
        }

        return input;
    }

    public List<String> getFileInput() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));

        List<String> input = new ArrayList<>();
        String line = br.readLine();

        while (line != null) {
            input.add(line);
            line = br.readLine();
        }
        return input;
    }
    
    public List<Double> convertToInt(String list) {
        String[] el = list.split(" ");
        List<Double> coeff = new ArrayList<>();
        for (int i = 0; i < el.length; i++) {
            coeff.add(Double.parseDouble((el[i])));
        }
        return coeff;
    }
}