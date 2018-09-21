#include <iostream>
#include <string>
#include "Matrix.h"
#include "toolbox.h"

int main(int argc, char const *argv[])
{

    std::string line;
    std::getline(std::cin, line);
    Matrix A = getMatrix(line);

    std::getline(std::cin, line);
    Matrix B = getMatrix(line);

    std::getline(std::cin, line);
    std::vector<double> piElements = splitString(line);
    Matrix pi = getMatrix(line);

    std::cout << A.toString() << std::endl
              << B.toString() << std::endl
              << pi.toString() << std::endl;

    Matrix product = pi * A;
    // std::cout << product.toString() << std::endl;
    Matrix res = product * B;

    std::cout << res.toString() << std::endl;

    return 0;
}