package CPU;

import java.awt.Point;
import java.util.Random;

import javafx.scene.shape.Cylinder;

public abstract class Zombie
{
  protected final Point spawnPoint; // (x,y) coordinate of spawn Point
  protected Cylinder model;
  protected static Random rand;

  public Zombie(int x, int y, Cylinder m)
  {
    spawnPoint = new Point(x, y);
    model = m;
    rand = new Random();
  }

  abstract public void determineNextMove();

}
