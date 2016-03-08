/*
 * Helper class for RoomGenerator. 
 * 
 * A RoomCluster is a room that we are looking at splitting in half with
 * either a horizontal or vertical hallway. 
 * 
 * Holds the cluster's dimensions and whether it will get a horizontal or vertical hallway.
 */
package RoomGenerator;

public class RoomCluster
{
  public int x, y, width, height;
  boolean giveHorizontalWall;

  public RoomCluster(int x, int y, int w, int h, boolean b)
  {
    this.x = x;
    this.y = y;
    width = w;
    height = h;
    giveHorizontalWall = b;
  }
}
