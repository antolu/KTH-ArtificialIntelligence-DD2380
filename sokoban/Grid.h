#ifndef grid_h
#define grid_h

#include <map>
#include "Cell.h"
#include <fstream>
#include <sstream>
#include <string>
#include <utility>
#include <vector>
#include <iostream>

typedef std::map<std::pair<int, int>, Cell> GridMap;
typedef std::map<std::pair<int, int>, bool> VisitedMap;

class Grid
{
  private:
    GridMap grid;
    VisitedMap visitedMap;
    std::string walk(Cell &cell, int origin);
    std::vector<Cell> getNeighbors(Cell cell);
    int maxRow = 0;
    int maxColumn = 0;
    void printMap();
    std::string parseResult(std::string input);

  public:
    Grid();
    void parse(std::ifstream & inStream);
};

#endif