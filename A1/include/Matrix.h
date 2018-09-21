#ifndef matrix_h
#define matrix_h

#include <map>
#include <string>
#include <vector>
#include <iostream>
#include <sstream>
#include <iomanip>

class Matrix
{
  private:
    int m;
    int n;

    double ** matrix;

  public:
    Matrix();
    Matrix(int row, int column);
    Matrix(int row, int column, std::vector<double> inData);

    void set(int rowPos, int columnPos, double data);
    void addToElement(int rowPos, int columnPos, double data);
    double get(int rowPos, int columnPos);

    int getColumnLength();
    int getRowLength();

    Matrix getRow(int row);
    Matrix getColumn(int column);

    Matrix transpose();

    Matrix operator+(Matrix &matrix);
    Matrix operator-(Matrix &matrix);
    Matrix operator*(Matrix &matrix);
    Matrix operator/(Matrix &matrix);
    Matrix scalarMult(double x);
    friend std::ostream &operator<<(std::ostream &strm, Matrix const &matrix);
    std::string toString();
    double getMatrixSum();
};

#endif