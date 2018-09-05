
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HMM {

    public static void main(String[] args) throws IOException {
        List<String> input = getStandardInput();
        
        Matrix A = new Matrix(convertToInt(input.get(0)));
        Matrix B = new Matrix(convertToInt(input.get(1)));
        Matrix pi = new Matrix(convertToInt(input.get(2)));
        //System.out.println(A.toString());
        //System.out.println(B.toString());
        //System.out.println(pi.toString());
        
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
    
}
