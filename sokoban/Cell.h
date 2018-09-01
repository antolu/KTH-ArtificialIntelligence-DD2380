#ifndef cell_h
#define cell_h

#include <map>
#include <string>
#include <iostream>

class Cell {
    private:
    bool isRoad;
    bool isBranch;
    bool isGoal;

    public:
    Cell();
    Cell(bool isRoad, int row, int column);

    bool getIsRoad();

    bool getIsBranch();
    void setIsBranch();

    bool getIsGoal();
    void setIsGoal();

    std::map<std::string, bool> neighbors;

    void setNeighbors(bool up, bool down, bool left, bool right);

    int rowi;
    int columni;
};

#endif