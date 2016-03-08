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
  private int startQuadrant;
  private int endQuadrant;

  public ArrayList<Point> zombs;

  Random rand;

  ArrayList<RoomCluster> cluster = new ArrayList<>();

  public HouseBuilder(int w, int h)
  {
    zombs = new ArrayList<>();

    mapWidth = w;
    mapHeight = h;

    house = new Tile[h][w];

    rand = new Random();
    cleanMap();
    markQuadrants();

    for (int i = 0; i < 4; i++)
    {
      partitionQuadrant(i);
    }

    startQuadrant = makePlayerSpawnPoint();
    endQuadrant = startQuadrant - 1;

    if (startQuadrant == 0)
    {
      endQuadrant = 3;
    }

    makeEndPoint(endQuadrant);

    connectQuadrants(startQuadrant);

    addObstacles();

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

  /*
   * Recursive method to connect all 4 quadrants together. Given a quadrant,
   * makes a door to the quadrant directly counter clockwise to that quadrant.
   * If the given quadrant is the quadrant with the end point, stop. We don't
   * want to make a door directly from the player quadrant to the final
   * quadrant.
   */
  private void connectQuadrants(int quadrant)
  {
    if (quadrant == endQuadrant) // we finished our path
    {
      return;
    }

    Point quadrantStart = findQuadrantStartPoint(quadrant);
    int startY = quadrantStart.y;
    int startX = quadrantStart.x;
    int width = mapWidth / 2 - 1;
    int height = mapHeight / 2 - 1;
    boolean done = false;

    if (quadrant == 0 || quadrant == 2)
    {
      for (int i = startY; i < startY + height - 1; i++)
      {
        if (rand.nextDouble() < .3)
        {
          house[i][mapWidth / 2].setTileType('D');
          house[i + 1][mapWidth / 2].setTileType('D');
          done = true;
        }
        if (done)
        {
          break;
        }
      }
    }
    else if (quadrant == 1 || quadrant == 3)
    {
      for (int i = startX; i < startX + width; i++)
      {
        if (rand.nextDouble() < .3)
        {
          house[mapHeight / 2][i].setTileType('D');
          house[mapHeight / 2][i + 1].setTileType('D');
          done = true;
        }
        if (done)
        {
          break;
        }
      }
    }

    if (quadrant == 4)
    {
      connectQuadrants(0);
    }
    else
    {
      connectQuadrants(quadrant + 1);
    }
  }

  /*
   * Given a quadrant chunk, a large room, split it up into multiple hallways
   * and rooms
   */
  private void partitionQuadrant(int quadrant)
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

  /*
   * Adds two doorways on a hallway, one on each long side.
   */
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

  /*
   * Randomly chooses points on the map. If the map is a room tile, check to see
   * if there are any obstacles within 2 tiles of that point. If there is
   * nothing, add an obstacle to that tile.
   */
  private void addObstacles()
  {
    boolean badspot = false;
    int numObstacles = 0;
    int x, y;

    while (numObstacles < 12)
    {
      int j = rand.nextInt(mapWidth - 7) + 3;
      int i = rand.nextInt(mapHeight - 7) + 3;

      if (house[i][j].getTileType() == '-')
      {
        for (int k = 1; k < 3; k++)
        {
          if (house[i - k][j].getTileType() != '-' || house[i + k][j].getTileType() != '-')
          {
            badspot = true;
          }
          if (house[i][j - k].getTileType() != '-' || house[i][j + k].getTileType() != '-')
          {
            badspot = true;
          }
          if (house[i - k][j - k].getTileType() != '-' || house[i + k][j + k].getTileType() != '-')
          {
            badspot = true;
          }
          if (house[i - k][j + k].getTileType() != '-' || house[i + k][j - k].getTileType() != '-')
          {
            badspot = true;
          }
        }

        if (!badspot)
        {
          house[i][j].setTileType('X');
          numObstacles++;
        }
      }
      badspot = false;
    }
  }

  /*
   * Given a cluster and a type of hallway(vertical or horizontal), if the
   * cluster is large enough, the method splits the cluster with the type of
   * hallway in a valid position.
   */
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

  /*
   * Marks the perimeter and split the four quadrants to four equal large rooms
   */
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
   * Given a quadrant, it finds two adjacent points on an outer wall that is not
   * obstructed by any walls in front of it and denotes it as the end point
   */
  private void makeEndPoint(int endQuadrant)
  {
    Point quadrantStart = findQuadrantStartPoint(endQuadrant);
    int startY = quadrantStart.y;
    int startX = quadrantStart.x;
    int width = mapWidth / 2 - 1;
    int height = mapHeight / 2 - 1;

    boolean found = false;

    while (!found)
    {
      // decides which one of the two outer walls to use
      if (rand.nextInt(2) == 0)
      {
        if (endQuadrant == 0 || endQuadrant == 1)
        {
          for (int i = startX; i < startX + width - 1; i++)
          {
            if (rand.nextDouble() < .3)
            {
              // Checks to make sure exit isn't behind a wall
              if (house[1][i].getTileType() != 'X' && house[1][i + 1].getTileType() != 'X')
              {
                house[0][i].setTileType('E');
                house[0][i + 1].setTileType('E');
                found = true;
              }
            }
            if (found)
            {
              break;
            }
          }
        }
        else if (endQuadrant == 2 || endQuadrant == 3)
        {
          for (int i = startX; i < startX + width - 1; i++)
          {
            if (rand.nextDouble() < .3)
            {
              if (house[startY + height - 1][i].getTileType() != 'X' && house[startY + height - 1][i + 1].getTileType() != 'X')
              {
                house[startY + height][i].setTileType('E');
                house[startY + height][i + 1].setTileType('E');
                found = true;
              }
            }
            if (found)
            {
              break;
            }
          }
        }
      }
      else
      {
        if (endQuadrant == 0 || endQuadrant == 3)
        {
          for (int i = startY; i < startY + height; i++)
          {
            if (rand.nextDouble() < .3)
            {
              if (house[i][startX + width - 1].getTileType() != 'X' && house[i + 1][startX + width - 1].getTileType() != 'X')
              {
                house[i][startX + width].setTileType('E');
                house[i + 1][startX + width].setTileType('E');
                found = true;
              }
            }
            if (found)
            {
              break;
            }
          }
        }
        else if (endQuadrant == 1 || endQuadrant == 2)
        {
          for (int i = startY; i < startY + height; i++)
          {
            if (rand.nextDouble() < .3)
            {
              if (house[i][1].getTileType() != 'X' && house[i + 1][1].getTileType() != 'E')
              {
                house[i][0].setTileType('E');
                house[i + 1][0].setTileType('E');
                found = true;
              }
            }
            if (found)
            {
              break;
            }
          }
        }
      }
    }
  }

  /*
   * Given a quadrant, returns the point to the top-left corner tile, the
   * starting tile.
   */
  private Point findQuadrantStartPoint(int quadrant)
  {
    int startX = 0;
    int startY = 0;

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

    return new Point(startX, startY);
  }

  /*
   * Randomly chooses 1 of the 4 quadrants, and denotes a spawn in any point in
   * a hallway
   */
  private int makePlayerSpawnPoint()
  {
    int spawnQ = rand.nextInt(4);

    int startX = 0;
    int startY = 0;
    int width = mapWidth / 2 - 1;
    int height = mapHeight / 2 - 1;

    if (spawnQ == 0)
    {
      startX = mapWidth / 2 + 1;
      startY += 1;
    }
    if (spawnQ == 1)
    {
      startX += 1;
      startY += 1;
    }
    if (spawnQ == 2)
    {
      startX += 1;
      startY = mapHeight / 2 + 1;
    }
    else if (spawnQ == 3)
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
            if (rand.nextDouble() < .3)
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

    return spawnQ;
  }

  /*
   * Initializes every tile in house[][] as a room tile, and decides if a zombie
   * will spawn there.
   */
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
            house[i][j] = new Tile('R', j, i);
          }
          else
          {
            house[i][j] = new Tile('L', j, i);
          }
        }
        else
        {
          house[i][j] = new Tile('-', j, i);
        }
      }
    }
  }

  private void printMap()
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
}
