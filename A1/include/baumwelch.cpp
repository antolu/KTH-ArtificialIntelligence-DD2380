#include "baumwelch.h"

BaumWelch::BaumWelch(Matrix A, Matrix B, Matrix pi, Matrix emissionSequence) 
{
    this->A = A;
    this->B = B;
    this->pi = pi;
    this->emissionSequence = emissionSequence;
}

std::vector<Matrix> BaumWelch::baumwelch()
{
    int T = emissionSequence.getRowLength();
    int N = A.getRowLength();
    int M = B.getColumnLength();
    int maxIters = 100;
    double oldLogProb = -std::numeric_limits<double>::max();

    double tolerance = 0.01;
    int z = 0;

    for (z = 0; z < maxIters; z++)
    {
        std::vector<Matrix> alphas;
        alphas.reserve(T);
        std::vector<double> scales;
        scales.reserve(T);

        double c0 = 0;

        Matrix a0(N, 1);
        for (int i = 0; i < N; i++)
        {
            a0.set(i, 0, pi.get(0, 1) * B.get(i, getEmission(0)));
            c0 = c0 + a0.get(i, 0);
        }

        c0 = 1.0 / c0;
        a0.scalarMult(c0);

        scales.push_back(c0);
        alphas.push_back(a0);

        for (int t = 1; t < T; t++)
        {
            double ct = 0.0;

            Matrix at(N, 1);
            for (int i = 0; i < N; i++)
            {
                double ati = 0.0;
                for (int j = 0; j < N; j++)
                {
                    ati += alphas[t - 1].get(j, 0) * A.get(j, i);
                }
                ati *= B.get(i, getEmission(t));
                at.set(i, 0, ati);
                ct += at.get(i, 0);
            }

            ct = 1.0 / ct;
            at = at.scalarMult(ct);
            alphas.push_back(at);
            scales.push_back(ct);
        }

        std::vector<Matrix> betas;
        betas.reserve(T);

        Matrix BTm1(N, 1);
        for (int i = 0; i < N; i++)
        {
            BTm1.set(i, 0, scales[(scales.size() - 1)]);
        }

        betas.push_back(BTm1);

        for (int t = T - 2; t >= 0; t--)
        {
            Matrix bt(N, 1);

            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    bt.set(i, 0, bt.get(i, 0) + A.get(i, j) * B.get(j, getEmission(t + 1)) * betas[0].get(j, 0));
                }
            }

            bt = bt.scalarMult(scales[t]);
            std::vector<Matrix> newBetas;
            newBetas.push_back(bt);
            newBetas.insert(std::end(newBetas), std::begin(betas), std::end(betas));
            betas = newBetas;
            // betas.push_back(bt);
            // betas.insert(betas.begin(), bt);
        }

        std::vector<Matrix> digammas;
        digammas.reserve(T);
        std::vector<Matrix> gammas;
        gammas.reserve(T);

        for (int t = 0; t < T - 1; t++)
        {
            double denom = 0;
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    denom += alphas[t].get(i, 0) * A.get(i, j) * B.get(j, getEmission(t + 1)) * betas[t + 1].get(j, 0);
                }
            }
            Matrix digamma(N, N);
            Matrix gamma(N, 1);
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    digamma.set(i, j, alphas[t].get(i, 0) * A.get(i, j) * B.get(j, getEmission(t + 1)) * betas[t + 1].get(j, 0) / denom);
                    gamma.set(i, 0, gamma.get(i, 0) + digamma.get(i, j));
                }
            }

            digammas.push_back(digamma);
            gammas.push_back(gamma);
        }

        double denom = 0;
        for (int i = 0; i < N; i++)
        {
            denom += alphas[T - 1].get(i, 0);
        }

        Matrix gamTm1(N, 1);
        for (int i = 0; i < N; i++)
        {
            gamTm1.set(i, 0, alphas[T - 1].get(i, 0) / denom);
        }

        gammas.push_back(gamTm1);

        for (int i = 0; i < N; i++)
        {
            pi.set(0, i, gammas[0].get(i, 0));
        }

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                double numer = 0.0;
                double denom2 = 0.0;
                for (int t = 0; t < T - 1; t++)
                {
                    numer = numer + digammas[t].get(i, j);
                    denom2 = denom2 + gammas[t].get(i, 0);
                }
                A.set(i, j, numer / denom2);
            }
        }

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < M; j++)
            {
                double numer = 0;
                double denom2 = 0;
                for (int t = 0; t < T; t++)
                {
                    if (getEmission(t) == j)
                    {
                        numer = numer + gammas[t].get(i, 0);
                    }
                    denom2 = denom2 + gammas[t].get(i, 0);
                }
                B.set(i, j, numer / denom2);
            }
        }

        double logProb = 0.0;
        for (int t = 0; t < T; t++)
        {
            logProb = logProb + std::log10(scales[t]);
        }
        logProb = -logProb;

        // std::cout << std::fabs(logProb - oldLogProb) << std::endl;
        // if (std::fabs(logProb - oldLogProb) < tolerance)
        // {
        //     break;
        // }
        oldLogProb = logProb;
        std::cout << z << std::endl;
    }

    std::cout << "Iterations: " << z << std::endl;

    std::vector<Matrix> retVec;

    retVec.push_back(A);
    retVec.push_back(B);
    retVec.push_back(pi);

    return retVec;
}

int BaumWelch::getEmission(int t) {
    if (t > emissionSequence.getRowLength())
        throw new std::logic_error("t not matching emission sequence length!");

    return (int) emissionSequence.get(t, 0);
}