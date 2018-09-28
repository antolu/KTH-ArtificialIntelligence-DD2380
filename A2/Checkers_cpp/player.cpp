#include "player.hpp"
#include <cstdlib>

#define MAX_DEPTH 17
#define MOVE_ORDERING
#define STATES_CHECKING
#define IDFS
#define TIME_LIMIT 0.1

bool sortDescending(const std::pair<int, checkers::GameState> &a,
                    const std::pair<int, checkers::GameState> &b)
{
    return (a.first > b.first);
}

bool sortAscending(const std::pair<int, checkers::GameState> &a,
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
    me = pState.getNextPlayer();
    opponent = me ^ (CELL_WHITE | CELL_RED);
    if (me&CELL_RED) {
        scoreboard[me] = scoreboardRed;
        scoreboard[opponent] = scoreboardWhite;
    }
    else {
        scoreboard[opponent] = scoreboardRed;
        scoreboard[me] = scoreboardWhite;
    }
    // std::cerr << me << " " << opponent << std::endl;

    std::vector<GameState> lNextStates;
    pState.findPossibleMoves(lNextStates);

    if (lNextStates.size() == 0)
        return GameState(pState, Move());
    if (lNextStates.size() == 1)
        return lNextStates[0];

    int bestResult = -1e9;
    int bestMove = 0;

#ifdef IDFS
    for (int depth = 1; depth < MAX_DEPTH; depth++)
    {
        std::pair<int, int> v = getBestMove(lNextStates, depth, pDue);
        if (v.first > bestResult)
        {
            bestResult = v.first;
            bestMove = v.second;
        }
        if (pDue.getSeconds() - pDue.now().getSeconds() < TIME_LIMIT)
            break;
    }
#else
    std::pair<int, int> v = getBestMove(lNextStates, MAX_DEPTH, pDue);
    bestResult = v.first;
    bestMove = v.second;
#endif

    /*
     * Here you should write your clever algorithms to get the best next move, ie the best
     * next state. This skeleton returns a random move instead.
     */
    return lNextStates[bestMove];
}

std::pair<int, int> Player::getBestMove(std::vector<GameState> lNextStates, int depth, const Deadline & pDue)
{

    int bestMove = 0;
    int bestResult = -999999;
    int alpha = -9999999;
    int beta = 9999999;
    for (int i = 0; i < lNextStates.size(); i++)
    {
        int v = alphabeta(lNextStates[i], depth, alpha, beta, me, pDue);
        if (v > bestResult)
        {
            bestResult = v;
            bestMove = i;
        }
        if (pDue.getSeconds() - pDue.now().getSeconds() < TIME_LIMIT)
            break;
    }
    return std::make_pair(bestResult, bestMove);
}

