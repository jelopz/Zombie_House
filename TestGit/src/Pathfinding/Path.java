package Pathfinding;

import java.util.ArrayList;

import RoomGenerator.Tile;

public class Path
{
  private ArrayList<Tile> path;

  public Path()
  {
    path = new ArrayList<>();
  }

  public void addTileToPath(Tile t)
  {
    path.add(t);
  }

  public ArrayList<Tile> getPath()
  {
    return path;
  }
}
