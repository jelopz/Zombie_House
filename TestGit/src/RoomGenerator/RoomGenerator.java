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

package RoomGenerator;

import java.awt.Point;
import java.util.Random;

public class RoomGenerator
{
  private final int MIN_ROOM_WIDTH = 4; // arbitrary,
  private final int MAX_ROOM_WIDTH = 6; // arbitrary, will change once we have a
                                        // better understanding of how big the
                                        // map should be
  private final int MIN_ROOM_HEIGHT = 3;// arbitrary,

  private Room[] rooms; // array of all the rooms
  private Hall[] halls; // array of all the halls
  private char[][] house; // The map, house[y][x]

  private int numRooms;
  private int numHalls;

  private int mapWidth;
  private int mapHeight;

  private Point playerSpawnPoint;

  Random rand;

  public RoomGenerator(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new char[h][w];

    numRooms = 5;
    rooms = new Room[numRooms]; // currently set up to have only 5 rooms
    halls = new Hall[40]; // 40 total halls: 20 vertical halls and 20
    // horizontal halls means 20 logical halls.
    // Never reaches this many with only 5 rooms.
    rand = new Random();
    cleanMap();
    makeRooms();
    makeHalls();
    makePlayerSpawnPoint();
    makeZombieSpawns();
    makeEndPoint();
    printMap();
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

  /*
   * Randomly chooses one of the 5 rooms and randomly chooses a spot in the room
   * to mark the player's spawn point on the map as a 'P'.
   */
  private void makePlayerSpawnPoint()
  {
    int hallSpawn = rand.nextInt(numHalls + 1); // chooses which room to spawn
                                                // in
    playerSpawnPoint = chooseHallPoint(hallSpawn); // chooses where to spawn
                                                   // inside that room
    //
    // rooms[roomSpawn].setPlayerSpawn(); // tells the room the player is
    // spawning
    // // in it

    house[playerSpawnPoint.y][playerSpawnPoint.x] = 'P';
  }

  private void makeEndPoint()
  {
    int hallSpawn = rand.nextInt(numHalls + 1);

    Point p = chooseHallPoint(hallSpawn);

    house[p.y][p.x] = 'E';
  }

  private void makeZombieSpawns()
  {
    for (int i = 0; i < numRooms; i++)
    {
      for (int j = rooms[i].getStartX(); j < rooms[i].getEndX(); j++)
      {
        for (int k = rooms[i].getStartY(); k < rooms[i].getEndY(); k++)
        {
          if (rand.nextDouble() < .1)
          {
            if (rand.nextInt(2) == 0)
            {
              house[k][j] = 'R';
            }
            else
            {
              house[k][j] = 'L';
            }
          }
        }
      }
    }
  }

  private Point chooseHallPoint(int hallIndex)
  {
    // // the x and y coordinates relative to the room
    // int xSpawn = rand.nextInt(rooms[hallIndex].getWidth());
    // int ySpawn = rand.nextInt(rooms[hallIndex].getHeight());
    //
    // // the x and y coordinates relative to the whole map
    // xSpawn = xSpawn + rooms[hallIndex].getStartX();
    // ySpawn = ySpawn + rooms[hallIndex].getStartY();

    int xSpawn = 0;
    int ySpawn = 0;
    int startY, endY, startX, endX, distance;
    boolean foundPoint = false;
    int i = 0;

    while (!foundPoint)
    {
      i++;
      if (halls[hallIndex].isVertical())
      {
        startY = halls[hallIndex].getStartY();
        endY = halls[hallIndex].getEndY();
        xSpawn = halls[hallIndex].getStartX();
        distance = Math.abs(startY - endY);

        if (startY < endY)
        {
          ySpawn = rand.nextInt(distance + 1) + startY;
          if (house[ySpawn][xSpawn] == 'H')
          {
            foundPoint = true;
          }
        }
        else
        {
          ySpawn = rand.nextInt(distance + 1) + endY;
          if (house[ySpawn][xSpawn] == 'H')
          {
            foundPoint = true;
          }
        }
      }
      else
      {
        startX = halls[hallIndex].getStartX();
        endX = halls[hallIndex].getEndX();
        ySpawn = halls[hallIndex].getStartY();
        distance = Math.abs(startX - endX);

        if (startX < endX)
        {
          xSpawn = rand.nextInt(distance + 1) + startX;
          if (house[ySpawn][xSpawn] == 'H')
          {
            foundPoint = true;
          }
        }
        else
        {
          xSpawn = rand.nextInt(distance + 1) + endX;
          if (house[ySpawn][xSpawn] == 'H')
          {
            foundPoint = true;
          }
        }
      }

      if (i == 50)
      {
        return chooseHallPoint(rand.nextInt(numHalls + 1));
      }
    }

    return new Point(xSpawn, ySpawn);
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

  private void makeHalls()
  {
    Room targetRoom;
    boolean found;

    int startX = 0;
    int startY = 0;
    int targetX = 0;
    int targetY = 0;
    numHalls = -1; // keeps track of how many halls we have.

    for (Room currentRoom : rooms)
    {
      found = false;
      while (!found)
      {
        found = true;
        targetRoom = rooms[rand.nextInt(5)];
        if (!(currentRoom.equals(targetRoom)))
        {

          // if its the first room in the array
          if (currentRoom.equals(rooms[0]))
          {
            // System.out.println(i);
            currentRoom.setIsConnected(true);
            targetRoom.setIsConnected(true);
          }

          // if the target room is connected
          // rooms only make hallways to connected rooms to prevent
          // unreachable rooms
          if (targetRoom.isConnected())
          {
            numHalls++;
            // current room found a valid path and is now connected
            // to the whole map
            currentRoom.setIsConnected(true);

            startX = currentRoom.getCenterX();
            startY = currentRoom.getCenterY();
            targetX = targetRoom.getCenterX();
            targetY = targetRoom.getCenterY();

            // make horizontal and vertical hallway
            // from startXY and endXY
            // add to halls[hallCounter]

            // each hall from one room to the other consists of two
            // halls,
            // a vertical one and a horizontal one.

            // this makes a vertical hall
            halls[numHalls] = new Hall(startX, startY, targetX, targetY, true);
            addHallToMap(halls[numHalls]);
            numHalls++;

            // adds the neighboring horizontal wall
            halls[numHalls] = halls[numHalls - 1].getNeighbor();
            addHallToMap(halls[numHalls]);
          }
          else
          {
            found = false;
          }

        }
        else
        {
          found = false;
        }
      }
    }
  }

  /*
   * If the hall is vertical, traverse only through the y positions to add path.
   * Else, traverse only through the x positions for horizontal path.
   */

  private void addHallToMap(Hall hall)
  {
    if (hall.isVertical())
    {
      if (hall.getEndY() > hall.getStartY())
      {
        for (int i = hall.getStartY(); i <= hall.getEndY(); i++)
        {
          if (house[i][hall.getStartX()] == '-')
          {
            house[i][hall.getStartX()] = 'H';
          }
        }
      }
      else
      {
        for (int i = hall.getEndY(); i <= hall.getStartY(); i++)
        {
          if (house[i][hall.getStartX()] == '-')
          {
            house[i][hall.getStartX()] = 'H';
          }
        }
      }
    }
    else
    {
      if (hall.getEndX() > hall.getStartX())
      {
        for (int i = hall.getStartX(); i <= hall.getEndX(); i++)
        {
          if (house[hall.getStartY()][i] == '-')
          {
            house[hall.getStartY()][i] = 'H';
          }
        }
      }
      else
      {
        for (int i = hall.getEndX(); i <= hall.getStartX(); i++)
        {
          if (house[hall.getStartY()][i] == '-')
          {
            house[hall.getStartY()][i] = 'H';
          }
        }
      }
    }
  }

  private void makeRooms()
  {
    int startX = 0;
    int startY = 0;
    int width = 0;
    int height = 0;
    boolean hasFoundLegalSpot = false;
    Room r;

    for (int i = 0; i < rooms.length; i++)
    {
      while (!hasFoundLegalSpot)
      {
        hasFoundLegalSpot = true;
        startX = rand.nextInt(mapWidth - MAX_ROOM_WIDTH) + 1;
        startY = rand.nextInt(mapHeight - MAX_ROOM_WIDTH) + 1;
        width = rand.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
        height = rand.nextInt(width - MIN_ROOM_HEIGHT) + MIN_ROOM_HEIGHT;

        if (i != 0)
        {
          r = new Room(startX, startY, width, height);
          for (int j = 0; j < i; j++)
          {
            if (intersection(r, rooms[j]))
            {
              hasFoundLegalSpot = false;
            }
          }
        }

        if (startX <= 0 || startY <= 0)
        {
          hasFoundLegalSpot = false;
        }
        else if (startX + width >= mapWidth || startY + height >= mapHeight)
        {
          hasFoundLegalSpot = false;
        }
      }

      hasFoundLegalSpot = false;
      rooms[i] = new Room(startX, startY, width, height);
      addRoomToMap(rooms[i]);
    }
  }

  private void addRoomToMap(Room room)
  {
    for (int i = room.getStartY(); i < (room.getStartY() + room.getHeight()); i++)
    {
      for (int j = room.getStartX(); j < (room.getStartX() + room.getWidth()); j++)
      {
        house[i][j] = 'O';
      }
    }
  }

  // private void printRooms() // debug
  // {
  // for (int i = 0; i < rooms.length; i++)
  // {
  // rooms[i].printCoordinates();
  // }
  // }

  // private void printHalls() // debug
  // {
  // for (int i = 0; i < 10; i++)
  // {
  // halls[i].printCoordinates();
  // }
  // }

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

  private boolean intersection(Room r1, Room r2)
  {
    if (r1.getStartX() > r2.getStartX() + r2.getWidth() || r1.getStartX() + r1.getWidth() < r2.getStartX() || r1.getStartY() > r2.getStartY() + r2.getHeight() || r1.getStartY() + r1.getHeight() < r2.getStartY())
    {
      return false;
    }
    return true;
  }

  public static void main(String[] args)
  {
    // RoomGenerator rg = new RoomGenerator(36, 36);
  }
}
