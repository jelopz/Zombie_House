/**
 * RoomGenerator class for the main application. Creates a procedurally generated map
 * consisting of tiles stored in a 2D array of Tiles. The map is completely space filled.
 * 
 * 
 * Given a width and height, HouseBuilder builds a map with these dimensions of a large room.
 * At each tile, a zombie has a chance to spawn there. Then, HouseBuilder partitions the map into 4 quadrants.
 * In these four quadrants we are left with 4 large chunks of rooms. Per quadrant, we start placing
 * hallways (and doors for these hallways), splitting each quadrant up into hallways and rooms randomly.
 * Once done, we add the player spawn point in a randomly selected quadrant and add the end point in
 * the quadrant counter clockwise to the player spawn. Then, from the player spawn, we make doorways from quadrant
 * to quadrant going clockwise.
 * 
 * Thus, a player must traverse through each quadrant to reach the end point.
 */

package RoomGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import application.Game;

/**
 * RoomGenerator class for the main application. Creates a procedurally generated map
 * consisting of tiles stored in a 2D array of Tiles. The map is completely space filled.
 * 
 * 
 * Given a width and height, HouseBuilder builds a map with these dimensions of a large room.
 * At each tile, a zombie has a chance to spawn there. Then, HouseBuilder partitions the map into 4 quadrants.
 * In these four quadrants we are left with 4 large chunks of rooms. Per quadrant, we start placing
 * hallways (and doors for these hallways), splitting each quadrant up into hallways and rooms randomly.
 * Once done, we add the player spawn point in a randomly selected quadrant and add the end point in
 * the quadrant counter clockwise to the player spawn. Then, from the player spawn, we make doorways from quadrant
 * to quadrant going clockwise.
 * 
 * Thus, a player must traverse through each quadrant to reach the end point.
 */
public class HouseBuilder {

	/** The minimum width for each room on the map */
	private final int MIN_ROOM_WIDTH = 6;

	/** The minimum height for each room on the map */
	private final int MIN_ROOM_HEIGHT = 6;

	/** The hall width. */
	private final int HALL_WIDTH = 3;

	/** The house. */
	private Tile[][] house; // The map, house[y][x]

	/** The map width. */
	private int mapWidth;

	/** The map height. */
	private int mapHeight;

	/** The player spawn point. */
	private Point playerSpawnPoint;

	/** The start quadrant. */
	private int startQuadrant;

	/** The quadrant where the end of the level resides */
	private int endQuadrant;

	Random rand;

	/**
	 * The list of all the clusters to be subdivided into rooms and hallways.
	 */
	ArrayList<RoomCluster> cluster = new ArrayList<>();

	/**
	 * Instantiates a new HouseBuilder class.
	 *
	 * @param w,
	 *            the total width of the map
	 * @param h,
	 *            the total height of the map
	 */
	public HouseBuilder(int w, int h) {
		mapWidth = w;
		mapHeight = h;

		house = new Tile[h][w];

		rand = new Random();
		cleanMap();
		markQuadrants();

		for (int i = 0; i < 4; i++) {
			partitionQuadrant(i);
		}

		startQuadrant = makePlayerSpawnPoint();
		endQuadrant = startQuadrant - 1;

		if (startQuadrant == 0) {
			endQuadrant = 3;
		}

		makeEndPoint(endQuadrant);

		connectQuadrants(startQuadrant);

		addObstacles();

		if (Game.debug) {
			printMap();
		}
	}

	/**
	 * Gets the map.
	 *
	 * @return the 2D Tile[][] map.
	 */
	public Tile[][] getMap() {
		return house;
	}

	/**
	 * Gets the player spawn point.
	 *
	 * @return the coordinates to the player's spawn point
	 */
	public Point getPlayerSpawnPoint() {
		return playerSpawnPoint;
	}

	/**
	 * Checks to see if a location is a legal spot to be on.
	 *
	 * @param x
	 *            the x value for the point
	 * @param y
	 *            the y value for the point
	 * @return true, if the point is a legal tile to be on
	 */
	public boolean isPointLegal(int x, int y) {
		if(x > mapWidth || y > mapWidth || x < 0 || y < 0)
		{
			return false;
		}
		if (house[y][x].getTileType() != 'X')
		{
			return true;
		}
		return false;
	}

	/**
	 * Given a tile, returns if that tile is denoted as the end of the map or
	 * not
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if is end point
	 */
	public boolean isEndPoint(int x, int y) {
		if (house[y][x].getTileType() == 'E') {
			return true;
		}
		return false;
	}

