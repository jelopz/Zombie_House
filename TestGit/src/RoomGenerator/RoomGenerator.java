/*
 * When RoomGenerator gets called, it creates a char[][] house where 'O'
 * denotes a walkable field and 'X' denotes a wall. 
 * 
 * Call RoomGenerator(width,height) to denote the width and height of the map
 * in tiles. Then, call RoomGenerator.getMap() to return the char[][] house map when done.
 * 
 * Currently the halls are being placed incorrectly. Program is liable to crash
 * If the mapWidth and mapHeight coordinates aren't large enough. 40x40 seems to be
 * large enough to not crash for now. Also, generator
 * only makes 5 rooms. These things will be changed as we progress in the project
 */

package RoomGenerator;

import java.util.Random;

public class RoomGenerator {
	private final int MIN_ROOM_WIDTH = 4; // arbitrary,
	private final int MAX_ROOM_WIDTH = 6; // arbitrary, will change once we have
											// a better understanding of how big
											// the map should be
	private final int MIN_ROOM_HEIGHT = 3;// arbitrary,

	private Room[] rooms; // array of all the rooms
	private Hall[] halls; // array of all the halls
	private char[][] house; // The map

	private int mapWidth;
	private int mapHeight;

	Random rand;

	public RoomGenerator(int w, int h) {
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
		// printRooms();
		makeHalls();
		// printRooms();
		// printHalls();
		printMap();
	}

	private void cleanMap() {
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				house[i][j] = 'X';
			}
		}
	}

	private void printMap() {
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				System.out.print(house[i][j]);
			}
			System.out.println();
		}
	}

	private void makeHalls() {
		Room targetRoom;
		boolean found = false;
		
		int startX = 0;
		int startY = 0;
		int targetX = 0;
		int targetY = 0;
		int hallCounter = 0; // keeps track of how many halls we have.

		for (Room currentRoom : rooms) {
			while (!found) {
				found = true;
				targetRoom = rooms[rand.nextInt(5)];
				if (!currentRoom.equals(targetRoom)) {
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
					halls[hallCounter] = new Hall(startX, startY, targetX, targetY, true);
					addHallToMap(halls[hallCounter]);
					hallCounter++;
					halls[hallCounter] = new Hall(halls[hallCounter - 1].getEndX(), halls[hallCounter - 1].getEndY(),
							targetX, targetY, false);
					addHallToMap(halls[hallCounter]);
					hallCounter++;

				} else {
					found = false;
				}
			}
			found = false;
		}
	}

	private void addHallToMap(Hall hall) {
		for (int i = hall.getStartY(); i < (hall.getStartY() + hall.getHeight()); i++) {
			for (int j = hall.getStartX(); j < (hall.getStartX() + hall.getWidth()); j++) {
				house[i][j] = 'O';
			}
		}
	}

	private void makeRooms() {
		int startX = 0;
		int startY = 0;
		int width = 0;
		int height = 0;
		boolean hasFoundLegalSpot = false;
		Room r;

		for (int i = 0; i < rooms.length; i++) {
			while (!hasFoundLegalSpot) {
				hasFoundLegalSpot = true;
				startX = rand.nextInt(13);
				startY = rand.nextInt(16);
				width = rand.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1) + MIN_ROOM_WIDTH;
				height = rand.nextInt(width - MIN_ROOM_HEIGHT) + MIN_ROOM_HEIGHT;

				if (i != 0) {
					r = new Room(startX, startY, width, height);
					for (int j = 0; j < i; j++) {
						if (intersection(r, rooms[j])) {
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

	private void addRoomToMap(Room room) {
		for (int i = room.getStartY(); i < (room.getStartY() + room.getHeight()); i++) {
			for (int j = room.getStartX(); j < (room.getStartX() + room.getWidth()); j++) {
				house[i][j] = 'O';
			}
		}
	}

	private void printRooms() {
		for (int i = 0; i < rooms.length; i++) {
			rooms[i].printCoordinates();
		}
	}

	private void printHalls() {
		for (int i = 0; i < halls.length; i++) {
			halls[i].printCoordinates();
		}
	}

	public char[][] getMap() {
		return house;
	}

	private boolean intersection(Room r1, Room r2) {
		if (r1.getStartX() > r2.getStartX() + r2.getWidth() || r1.getStartX() + r1.getWidth() < r2.getStartX()
				|| r1.getStartY() > r2.getStartY() + r2.getHeight()
				|| r1.getStartY() + r1.getHeight() < r2.getStartY()) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		RoomGenerator rg = new RoomGenerator(40, 40);
	}
}
