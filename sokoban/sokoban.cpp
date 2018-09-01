#include <string>
#include <fstream>
#include <iostream>
#include <map>
#include "Grid.h"

int main(int argc, char const *argv[])
{
    // if (argc != 2) {
    //     std::cout << "Not enough arguments!" << std::endl;
    //     return 1;
    // }

    std::ifstream inFile;
    inFile.open("00_sample.in");
    // inFile.open(std::string(argv[1]).c_str());

    Grid grid;
    grid.parse(inFile);

    return 0;
}
