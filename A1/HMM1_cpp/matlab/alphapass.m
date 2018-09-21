function alphas = alphapass(A, B, pi, emissionSequence, T, alphas)

alpha = B(:, emissionSequence(1) + 1).*pi';

alphas = cell(T, 1);
alphas{1} = alpha;

for t=2:T
    alpha =  A'*alpha.*B(:, emissionSequence(t) + 1);
    alphas{t} = alpha;
end

end