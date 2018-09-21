A = [0 0.8 0.1 0.1; 0.1 0.0 0.8 0.1; 0.1 0.1 0.0 0.8; 0.8 0.1 0.1 0];
B = [0.9 0.1 0 0; 0 0.9 0.1 0 ; 0 0 0.9 0.1; 0.1 0 0 0.9];
pi = [1 0 0 0];

emissionSequence = [0 1 2 3 0 1 2 3]';

alphas = alphapass(A, B, pi, emissionSequence, 8);

sum(alphas{end})