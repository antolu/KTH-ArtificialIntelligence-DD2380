# Answers to questions

### Question 1

Assuming it is equally probable to take any coin initially, the pi matrix is a uniform distribution. Additionally the A matrix should also be uniform, since flipping one coin at a time t does not affect the probability of flipping the same coin, or the other coin, the A matrix should be a uniform 2x2 matrix. Lastly the B matrix is formulated as 

B = 
|   |   |
|---|---|
| 0.9 | 0.1 |
| 0.5 | 0.5 |

if assuming that the first column corresponds to the tail observation, and the second column corresponds to the tail observation. The first row is the cheated coin, while the second state is the normal coin. 


### Question 2

Multiplying the current state distribution (row vector) with the transition matrix A, yields the state distribution at the next time step.

### Question 3 

Multiplying the current state distribution with the observation/emission matrix yields the probabilities to observe certain emissions at that time step.

### Question 4

Given that `alpha_t(i) = P(O_1:t = o_1:t, Xt = xt)`, suppose that `O_1:t = o_1:t == (O1 = o1) Λ ... Λ (O1 = o1)`, then given the formula `P(A|B) = P(A Λ B) / P(B)`, it is fully valid to write the first expression in its conditioned form.  

Additionally, the probability of observing Ot given Xt where Xt is known, is conditionally independant of the previous terms in the emission sequence.

### Question 5

The delta matrix has as many rows as hidden states and columns as many as the time steps. The deltaidx has the same size as delta. 

### Question 6 

The sum over the alphas in the denominator acts as a normalizing agent. As the alphas get smaller for each iteration (the probability of observing a probability sequence, and being in a specific state up to a time t), it is necessary to normalize the parameters to avoid underflow.

### Question 7

The algorithm converges. The number of observations required for convergence of course depends on how convergence is defined, and implemented in the algorithm. Comparing the estimated A and B matrices against the given HMM dynamics in the lab instructions, the estimations were quite close to the given ones, both for N=1000 and N=10000 oservations. The stamp tutorial implements one way of convergence, but the preferred way would likely be to take the absolut difference between matrices in two iterations, squaring elementwise, and taking the square root over the element sums, just as vector distance is calculated, and let the difference be lower than a given tolerance. 

There is of course also the issue that the matrices A and B is not simply "normal" matrices. They are probability distributions, and do not exist in euclidean space, and thus "distance" is not clearly defined, and that is probably why stamps tutorial uses a logarithmic probability for comparison.

With 50 observations, BW converged after 89 iterations with tolerance 0.001.  
With 100 observations, BW converged after 67 iterations with tolerance 0.001.  
With 1000 observations, BW converged after 124 iterations with tolerance 0.001. 
With 10000 observations, BW converged after 163 iterations with tolerance 0.001. 

In our BW algorithm, the algorithms converges even for small observation sequences with only 10 elements. However it does not converge to the "correct" values of the original model. Therefore, the algorithm could converge even if the estimated parameters are very far from the realistic ones.

### Question 8

As mentioned above, how is distance between two matrices defined? The definition above is likely not the reasonable, and intuitive one. 

Using the given initialization parameters and the heuristic method ("normal vector distance"), the "distance" between "real" A matrix and the estimated one was 0.061, while the same for B was 0.188, which may not be an optimal estimation, but a decent one.

Using another set of initialization matrices, the "distance" for A was 0.477 and for B 1.401.

Both measurements were performed with a tolerance of 0.001. With decreasing convergence tolerance, the "distance" between the "real" matrices and estimated ones should decrease.

What issue are you talking about?
PROBABLY SOMETHING ABOUT LOGARITHMIC PROBABILITIES

### Question 9

Initializing with 2 states and 1000 overvations gives

A = 
|   |   |
|---|---|
| 0.66 | 0.34 |
| 0.18 | 0.82 |

B = 
|   |   |   |   |
|---|---|---|---|
| 0.69 | 0.16 | 0.08 | 0.07 |
| 0.01 | 0.29 | 0.32 | 0.38 |

After 45 iterations with tolerance 0.001. oldLogProb -584.

Initializing with 3 states and 1000 observation gives

After 124 iterations we receive oldLogProb = -580.

Initializing with 4 states and 1000 observations gives

After 353 iterations we reach oldLogProb = -578.

The more hidden states there are, the more processing time/power is required for the Baum-welch algorithm to converge. Thus finding an optimal number of hidden states is likely not mathematical, but weighing the estimated model probabilities against processing time.

Given this exercise, of course 3 states and 4 observations makes the most sense, as the observation sequence is generated from that model. But 

With more observations, a longer observation sequence is required for accurately estimated matrices. For the given observation sequence with 4 observable states, we cannot "concenate" the observation matrix, or extend it, since data would be overflowing/missing from the given observation sequence.

### Question 10

Uniform distribution: 

A = 
|  -  |  -   |  -   |
|-----|------|------|
| 1/3 | 1/3  | 1/3  |
| 1/3 | 1/3  | 1/3  |
| 1/3 | 1/3  | 1/3  |

B = 
|  -   |  -   |  -   |  -   |
| ---- | ---- | ---- | ---- |
| 0.25 | 0.25 | 0.25 | 0.25 |
| 0.25 | 0.25 | 0.25 | 0.25 |
| 0.25 | 0.25 | 0.25 | 0.25 |

pi = 
|  -   |  -   |  -   |
| ---- | ---- | ---- |
| 0.33 | 0.33 | 0.33 |


