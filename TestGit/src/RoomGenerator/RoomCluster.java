/**
 * Helper class for RoomGenerator.
 * 
 * A RoomCluster is a room that we are looking at splitting in half with either
 * a horizontal or vertical hallway.
 * 
 * Holds the cluster's dimensions and whether it will get a horizontal or
 * vertical hallway.
 */

package RoomGenerator;

/**
 * Helper class for RoomGenerator.
 * 
 * A RoomCluster is a room that we are looking at splitting in half with either
 * a horizontal or vertical hallway.
 * 
 * Holds the cluster's dimensions and whether it will get a horizontal or
 * vertical hallway.
 */
public class RoomCluster {

	/** The top left most x value in the room cluster. */
	public int x;

	/** The top left most y value in the room cluster. */
	public int y;

	/** The width. */
	public int width;

	/** The height. */
	public int height;

	/**
	 * Will the cluster recieve a horizontal or vertical hallway to partition
	 * it?
	 */
	boolean giveHorizontalWall;

	/**
	 * Instantiates a new room cluster.
	 *
	 * @param x
	 *            the top left most x value in the room cluster
	 * @param y
	 *            The top left most y value in the room cluster.
	 * @param w
	 *            the width
	 * @param h
	 *            the hiehgt
	 * @param b
	 *            the boolean to denote if this cluster is set to recieve a
	 *            horizontal or vertical hallway
	 */
	public RoomCluster(int x, int y, int w, int h, boolean b) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		giveHorizontalWall = b;
	}
}
