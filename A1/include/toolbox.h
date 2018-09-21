#ifndef toolbox_h
#define toolbox_h

#include <string>
#include <sstream>
#include <vector>
#include <iostream>
#include <cmath>
#include "Matrix.h"

std::vector<double> splitString(std::string toSplit);

Matrix getMatrix(std::string line);

Matrix getObservationSequence(std::string line);

#endif