#include "player.hpp"
#include <cstdlib>

#define MAX_DEPTH 4
#define MOVE_ORDERING true
#define STATES_CHECKING true

bool sortDescending(const std::pair<int, checkers::GameState> &a,
                    const std::pair<int, checkers::GameState> &b)
{
    return (a.first < b.first);
}

bool sortAscending(const std::pair<int, checkers::GameState> &a,
                   const std::pair<int, checkers::GameState> &b)
{
    return (a.first > b.first);
}

namespace checkers
{

Player::Player()
{
}

GameState Player::play(const GameState &pState, const Deadline &pDue)
{
    //std::cerr << "Processing " << pState.toMessage() << std::endl;

    opponent = pState.getNextPlayer();
    me = opponent ^ (CELL_WHITE | CELL_RED);
    // std::cerr << me << " " << opponent << std::endl;

    std::vector<GameState> lNextStates;
    pState.findPossibleMoves(lNextStates);

    if (lNextStates.size() == 0)
        return GameState(pState, Move());
    if (lNextStates.size() == 1)
        return lNextStates[0];

    int bestResult = -1e9;
    int bestMove = 0;

    for (int depth = 1; depth <= MAX_DEPTH; depth++)
    {
        // std::cerr << "Depth: " << depth << std::endl;
        visited.clear();
        std::pair<int, int> v = getBestMove(lNextStates, depth);
        if (v.first > bestResult)
        {
            // std::cerr << "Chose depth " << depth << std::endl;
            bestResult = v.first;
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

int Player::alphabeta(GameState &gamestate, int depth, int alpha, int beta, uint8_t player)
{
    int bestPossible = 0;

    if (gamestate.isEOG() || depth == 0)
    {
        bestPossible = utility(gamestate, depth);
    }
    else
    {
        std::vector<GameState> lNextStates;
        gamestate.findPossibleMoves(lNextStates);

        std::vector<std::pair<int, GameState>> prelimResults;
        prelimResults.reserve(lNextStates.size());

        // Fix move-ordering
        if (MOVE_ORDERING) // depth > 0
        {
            if (player == me)
            {
                bestPossible = -99999;
                for (int i = 0; i < lNextStates.size(); i++)
                {
                    int res = utility(lNextStates[i], depth);
                    prelimResults.push_back(std::make_pair(res, lNextStates[i]));
                }
                std::sort(prelimResults.begin(), prelimResults.end(), sortDescending);
                // if (prelimResults.size() > 1 && prelimResults[0].first != prelimResults[1].first) // Check if move ordering works
                // {
                //     for (int i = 0; i < prelimResults.size(); i++)
                //     {
                //         std::cerr << prelimResults[i].first << std::endl;
                //     }
                //     std::cerr << std::endl
                //               << std::endl;
                // }
            }
            else if (player == opponent)
            {
                bestPossible = 99999;
                for (int i = 0; i < lNextStates.size(); i++)
                {
                    int res = utility(lNextStates[i], depth);
                    prelimResults.push_back(std::make_pair(res, lNextStates[i]));
                }
                std::sort(prelimResults.begin(), prelimResults.end(), sortAscending);
            }
        }
        else
        {
            for (int i = 0; i < lNextStates.size(); i++)
                prelimResults.push_back(std::make_pair(i, lNextStates[i]));
        }

        if (player == me)
        {
            bestPossible = -99999;
            for (int i = 0; i < prelimResults.size(); i++)
            {
                if (STATES_CHECKING)
                {
                    if (!visited[prelimResults[i].second.toMessage()])
                    {
                        visited[prelimResults[i].second.toMessage()] = true;
                    }
                    else
                    {
                        continue;
                    }
                }
                int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, opponent);
                bestPossible = std::max(bestPossible, res);
                alpha = std::max(alpha, res);
                if (beta <= alpha)
                    break;
            }
        }
        else if (player == opponent)
        {
            bestPossible = 99999;
            for (int i = 0; i < prelimResults.size(); i++)
            {
                if (STATES_CHECKING)
                {
                    if (!visited[prelimResults[i].second.toMessage()])
                    {
                        visited[prelimResults[i].second.toMessage()] = true;
                    }
                    else
                    {
                        continue;
                    }
                }
                int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, player);
                bestPossible = std::min(bestPossible, res);
                beta = std::min(beta, res);
                if (beta <= alpha)
                    break;
            }
        }
    }
    return bestPossible;
}

int Player::utility(GameState &gamestate, int depth)
{
    /* Check if the game is in terminal state, and determine rewards */
    if (gamestate.isEOG())
    {
        // std::cerr << "Something" << std::endl;
        if ((gamestate.isRedWin() && me == CELL_RED) || (gamestate.isWhiteWin() && me == CELL_WHITE))
            return -1e3;
        else if ((gamestate.isRedWin() && opponent == CELL_RED) || (gamestate.isWhiteWin() && opponent == CELL_WHITE))
            return 1e3 + depth * 100;
        else
            return 0;
    }

    /* Check for the status of the game, who is most likely to win */
    int winPoints, opponentPoints = 0;
    int marker = 0;
    int value = 0;

    for (int i = 0; i < 32; i++)
    {
        marker = gamestate.at(i);
        if (marker & me)
        {
            winPoints++;
        }
        else if (marker == CELL_KING)
        {
            winPoints += 2;
        }
        // else if (marker & opponent)
        // {
        //     opponentPoints++;
        // }
    }
    return depth * (winPoints - opponentPoints);
}

} // namespace checkers
