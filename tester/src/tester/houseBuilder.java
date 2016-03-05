/*
 * When RoomGenerator gets called, it creates a char[][] house where 'O'
 * denotes a walkable field and 'X' denotes a wall. 
 * 
 * Call RoomGenerator(width,height) to denote the width and height of the map
 * in tiles. Then, call RoomGenerator.getMap() to return the char[][] house map when done.
 * 
 * Currently the halls are working better than previous version. Program isn't liable
 * to crash anymore (as far as I can tell). 20x20 is probably the smallest map to go with,
 * though.
 * 
 * Rooms are not guaranteed to be connecting.
 * 
 * Still only makes 5 rooms. Subject to change.
 */

package tester;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class houseBuilder
{
  private final int MIN_ROOM_WIDTH = 4; // arbitrary,
  private final int MAX_ROOM_WIDTH = 6; // arbitrary, will change once we have a
                                        // better understanding of how big the
                                        // map should be
  private final int MIN_ROOM_HEIGHT = 3;// arbitrary,

  // Arraylist of each quadrants rooms and halls

  private char[][] house; // The map, house[y][x]

  private int mapWidth;
  private int mapHeight;

  private Point playerSpawnPoint;

  Random rand;

  ArrayList<RoomCluster> cluster = new ArrayList<>();

  public houseBuilder(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new char[h][w];

    rand = new Random();
    cleanMap();
    markQuadrants();

    for (int i = 3; i < 4; i++)
    {
      partitionMap(i);
    }

    if (true)
    {
      printMap();
    }
  }

  private void partitionMap(int quadrant)
  {
    int startX = 0;
    int startY = 0;
    int width = 25;
    int height = 25;
    boolean horizontalHall = true;

    if (quadrant == 3)
    {
      startX = 26;
      startY = 26;
    }
    RoomCluster initial, c1, c2;
    cluster.add(new RoomCluster(startX, startY, width, height, true));

    int z = 0;
    while (!cluster.isEmpty())
    {
      initial = cluster.remove(0);
      makeHall(initial, initial.giveHorizontalWall);
      horizontalHall = !horizontalHall;
    }
  }

  private void makeHall(RoomCluster c, boolean h)
  {
    if (!h)
    {
      int r;

      if (c.width - 9 <= 0)
      {
        return;
      }
      else
      {
        r = rand.nextInt(c.width - 9) + c.x + 3;
        System.out.println();
      }
      
      int c1Width = Math.abs(c.x - r);
      int c2Width = c.width - c1Width - 3;

      if (c1Width > 3 && c2Width > 3)
      {
        cluster.add(new RoomCluster(c.x, c.y, c1Width, c.height, !h));
        cluster.add(new RoomCluster(r + 3, c.y, c2Width, c.height, !h));
        // System.out.println(r);
      }
      for (int y = c.y; y < (c.y + c.height); y++)
      {
        for (int x = r; x < r + 3; x++)
        {
          house[y][x] = 'H';
        }
      }
    }
    else // gets a horizontal hall
    {
      int r;

      if (c.height - 9 <= 0)
      {
        return;
      }
      else
      {
        
        r = rand.nextInt(c.height - 9) + c.y + 3;
        System.out.println(r);
      }
      int c1Height = Math.abs(c.y - r);
      int c2Height = c.height - c1Height - 3;

      if (c1Height > 3 && c2Height > 3)
      {
        cluster.add(new RoomCluster(c.x, c.y, c.width, c1Height, !h));
        cluster.add(new RoomCluster(c.x, r + 3, c.width, c2Height, !h));
      }
      for (int y = r; y < r + 3; y++)
      {
        for (int x = c.x; x < (c.x + c.width); x++)
        {
          house[y][x] = 'H';
        }
      }
    }

  }

  public char[][] getMap()
  {
    return house;
  }

  public Point getPlayerSpawnPoint()
  {
    return playerSpawnPoint;
  }

  /*
   * Checks to see if a location is a legal spot to be on.
   * 
   * I think this will be very useful for collision testing. Essentially, you
   * pass the (players position)/tile_size to get the players location
   * represented on the 2D array. If the players position does not equal 'X', a
   * wall, then the position is valid.
   * 
   * There's still a little bit more thinking to go into it but I think this is
   * the route that I was planning to use in conjunction with the Zombie AI and
   * I feel this will also
   */
  public boolean isPointLegal(int x, int y)
  {
    if (house[y][x] != '-')
    {
      return true;
    }
    return false;
  }

  public boolean isEndPoint(int x, int y)
  {
    if (house[y][x] == 'E')
    {
      return true;
    }
    return false;
  }

  private void markQuadrants()
  {
    for (int i = 0; i < mapWidth; i++)
    {
      house[i][25] = 'X';
      house[25][i] = 'X';
      house[0][i] = 'X';
      house[i][0] = 'X';
    }
  }

  private void cleanMap()
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        house[i][j] = '-';
      }
    }
  }

  private void printMap() // debug
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        System.out.print(house[i][j]);
      }
      System.out.println();
    }
  }

  public static void main(String[] args)
  {
    houseBuilder rg = new houseBuilder(51, 51);

    // int x = 0;
    // for (int i = 0; i < 51; i++)
    // {
    // System.out.println(new Random().nextInt(25-9) + 29);
    // }
  }
}
