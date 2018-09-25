#ifndef _TICTACTOE_PLAYER_HPP_
#define _TICTACTOE_PLAYER_HPP_

#include "constants.hpp"
#include "deadline.hpp"
#include "move.hpp"
#include "gamestate.hpp"
#include <vector>

namespace TICTACTOE
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
     * @param gamestate The current state we are analyzing
     * @param player The current player
     * 
     * @return Returns a heuristic value that aproximates an utility
     * function of the state.
     */
    int minimax(GameState & gamestate, int player, int depth);

    /**
     * @param gamestate The current state we are analyzing
     * @param depth The depth of the current search tree
     * @param alpha The current best value achievable by player X
     * @param beta The current best value achievable by B
     * @param player The current player
     * 
     * @return The minimax value of the state
     */
    int alphabeta(GameState & gamestate, int depth, int & alpha, int & beta, int player);

    /**
     * @param gamestate The current state we are analying
     * 
     * @return Returns the utility of this game state.
     */
    int utility(GameState & gamestate, int & depth);
    int eval(GameState & gamestate, int & player);

    uint8_t playerX;
    uint8_t playerO;
    const int me = 0;
    const int opp = 1;

    static int const winCombinations[10][4];
    static int const rewards[5][5];
};

/*namespace TICTACTOE*/ }

#endif
