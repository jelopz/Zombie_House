package RoomGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class HouseBuilder
{
  private final int MIN_ROOM_WIDTH = 6;
  private final int MIN_ROOM_HEIGHT = 6;
  private final int HALL_WIDTH = 3;

  // Arraylist of each quadrants rooms and halls

  private char[][] house; // The map, house[y][x]

  private int mapWidth;
  private int mapHeight;

  private Point playerSpawnPoint;

  Random rand;

  ArrayList<RoomCluster> cluster = new ArrayList<>();

  public HouseBuilder(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new char[h][w];

    rand = new Random();
    cleanMap();
    markQuadrants();

    for (int i = 0; i < 4; i++)
    {
      partitionMap(i);
      // makeDoors(i);
      // makeWalls(i);
    }

    if (true)
    {
      printMap();
    }
  }

  // private void makeDoors(int quadrant)
  // {
  // int startX = 0;
  // int startY = 0;
  // int width = mapWidth / 2 - 1;
  // int height = mapHeight / 2 - 1;
  //
  // if (quadrant == 0)
  // {
  // startX = mapWidth / 2 + 1;
  // startY += 1;
  // }
  // if (quadrant == 1)
  // {
  // startX += 1;
  // startY += 1;
  // }
  // if (quadrant == 2)
  // {
  // startX += 1;
  // startY = mapHeight / 2 + 1;
  // }
  // else if (quadrant == 3)
  // {
  // startX = mapWidth / 2 + 1;
  // startY = mapHeight / 2 + 1;
  // }
  //
  // int numberHalls = 0;
  // int hallLen = 0;
  //
  // for(int y = startY; y < height; y++)
  // {
  // for(int x = startX; x < width; x++)
  // {
  // if(house[y][x] == '-' && house[y][x+2] == 'H') //found a hall with a room
  // to the left
  // {
  // hallLen = findLengthOfHall(x,y,0);
  // }
  // else if(house[y][x] == '-' && house[y+2][x] == 'H') //found hall with a
  // room to the north
  // {
  // hallLen = findLengthOfHall(x,y);
  // }
  // else if(house[y][x] == 'X' && house[y][x+2] == 'H') //found hall with a
  // room to the right
  // {
  // hallLen = findLengthOfHall(x,y);
  // }
  // else if(house[y][x] == 'X' && house[y+2][x] == 'H') //found hall with room
  // to the south
  // {
  // hallLen = findLengthOfHall(x,y);
  // }
  // }
  // }
  // }

  // private int findLengthOfHall(int x, int y)
  // {
  //
  // }

  private void makeWalls(int quadrant)
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        if ((house[i][j] == 'H') && (house[i][j - 1] == '-'))
        {
          house[i][j - 1] = 'X';
        }
        else if ((house[i][j] == 'H') && (house[i][j + 1] == '-'))
        {
          house[i][j + 1] = 'X';
        }

        if ((house[i][j] == 'H') && (house[i - 1][j] == '-'))
        {
          house[i - 1][j] = 'X';
        }
        else if ((house[i][j] == 'H') && (house[i + 1][j] == '-'))
        {
          house[i + 1][j] = 'X';
        }
      }
    }
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
      house[d][hallStartPoint - 1] = 'D';
      house[d + 1][hallStartPoint - 1] = 'D';
      d = rand.nextInt(c.height - 3) + c.y + 1;
      house[d][hallStartPoint + 3] = 'D';
      house[d + 1][hallStartPoint + 3] = 'D';
    }
    else
    {
      int d = rand.nextInt(c.width - 1) + c.x;
      house[hallStartPoint - 1][d] = 'D';
      house[hallStartPoint - 1][d + 1] = 'D';
      d = rand.nextInt(c.width - 1) + c.x;
      house[hallStartPoint + 3][d] = 'D';
      house[hallStartPoint + 3][d + 1] = 'D';
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
        if (house[y][r - 1] != 'D' && house[y][r + 3] != 'D')
        {
          house[y][r - 1] = 'X';
          house[y][r + 3] = 'X';
        }
        for (int x = r; x < r + 3; x++)
        {
          if (house[y][x] != 'X' && house[y][x] != 'D')
          {
            house[y][x] = 'H';
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
          if (house[y][x] != 'X' && house[y][x] != 'D')
          {
            house[y][x] = 'H';
          }
          
          if(house[r-1][x] != 'D' && house[r+3][x] != 'D')
          {
            house[r - 1][x] = 'X';
            house[r + 3][x] = 'X';
          }
        }
      }

      addDoorsToHall(r, h, c);
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

    for (int i = 0; i < mapHeight; i++)
    {
      house[i][mapWidth / 2] = 'X';
      house[i][0] = 'X';
      house[i][mapWidth - 1] = 'X';
    }

    for (int i = 0; i < mapWidth; i++)
    {
      house[mapHeight / 2][i] = 'X';
      house[0][i] = 'X';
      house[mapHeight - 1][i] = 'X';
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
    HouseBuilder rg = new HouseBuilder(51, 41);
  }
}
