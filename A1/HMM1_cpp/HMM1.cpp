#include <iostream>
#include <string>
#include "Matrix.h"
#include "toolbox.h"
#include "alphapass.h"
#include <iomanip>

int main(int argc, char const *argv[])
{
    std::string line;
    std::getline(std::cin, line);
    Matrix A = getMatrix(line);

    std::getline(std::cin, line);
    Matrix B = getMatrix(line);

    std::getline(std::cin, line);
    Matrix pi = getMatrix(line);
    
    std::getline(std::cin, line);
    Matrix emissionSequence = getObservationSequence(line);

    // std::cout << A << std::endl
    //           << B << std::endl
    //           << pi << std::endl
    //           << emissionSequence << std::endl;

    Matrix alpha = alphapass(A, B, pi, emissionSequence, emissionSequence.getRowLength());

    std::cout << alpha.getMatrixSum() << std::endl;

    return 0;
}