/*
 * Comparator class used to sort our Tiles by their priority in a PriorityQueue
 */

package RoomGenerator;

import java.util.Comparator;

public class TileComparator implements Comparator<Tile>
{

  @Override
  public int compare(Tile node1, Tile node2)
  {
    return node1.priority - node2.priority;
  }
}
