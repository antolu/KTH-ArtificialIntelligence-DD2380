#include "player.hpp"
#include "constants.hpp"
#include <cstdlib>

#define DEPTH 1

namespace TICTACTOE
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

    for (int i = 0; i < 10; i++)
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

int Player::eval(GameState &gamestate, int &player)
{
    return 0;
}

int const Player::winCombinations[10][4] = {
    {0, 1, 2, 3},
    {4, 5, 6, 7},
    {8, 9, 10, 11},
    {12, 13, 14, 15},
    {0, 4, 8, 12},
    {1, 5, 9, 13},
    {2, 6, 10, 14},
    {3, 7, 11, 15},
    {0, 5, 10, 15},
    {3, 6, 9, 12}};

int const Player::rewards[5][5] = {
    {1, -10, -100, -1000, -10000},
    {10, 0, 0, 0, 0},
    {100, 0, 0, 0, 0},
    {1000, 0, 0, 0, 0},
    {10000, 0, 0, 0, 0}};

/*namespace TICTACTOE*/ // namespace TICTACTOE
}
