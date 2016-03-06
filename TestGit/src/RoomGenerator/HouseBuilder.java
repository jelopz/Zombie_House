package RoomGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import application.Game;

public class HouseBuilder
{
  private final int MIN_ROOM_WIDTH = 6;
  private final int MIN_ROOM_HEIGHT = 6;
  private final int HALL_WIDTH = 3;

  // Arraylist of each quadrants rooms and halls

  private Tile[][] house; // The map, house[y][x]

  private int mapWidth;
  private int mapHeight;

  private Point playerSpawnPoint;

  Random rand;

  ArrayList<RoomCluster> cluster = new ArrayList<>();

  public HouseBuilder(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new Tile[h][w];

    rand = new Random();
    cleanMap();
    markQuadrants();

    for (int i = 0; i < 4; i++)
    {
      partitionMap(i);
    }

    makePlayerSpawnPoint();

    if (Game.debug)
    {
      printMap();
    }
  }

  public Tile[][] getMap()
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
   */
  public boolean isPointLegal(int x, int y)
  {
    if (house[y][x].getTileType() != 'X')
    {
      return true;
    }
    return false;
  }

  public boolean isEndPoint(int x, int y)
  {
    if (house[y][x].getTileType() == 'E')
    {
      return true;
    }
    return false;
  }

  private void partitionMap(int quadrant)
  {
    int startX = 0;
    int startY = 0;
    int width = mapWidth / 2 - 1;
    int height = mapHeight / 2 - 1;

    if (quadrant == 0)
    {
      startX = mapWidth / 2 + 1;
      startY += 1;
    }
    if (quadrant == 1)
    {
      startX += 1;
      startY += 1;
    }
    if (quadrant == 2)
    {
      startX += 1;
      startY = mapHeight / 2 + 1;
    }
    else if (quadrant == 3)
    {
      startX = mapWidth / 2 + 1;
      startY = mapHeight / 2 + 1;
    }

    RoomCluster current;
    cluster.add(new RoomCluster(startX, startY, width, height, true));

    while (!cluster.isEmpty())
    {
      current = cluster.remove(0);
      makeHall(current, current.giveHorizontalWall);
    }
  }

  private void addDoorsToHall(int hallStartPoint, boolean isVertical, RoomCluster c)
  {
    if (!isVertical)
    {
      int d = rand.nextInt(c.height - 3) + c.y + 1;
      house[d][hallStartPoint - 1].setTileType('D');
      house[d + 1][hallStartPoint - 1].setTileType('D');
      d = rand.nextInt(c.height - 3) + c.y + 1;
      house[d][hallStartPoint + 3].setTileType('D');
      house[d + 1][hallStartPoint + 3].setTileType('D');
    }
    else
    {
      int d = rand.nextInt(c.width - 1) + c.x;
      house[hallStartPoint - 1][d].setTileType('D');
      house[hallStartPoint - 1][d + 1].setTileType('D');
      d = rand.nextInt(c.width - 1) + c.x;
      house[hallStartPoint + 3][d].setTileType('D');
      house[hallStartPoint + 3][d + 1].setTileType('D');
    }
  }

