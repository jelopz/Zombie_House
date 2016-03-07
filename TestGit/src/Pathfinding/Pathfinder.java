/*
 * Performs the A* Path finding algorithm given a 2D Array of Tiles.
 */

package Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import RoomGenerator.TileComparator;

public class Pathfinder
{
  private Comparator<Tile> comparator;// for priority queue = new
                                      // TileComparator();
  private PriorityQueue<Tile> frontier;// = new PriorityQueue<>(10, comparator);

  private ArrayList<Tile> visitedNodes;

  public Pathfinder()
  {
    comparator = new TileComparator();
    frontier = new PriorityQueue<>(10, comparator);
    visitedNodes = new ArrayList<>();
  }

  public void init(Point startingPoint, Point targetPoint, Tile[][] house)
  {
    frontier.add(house[startingPoint.y][startingPoint.x]);
    frontier.peek().visited = true;

    if (!visitedNodes.isEmpty())
    {
      System.out.println("clean yo list");
    }

    visitedNodes.add(frontier.peek());

    AStar(targetPoint, house);
  }
  
  private void cleanVisitedList()
  {
    for(Tile t : visitedNodes)
    {
      t.visited = false;
    }
    
    visitedNodes.clear();
  }

  private void AStar(Point targetPoint, Tile[][] house)
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
        System.out.println("0 debug");
        neighbors[0] = null;
      }

      if (!(currentX + 1 > 50))
      {
        eastNeighbor = house[currentY][currentX + 1];
        neighbors[1] = eastNeighbor;
      }
      else
      {
        System.out.println("1 debug");
        neighbors[1] = null;
      }

      if (!(currentY + 1 > 40))
      {
        southNeighbor = house[currentY + 1][currentX];
        neighbors[2] = southNeighbor;
      }
      else
      {
        System.out.println("2 debug");
        neighbors[2] = null;
      }

      if (!(currentX - 1 < 0))
      {
        westNeighbor = house[currentY][currentX - 1];
        neighbors[3] = westNeighbor;
      }
      else
      {
        System.out.println("3 debug");
        neighbors[3] = null;
      }

      if (currentNode.getTileType() == house[targetPoint.y][targetPoint.x].getTileType())
      {
        found = true;
        break;
      }
      else if(newCost > 15)
      {
        tooLong = true;
      }

      for (int i = 0; i < 4; i++)
      {
        if (neighbors[i] == null)
        {
          System.out.println("bad" + i);
        }
        else if(tooLong)
        {
          System.out.println("do nothing cuz too long");
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
      // currentNode.printParents();
      System.out.println("found");
      currentNode.printPath();
    }
    else
    {
      // System.out.println(currentStart + " to " + currentGoal + " is
      // impossible.");
      System.out.println("didnt find");
    }
    
    cleanVisitedList();
  }

  public static void main(String[] args)
  {
    HouseBuilder h = new HouseBuilder(51, 41);
    Tile[][] map = h.getMap();
    ArrayList<Point> pp = new ArrayList<>();
    for (int i = 0; i < 41; i++)
    {
      for (int j = 0; j < 51; j++)
      {
        if (map[i][j].getTileType() == 'L')
        {
          pp.add(new Point(j, i));
        }
      }
    }

    Pathfinder p = new Pathfinder();

    for (Point z : pp)
    {
      System.out.println(z);
    }
    System.out.println("we lookin: " + pp.get(0));
    System.out.println("player: " + h.getPlayerSpawnPoint());
    p.init(pp.get(0), h.getPlayerSpawnPoint(), map);
    
//    System.out.println("we lookin: " + pp.get(1));
//    System.out.println("player: " + h.getPlayerSpawnPoint());
//    p.init(pp.get(1), h.getPlayerSpawnPoint(), map);

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
