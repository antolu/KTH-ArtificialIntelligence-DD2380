#include "Cell.h"

Cell::Cell() : 
isRoad(false), 
isBranch(false),
isGoal(false)
{
}

Cell::Cell(bool isRoadIn, int row, int column) {
    isRoad = isRoadIn;
    rowi = row;
    columni = column;
    isBranch = false;
    isGoal = false;
}

bool Cell::getIsRoad()
{
    return isRoad;
}

bool Cell::getIsBranch() 
{
    return isBranch;
}

void Cell::setIsBranch() {
    isBranch = true;
}

bool Cell::getIsGoal()
{
    return isGoal;
}

void Cell::setIsGoal() {
    isGoal = true;
    isRoad = true;
}

void Cell::setNeighbors(bool up, bool down, bool left, bool right) {
    bool array[4] = {up, down, left, right};
    int k = 0;
    for (int i = 0; i < 4; i++) {
        if (array[i] == true) {
            k++;
        }
    }
    if (k > 1) {
        isBranch = true;
    }
    neighbors["up"] = up;
    neighbors["down"] = down;
    neighbors["left"] = left;
    neighbors["right"] = right;
}