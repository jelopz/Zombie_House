/*
 * Performs the Djikstra path finding algorithm given a 2D Array of Tiles.
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

/**
 * The Class Pathfinder.
 */
public class Pathfinder
{

  /** True if we found a path in the previous search */
  private boolean pathExists;

  /** Comparator for the PriorityQueue */
  private Comparator<Tile> comparator;

  /** For pathfinding algorithm */
  private PriorityQueue<Tile> frontier;

  /** The nodes visited in last path finding search */
  private HashSet<Tile> visitedNodes;

  /**
   * Starting from the target/player to the first movement required by the
   * zombie. Does not contain initial zombie starting tile
   */
  private ArrayList<Tile> path;

  /**
   * Instantiates a new pathfinder by initializing the comparator.
   */
  public Pathfinder()
  {
    comparator = new TileComparator();
  }

  /**
   * Find the standard straight line Euclidean distance between two points.
   *
   * @param x1
   *          x value for first point
   * @param y1
   *          y value for first point
   * @param x2
   *          x value for second point
   * @param y2
   *          y value for second point
   * @return the Euclidean distance
   */
  public static double findEucl(double x1, double y1, double x2, double y2)
  {
    double xDiff = x1 - x2;
    double xSqr = Math.pow(xDiff, 2);

    double yDiff = y1 - y2;
    double ySqr = Math.pow(yDiff, 2);

    double output = Math.sqrt(xSqr + ySqr);
    return output;
  }

  /**
   * Initializes a new path, frontier, and visitedNodes set. Adds the zombies
   * starting point to the frontier, and begins the search
   * 
   * @param startingPoint
   *          the zombie's current location
   * @param targetPoint
   *          the player's current location
   * @param house
   *          our tile grid map
   */
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

  /**
   * @return true, if previous search resulted in a path found
   */
  public boolean doesPathExist()
  {
    return pathExists;
  }

  /**
   * @return the path, null if no path exists.
   */
  public ArrayList<Tile> getPath()
  {
    return path;
  }

  /**
   * Wipes the information pertenent to the previous path finding search clean.
   * Leaves only the x,y coordinates and the tiletype.
   * 
   * @param house
   *          our tile grid map
   */
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

  /**
   * Valid moves are north, south, east, west. Given a node, check these four
   * locations. If the cost of a newfound node is greater than 15, discard it.
   * Else, if it either hasn't been found before or if the newCost is greater
   * than it's older cost, set it with the newCost and add it to the frontier.
   * 
   * If a path is found, set pathExists to true. Else, set it to false.
   *
   * @param targetPoint
   *          the target/player's location
   * @param house
   *          our map
   */
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
