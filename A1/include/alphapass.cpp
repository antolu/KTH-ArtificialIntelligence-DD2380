#include "alphapass.h"

Matrix alphapass(Matrix A, Matrix B, Matrix pi, Matrix emissionSequence, int T) {
        Matrix piT = pi.transpose();
        Matrix BColumn = B.getColumn((int) emissionSequence.get(0, 0));
        Matrix alpha = BColumn/piT;
        
        for(int t = 1; t < T; t++) {
            BColumn = B.getColumn((int) emissionSequence.get(t, 0));
            Matrix AT = A.transpose();
            alpha = AT*alpha;
            alpha = alpha/BColumn;
        }

        return alpha;
}