With A, B and pi matrices having uniform distributions on each row will leave the Baum-welch algorithm unable to learn anything as it is iterating. Every operation will give the same answer in each loop, and the algorithm will terminate, since the difference between two estimated A-, B-, and pi-matrices will be little to none, and the algorithm determines it has converged. Even without a terminating statement, leaving the algorithm to run 10 000 iterations, no matrices will have noticable changes.

If A, B and pi are uniform the algorithm will be in a local maximum that it cannot get out of, and thus the method will not converge. 

---

##### Diagonal A-matrix, pi = (0,0,1)

A = 
|  -  |  -  |  -  |
|-----|-----|-----|
| 1.0 | 0.0 | 0.0 |
| 0.0 | 1.0 | 0.0 |
| 0.0 | 0.0 | 1.0 |

B = 
|  -   |  -   |  -   |  -   |
|------|------|------|------|
| 0.5  | 0.2  | 0.11 | 0.19 |
| 0.22 | 0.28 | 0.23 | 0.27 |
| 0.19 | 0.21 | 0.15 | 0.45 |

pi = 
|  -  |  -  |  -  |
|-----|-----|-----|
| 0.0 | 0.0 | 1.0 |

Using a diagonal A-matrix and concentrating all initial probability to the last state leads to both A and B matrices being trained to ~0. This is because the diagonal matrix leaves to space for state transitions to other states, and a state can only transition to itself. In the Baum-welch algorithm supplied in the stamp tutorial, this leads to a divison by 0, which completely breaks the algorithm. :)

---

##### Matrices close to the solution: 

A = 
|  -   |  -   |  -   |
|------|------|------|
| 0.6  | 0.1  | 0.3  |
| 0.0  | 0.9  | 0.1  |
| 0.15 | 0.33 | 0.52 |

B = 
|  -   |  -   |  -   |  -   |
|------|------|------|------|
| 0.67 | 0.12 | 0.17 | 0.04 |
| 0.15 | 0.38 | 0.25 | 0.22 |
| 0.04 | 0.12 | 0.15 | 0.19 |

pi =
|  -  |  -   |  -   |
|-----|------|------|
| 0.9 | 0.05 | 0.05 |

Initializing with matrices very close to the solution will make the Baum-welch converge faster, with fewer iterations in the algorithm. Using the supplied observation sequence with 1000 observations and the matrices above with a tolerance set to 0.01 according to the stamp algorithm, the number of iterations required for convergence was almost cut in half.

## Additional questions requiring answers

#### What is a HMM, what defines it, and what are the matrices

Firstly, a markov process is a random process whose future probabilities are determined by its most recent values. A first order markov model bases the next state only on the present state, and none before that. 

A Hidden Markov Model is a markov model, where the system being modeled is assumed to be a Markov process, however with unobserved (hidden) states, and can only be observed with emissions from the hidden states. Each state has a probability distribution over the possible emissions. Therefore a sequence of emissions generated by a HMM gives information about the sequence of states. 

The matrices in a HMM are A, B and pi.  
A is the state transition matrix, a row-stochastic matrix that specifies the probability distribution of being in a state xi, and transitioning to a state xj.  
The B matrix is the observation/emission matrix, and gives the probability distribution of observing an emission k while being in state xj.  
The pi matrix is the initial probability distribution of states, at t = 0. 

#### Which 3 problems in the labs were solved with HMMs

1. HMM0: in this task, A, B, and pi matrices were given, and the task was to predict the probability distribution for the different emissions after one (1) state transition from the initial state.

2. HMM1: in this task, A, B and pi matrices, as well as an emission sequence were given. The task was to determine the probability of observing the given emission sequence. 

3. HMM2: in this task, A, B and pi matrices, as well as an emission sequence were given. The task was to determine the most likely sequence of hidden states that generated the given emission sequence. 

4. HMM3: in this task, initial guess for A, B and pi matrices, as well a complete emission sequence were given. The task was to estimate the "real" values of the A, B, and pi matrices, ie the model parameters. That is to train the HMM to maximize the probability of observing the given emission sequence. 

#### Which algorithms did you use for these problems?

1. HMM0: None.
2. HMM1: The forward algorithm, ie the alpha pass.
3. HMM2: The dynamic programming algorithm: viterbi.
4. HMM3: The iterative Baum-welch algorithm.

#### Explain implementation of HMM0-HMM3

1. HMM0: Given the initial state in a row vector form, multiply with A to get probability distribtion of next state, then multiply with B to receive probability distribution of emissions, that is the answer. 

2. HMM1: Determine the first alpha (alpha1) by multiplying the initial state matrix elementwise with the B-column corresponding to the first emission. Then iterate over a for-loop, multiplying the transition matrix with the previous alpha, to reach the next estimated state, then multiply elementwise with the B-column coresponding to the emission at that timestep, then repeat. Each vector alpha_t is the probability distribution of having observed the emissions in the emission sequence up to the time t, and being in that state (element). Thus the element sum of the final alpha, alphaT, is the total probability of being in any state at time t, and having observed the given emission sequence. 

3. HMM2: 


4. HMM3: 


#### Duckhunt: what could be changed if we knew vertical movement was independant of horizontal movement

One solution to this is to model horizontal and vertical movement separately, and use both models simultaneusly to determine next emissions.

#### Duckhunt: what would happen if it was a cloudy day and observations were missing for a few timesteps?

One can "fill in the gaps" with the most likely next observations, based on the previous likely hidden state. Of course this newly constructed observation sequence can not be used to train the HMM afterwards. 