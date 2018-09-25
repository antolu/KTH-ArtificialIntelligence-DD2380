#include "player.hpp"
#include <cstdlib>

#define MAX_DEPTH 50

bool sortbysec(const std::pair<int, checkers::GameState> &a,
               const std::pair<int, checkers::GameState> &b)
{
    return (a.first < b.first);
}

namespace checkers
{

Player::Player()
{
}

GameState Player::play(const GameState &pState, const Deadline &pDue)
{
    //std::cerr << "Processing " << pState.toMessage() << std::endl;

    visited.clear();

    std::vector<GameState> lNextStates;
    pState.findPossibleMoves(lNextStates);

    if (lNextStates.size() == 0)
        return GameState(pState, Move());
    if (lNextStates.size() == 1)
        return lNextStates[0];

    int bestResult = -1e9;
    int bestMove = 0;

    for (int depth = 1; depth < MAX_DEPTH; depth++)
    {
        std::pair<int, int> v = getBestMove(lNextStates, depth);
        if (v.first > bestResult)
        {
            bestResult = std::max(bestResult, v.first);
            bestMove = v.second;
        }
    }

    /*
     * Here you should write your clever algorithms to get the best next move, ie the best
     * next state. This skeleton returns a random move instead.
     */
    return lNextStates[bestMove];
}

std::pair<int, int> Player::getBestMove(std::vector<GameState> lNextStates, int depth)
{

    int bestMove = 0;
    int bestResult = -999999;
    int alpha = -9999999;
    int beta = 9999999;
    for (int i = 0; i < lNextStates.size(); i++)
    {
        int v = alphabeta(lNextStates[i], depth, alpha, beta, me);
        if (v > bestResult)
        {
            bestResult = v;
            bestMove = i;
        }
    }
    return std::make_pair(bestResult, bestMove);
}

int Player::alphabeta(GameState &gamestate, int depth, int &alpha, int &beta, int player)
{
    int bestPossible = 0;

    if (gamestate.isEOG() || depth == 0)
    {
        bestPossible = utility(gamestate, depth);
    }

    std::vector<GameState> lNextStates;
    gamestate.findPossibleMoves(lNextStates);

    std::vector<std::pair<int, GameState>> prelimResults;
    prelimResults.reserve(lNextStates.size());

    // Fix move-ordering
    if (false) // depth > 0
    {
        if (player == me)
        {
            bestPossible = -99999;
            for (int i = 0; i < lNextStates.size(); i++)
            {
                int res = alphabeta(lNextStates[i], 0, alpha, beta, opp);
                prelimResults.push_back(std::make_pair(res, lNextStates[i]));
            }
        }
        else if (player == opp)
        {
            bestPossible = 99999;
            for (int i = 0; i < lNextStates.size(); i++)
            {
                int res = alphabeta(lNextStates[i], 0, alpha, beta, me);
                prelimResults.push_back(std::make_pair(res, lNextStates[i]));

            }
        }
        // // Sort game states
        // std::sort(prelimResults.begin(), prelimResults.end(), sortbysec);
    }
    else
    {
        for (int i = 0; i < lNextStates.size(); i++)
            prelimResults.push_back(std::make_pair(i, lNextStates[i]));
    }

    if (gamestate.isEOG() || depth == 0)
    {
        bestPossible = utility(gamestate, depth);
    }
    if (player == me)
    {
        bestPossible = -99999;
        for (int i = 0; i < prelimResults.size(); i++)
        {
            if (!visited[prelimResults[i].second.toMessage()])
            {
                visited[prelimResults[i].second.toMessage()] = true;
            }
            else
            {
                continue;
            }
            int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, opp);
            bestPossible = std::max(bestPossible, res);
            alpha = std::max(alpha, res);
            if (beta <= alpha)
                break;
        }
    }
    else if (player == opp)
    {
        bestPossible = 99999;
        for (int i = 0; i < prelimResults.size(); i++)
        {
            if (!visited[prelimResults[i].second.toMessage()])
            {
                visited[prelimResults[i].second.toMessage()] = true;
            }
            else
            {
                continue;
            }
            int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, me);
            bestPossible = std::min(bestPossible, res);
            beta = std::min(beta, res);
            if (beta <= alpha)
                break;
        }
    }
    return bestPossible;
}

int Player::utility(GameState &gamestate, int &depth)
{
    /* Check if the game is in terminal state, and determine rewards */
    if (gamestate.isEOG())
    {
        if (gamestate.isRedWin())
            return 1e5;
        else if (gamestate.isWhiteWin())
            return -1e5;
        if (gamestate.isDraw())
            return -10;
        else
            return 0;
    }

    /* Check for the status of the game, who is most likely to win */
    int winPoints, opponentPoints = 0;
    uint8_t marker = 0;
    int value = 0;

    for (int i = 0; i < 32; i++)
    {
        marker = gamestate.at(i);
        if (marker == CELL_RED)
        {
            winPoints++;
        }
        else if (marker == CELL_KING)
        {
            winPoints -= 5;
        }
        // else if (marker == CELL_WHITE) {
        //     opponentPoints++;
        // }
    }
    return winPoints - opponentPoints;
}

/*namespace checkers*/ // namespace checkers
} // namespace checkers
