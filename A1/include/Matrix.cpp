#include "Matrix.h"

Matrix::Matrix() {}

Matrix::Matrix(int row, int column)
{
    this->m = row;
    this->n = column;

    matrix = new double *[m];
    for (int i = 0; i < m; i++)
    {
        matrix[i] = new double[n];
    }
}

Matrix::Matrix(int row, int column, std::vector<double> inData) : Matrix(row, column)
{
    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            matrix[i][j] = inData[i * n + j];
        }
    }
}

void Matrix::set(int rowPos, int columnPos, double data)
{
    matrix[rowPos][columnPos] = data;
}

void Matrix::addToElement(int rowPos, int columnPos, double data)
{
    matrix[rowPos][columnPos] += data;
}

double Matrix::get(int rowPos, int columnPos)
{
    return matrix[rowPos][columnPos];
}

int Matrix::getColumnLength()
{
    return n;
}

int Matrix::getRowLength()
{
    return m;
}

Matrix Matrix::getRow(int row)
{
    Matrix retMatrix(1, n);
    for (int i = 0; i < n; i++)
    {
        retMatrix.set(0, i, matrix[row][i]);
    }

    return retMatrix;
}

Matrix Matrix::getColumn(int column)
{
    Matrix retMatrix(m, 1);
    for (int i = 0; i < m; i++)
    {
        retMatrix.set(i, 0, matrix[i][column]);
    }

    return retMatrix;
}

Matrix Matrix::transpose()
{
    Matrix transpose(n, m);
    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            transpose.set(j, i, matrix[i][j]);
        }
    }
    return transpose;
}

Matrix Matrix::operator*(Matrix &other)
{
    if (n != other.m)
    {
        throw new std::logic_error("Mismatched rows or columns");
    }

    Matrix retMatrix(m, other.n);

    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < other.n; j++)
        {
            for (int k = 0; k < other.m; k++)
            {
                retMatrix.addToElement(i, j, get(i, k) * other.get(k, j));
            }
        }
    }

    return retMatrix;
}

Matrix Matrix::operator+(Matrix &matrix)
{
    if (this->n != matrix.n || this->m != matrix.m)
    {
        throw new std::logic_error("Matrix dimensions must be same");
    }

    Matrix retMatrix(this->m, this->n);

    for (int i = 0; i < this->m; i++)
    {
        for (int j = 0; j < this->n; j++)
        {
            retMatrix.set(i, j, get(i, j) + matrix.get(i, j));
        }
    }

    return retMatrix;
}

Matrix Matrix::operator-(Matrix &matrix)
{
    if (this->n != matrix.n || this->m != matrix.m)
    {
        throw new std::logic_error("Matrix dimensions must be same");
    }

    Matrix retMatrix(this->m, this->n);

    for (int i = 0; i < this->m; i++)
    {
        for (int j = 0; j < this->n; j++)
        {
            retMatrix.set(i, j, get(i, j) - matrix.get(i, j));
        }
    }

    return retMatrix;
}

Matrix Matrix::operator/(Matrix &matrix)
{
    if (this->n != matrix.n || this->m != matrix.m)
    {
        throw new std::logic_error("Matrix dimensions must be same");
    }

    Matrix retMatrix(this->m, this->n);

    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            retMatrix.set(i, j, get(i, j) * matrix.get(i, j));
        }
    }
    return retMatrix;
}

Matrix Matrix::scalarMult(double x)
{
    Matrix retMatrix(this->m, this->n);

    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            retMatrix.set(i, j, get(i, j) * x);
        }
    }
    return retMatrix;
}

std::ostream &operator<<(std::ostream &strm, Matrix const &matrix)
{
    std::string retString = std::to_string(matrix.m) + " " + std::to_string(matrix.n);

    strm << retString;

    for (int i = 0; i < matrix.m; i++)
    {
        for (int j = 0; j < matrix.n; j++)
        {
            strm << " ";
            strm << matrix.matrix[i][j];
        }
    }
    return strm;
}

std::string Matrix::toString()
{
    std::string retString = std::to_string(m) + " " + std::to_string(n);

    std::stringstream stream;
    stream << retString;

    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            stream << " ";
            stream << get(i, j);
        }
    }

    return stream.str();
}

double Matrix::getMatrixSum()
{
    double sum = 0.0;
    for (int i = 0; i < m; i++)
    {
        for (int j = 0; j < n; j++)
        {
            sum += matrix[i][j];
        }
    }
    return sum;
}