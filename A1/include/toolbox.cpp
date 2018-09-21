#include "toolbox.h"

// std::vector<double> split(const std::string &s, char delim) {
//   std::stringstream ss(s);
//   std::string item;
//   std::vector<double> elems;
//   while (std::getline(ss, item, delim)) {
//     elems.push_back(std::move(std::stod(item))); // if C++11 (based on comment from @mchiasson)
//   }
//   return elems;
// }

std::vector<double> splitString(std::string toSplit) {
    std::vector<double> retVec;

    std::istringstream sstream(toSplit);
    double value;

    while(sstream) {
        sstream >> value;
        retVec.push_back(value);
    }

    retVec.pop_back();

    return retVec;
}

Matrix getMatrix(std::string line) {
    std::istringstream sstream(line);
    int sizeRow;
    int sizeColumn;
    sstream >> sizeRow;
    sstream >> sizeColumn;

    size_t contentStart = ceil(log10(sizeRow)) + 2 + ceil(log10(sizeColumn));
    std::string subString = line.substr(contentStart, line.size() - contentStart);

    std::vector<double> elements = splitString(subString);

    return Matrix(sizeRow, sizeColumn, elements);
}

Matrix getObservationSequence(std::string line) {
    std::istringstream sstream(line);
    int sizeRow;
    sstream >> sizeRow;

    size_t contentStart = ceil(log10(sizeRow) + 1);
    std::string subString = line.substr(contentStart, line.size() - contentStart);

    std::vector<double> elements = splitString(subString);

    return Matrix(sizeRow, 1, elements);
}