  private void makeHall(RoomCluster c, boolean h)
  {
    if (!h)
    {
      int r;

      if (c.width <= (MIN_ROOM_WIDTH * 2) + 3)
      {
        return;
      }
      else
      {
        r = rand.nextInt(c.width - (2 * MIN_ROOM_WIDTH + HALL_WIDTH)) + c.x + MIN_ROOM_WIDTH;
      }

      int c1Width = Math.abs(c.x - r);
      int c2Width = c.width - c1Width - 3;

      if (c1Width > MIN_ROOM_WIDTH && c2Width > MIN_ROOM_WIDTH)
      {
        cluster.add(new RoomCluster(c.x, c.y, c1Width, c.height, !h));
        cluster.add(new RoomCluster(r + 3, c.y, c2Width, c.height, !h));
      }
      for (int y = c.y; y < (c.y + c.height); y++)
      {
        if (house[y][r - 1].getTileType() != 'D' && house[y][r + 3].getTileType() != 'D')
        {
          house[y][r - 1].setTileType('X');
          house[y][r + 3].setTileType('X');
        }
        for (int x = r; x < r + 3; x++)
        {
          if (house[y][x].getTileType() != 'X' && house[y][x].getTileType() != 'D')
          {
            house[y][x].setTileType('H');
          }
        }
      }
      addDoorsToHall(r, h, c);
    }
    else // gets a horizontal hall
    {
      int r;

      if (c.height < (MIN_ROOM_HEIGHT * 2) + 3)
      {
        return;
      }
      else
      {
        r = rand.nextInt(c.height - (2 * MIN_ROOM_HEIGHT + HALL_WIDTH)) + c.y + MIN_ROOM_HEIGHT;
      }
      int c1Height = Math.abs(c.y - r);
      int c2Height = c.height - c1Height - 3;

      if (c1Height > MIN_ROOM_WIDTH && c2Height > MIN_ROOM_WIDTH)
      {
        cluster.add(new RoomCluster(c.x, c.y, c.width, c1Height, !h));
        cluster.add(new RoomCluster(c.x, r + 3, c.width, c2Height, !h));
      }

      for (int y = r; y < r + 3; y++)
      {
        for (int x = c.x; x < (c.x + c.width); x++)
        {
          if (house[y][x].getTileType() != 'X' && house[y][x].getTileType() != 'D')
          {
            house[y][x].setTileType('H');
          }

          if (house[r - 1][x].getTileType() != 'D' && house[r + 3][x].getTileType() != 'D')
          {
            house[r - 1][x].setTileType('X');
            house[r + 3][x].setTileType('X');
          }
        }
      }

      addDoorsToHall(r, h, c);
    }

  }

  private void markQuadrants()
  {

    for (int i = 0; i < mapHeight; i++)
    {
      house[i][mapWidth / 2].setTileType('X');
      house[i][0].setTileType('X');
      house[i][mapWidth - 1].setTileType('X');
    }

    for (int i = 0; i < mapWidth; i++)
    {
      house[mapHeight / 2][i].setTileType('X');
      house[0][i].setTileType('X');
      house[mapHeight - 1][i].setTileType('X');
    }
  }

  /*
   * Randomly chooses 1 of the 4 quadrants, and denotes a spawn in any point in
   * a hallway
   */
  private void makePlayerSpawnPoint()
  {
    int i = rand.nextInt(4);

    int startX = 0;
    int startY = 0;
    int width = mapWidth / 2 - 1;
    int height = mapHeight / 2 - 1;

    if (i == 0)
    {
      startX = mapWidth / 2 + 1;
      startY += 1;
    }
    if (i == 1)
    {
      startX += 1;
      startY += 1;
    }
    if (i == 2)
    {
      startX += 1;
      startY = mapHeight / 2 + 1;
    }
    else if (i == 3)
    {
      startX = mapWidth / 2 + 1;
      startY = mapHeight / 2 + 1;
    }

    boolean found = false;

    while (!found)
    {
      for (int j = startY; j <= (startY + height); j++)
      {
        for (int k = startX; k <= (startX + width); k++)
        {
          if (house[j][k].getTileType() == 'H')
          {
            if (rand.nextDouble() < .9)
            {
              house[j][k].setTileType('P');
              playerSpawnPoint = new Point(k, j);
              found = true;
            }
          }
          if (found)
          {
            break;
          }
        }
        if (found)
        {
          break;
        }
      }

    }
  }

  private void cleanMap()
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        if (rand.nextDouble() < .01)
        {
          if (rand.nextInt(2) == 0)
          {
            house[i][j] = new Tile('R');
          }
          else
          {
            house[i][j] = new Tile('L');
          }
        }
        else
        {
          house[i][j] = new Tile('-');
        }
      }
    }
  }

  private void printMap() // debug
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        System.out.print(house[i][j].getTileType());
      }
      System.out.println();
    }
  }

  public static void main(String[] args)
  {
    HouseBuilder rg = new HouseBuilder(51, 41);
  }
}
