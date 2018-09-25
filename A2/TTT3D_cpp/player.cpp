#include "player.hpp"
#include "constants.hpp"
#include <cstdlib>

#define DEPTH 3

namespace TICTACTOE3D
{

Player::Player()
{ }

/**
 * Hardcoded to play as X player as Kattis expects this
 */
GameState Player::play(const GameState &pState, const Deadline &pDue)
{
    // playerX = pState.getNextPlayer();
    // playerO = playerX ^ (CELL_X | CELL_O); // Current player
    // std::cerr << playerX << playerO << std::endl;
    //std::cerr << "Processing " << pState.toMessage() << std::endl;

    std::vector<GameState> lNextStates;
    pState.findPossibleMoves(lNextStates);

    if (lNextStates.size() == 0)
        return GameState(pState, Move());
    if (lNextStates.size() == 1) 
        return lNextStates[0];

    int bestMove = 0;
    int bestResult = -999999;
    int alpha = -9999999;
    int beta = 9999999;
    for (int i = 0; i < lNextStates.size(); i++) {
        int v = alphabeta(lNextStates[i], DEPTH, alpha, beta, me);
        if (v > bestResult) {
            bestResult = v;
            bestMove = i;
        }
    }

    /*
     * Here you should write your clever algorithms to get the best next move, ie the best
     * next state. This skeleton returns a random move instead.
     */
    return lNextStates[bestMove];
}

int Player::minimax(GameState &gamestate, int player, int depth)
{
    std::vector<GameState> lNextStates;
    gamestate.findPossibleMoves(lNextStates);

    if (gamestate.isEOG() || depth == 0)
    {
        return utility(gamestate, depth);
    }

    if (player == playerO)
    {
        int bestPossible = -9999;
        for (GameState childState : lNextStates)
        {
            int v = minimax(childState, playerX, depth - 1);
            if (v > bestPossible)
            {
                bestPossible = v;
            }
        }
        return bestPossible;
    }
    else if (player == playerX)
    {
        int bestPossible = 9999;
        for (GameState childState : lNextStates)
        {
            int v = minimax(childState, playerO, depth - 1);
            if (v < bestPossible)
            {
                bestPossible = v;
            }
        }
        return bestPossible;
    }

    return 0;
}

int Player::alphabeta(GameState &gamestate, int depth, int &alpha, int &beta, int player)
{
    int bestPossible = 0;

    std::vector<GameState> lNextStates;
    gamestate.findPossibleMoves(lNextStates);

    if (gamestate.isEOG() || depth == 0) {
        bestPossible = utility(gamestate, depth);
    }
    else if (player == me)
    {
        bestPossible = -99999;
        for (GameState childState : lNextStates)
        {
            int res = alphabeta(childState, depth - 1, alpha, beta, opp);
            bestPossible = std::max(bestPossible, res);
            alpha = std::max(alpha, res);
            if (beta <= alpha) 
                break;
        }
    }
    else if (player == opp)
    {
        bestPossible = 99999;
        for (GameState childState : lNextStates)
        {
            int res = alphabeta(childState, depth - 1, alpha, beta, me);
            bestPossible = std::min(bestPossible, res);
            beta = std::min(beta, res);
            if (beta <= alpha)
                break;
        }
    }
    return bestPossible;
}

int Player::utility(GameState &gamestate, int & depth)
{
    /* Check if the game is in terminal state, and determine rewards */
    if (gamestate.isEOG())
    {
        if (gamestate.isXWin())
            return 1e5 + depth * 10;
        else if (gamestate.isOWin())
            return -1e5;
        else
            return 0;
    }

    /* Check for the status of the game, who is most likely to win */
    int winPoints, opponentPoints = 0;
    int marker = 0;
    int value = 0;

    for (int i = 0; i < 76; i++)
    {
        winPoints = opponentPoints = 0;
        for (int j = 0; j < 4; j++)
        {
            uint8_t marker = gamestate.at(winCombinations[i][j]);
            if (marker == CELL_X)
                winPoints++;
            else if (marker == CELL_O)
                opponentPoints++;
        }
        value += rewards[winPoints][opponentPoints];
    }
    return value;
}

int const Player::winCombinations[76][4] = {
    /* Normal win conditions, row - based */
    {0, 1, 2, 3}, 
    {4, 5, 6, 7}, 
    {8, 9, 10, 11}, 
    {12, 13, 14, 15}, 
    {16, 17, 18, 19}, 
    {20, 21, 22, 23}, 
    {24, 25, 26, 27}, 
    {28, 29, 30, 31}, 
    {32, 33, 34, 35}, 
    {36, 37, 38, 39}, 
    {40, 41, 42, 43}, 
    {44, 45, 46, 47},
    {48, 49, 50, 51}, 
    {52, 53, 54, 55},
    {56, 57, 58, 59},
    {60, 61, 62, 63},
    /* Normal win conditions, column - based */
    {0, 4, 8, 12}, 
    {1, 5, 9, 13}, 
    {2, 6, 10, 14}, 
    {3, 7, 11, 15}, 
    {16, 20, 24, 28}, 
    {17, 21, 25, 29}, 
    {18, 22, 26, 30}, 
    {19, 23, 27, 31}, 
    {32, 36, 40, 44}, 
    {33, 37, 41, 45}, 
    {34, 38, 42, 46}, 
    {35, 39, 43, 47}, 
    {48, 52, 56, 60}, 
    {49, 53, 57, 61}, 
    {50, 54, 58, 62}, 
    {51, 55, 59, 63},
    /* "Vertical" columns */
    {0, 16, 32, 48}, 
    {1, 17, 33, 49}, 
    {2, 18, 34, 50}, 
    {3, 19, 35, 51}, 
    {4, 20, 36, 52}, 
    {5, 21, 37, 53}, 
    {6, 22, 38, 54}, 
    {7, 23, 39, 55}, 
    {8, 24, 40, 56}, 
    {9, 25, 41, 57}, 
    {10, 26, 42, 58}, 
    {11, 27, 43, 59}, 
    {12, 28, 44, 60}, 
    {13, 29, 45, 61}, 
    {14, 30, 46, 62}, 
    {15, 31, 47, 63}, 
    /* Normal diagonals, topographical */
    {0, 5, 10, 15},   // First layer
    {3, 6, 9, 12},    // First layer
    {16, 21, 26, 31}, // Second layer
    {19, 22, 25, 28}, // Second layer
    {32, 37, 42, 47}, 
    {35, 38, 41, 44}, 
    {48, 53, 58, 63}, 
    {51, 54, 57, 60},
    /* Normal diagonals, from left */
    {48, 36, 24, 12}, 
    {0, 20, 40, 60}, 
    {49, 37, 25, 13}, 
    {1, 21, 41, 61}, 
    {50, 38, 26, 14}, 
    {2, 22, 42, 62}, 
    {51, 39, 27, 15}, 
    {3, 23, 43, 63},
    /* Normal diagonals, from top */
    {0, 17, 34, 51}, 
    {3, 18, 33, 48}, 
    {4, 21, 38, 55}, 
    {7, 22, 37, 52},
    {8, 25, 42, 59}, 
    {11, 26, 41, 56}, 
    {12, 29, 46, 63}, 
    {15, 30, 45, 60},
    /* 3D diagonals */
    {0, 21, 42, 63}, 
    {3, 22, 41, 60}, 
    {48, 37, 26, 15}, 
    {51, 38, 25, 12}, 
    }; 

int const Player::rewards[5][5] = {
    {1, -10, -100, -1000, -10000},
    {10, 0, 0, 0, 0},
    {100, 0, 0, 0, 0},
    {1000, 0, 0, 0, 0},
    {10000, 0, 0, 0, 0}};

/*namespace TICTACTOE*/ // namespace TICTACTOE
}
