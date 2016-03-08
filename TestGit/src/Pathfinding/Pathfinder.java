/*
 * Performs the A* Path finding algorithm given a 2D Array of Tiles.
 */

package Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeSet;

import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import RoomGenerator.TileComparator;
import application.Game;

public class Pathfinder
{
  private boolean pathExists;

  private Comparator<Tile> comparator;// for priority queue
  private PriorityQueue<Tile> frontier;// priority queue for search algorithm
  private HashSet<Tile> visitedNodes;
  // private Path path;
  private ArrayList<Tile> path;

  public Pathfinder()
  {
    comparator = new TileComparator();
  }

  public void init(Point startingPoint, Point targetPoint, Tile[][] house)
  {
    path = new ArrayList<>();
    frontier = new PriorityQueue<>(10, comparator);
    visitedNodes = new HashSet<>();

    frontier.add(house[startingPoint.y][startingPoint.x]);
    frontier.peek().visited = true;

    visitedNodes.add(frontier.peek());

    Dijkstra(targetPoint, house);
  }

  public boolean doesPathExist()
  {
    return pathExists;
  }

  public ArrayList<Tile> getPath()
  {
    return path;
  }

  public double findEucl(double x1, double y1, double x2, double y2)
  {
    double xDiff = x1 - x2;
    double xSqr = Math.pow(xDiff, 2);

    double yDiff = y1 - y2;
    double ySqr = Math.pow(yDiff, 2);

    double output = Math.sqrt(xSqr + ySqr);
    return output;
  }

  private void cleanVisitedList(Tile[][] house)
  {
    for (int i = 0; i < 51; i++)
    {
      for (int j = 0; j < 41; j++)
      {
        house[i][j].reset();
      }
    }
  }

  private void Dijkstra(Point targetPoint, Tile[][] house)
  {
    Tile currentNode = null;

    Tile northNeighbor = null;
    Tile eastNeighbor = null;
    Tile southNeighbor = null;
    Tile westNeighbor = null;
    Tile[] neighbors = new Tile[4];

    int newCost;
    int currentX;
    int currentY;
    boolean found = false;
    boolean tooLong = false;

    while (!frontier.isEmpty())
    {
      currentNode = frontier.remove();
      currentX = currentNode.getX();
      currentY = currentNode.getY();

      newCost = currentNode.currentCost + 1;

      if (!(currentY - 1 < 0))
      {
        northNeighbor = house[currentY - 1][currentX];
        neighbors[0] = northNeighbor;
      }
      else
      {
        neighbors[0] = null;
      }

      if (!(currentX + 1 > 50))
      {
        eastNeighbor = house[currentY][currentX + 1];
        neighbors[1] = eastNeighbor;
      }
      else
      {
        neighbors[1] = null;
      }

      if (!(currentY + 1 > 40))
      {
        southNeighbor = house[currentY + 1][currentX];
        neighbors[2] = southNeighbor;
      }
      else
      {
        neighbors[2] = null;
      }

      if (!(currentX - 1 < 0))
      {
        westNeighbor = house[currentY][currentX - 1];
        neighbors[3] = westNeighbor;
      }
      else
      {
        neighbors[3] = null;
      }

      // if (currentNode.getTileType() ==
      // house[targetPoint.y][targetPoint.x].getTileType())
      if ((currentNode.getX() == targetPoint.x) && (currentNode.getY() == targetPoint.y))
      {
        found = true;
        break;
      }
      else if (newCost > 15)
      {
        tooLong = true;
      }

      for (int i = 0; i < 4; i++)
      {
        if (neighbors[i] == null || tooLong)
        {
          // do nothing
        }
        else if ((neighbors[i].getTileType() != 'X') && ((!neighbors[i].visited || (newCost < neighbors[i].currentCost))))
        {
          neighbors[i].visited = true;
          visitedNodes.add(neighbors[i]);
          neighbors[i].currentCost = newCost;
          neighbors[i].priority = newCost;
          frontier.add(neighbors[i]);
          neighbors[i].parent = currentNode;
        }
      }
    }

    if (found)
    {
      currentNode.printPath(path);

      pathExists = true;
    }
    else
    {
      pathExists = false;
    }

    cleanVisitedList(house);
  }
}
