# Answers to questions

> 1. Describe the possible states, initial state, transition function. 
> 2. Describe the terminal states of both checkers and tic-tac-toe. 
> 3. Why is nu(A, s) #{white checkers} - #{red checkers} a valid heuristic? 
> 4. When does nu best approximate the utility function, and why. 
> 5. Can you provide and example of a state s where nu(A, s) > 0 and B wins in the following turn? (Hint: recall the rules for jumping in checkers.) 
> 6. Will eta suffer from the same problem (referred to in the last question) as the evaluation function nu? 


1. Initial state: the initial board. 12 markers for each side placed on opposing sides on the board. 
Possible states: all legal moves. 
Transition function: makes a valid move. 

Otherwise generally speaking, the initial state is the initial setup of the game, the possible states represent all valid states we can transition to, and the transition functions transitions from s to s' using the move a.

2. 
Checkers: 
* Red win: only red markers remaining.
* White win: only white markers remaining.
* Other win: no valid moves for opponent remaining. 
* Draw: no capture within last 50 turns.

Tic-tac-toe: 
* Red win: 4 red markers in a row along any valid row, column or diagonal.
* White win: 4 white markers in a row along any valid row, column or diagonal.
* Draw: no more valid moves on the board. 

3. The point of the game is to outplay your opponent, ie capturing all the opponents markers. Thus maximizing the number of markers on your side, while minimizing the opponents markers (to zero). 

4. When the game is over. A utility function gives value for terminal state typically (+1,-1,0), thus the evaluation function is most similar to the utility function when the game is over. 

5. The marker in checkers may make several jumps within one turn if the opponents markers are lined up "properly". Thus several markers may be captured in one turn, and one can win the game, even if the naive heuristic does not favor you. 

6. Theoretically, it should not, since the heuristic directly counts the probability of achieving victory. However the downside of this is, as least how I've understood the question, that one needs to go to the bottom of the tree to determine if the current branch has wins or not. And in this case one could simply just use a normal utility function instead, can they not?

# Answers to other questions

> 1. Describe the minimax algorithm. 
> 2. Describe the alpha beta pruning in depth. 
> 3. Limitations of minimax algorithm: how can we force a draw intentionally in the game?
> 4. Can the use of symmetry increase the score?
> 5. IF a player tries to win, and the other tries to draw, can minimax solve it?

1. Preferable to use for two-player, zero-sum games. Maximizes gain for one player, while at the same time minimizing gain for opponent using alternating recursion. 

2. Seeks to minimize the amount of nodes searched using the minimax algorithm. It stops evaluating a move when at least one possibility has been found that proves the move to be worse than the previously examined move. When applied to a standard minimax tree, it returns the same move as minimax would, but prunes away branches that cannot possibly influence the final decision. 

3. Weigh states that give draw higher than anything else, or let the evaluation always be negative. 

4. This depends on the formulation of question. Should a better performing minimax algorithm affect score (ie being able to search deeper), one can make use of symmetry of the board. When storing game states in a hash table (repeated states checking), one can use the inverted game state if it exists in the hash table already, and inverting the score. 

5. No, because minimax relies on the zero-sum hypothesis. So A and B should aim for directly opposite goals.

# Kattis submissions:

2D Tic-Tac-Toe
[3128717](https://kth.kattis.com/submissions/3128713)

3D Tic-Tac-Toe
[3130056](https://kth.kattis.com/submissions/3130056)

Checkers
[3191027](https://kth.kattis.com/submissions/3191027)