int Player::alphabeta(GameState &gamestate, int depth, int alpha, int beta, uint8_t player, const Deadline & pDue)
{
    int bestPossible = 0;

    if (gamestate.isEOG() || depth == 0)
    {
        bestPossible = utility(gamestate, player);
        return bestPossible;
    }
    std::vector<GameState> lNextStates;
    gamestate.findPossibleMoves(lNextStates);

    std::vector<std::pair<int, GameState>> prelimResults;
    prelimResults.reserve(lNextStates.size());

// Fix move-ordering
#ifdef MOVE_ORDERING
    // Find values from last depth run 
    int res;
    for (int i = 0; i < lNextStates.size(); i++)
    {
        int hash = hashState(lNextStates[i]);
        if (moveOrdering.count(hash)) {
            res = moveOrdering[hash];
            prelimResults.push_back(std::make_pair(res, lNextStates[i]));
        }
        else {
            prelimResults.push_back(std::make_pair(-10, lNextStates[i]));
        }
    }
    if (player == me) // Sort in descending order
    {
        std::sort(prelimResults.begin(), prelimResults.end(), sortDescending);
        // std::cerr << "me" << std::endl;
    }
    else if (player == opponent) // Sort in ascending order
    {
        std::sort(prelimResults.begin(), prelimResults.end(), sortAscending);
        // std::cerr << "opponent" << std::endl;
    }
    // if (prelimResults.size() > 1 && prelimResults[0].first != prelimResults[1].first) // Check if move ordering works
    // {
    //     for (int i = 0; i < prelimResults.size(); i++)
    //     {
    //         std::cerr << prelimResults[i].first << std::endl;
    //     }
    //     std::cerr << std::endl
    //                 << std::endl;
    // }
#else
    for (int i = 0; i < lNextStates.size(); i++)
        prelimResults.push_back(std::make_pair(i, lNextStates[i]));
#endif

    if (player == me)
    {
        bestPossible = -99999;
        for (int i = 0; i < prelimResults.size(); i++)
        {
            if (pDue.getSeconds() - pDue.now().getSeconds() < TIME_LIMIT)
                break;
            int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, opponent, pDue);
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
            if (pDue.getSeconds() - pDue.now().getSeconds() < TIME_LIMIT)
                break;
            int res = alphabeta(prelimResults[i].second, depth - 1, alpha, beta, me, pDue);
            bestPossible = std::min(bestPossible, res);
            beta = std::min(beta, res);
            if (beta <= alpha)
                break;
        }
    }
    return bestPossible;
}

int Player::utility(GameState &gamestate, uint8_t player)
{
    /* Check if the game is in terminal state, and determine rewards */
    if (gamestate.isEOG())
    {
        if ((gamestate.isRedWin() && me&CELL_RED) || (gamestate.isWhiteWin() && me&CELL_WHITE))
            return 1e3;
        else if ((gamestate.isRedWin() && opponent&CELL_RED) || (gamestate.isWhiteWin() && opponent&CELL_WHITE))
            return -1e3;
        else
            return 0;
    }

    /* Check for the status of the game, who is most likely to win */
    int winPoints = 0;
    int opponentPoints = 0;
    uint8_t marker = 0;
    int value = 0;

    for (int i = 0; i < 32; i++)
    {
        marker = gamestate.at(i);
        if (marker&me)
        {
            winPoints += 2;
            winPoints += scoreboard[me][i];
        }
        else if (marker&opponent)
        {
            opponentPoints += 2;
            opponentPoints += scoreboard[opponent][i];
        }
        else if (marker == (CELL_RED | CELL_KING)) {
            if (me&CELL_RED) {
                winPoints += 5;
                winPoints += scoreboardRed[i];
            }
            else {
                opponentPoints += 5;
                winPoints += scoreboardWhite[i];
            }
        }
        else if (marker == (CELL_WHITE | CELL_KING)) {
            if (me&CELL_WHITE) {
                winPoints += 5;
                winPoints += scoreboardWhite[i];
            }
            else {
                opponentPoints += 5;
                winPoints += scoreboardRed[i];
            }
        }
    }
    value = winPoints - opponentPoints;
    moveOrdering[hashState(gamestate)] = value;
    return winPoints - opponentPoints;
}

int Player::hashState(GameState & gamestate) {
    int hash = gamestate.getNextPlayer(); 

    for (int i = 0; i < 32; i++) {
        hash *= 5;
        hash += gamestate.at(i);
    }
    return hash;
}

int Player::scoreboardWhite[32] = {
      4, 3, 3, 4,
    3, 2, 2, 2, 
      2, 2, 2, 2, 
    2, 1, 1, 1, 
      1, 1, 1, 2, 
    2, 0, 0, 0,
      1, 0, 0, 2,
    2, 0, 0, 2
};

int Player::scoreboardRed[32] = {
      2, 0, 0, 2, 
    2, 0, 0, 1, 
      0, 0, 0, 2,
    2, 1, 1, 1, 
      1, 1, 1, 2, 
    2, 2, 2, 2, 
      2, 2, 2, 3,
    4, 3, 3, 4
};

} // namespace checkers
