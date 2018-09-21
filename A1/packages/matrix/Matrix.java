package matrix;
import java.util.List;


public class Matrix {
    
    private double[][] matrix;
    public int m;
    public int n;
    
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
    
    public Matrix(int m, int n, List<Double> coeff) {
        this(m, n);

        for (int i = 0; i < m; i++){
            for(int j = 0;j < n; j++){
                matrix[i][j] = coeff.get(i*n+j);
            }
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
    
    public Matrix add(Matrix other) {
        Matrix sum = new Matrix(m, n);
        
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                set(i,j, get(i,j) + other.get(i,j));
        
        return sum;
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
    
    public Matrix getColumn(int col) {
        Matrix column = new Matrix(m,1);
        
        for (int i = 0; i < m; i++) {
            column.matrix[i][0] = matrix[i][col];
        }
        return column;
    }
    
    public Matrix getRow(int row) {
        Matrix reRow = new Matrix(1,n);
        
        for (int i = 0; i < n; i++) {
            reRow.matrix[0][i] = matrix[row][i];
        }
        return reRow;
    }
    
    public Matrix elementWiseMult(Matrix other) {
        Matrix prod = new Matrix(m, n);
        
        for(int i=0; i < m; i++) {
            for (int j = 0; j < n; j++){
                prod.matrix[i][j] = matrix[i][j]*other.matrix[i][j];
            }
        }
        return prod;
    }
    
    public Matrix transpose() {
        Matrix transpose = new Matrix(n,m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transpose.matrix[j][i] = matrix[i][j];
            }
        }
        return transpose;
    }
    
    
    public double sum() {
        double sum = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }
    
    public Matrix scalarMult(double value) {
        Matrix scalarMatrix = new Matrix(m,n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                scalarMatrix.matrix[i][j]= matrix[i][j] * value;
            }
        }
        return scalarMatrix;
    }
    
    public double get(int i, int j){
        return matrix[i][j];
    }
    
    public void set(int i, int j, double value){
        matrix[i][j] = value;
    }
    
    public double getMax() {
        double maxValue = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > maxValue) {
                    maxValue = matrix[i][j];
                }
            }
        }
        return maxValue;
    } 
    
    public int[] indexOf(double max) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == max) {
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{-1,-1};
    }
    
    public Matrix clone() {
        Matrix clone = new Matrix(m,n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                clone.set(i, j, this.get(i,j));
            }
        }
        return clone;
    }
}

