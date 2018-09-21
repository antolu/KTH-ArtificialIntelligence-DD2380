
import java.util.List;


public class Matrix {
    
    double[][] matrix;
    int rows;
    int columns;
    
    public Matrix(List<Double> coeff) {
        rows = coeff.get(0).intValue();
        columns = coeff.get(1).intValue();
        matrix = new double[rows][];
        
        for (int i = 0; i < rows; i++) {
            matrix[i] = new double[columns];
        }
        
        for (int i = 0; i < rows; i++){
            for(int j = 0;j < columns; j++){
                matrix[i][j] = coeff.get(2+i*columns+j);
            }
        }  
    }
    
    public Matrix(int m, int n) {
        this.rows = m;
        this.columns = n;
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
        
        Matrix product = new Matrix(rows, other.columns);
        
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < other.columns; j++){
                for( int k = 0; k < other.rows; k++){
                    product.matrix[i][j] += matrix[i][k] * other.matrix[k][j];
                }                
            }
        }
        return product;
    }
    
    public Matrix add(Matrix other) {
        Matrix sum = new Matrix(rows, columns);
        
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                set(i,j, get(i,j) + other.get(i,j));
        
        return sum;
    }
    
    
    public static String toString(double[][] matrix) {
        String reString = "";
        for (int i = 0; i < matrix.length; i++){
            for(int j = 0;j < matrix[0].length; j++){
                reString += String.format("%.2f",matrix[i][j]) + ", ";
            }
            reString += "\n";
        }
        return reString;
    }
    
    public static String toString(double[] matrix) {
        String reString = "";
        for (int i = 0; i < matrix.length; i++){
            reString += String.format("%.2f",matrix[i]) + ", ";
        }
        reString += "\n";
        return reString;
    }
    
    
    public String toKattisString() {
        String reString = rows + " " + columns ;
        for (int i = 0; i < rows; i++){
            for(int j = 0;j < columns; j++){
                reString += " " +0.001 * Math.round(1000 * matrix[i][j]);
            }
        }
        return reString;
    }
  
    
    public Matrix getColumn(int col) {
        Matrix column = new Matrix(rows,1);
        
        for (int i = 0; i < rows; i++) {
            column.matrix[i][0] = matrix[i][col];
        }
        return column;
    }
    
    public Matrix getRow(int row) {
        Matrix reRow = new Matrix(1,columns);
        
        for (int i = 0; i < columns; i++) {
            reRow.matrix[0][i] = matrix[row][i];
        }
        return reRow;
    }
    
    public Matrix elementWiseMult(Matrix other) {
        Matrix prod = new Matrix(rows, columns);
        
        for (int i=0; i < rows; i++) {
            for (int j = 0; j < columns; j++){
                prod.matrix[i][j] = matrix[i][j]*other.matrix[i][j];
            }
        }
        return prod;
    }
    
    public Matrix transpose() {
        Matrix transpose = new Matrix(columns,rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transpose.matrix[j][i] = matrix[i][j];
            }
        }
        return transpose;
    }
    
    
    public double sum() {
        double sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }
    
    public Matrix scalarMult(double value) {
        Matrix scalarMatrix = new Matrix(rows,columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
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
    
    public static double getMax(double[] matrix) {
        double maxValue = 0;
        for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] > maxValue) {
                    maxValue = matrix[i];
                }
            }
        return maxValue;
    }
    
    public static int argMax(double[] matrix) {
        double maxValue = -1;
        int index = -1;
        for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] > maxValue) {
                    maxValue = matrix[i];
                    index = i;
                }
            }
        return index;
    }
    
    public static int indexOf(double max, double[] matrix) {
        for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] == max) {
                    return i;
                }
            }
        return -1;
    }
    
    public static double[][] clone(double[][] matrix) {
        double[][] clone = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                clone[i][j] = matrix[i][j];
            }
        }
        return clone;
    }
    
    public static double[] clone(double[] matrix) {
        double[] clone = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
                clone[i] = matrix[i];
            }
        return clone;
    }
    
    public double distance(Matrix other) {
        double distance = 0;
        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                distance += Math.pow(this.get(i,j)-other.get(i,j),2);
            }
        }
        return Math.sqrt(distance);
    }
    
    public static double[][] matrix(int m, int n){
        double[][] re = new double[m][];
        for (int i = 0; i < m; i++)
            re[i] = new double[n];
        return re;
    }
    
    public static Matrix uniform(int m, int n) {
        Matrix re = new Matrix(m, n);
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                re.set(i, j, 1.0/n);
        return re;
    }
    
    public static Matrix diagonal(int m, int n){
        Matrix re = new Matrix(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j)
                    re.set(i,j,1);
                else
                    re.set(i,j,0);
            }
        }
        return re; 
    }
    
    
    public static double[][] startMatrix(int m, int n) {
        double[][] re = matrix(m,n);
        for (int i = 0; i < m; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                re[i][j] = 1.0 / n + (Math.random() - 0.5) / n;
                sum += re[i][j];
            }
            for (int j = 0; j < n; j++){
                re[i][j] = re[i][j] / sum;
            }
        }
        return re;
    }
    
    public static double[] startPi(int n) {
        double[] re = new double[n];
            double sum = 0;
            for (int j = 0; j < n; j++) {
                re[j] = 1.0 / n + (Math.random() - 0.5) / n;
                sum += re[j];
            }
            for (int j = 0; j < n; j++){
                re[j] = re[j] / sum;
            }
        return re;
    }
    
    
    
    public static double[][] multiply(double[][] A, double[][] B) {
        
        double[][] matrix = Matrix.matrix(A.length, B[0].length);
        
        for (int i = 0; i < A.length; i++){
            for (int j = 0; j < B[0].length; j++){
                for( int k = 0; k < B.length; k++){
                    matrix[i][j] += A[i][k] * B[k][j];
                }                
            }
        }
        return matrix;
    }
    
    public static double[] multiply(double[] pi, double[][] B) {
        
        double[] matrix = new double[B[0].length];
        
        for (int j = 0; j < matrix.length; j++){
            for( int k = 0; k < B.length; k++){
                matrix[j] += pi[k] * B[k][j];
            }                
        }
        
        return matrix;
    }
}