	/**
	 * Recursive method to connect all 4 quadrants together. Given a quadrant,
	 * makes a door to the quadrant directly counter clockwise to that quadrant.
	 * If the given quadrant is the quadrant with the end point, stop. We don't
	 * want to make a door directly from the player quadrant to the final
	 * quadrant.
	 *
	 * @param quadrant
	 *            the quadrant
	 */
	private void connectQuadrants(int quadrant) {
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

		if (quadrant == 0 || quadrant == 2) {
			for (int i = startY; i < startY + height - 1; i++) {
				if (rand.nextDouble() < .3) {
					house[i][mapWidth / 2].setTileType('D');
					house[i + 1][mapWidth / 2].setTileType('D');
					done = true;
				}
				if (done) {
					break;
				}
			}
		} else if (quadrant == 1 || quadrant == 3) {
			for (int i = startX; i < startX + width; i++) {
				if (rand.nextDouble() < .3) {
					house[mapHeight / 2][i].setTileType('D');
					house[mapHeight / 2][i + 1].setTileType('D');
					done = true;
				}
				if (done) {
					break;
				}
			}
		}

		if (quadrant == 4) {
			connectQuadrants(0);
		} else {
			connectQuadrants(quadrant + 1);
		}
	}

	/**
	 * Given a quadrant chunk, a large room, split it up into multiple hallways
	 * and rooms
	 *
	 * @param quadrant
	 *            the quadrant
	 */
	private void partitionQuadrant(int quadrant) {
		int startX = 0;
		int startY = 0;
		int width = mapWidth / 2 - 1;
		int height = mapHeight / 2 - 1;

		if (quadrant == 0) {
			startX = mapWidth / 2 + 1;
			startY += 1;
		}
		if (quadrant == 1) {
			startX += 1;
			startY += 1;
		}
		if (quadrant == 2) {
			startX += 1;
			startY = mapHeight / 2 + 1;
		} else if (quadrant == 3) {
			startX = mapWidth / 2 + 1;
			startY = mapHeight / 2 + 1;
		}

		RoomCluster current;
		cluster.add(new RoomCluster(startX, startY, width, height, true));

		while (!cluster.isEmpty()) {
			current = cluster.remove(0);
			makeHall(current, current.giveHorizontalWall);
		}
	}

	/**
	 * Adds two doorways on a hallway, one on each long side.
	 *
	 * @param hallStartPoint
	 *            the hall start point, the top left most pathable tile in the
	 *            quadrant
	 * @param isVertical
	 *            Denotes if the hallway is vertical or horizontal
	 * @param c
	 *            The room cluster we are currently working inside of
	 */
	private void addDoorsToHall(int hallStartPoint, boolean isVertical, RoomCluster c) {
		if (!isVertical) {
			int d = rand.nextInt(c.height - 3) + c.y + 1;
			house[d][hallStartPoint - 1].setTileType('D');
			house[d + 1][hallStartPoint - 1].setTileType('D');
			d = rand.nextInt(c.height - 3) + c.y + 1;
			house[d][hallStartPoint + 3].setTileType('D');
			house[d + 1][hallStartPoint + 3].setTileType('D');
		} else {
			int d = rand.nextInt(c.width - 1) + c.x;
			house[hallStartPoint - 1][d].setTileType('D');
			house[hallStartPoint - 1][d + 1].setTileType('D');
			d = rand.nextInt(c.width - 1) + c.x;
			house[hallStartPoint + 3][d].setTileType('D');
			house[hallStartPoint + 3][d + 1].setTileType('D');
		}
	}

	/**
	 * Randomly chooses points on the map. If the map is a room tile, check to
	 * see if there are any obstacles within 2 tiles of that point. If there is
	 * nothing, add an obstacle to that tile.
	 */
	private void addObstacles() {
		boolean badspot = false;
		int numObstacles = 0;
		int x, y;

		while (numObstacles < 12) {
			int j = rand.nextInt(mapWidth - 7) + 3;
			int i = rand.nextInt(mapHeight - 7) + 3;

			if (house[i][j].getTileType() == '-') {
				for (int k = 1; k < 3; k++) {
					if (house[i - k][j].getTileType() != '-' || house[i + k][j].getTileType() != '-') {
						badspot = true;
					}
					if (house[i][j - k].getTileType() != '-' || house[i][j + k].getTileType() != '-') {
						badspot = true;
					}
					if (house[i - k][j - k].getTileType() != '-' || house[i + k][j + k].getTileType() != '-') {
						badspot = true;
					}
					if (house[i - k][j + k].getTileType() != '-' || house[i + k][j - k].getTileType() != '-') {
						badspot = true;
					}
				}

				if (!badspot) {
					house[i][j].setTileType('X');
					numObstacles++;
				}
			}
			badspot = false;
		}
	}

