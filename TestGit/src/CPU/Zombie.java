package CPU;

public abstract class Zombie
{
  private int startPointX;
  private int startPointY;

  public Zombie(int x, int y)
  {
    startPointX = x;
    startPointY = y;
  }
  
  abstract void determineNextMove();
  
}
