# HMM3 Answer to questions

## Question 7

The algorithm converges. The number of observations required for convergence of course depends on how convergence is defined, and implemented in the algorithm. Comparing the estimated A and B matrices against the given HMM dynamics in the lab instructions, the estimations were quite close to the given ones, both for N=1000 and N=10000 oservations. The stamp tutorial implements one way of convergence, but the preferred way would likely be to take the absolut difference between matrices in two iterations, squaring elementwise, and taking the square root over the element sums, just as vector distance is calculated, and let the difference be lower than a given tolerance. 

## Question 8

As mentioned above, how is distance between two matrices defined? The definition above is likely the reasonable, and intuitive one. 

Using the given initialization parameters, the "distance" between "real" A matrix and the estimated one was 0.061, while the same for B was 0.188, which may not be an optimal estimation, but a decent one.

Using another set of initialization matrices, the "distance" for A was 0.477 and for B 1.401.

Both measurements were performed with a tolerance of 0.001. With decreasing convergence tolerance, the "distance" between the "real" matrices and estimated ones should decrease.

What issue are you talking about?

## Question 9

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

The hidden states there are, the more processing time/power is required for the Baum-welch algorithm to converge. Thus finding an optimal number of hidden states is likely not mathematical, but weighing the estimated model probabilities against processing time.

Given this exercise, of course 3 states and 4 observations makes the most sense, as the observation sequence is generated from that model. But 

With more observations, a longer observation sequence is required for accurately estimated matrices. For the given observation sequence with 4 observable states, we cannot "concenate" the observation matrix, or extend it, since data would be overflowing/missing from the given observation sequence.

## Question 10

Uniform distribution: 

A = 
|  -  |  -   |  -   |
|-----|------|------|
| 0.7 | 0.05 | 0.25 |
| 0.1 | 0.8  | 0.1  |
| 0.2 | 0.3  | 0.5  |

B = 
|  -  |  -  |  -  |  -   |
|-----|-----|-----|------|
| 0.7 | 0.2 | 0.1 | 0.0  |
| 0.1 | 0.4 | 0.3 | 0.27 |
| 0.0 | 0.1 | 0.2 | 0.7  |

pi = 
|  -   |  -   |  -   |
|------|------|------|
| 0.33 | 0.33 | 0.33 |


With A, B and pi matrices having uniform distributions on each row will leave the Baum-welch algorithm unable to learn anything as it is iterating. Every operation will give the same answer in each loop, and the algorithm will terminate, since the difference between two estimated A-, B-, and pi-matrices will be little to none, and the algorithm determines it has converged. Even without a terminating statement, leaving the algorithm to run 10 000 iterations, no matrices will have noticable changes. 

---

### Diagonal A-matrix, pi = (0,0,1)

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

### Matrices close to the solution: 

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