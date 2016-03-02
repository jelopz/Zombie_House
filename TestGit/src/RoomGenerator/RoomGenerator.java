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
import java.util.ArrayList;
import java.util.Random;

import application.Game;

public class RoomGenerator
{
  private final int MIN_ROOM_WIDTH = 4; // arbitrary,
  private final int MAX_ROOM_WIDTH = 6; // arbitrary, will change once we have a
                                        // better understanding of how big the
                                        // map should be
  private final int MIN_ROOM_HEIGHT = 3;// arbitrary,

  // Arraylist of all rooms and halls
  private ArrayList<Room> rooms;
  private ArrayList<Hall> halls;
  private char[][] house; // The map, house[y][x]

  private int mapWidth;
  private int mapHeight;

  private Point playerSpawnPoint;

  Random rand;

  public RoomGenerator(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new char[h][w];

    rooms = new ArrayList<>();
    halls = new ArrayList<>();
    rand = new Random();
    cleanMap();
    makeRooms();
    makeHalls();
    makePlayerSpawnPoint();
    makeZombieSpawns();
    makeEndPoint();
    if(Game.debug)
    {
      printMap();
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

  /*
   * Randomly chooses one of the 5 rooms and randomly chooses a spot in the room
   * to mark the player's spawn point on the map as a 'P'.
   */
  private void makePlayerSpawnPoint()
  {
    int hallSpawn = rand.nextInt(halls.size()); // chooses which room to spawn
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
    int hallSpawn = rand.nextInt(halls.size());

    Point p = chooseHallPoint(hallSpawn);

    house[p.y][p.x] = 'E';
  }

  private void makeZombieSpawns()
  {
    for (int i = 0; i < rooms.size(); i++)
    {
      Room room = rooms.get(i);
      for (int j = room.getStartX(); j < room.getEndX(); j++)
      {
        for (int k = room.getStartY(); k < room.getEndY(); k++)
        {
          if (rand.nextDouble() < .05)
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
    int xSpawn = 0;
    int ySpawn = 0;
    int startY, endY, startX, endX, distance;
    boolean foundPoint = false;
    int i = 0;

    while (!foundPoint)
    {
      i++;
      // if (halls[hallIndex].isVertical())
      if (halls.get(hallIndex).isVertical())
      {
        startY = halls.get(hallIndex).getStartY();
        endY = halls.get(hallIndex).getEndY();
        xSpawn = halls.get(hallIndex).getStartX();
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
        startX = halls.get(hallIndex).getStartX();
        endX = halls.get(hallIndex).getEndX();
        ySpawn = halls.get(hallIndex).getStartY();
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
        return chooseHallPoint(rand.nextInt(halls.size()));
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
    // numHalls = -1; // keeps track of how many halls we have.

    for (Room currentRoom : rooms)
    {
      found = false;
      while (!found)
      {
        found = true;
        // targetRoom = rooms[rand.nextInt(5)];
        targetRoom = rooms.get(rand.nextInt(rooms.size()));
        if (!(currentRoom.equals(targetRoom)))
        {

          // if its the first room in the array
          if (currentRoom.equals(rooms.get(0)))
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
            // numHalls++;
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

            halls.add(new Hall(startX, startY, targetX, targetY, true));
            halls.add(halls.get(halls.size() - 1).getNeighbor());
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

    for (int i = 0; i < halls.size(); i++)
    {
      addHallToMap(halls.get(i));
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
    boolean isFirstRoom = true;
    Room r;

    for (int i = 0; i < 100;) // until 100 failures
    {
      while (!hasFoundLegalSpot)
      {
        hasFoundLegalSpot = true;
        startX = rand.nextInt(mapWidth - MAX_ROOM_WIDTH) + 1;
        startY = rand.nextInt(mapHeight - MAX_ROOM_WIDTH) + 1;
        width = rand.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
        height = rand.nextInt(width - MIN_ROOM_HEIGHT) + MIN_ROOM_HEIGHT;

        if (!isFirstRoom)
        {
          r = new Room(startX, startY, width, height);
          for (int j = 0; j < rooms.size(); j++)
          {
            if (intersection(r, rooms.get(j)))
            {
              i++;
              hasFoundLegalSpot = false;
            }
          }
        }

        if (startX <= 0 || startY <= 0)
        {
          i++;
//          System.out.println(i);
          if (i == 0 && isFirstRoom)
          {
//            System.out.println("wow");
          }
          hasFoundLegalSpot = false;
        }
        else if (startX + width >= mapWidth || startY + height >= mapHeight)
        {
          i++;
//          System.out.println(i + "     ---2");
          if (i == 0 && isFirstRoom)
          {
//            System.out.println("wow2");
          }
          hasFoundLegalSpot = false;
        }

      }

      hasFoundLegalSpot = false;
      isFirstRoom = false;
      rooms.add(new Room(startX, startY, width, height));
      // addRoomToMap();
    }

    for (int i = 0; i < rooms.size(); i++)
    {
      addRoomToMap(rooms.get(i));
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
