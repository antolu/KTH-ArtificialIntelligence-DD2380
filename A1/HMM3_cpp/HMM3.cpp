#include <iostream>
#include <map>
#include <fstream>
#include <string>
#include "Matrix.h"
#include "toolbox.h"
#include "baumwelch.h"
#include <iomanip>

int main(int argc, char const *argv[])
{
    std::ifstream file;
    file.open("input.txt");
    std::string line;
    // std::getline(std::cin, line);
    std::getline(file, line);
    Matrix A = getMatrix(line);

    std::getline(file, line);
    // std::getline(std::cin, line);
    Matrix B = getMatrix(line);

    std::getline(file, line);
    // std::getline(std::cin, line);
    Matrix pi = getMatrix(line);
    
    std::getline(file, line);
    // std::getline(std::cin, line);
    Matrix emissionSequence = getObservationSequence(line);

    BaumWelch baumwelch(A, B, pi, emissionSequence);

    std::vector<Matrix> results = baumwelch.baumwelch();

    std::cout << A << std::endl
              << B << std::endl
              << results[0] << std::endl
              << results[1] << std::endl;
    //           << pi << std::endl
    //           << emissionSequence << std::endl;

    return 0;
}