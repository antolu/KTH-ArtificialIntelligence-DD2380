#include "Grid.h"

Grid::Grid()
{
}

void Grid::parse(std::ifstream &inStream)
{

    std::string line;
    int row = 0;
    int column = 0;

    int player[2];

    // Parse the input, determine which coordinates are roads
    while (std::getline(inStream, line))
    {
        // std::cout << line << std::endl;
        for (char c : line)
        {
            if (c == ' ')
            {
                grid[std::make_pair(row, column)] = Cell(true, row, column);
            }
            else if (c == '@')
            {
                player[0] = row;
                player[1] = column;
                grid[std::make_pair(row, column)] = Cell(true, row, column);
            }
            else if (c == '.')
            {
                grid[std::make_pair(row, column)] = Cell(true, row, column);
                grid.at(std::make_pair(row, column)).setIsGoal();
            }
            else
            {
                grid[std::make_pair(row, column)] = Cell(false, row, column);
            }

            column++;
        }
        maxColumn = column;
        column = 0;
        row++;
    }
    maxColumn = 7;
    maxRow = row - 1;
    grid[std::make_pair(maxRow, maxColumn)] = Cell(true, maxRow, maxColumn);

    printMap();

    std::string solution = walk(grid[std::make_pair(player[0], player[1])], -1);

    if (solution == "")
        std::cout << "FAIL" << std::endl;
    else
        std::cout << parseResult(solution) << std::endl;
}

void Grid::printMap()
{
    std::cout << std::endl;
    for (int row = 0; row <= maxRow; row++)
    {
        for (int column = 0; column <= maxColumn; column++)
        {
            std::cout << grid.at(std::make_pair(row, column)).getIsRoad();
        }
        std::cout << std::endl;
    }
    std::cout << std::endl;
    for (int row = 0; row <= maxRow; row++)
    {
        for (int column = 0; column <= maxColumn; column++)
        {
            std::cout << grid.at(std::make_pair(row, column)).getIsGoal();
        }
        std::cout << std::endl
                  << std::endl;
    }
}

std::string Grid::walk(Cell &cell, int origin)
{
    std::cout << "R" << cell.rowi << " "
              << "C" << cell.columni << std::endl;
    if (visitedMap[std::make_pair(cell.rowi, cell.columni)] == true)
        return "visited";

    visitedMap[std::make_pair(cell.rowi, cell.columni)] = true;

    std::vector<Cell> nearCells = getNeighbors(cell);

    std::string goal = "";
    for (int i = 0; i < 4; i++)
    {
        Cell newCell = nearCells.at(i);
        std::cout << newCell.rowi << " " << newCell.columni << std::endl;
        if (newCell.getIsGoal())
        {
            std::cout << "GOAL" << std::endl;
            return "GOAL" + std::to_string(i);
        }
        if (newCell.getIsRoad() && !visitedMap[std::make_pair(newCell.rowi, newCell.columni)])
        {
            goal = walk(newCell, i);
            if (goal.length() > 4 && goal.substr(0,4) == "GOAL")
            {
                return std::to_string(origin) + " " + std::to_string(i) + " " + goal.substr(4, 1);
            }
            else if (goal != "")
            {
                return std::to_string(origin) + " " + goal;
            }
            else if (goal == "") {
                continue;
            }
            else
                return goal;
        }
    }
    return goal;
}

std::vector<Cell> Grid::getNeighbors(Cell cell)
{
    int row = cell.rowi;
    int column = cell.columni;

    std::vector<Cell> retVec;

    int yCoord[4] = {row - 1, row + 1, row, row};
    int xCoord[4] = {column, column, column - 1, column + 1};

    for (int i = 0; i < 4; i++)
    {
            std::pair<int, int> key = std::make_pair(yCoord[i], xCoord[i]);
            if (grid.count(key))
            {
                retVec.push_back(grid[key]);
            }
            else
                retVec.push_back(Cell(false, yCoord[i], xCoord[i]));
    }

    return retVec;
}

std::string Grid::parseResult(std::string input) {
    std::string path = input.substr(3, input.length() - 3);

    std::string result = "";

    for (char c: path) {
        if (c == ' ') result += c;
        else if (c == '0') result += 'U';
        else if (c == '1') result += 'D';
        else if (c == '2') result += 'L';
        else if (c == '3') result += 'R';
    }

    return result;
}