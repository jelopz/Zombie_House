/**
 * Comparator class used to sort our Tiles by their priority in a PriorityQueue
 */

package RoomGenerator;

import java.util.Comparator;

/**
 * Comparator class used to sort our Tiles by their priority in a PriorityQueue
 */
public class TileComparator implements Comparator<Tile> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Tile node1, Tile node2) {
		return node1.priority - node2.priority;
	}
}