	/**
	 * Given a cluster and a type of hallway(vertical or horizontal), if the
	 * cluster is large enough, the method splits the cluster with the type of
	 * hallway in a valid position.
	 *
	 * @param c
	 *            The room cluster we are currently adding a hallway to
	 * @param isVertical
	 *            Denotes if the hallway to add will be verticla or horizontal
	 */
	private void makeHall(RoomCluster c, boolean isVertical) {
		if (!isVertical) {
			int r;

			if (c.width <= (MIN_ROOM_WIDTH * 2) + 3) {
				return;
			} else {
				r = rand.nextInt(c.width - (2 * MIN_ROOM_WIDTH + HALL_WIDTH)) + c.x + MIN_ROOM_WIDTH;
			}

			int c1Width = Math.abs(c.x - r);
			int c2Width = c.width - c1Width - 3;

			if (c1Width > MIN_ROOM_WIDTH && c2Width > MIN_ROOM_WIDTH) {
				cluster.add(new RoomCluster(c.x, c.y, c1Width, c.height, !isVertical));
				cluster.add(new RoomCluster(r + 3, c.y, c2Width, c.height, !isVertical));
			}
			for (int y = c.y; y < (c.y + c.height); y++) {
				if (house[y][r - 1].getTileType() != 'D' && house[y][r + 3].getTileType() != 'D') {
					house[y][r - 1].setTileType('X');
					house[y][r + 3].setTileType('X');
				}
				for (int x = r; x < r + 3; x++) {
					if (house[y][x].getTileType() != 'X' && house[y][x].getTileType() != 'D') {
						house[y][x].setTileType('H');
					}
				}
			}
			addDoorsToHall(r, isVertical, c);
		} else // gets a horizontal hall
		{
			int r;

			if (c.height < (MIN_ROOM_HEIGHT * 2) + 3) {
				return;
			} else {
				r = rand.nextInt(c.height - (2 * MIN_ROOM_HEIGHT + HALL_WIDTH)) + c.y + MIN_ROOM_HEIGHT;
			}
			int c1Height = Math.abs(c.y - r);
			int c2Height = c.height - c1Height - 3;

			if (c1Height > MIN_ROOM_WIDTH && c2Height > MIN_ROOM_WIDTH) {
				cluster.add(new RoomCluster(c.x, c.y, c.width, c1Height, !isVertical));
				cluster.add(new RoomCluster(c.x, r + 3, c.width, c2Height, !isVertical));
			}

			for (int y = r; y < r + 3; y++) {
				for (int x = c.x; x < (c.x + c.width); x++) {
					if (house[y][x].getTileType() != 'X' && house[y][x].getTileType() != 'D') {
						house[y][x].setTileType('H');
					}

					if (house[r - 1][x].getTileType() != 'D' && house[r + 3][x].getTileType() != 'D') {
						house[r - 1][x].setTileType('X');
						house[r + 3][x].setTileType('X');
					}
				}
			}

			addDoorsToHall(r, isVertical, c);
		}

	}

	/**
	 * Marks the perimeter and split the four quadrants to four equal large
	 * rooms
	 */
	private void markQuadrants() {

		for (int i = 0; i < mapHeight; i++) {
			house[i][mapWidth / 2].setTileType('X');
			house[i][0].setTileType('X');
			house[i][mapWidth - 1].setTileType('X');
		}

		for (int i = 0; i < mapWidth; i++) {
			house[mapHeight / 2][i].setTileType('X');
			house[0][i].setTileType('X');
			house[mapHeight - 1][i].setTileType('X');
		}
	}

