#ifndef _CHECKERS_PLAYER_HPP_
#define _CHECKERS_PLAYER_HPP_

#include "constants.hpp"
#include "deadline.hpp"
#include "move.hpp"
#include "gamestate.hpp"
#include <vector>
#include <map>
#include <algorithm>
#include <utility>
#include <iterator>

namespace checkers
{

class Player
{
public:
    Player();
    ///perform a move
    ///\param pState the current state of the board
    ///\param pDue time before which we must have returned
    ///\return the next state the board is in after our move
    GameState play(const GameState &pState, const Deadline &pDue);

private:
    /**
     * @param depth The depth to search to.
     * 
     * @return Returns the best move.
     */
    std::pair<int, int> getBestMove(std::vector<GameState> lNextStates, int depth, const Deadline & pDue);

    /**
     * @param gamestate The current state we are analyzing
     * @param depth The depth of the current search tree
     * @param alpha The current best value achievable by player X
     * @param beta The current best value achievable by B
     * @param player The current player
     * 
     * @return The minimax value of the state
     */
    int alphabeta(GameState & gamestate, int depth, int alpha, int beta, uint8_t player, const Deadline & pDue);

    /**
     * @param gamestate The current state we are analying
     * 
     * @return Returns the utility of this game state.
     */
    int utility(GameState & gamestate, uint8_t player);

    int hashState(GameState & gamestate);

    uint8_t me;
    uint8_t opponent;
    // const int me = 0;
    // const int opp = 1;
    
    std::map<std::string, bool> visited;
    std::map<int, int> moveOrdering;

    std::map<uint8_t, int*> scoreboard;
    static int scoreboardWhite[32];
    static int scoreboardRed[32];
};

/*namespace checkers*/ }

#endif
