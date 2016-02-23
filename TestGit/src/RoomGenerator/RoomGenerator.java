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

import java.util.Random;

public class RoomGenerator
{
  private final int MIN_ROOM_WIDTH = 4; // arbitrary,
  private final int MAX_ROOM_WIDTH = 6; // arbitrary, will change once we have a
                                        // better understanding of how big the
                                        // map should be
  private final int MIN_ROOM_HEIGHT = 3;// arbitrary,

  private final int NUM_ZOMBIES = 5;
  
  private Room[] rooms; // array of all the rooms
  private Hall[] halls; // array of all the halls
  private char[][] house; // The map, house[y][x]

  private int mapWidth;
  private int mapHeight;

  Random rand;

  public RoomGenerator(int w, int h)
  {
    mapWidth = w;
    mapHeight = h;

    house = new char[h][w];

    rooms = new Room[5]; // currently set up to have only 5 rooms
    halls = new Hall[40]; // 40 total halls: 20 vertical halls and 20
    // horizontal halls means 20 logical halls.
    // Never reaches this many with only 5 rooms.
    rand = new Random();
    cleanMap();
    makeRooms();
    makeHalls();
    makePlayerSpawnPoint();

    printMap();
  }

  /*
   * Randomly chooses one of the 5 rooms and randomly chooses a spot in the room
   * to mark the player's spawn point on the map as a 'P'.
   */
  private void makePlayerSpawnPoint()
  {
    int roomSpawn = rand.nextInt(5); // Randomly chooses one of the 5 rooms.

    // the x and y coordinates relative to the room
    int xSpawn = rand.nextInt(rooms[roomSpawn].getWidth());
    int ySpawn = rand.nextInt(rooms[roomSpawn].getHeight());

    // the x and y coordinates relative to the whole map
    xSpawn = xSpawn + rooms[roomSpawn].getStartX();
    ySpawn = ySpawn + rooms[roomSpawn].getStartY();

    house[ySpawn][xSpawn] = 'P';
  }
  
  private void makeZombieSpawns()
  {
    for(int i = 0; i < NUM_ZOMBIES; i++)
    {
      
    }
  }

  private void cleanMap()
  {
    for (int i = 0; i < mapHeight; i++)
    {
      for (int j = 0; j < mapWidth; j++)
      {
        house[i][j] = 'X';
      }
    }
  }

  private void makeHalls()
  {
    Room targetRoom;
    boolean found = false;

    int startX = 0;
    int startY = 0;
    int targetX = 0;
    int targetY = 0;
    int hallCounter = 0; // keeps track of how many halls we have.

    for (Room currentRoom : rooms)
    {
      while (!found)
      {
        found = true;
        targetRoom = rooms[rand.nextInt(5)];
        if (!(currentRoom.equals(targetRoom)))
        {

          // if its the first room in the array
          if (currentRoom.equals(rooms[0]))
          {
            currentRoom.setIsConnected(true);
            targetRoom.setIsConnected(true);
          }

          // if the target room is connected
          // rooms only make hallways to connected rooms to prevent
          // unreachable rooms
          if (targetRoom.isConnected())
          {
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
            halls[hallCounter] = new Hall(startX, startY, targetX, targetY, true);
            addHallToMap(halls[hallCounter]);
            hallCounter++;

            // adds the neighboring horizontal wall
            halls[hallCounter] = halls[hallCounter - 1].getNeighbor();
            addHallToMap(halls[hallCounter]);
            hallCounter++;
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
      found = false;
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
          house[i][hall.getStartX()] = 'O';
        }
      }
      else
      {
        for (int i = hall.getEndY(); i <= hall.getStartY(); i++)
        {
          house[i][hall.getStartX()] = 'O';
        }
      }
    }
    else
    {
      if (hall.getEndX() > hall.getStartY())
      {
        for (int i = hall.getStartX(); i <= hall.getEndX(); i++)
        {
          house[hall.getStartY()][i] = 'O';
        }
      }
      else
      {
        for (int i = hall.getEndX(); i <= hall.getStartX(); i++)
        {
          house[hall.getStartY()][i] = 'O';
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
        startX = rand.nextInt(13);
        startY = rand.nextInt(16);
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

  private void printRooms() // debug
  {
    for (int i = 0; i < rooms.length; i++)
    {
      rooms[i].printCoordinates();
    }
  }

  private void printHalls() // debug
  {
    for (int i = 0; i < 10; i++)
    {
      halls[i].printCoordinates();
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

  public char[][] getMap()
  {
    return house;
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
    RoomGenerator rg = new RoomGenerator(20, 20);
  }
}
