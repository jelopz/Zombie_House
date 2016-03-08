/**
 * Tile object to represent a space in our 2D Map array.
 */

package RoomGenerator;

import java.util.ArrayList;

/**
 * Tile object to represent a space in our 2D Map array.
 */
public class Tile {

	/** The x value. */
	private int x;

	/** The y value. */
	private int y;

	/**
	 * The tile type, denoting if it's a room tile, halltile, player/zombie
	 * spawn.
	 */
	private char tileType;

	/**
	 * In a path, the parent denotes what tile came immediately before this one
	 * in the path
	 */
	public Tile parent;

	/** The current number of tiles in the path. */
	public int currentCost;

	/** If the tile was visited during a pathfinding session */
	public boolean visited;

	/** Used to sort the priority queue of tiles. */
	public int priority;

	/**
	 * Instantiates a new tile.
	 *
	 * @param c
	 *            The tile type
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 */
	public Tile(char c, int x, int y) {
		tileType = c;
		this.x = x;
		this.y = y;

		reset();
	}

	/**
	 * Reset's the tile to have it ready for the next Pathfinder process
	 */
	public void reset() {
		currentCost = 0;
		visited = false;
		priority = 0;
		parent = null;
	}

	/**
	 * Gets the tile type, generally useful to know if it's a wall tile, the end
	 * point, or it's pathable.
	 *
	 * @return the tile type
	 */
	public char getTileType() {
		return tileType;
	}

	/**
	 * Sets the tile type.
	 *
	 * @param c
	 *            the new tile type
	 */
	public void setTileType(char c) {
		tileType = c;
	}

	/**
	 * Gets the x value of the tile in the 2D Tile[][] array.
	 *
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y value of the tile in the 2D Tile[][] array..
	 *
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Prints the path from the target tile (in our application, the target tile
	 * is always the player's tile) back to the zombie's tile.
	 *
	 * @param p
	 *            the p
	 */
	public void printPath(ArrayList<Tile> p) {
		if (parent != null) {
			// System.out.println("( " + x + " , " + y + " ) , ");
			p.add(this);
			parent.printPath(p);
		}
	}
}
