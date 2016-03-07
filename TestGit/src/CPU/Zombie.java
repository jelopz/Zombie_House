package CPU;

import java.awt.Point;
import java.util.Random;
import Hitbox.Hitbox;
import Pathfinding.Pathfinder;
import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;

public abstract class Zombie
{
  protected final Point spawnPoint; // (x,y) coordinate of spawn Point
  protected Group model;
  protected static Random rand;
  protected Hitbox hitbox;
  protected double angleZ, angleX;
  protected boolean isStuck;
  protected boolean smellsPlayer;
  protected Pathfinder pathfinder;
  private double radius;

  public Zombie(int x, int y, Group m)
  {
    spawnPoint = new Point(x, y);
    model = m;
    rand = new Random();
    hitbox = new Hitbox(model);
    radius = Game.TILE_SIZE / 4;
    isStuck = false;
    smellsPlayer = false;
    pathfinder = new Pathfinder();
  }

  public boolean zombieCollision(Zombie z)
  {
    double zDistance = z.model.getTranslateZ() - model.getTranslateZ();
    double xDistance = z.model.getTranslateX() - model.getTranslateX();
    zDistance *= zDistance;
    xDistance *= xDistance;

    double distance = Math.sqrt(zDistance + xDistance);

    if (distance <= Game.TILE_SIZE / 2)
    {
      return true;
    }

    return false;
  }

  abstract public void determineNextMove(HouseBuilder house, double playerZ, double playerX);

  abstract public void move(HouseBuilder house);

//  protected void smell(double playerZ, double playerX)
//  {
//    double d = pathfinder.findEucl(playerZ, playerX, model.getTranslateZ(), model.getTranslateX());
//
//    if (d/Game.TILE_SIZE < 15)
//    {
//      smellsPlayer = true;
//    }
//    else
//    {
//      smellsPlayer = false;
//    }
//  }

  protected void findNextAngle(HouseBuilder house)
  {
    angleZ = rand.nextDouble();
    angleX = Math.sqrt(1 - (angleZ * angleZ));

    if (rand.nextInt(2) == 0) // 50/50 chance of x being positive or
    // negative
    {
      angleZ = -1 * angleZ;
    }

    if (rand.nextInt(2) == 0) // 50/50 chance of y being positive or
    // negative
    {
      angleX = -1 * angleX;
    }
  }

}
