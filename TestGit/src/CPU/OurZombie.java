/*
 * Zombie AI class.
 * 
 * Each zombie is either of type RandomWalk or LineWalk, denoted by the isRandom boolean.
 * 
 * Each zombie behaves exactly the same when it smells the player. Differences only exist
 * when the zombie is deciding when and where to move.
 */

package CPU;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import Hitbox.Hitbox;
import Pathfinding.Pathfinder;
import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import application.Game;
import javafx.scene.Group;

/**
 * Zombie AI class.
 * 
 * Each zombie is either of type RandomWalk or LineWalk, denoted by the isRandom
 * boolean.
 * 
 * Each zombie behaves exactly the same when it smells the player. Differences
 * only exist when the zombie is deciding when and where to move.
 */
public class OurZombie
{

  /** The zombie's model. */
  private Group model;

  /** The rand. */
  private static Random rand;

  /** The zombie's hitbox. */
  private Hitbox hitbox;

  /** The current heading for the Z axis */
  private double angleZ;

  /** The current heading for the X axis */
  private double angleX;

  /** Denotes if a zombie has run into a wall or another zombie */
  private boolean isStuck;

  /** Does the zombie smell the player or not? */
  private boolean smellsPlayer;

  /** Does the zombie currently have a header? */
  private boolean hasAngle;

  /** The zombie's pathfinder to check to see if the zombie is in smell range */
  private Pathfinder pathfinder;

  /** The current target tile. */
  private Tile currentTargetTile;

  /** The model's next position on the Z axis */
  private double translationZ;

  /** The model's next position on the X axis */
  private double translationX;

  /** The current path to the player, if in smell range. */
  private ArrayList<Tile> currentPath;

  /** Is the zombie a RandomWalk or LineWalk zombie? */
  private boolean isRandom;

  /**
   * Instantiates a new zombie.
   *
   * @param m
   *          the zombie's model
   * @param b
   *          the boolean to denote if the zombie is of type RandomWalk or not
   */
  public OurZombie(Group m, boolean b)
  {
    model = m;
    rand = new Random();
    hitbox = new Hitbox(model);
    isStuck = false;
    smellsPlayer = false;
    hasAngle = false;
    pathfinder = new Pathfinder();
    isRandom = b;
  }

  /**
   * Gets the model.
   *
   * @return the model
   */
  public Group getModel()
  {
    return model;
  }

  /**
   * First checks for the Euclidean distance between the zombie and the player.
   * If it's within 15 tiles, we check to see if the player is within smell
   * range. If the euclidean distance is greater than the zombie smell range, we
   * know that it has no chance of it actually being in range to smell.
   * 
   * We use pathfinder to check to see if there is a path within the smell range
   * to the player. If the zombie is within smell range, we set smellsPlayer to
   * true.
   * 
   * If the zombie smells the player, it automatically moves to the center of
   * it's current tile.
   * 
   * If the zombie is NOT within smell range, it proceeds with it's respective
   * types standard AI.
   * 
   * A RandomWalk chooses a new heading every update.
   * 
   * A LineWalk chooses a new heading every update IF it has no current heading.
   * Else, he continues on it's current heading.
   *
   * @param house
   *          the house, the current map
   * @param playerZ
   *          the player's z coordinate
   * @param playerX
   *          the player's x coordinate
   */

  public void determineNextMove(HouseBuilder house, double playerZ, double playerX)
  {
    double zZ = model.getTranslateZ();
    double zX = model.getTranslateX();
    double d = Pathfinder.findEucl(playerZ, playerX, zZ, zX);

    smellsPlayer = false;
    currentPath = null;

    if (d / Game.TILE_SIZE < 15)
    {
      smell(house.getMap(), playerZ, playerX);

      if (pathfinder.doesPathExist())
      {
        smellsPlayer = true;
        currentPath = pathfinder.getPath();
        if (Game.debug)
        {
          System.out.println("CLOSE ENOUGH TO SMELL green " + this);
        }
      }
    }

    if (smellsPlayer && currentPath != null)
    {
      if (Game.debug)
      {
        for (Tile q : currentPath)
        {
          System.out.print("( " + q.getX() + " , " + q.getY() + " ) , ");
        }
        System.out.println();
      }

      // centers model to the tile when smelling player
      double z = Math.floor((model.getTranslateZ() / Game.TILE_SIZE)) * Game.TILE_SIZE;
      double x = Math.floor((model.getTranslateX() / Game.TILE_SIZE)) * Game.TILE_SIZE;

      model.setTranslateZ(z);
      model.setTranslateX(x);

      findNextPath((int) (z / 56), (int) (x / 56));
      isStuck = false;
    }
    else if (isRandom) // randomWalk default AI
    {
      findNextAngle(house);
      isStuck = false;
    }
    else if (!hasAngle) // linewalk default AI
    {
      findNextAngle(house);
      hasAngle = true;
    }
  }

