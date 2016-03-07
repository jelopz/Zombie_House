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

public class Pathfinder
{
  private Comparator<Tile> comparator;// for priority queue 
  private PriorityQueue<Tile> frontier;// priority queue for search algorithm
  private HashSet<Tile> visitedNodes;
  private Path path;

  public Pathfinder()
  {
    comparator = new TileComparator();
  }

  public void init(Point startingPoint, Point targetPoint, Tile[][] house)
  {
    path = new Path();
    frontier = new PriorityQueue<>(10, comparator);
    visitedNodes = new HashSet<>();

    frontier.add(house[startingPoint.y][startingPoint.x]);
    frontier.peek().visited = true;

    if (!visitedNodes.isEmpty())
    {
      System.out.println("clean yo list");
    }

    visitedNodes.add(frontier.peek());

    Dijkstra(targetPoint, house);
  }

  private void cleanVisitedList(Tile[][] house)
  {
    for (Tile t : visitedNodes)
    {
      t.reset();
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

      if (currentNode.getTileType() == house[targetPoint.y][targetPoint.x].getTileType())
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
          //do nothing
        }
        else if ((neighbors[i].getTileType() != 'X') &&
            ((!neighbors[i].visited || (newCost < neighbors[i].currentCost))))
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
      // currentNode.printParents();
      currentNode.printPath(path);
      System.out.println("found");
      ArrayList<Tile> p1 = path.getPath();
      Tile t;
      
      for(Tile q : p1)
      {
        System.out.println("( " + q.getX() + " , " + q.getY() + " ) , ");
      }
      
    }
    else
    {
      // System.out.println(currentStart + " to " + currentGoal + " is
      // impossible.");
      System.out.println("didnt find");
    }

    cleanVisitedList(house);
  }

  public static void main(String[] args)
  {
    HouseBuilder h = new HouseBuilder(51, 41);
    Tile[][] map = h.getMap();

    Pathfinder p = new Pathfinder();

    // grab all the points zombies from the map
    ArrayList<Point> pp = new ArrayList<>();
    for (int i = 0; i < 41; i++)
    {
      for (int j = 0; j < 51; j++)
      {
        if (map[i][j].getTileType() == 'L' || map[i][j].getTileType() == 'R')
        {
          pp.add(new Point(j, i));
        }
      }
    }

    // for each zombie, if its in the smell radius, print the path
    for (Point z : pp)
    {
      System.out.println("we lookin: " + z);
      System.out.println("player: " + h.getPlayerSpawnPoint());
      p.init(z, h.getPlayerSpawnPoint(), map);
    }

    // prints the map
    for (int i = 0; i < 41; i++)
    {
      for (int j = 0; j < 51; j++)
      {
        System.out.print(map[i][j].getTileType());
      }
      System.out.println();
    }
  }
}
