package tester;

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