  /**
   * If the zombie smells the player, the zombie chases the player.
   * 
   * Else,
   * 
   * The zombie follows it's current heading.
   *
   * @param house
   *          the house, the current map
   */
  public void move(HouseBuilder house)
  {
    if (smellsPlayer)
    {
      chasePlayer(house);
    }
    else
    {

      translationZ = model.getTranslateZ() + angleZ;
      translationX = model.getTranslateX() + angleX;

      hitbox.updateBoundaryPoints(translationZ, translationX);

      if (!hitbox.isWallCollision(house))
      {

        for (OurZombie z : Game.zombies)
        {
          if ((!z.equals(this)) && zombieCollision(z))
          {
            if (isRandom)
            {
              isStuck = true;
            }
            else
            {
              hasAngle = false;
            }
            model.setTranslateZ(translationZ - 2 * angleZ);
            model.setTranslateX(translationX - 2 * angleX);
          }
        }
        if (isRandom)
        {
          if (!isStuck)
          {
            model.setTranslateZ(translationZ);
            model.setTranslateX(translationX);
          }
        }
        else
        {
          if (hasAngle)
          {
            model.setTranslateZ(translationZ);
            model.setTranslateX(translationX);
          }
        }
      }
      else
      {
        hasAngle = false;
      }
    }
  }

  /**
   * Checks to see if this zombie is colliding with the given zombie. If there
   * is, both zombies test back so they don't get stuck inside eachother
   *
   * @param z
   *          A zombie we are testing with this zombie.
   * @return true, if a collision occurs.
   */

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

  /**
   * Using the currentPath variable given by the pathfinder, the zombie follows
   * the path, tile to tile, that leads towards the player location.
   *
   * @param house
   *          the house, the current map
   */

  private void chasePlayer(HouseBuilder house)
  {
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

  /**
   * Uses the Zombie's pathfinder to determine if there exists a path to the
   * player within the smell range.
   * 
   * After calling this method, call pathfinder.doesPathExist() to determine if
   * a path does or does not exist.
   *
   * @param house
   *          the house, the current map
   * @param playerZ
   *          the player's current z coordinate
   * @param playerX
   *          the player's current x coordinate
   */
  private void smell(Tile[][] house, double playerZ, double playerX)
  {
    int sX = (int) (model.getTranslateZ() / Game.TILE_SIZE); // starting X tile
    int sY = (int) (model.getTranslateX() / Game.TILE_SIZE); // starting Y tile

    int tX = (int) (playerZ / Game.TILE_SIZE); // target X tile
    int tY = (int) (playerX / Game.TILE_SIZE); // target Y tile

    if (tX == 0)
    {
      tX = 1;
    }
    else if (tY == 0)
    {
      tY = 1;
    }
    else if (tX == 49)
    {
      tX = 48;
    }
    else if (tY == 49)
    {
      tY = 48;
    }
    if (Game.debug)
    {
      System.out.println(sX + " " + sY + "    " + tX + " " + tY);
    }

    pathfinder.init(new Point(sX, sY), new Point(tX, tY), house);
  }

  /**
   * Determines a heading randomly.
   *
   * @param house
   *          the house, the current map
   */
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

  /**
   * Used in conjunction with the chasePlayer method. Since a path is a list
   * from tile to tile, once the zombie reaches a tile it needs the direction to
   * the next one.
   * 
   * findNextPath removes the next tile in the path and sets it to
   * currentTargetTile. if currentPath is empty, that means the zombie reached
   * where it smelled the player in the last decision update.
   *
   * @param x
   *          the model's current x coordinate
   * @param y
   *          the model's current y coordinate
   */
  private void findNextPath(int x, int y)
  {
    if (!currentPath.isEmpty())
    {
      currentTargetTile = currentPath.get(currentPath.size() - 1);
      currentPath.remove(currentPath.size() - 1);
    }
    if (x == currentTargetTile.getX())
    {
      if (y < currentTargetTile.getY())
      {
        angleZ = 0;
        angleX = 1;
      }
      else
      {
        angleZ = 0;
        angleX = -1;
      }
    }
    else
    {
      if (x < currentTargetTile.getX())
      {
        angleZ = 1;
        angleX = 0;
      }
      else
      {
        angleZ = -1;
        angleX = 0;
      }
    }
  }

}
