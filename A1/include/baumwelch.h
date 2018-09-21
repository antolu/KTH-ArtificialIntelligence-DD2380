#ifndef baumwelch_h
#define baumwelch_h

#include <cmath>
#include <map>
#include <vector>
#include <limits>
#include "Matrix.h"

class BaumWelch {

    private:
        Matrix A;
        Matrix B;
        Matrix pi;
        Matrix emissionSequence;


        int getEmission(int t);

    public:
        BaumWelch(Matrix A, Matrix B, Matrix pi, Matrix emissionSequence);
        std::vector<Matrix> baumwelch();

};

#endif