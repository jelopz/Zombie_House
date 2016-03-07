package CPU;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import Hitbox.Hitbox;
import Pathfinding.Path;
import Pathfinding.Pathfinder;
import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import application.Game;
import javafx.scene.Group;

public class OurZombie
{
  private final Point spawnPoint; // (x,y) coordinate of spawn Point
  private Group model;
  private static Random rand;
  private Hitbox hitbox;
  private double angleZ, angleX;
  private boolean isStuck;
  private boolean smellsPlayer;
  private Pathfinder pathfinder;

  private double translationZ, translationX;

  private ArrayList<Tile> currentPath;

  private double radius;

  public OurZombie(int x, int y, Group m)
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

  private boolean zombieCollision(OurZombie z)
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

  public void determineNextMove(HouseBuilder house, double playerZ, double playerX)
  {
    double d = pathfinder.findEucl(playerZ, playerX, model.getTranslateZ(), model.getTranslateX());

    if (d / Game.TILE_SIZE < 15)
    {
      smell(house.getMap(), playerZ, playerX);

      if (pathfinder.doesPathExist())// && isValid(playerZ, playerX,
                                     // pathfinder.getPath()) &&
                                     // !pathfinder.getPath().getPath().isEmpty())
      {
        if (Game.debug)
        {
          System.out.println("CLOSE ENOUGH TO SMELL  green " + this);
        }
      }
    }

    findNextAngle(house);
    isStuck = false;
  }

  private boolean isValid(double playerZ, double playerX, Path p)
  {
    if (p.getPath().get(0).getX() == (int) (playerZ / 56) && p.getPath().get(0).getY() == (int) (playerX / 56))
    {
      return true;
    }
    return false;
  }

  public void move(HouseBuilder house)
  {
    // System.out.println(model.getTranslateZ());
    translationZ = model.getTranslateZ() + angleZ;
    translationX = model.getTranslateX() + angleX;

    hitbox.updateBoundaryPoints(translationZ, translationX);

    if (!hitbox.isWallCollision(house))
    {

      for (OurZombie z : Game.zombies)
      {
        if ((!z.equals(this)) && zombieCollision(z))
        {
          isStuck = true;
          model.setTranslateZ(translationZ - 2 * angleZ);
          model.setTranslateX(translationX - 2 * angleX);
        }
      }

      if (!isStuck)
      {
        model.setTranslateZ(translationZ);
        model.setTranslateX(translationX);
      }
    }
  }

  private void smell(Tile[][] house, double playerZ, double playerX)
  {
    int sX = (int) (model.getTranslateZ() / Game.TILE_SIZE); // starting X tile
    int sY = (int) (model.getTranslateX() / Game.TILE_SIZE); // starting Y tile

    int tX = (int) (playerZ / Game.TILE_SIZE); // target X tile
    int tY = (int) (playerX / Game.TILE_SIZE); // target Y tile

    if (Game.debug)
    {
      System.out.println(sX + " " + sY + "    " + tX + " " + tY);
    }

    pathfinder.init(new Point(sX, sY), new Point(tX, tY), house);
  }

  private void findNextAngle(HouseBuilder house)
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
