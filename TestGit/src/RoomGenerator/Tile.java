/*
 * Tile object to represent a space in our 2D Map array.
 */

package RoomGenerator;

import java.util.ArrayList;

public class Tile
{
  private int x;
  private int y;
  private char tileType;

  public Tile parent;
  public int currentCost;
  public boolean visited;
  public int priority;

  public Tile(char c, int x, int y)
  {
    tileType = c;
    this.x = x;
    this.y = y;

    reset();
  }

  public void reset()
  {
    currentCost = 0;
    visited = false;
    priority = 0;
    parent = null;
  }

  public char getTileType()
  {
    return tileType;
  }

  public void setTileType(char c)
  {
    tileType = c;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public void printPath(ArrayList<Tile> p)
  {
    if (parent != null)
    {
//      System.out.println("( " + x + " , " + y + " ) , ");
      p.add(this);
      parent.printPath(p);
    }
  }
}