	/**
	 * Given a quadrant, it finds two adjacent points on an outer wall that is
	 * not obstructed by any walls in front of it and denotes it as the end
	 * point
	 *
	 * @param endQuadrant
	 *            The quadrant to place the endpoint inside of
	 */
	private void makeEndPoint(int endQuadrant) {
		Point quadrantStart = findQuadrantStartPoint(endQuadrant);
		int startY = quadrantStart.y;
		int startX = quadrantStart.x;
		int width = mapWidth / 2 - 1;
		int height = mapHeight / 2 - 1;

		boolean found = false;

		while (!found) {
			// decides which one of the two outer walls to use
			if (rand.nextInt(2) == 0) {
				if (endQuadrant == 0 || endQuadrant == 1) {
					for (int i = startX; i < startX + width - 1; i++) {
						if (rand.nextDouble() < .3) {
							// Checks to make sure exit isn't behind a wall
							if (house[1][i].getTileType() != 'X' && house[1][i + 1].getTileType() != 'X') {
								house[0][i].setTileType('E');
								house[0][i + 1].setTileType('E');
								found = true;
							}
						}
						if (found) {
							break;
						}
					}
				} else if (endQuadrant == 2 || endQuadrant == 3) {
					for (int i = startX; i < startX + width - 1; i++) {
						if (rand.nextDouble() < .3) {
							if (house[startY + height - 1][i].getTileType() != 'X'
									&& house[startY + height - 1][i + 1].getTileType() != 'X') {
								house[startY + height][i].setTileType('E');
								house[startY + height][i + 1].setTileType('E');
								found = true;
							}
						}
						if (found) {
							break;
						}
					}
				}
			} else {
				if (endQuadrant == 0 || endQuadrant == 3) {
					for (int i = startY; i < startY + height; i++) {
						if (rand.nextDouble() < .3) {
							if (house[i][startX + width - 1].getTileType() != 'X'
									&& house[i + 1][startX + width - 1].getTileType() != 'X') {
								house[i][startX + width].setTileType('E');
								house[i + 1][startX + width].setTileType('E');
								found = true;
							}
						}
						if (found) {
							break;
						}
					}
				} else if (endQuadrant == 1 || endQuadrant == 2) {
					for (int i = startY; i < startY + height; i++) {
						if (rand.nextDouble() < .3) {
							if (house[i][1].getTileType() != 'X' && house[i + 1][1].getTileType() != 'E') {
								house[i][0].setTileType('E');
								house[i + 1][0].setTileType('E');
								found = true;
							}
						}
						if (found) {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Given a quadrant, returns the point to the top-left corner tile, the
	 * starting tile.
	 *
	 * @param quadrant
	 *            The quadrant we are trying to find the top-left corner tile
	 *            for.
	 * @return The start point, the location of the top left most corner tile in
	 *         the quadrant.
	 */
	private Point findQuadrantStartPoint(int quadrant) {
		int startX = 0;
		int startY = 0;

		if (quadrant == 0) {
			startX = mapWidth / 2 + 1;
			startY += 1;
		}
		if (quadrant == 1) {
			startX += 1;
			startY += 1;
		}
		if (quadrant == 2) {
			startX += 1;
			startY = mapHeight / 2 + 1;
		} else if (quadrant == 3) {
			startX = mapWidth / 2 + 1;
			startY = mapHeight / 2 + 1;
		}

		return new Point(startX, startY);
	}

	/**
	 * Randomly chooses 1 of the 4 quadrants, and denotes a spawn in any point
	 * in a hallway.
	 * 
	 * Returns the quadrant the player spawned in and sets playerSpawnPoint to
	 * the appropriate point.
	 *
	 * @return An integer denoting what quadrant the method chose.
	 */
	private int makePlayerSpawnPoint() {
		int spawnQ = rand.nextInt(4);

		int startX = 0;
		int startY = 0;
		int width = mapWidth / 2 - 1;
		int height = mapHeight / 2 - 1;

		if (spawnQ == 0) {
			startX = mapWidth / 2 + 1;
			startY += 1;
		}
		if (spawnQ == 1) {
			startX += 1;
			startY += 1;
		}
		if (spawnQ == 2) {
			startX += 1;
			startY = mapHeight / 2 + 1;
		} else if (spawnQ == 3) {
			startX = mapWidth / 2 + 1;
			startY = mapHeight / 2 + 1;
		}

		boolean found = false;

		while (!found) {
			for (int j = startY; j <= (startY + height); j++) {
				for (int k = startX; k <= (startX + width); k++) {
					if (house[j][k].getTileType() == 'H') {
						if (rand.nextDouble() < .3) {
							house[j][k].setTileType('P');
							playerSpawnPoint = new Point(k, j);
							found = true;
						}
					}
					if (found) {
						break;
					}
				}
				if (found) {
					break;
				}
			}

		}

		return spawnQ;
	}

	/**
	 * Initializes every tile in house[][] as a room tile, and decides if a
	 * zombie will spawn there.
	 */
	private void cleanMap() {
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				if (rand.nextDouble() < .01) {
					if (rand.nextInt(2) == 0) {
						house[i][j] = new Tile('R', j, i);
					} else {
						house[i][j] = new Tile('L', j, i);
					}
				} else {
					house[i][j] = new Tile('-', j, i);
				}
			}
		}
	}

	/**
	 * For debugging and testing purposes, prints the map by each char tileType
	 */
	private void printMap() {
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				System.out.print(house[i][j].getTileType());
			}
			System.out.println();
		}
	}
}
