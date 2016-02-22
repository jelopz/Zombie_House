package CPU;

import java.awt.Point;

public abstract class Zombie
{
  private Point spawnPoint; //(x,y) coordinate of spawn Point

  public Zombie(int x, int y)
  {
    spawnPoint = new Point(x,y);
  }
  
  abstract void determineNextMove();
  
}
