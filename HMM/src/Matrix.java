
import java.util.List;


public class Matrix {
    
    double[][] matrix;
    int m;
    int n;
    
    public Matrix(List<Double> coeff) {
        m = coeff.get(0).intValue();
        n = coeff.get(1).intValue();
        
        matrix = new double[m][];
        
        for (int i = 0; i < m; i++) {
            matrix[i] = new double[n];
        }
        
        for (int i = 0; i < m; i++){
            for(int j = 0;j < n; j++){
                matrix[i][j] = coeff.get(2+i*n+j);
            }
        }  
    }
    
    public Matrix(int m, int n) {
        this.m = m;
        this.n = n;
        matrix = new double[m][];
       
        for (int i = 0; i < m; i++) {
            matrix[i] = new double[n];
        }
    }
    
    
    public Matrix multiply(Matrix other) {
        
        Matrix product = new Matrix(m, other.n);
        
        for (int i = 0; i < m; i++){
            for (int j = 0; j < other.n; j++){
                for( int k = 0; k < other.m; k++){
                    product.matrix[i][j] += matrix[i][k] * other.matrix[k][j];
                }                
            }
        }
        return product;
    }
    
    
    public String toString() {
        String reString = "";
        for (int i = 0; i < m; i++){
            for(int j = 0;j < n; j++){
                reString += matrix[i][j] + ", ";
            }
            reString += "\n";
        }
        return reString;
    }
    
    
    public String toKattisString() {
        String reString = m + " " + n ;
        for (int i = 0; i < m; i++){
            for(int j = 0;j < n; j++){
                reString += " " +0.001 * Math.round(1000 * matrix[i][j]);
            }
        }
        return reString;
    }
}
