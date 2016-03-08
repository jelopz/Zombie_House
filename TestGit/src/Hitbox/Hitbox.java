/**
 * Hitbox class is used by the player and the zombies to determine if they are colliding against walls, zombies, or the player.
 * Also used to determine if the player reaches the end point
 * 
 * The hitbox class itself consists of 8 points, for the invisible hexagonal hitbox surrounding zombies and the player.
 * If any of these points hit something, a collision occurs and the player/zombie stops moving.
 */

package Hitbox;

import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * Hitbox class is used by the player and the zombies to determine if they are
 * colliding against walls, zombies, or the player. Also used to determine if
 * the player reaches the end point
 * 
 * The hitbox class itself consists of 8 points, for the invisible hexagonal
 * hitbox surrounding zombies and the player. If any of these points hit
 * something, a collision occurs and the player/zombie stops moving.
 */
public class Hitbox
{

  /** The 8 points on the octogon shaped hitbox */
  private Point[] points;

  /**
   * Instantiates a new hitbox.
   *
   * @param xform
   *          the xform to add the hitbox to.
   */
  public Hitbox(Group xform)
  {
    points = new Point[8];
    generateHitbox(xform, Game.debug);
  }

  /**
   * Returns the point from the given index.
   *
   * @param i
   *          one of the 8 points that surround the hitbox.
   * @return the point, if it is a valid index, else, returns null.
   */

  public Point getPoint(int i)
  {
    if (i >= 0 && i <= 7)
    {
      return points[i];
    }
    return null;
  }

  /**
   * Updates the 8 points on the collision detecting octogon based on the
   * players location
   *
   * @param nextZ
   *          the next Z position on the map for the model.
   * @param nextX
   *          the next X position on the map for the model
   */

  public void updateBoundaryPoints(double nextZ, double nextX)
  {
    // I tried to simplify this further but apparently I don't know
    // how to do algebra and broke it so I'm leaving it like this
    // for now

    points[0].x = (nextX + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[0].z = (nextZ + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[1].x = (nextX + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[1].z = (nextZ + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[2].x = (nextX + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[2].z = (nextZ - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[3].x = (nextX + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[3].z = (nextZ - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[4].x = (nextX - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[4].z = (nextZ - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[5].x = (nextX - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[5].z = (nextZ - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[6].x = (nextX - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[6].z = (nextZ + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[7].x = (nextX - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[7].z = (nextZ + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
  }

  /**
   * Takes a look at each point on the octogon and determines what tile it's on.
   * it then checks to see if that tile is a legal tile to be on.
   *
   * @param house
   *          the house
   * @return true, if a point on the hitbox is colliding with a wall
   */
  public boolean isWallCollision(HouseBuilder house)
  {
    int x, y;
    for (int i = 0; i < 8; i++) // get what tile the point is on.
    {

      x = (int) (points[i].z / Game.TILE_SIZE); // x
      y = (int) (points[i].x / Game.TILE_SIZE); // y

      if (!house.isPointLegal(x, y)) // is that tile not a legal move?
      {
        return true; // the point is colliding with something
      }
    }

    return false;
  }

  /**
   * Checks if any of the points has reached the end point tile
   *
   * @param house
   *          Our current house/map
   * @return true, if a point has reached the end point
   */

  public boolean hasReachedGoal(HouseBuilder house)
  {
    int x, y;
    for (int i = 0; i < 8; i++) // get what tile the point is on.
    {

      x = (int) (points[i].z / Game.TILE_SIZE); // z //x
      y = (int) (points[i].x / Game.TILE_SIZE); // x //y

      if (house.isEndPoint(x, y))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Generates a set of 8 points in the shape of an octogon to create a hitbox
   * for the player. A valid move is defined as a location where none of these 8
   * points are on a Wall tile.
   * 
   * Passing in the appropriate xform is only necessary for creating the
   * graphical representation of the hitbox
   *
   * @param xform
   *          the xform we are giving a hitbox
   * @param debug
   *          debug variable for testing purposes
   */

  private void generateHitbox(Group xform, boolean debug)
  {
    double z = 0;
    double x = 0;
    PhongMaterial pathable = null;

    if (debug)
    {
      pathable = new PhongMaterial();
      pathable.setDiffuseColor(Color.WHITE);
      pathable.setSpecularColor(Color.ORANGE);
    }
    for (int t = 0; t < 8; t++)
    {
      Box newBox = null;

      if (debug)
      {
        newBox = new Box(1, 100, 1);
        newBox.setMaterial(pathable);
        newBox.setTranslateY(Game.WALL_HEIGHT / 2);
      }

      if (t == 0)
      {
        x = (0 + Game.TILE_SIZE / 4) / 2;
        z = (0 + Game.TILE_SIZE / 2) / 2;

        points[0] = new Point(z, x);
      }
      if (t == 1)
      {
        x = (0 + Game.TILE_SIZE / 2) / 2;
        z = (0 + Game.TILE_SIZE / 4) / 2;

        points[1] = new Point(z, x);
      }
      if (t == 2)
      {
        x = (0 + Game.TILE_SIZE / 2) / 2;
        z = (0 - Game.TILE_SIZE / 4) / 2;

        points[2] = new Point(z, x);
      }
      if (t == 3)
      {
        x = (0 + Game.TILE_SIZE / 4) / 2;
        z = (0 - Game.TILE_SIZE / 2) / 2;

        points[3] = new Point(z, x);
      }
      if (t == 4)
      {
        x = (0 - Game.TILE_SIZE / 4) / 2;
        z = (0 - Game.TILE_SIZE / 2) / 2;

        points[4] = new Point(z, x);
      }
      if (t == 5)
      {
        x = (0 - Game.TILE_SIZE / 2) / 2;
        z = (0 - Game.TILE_SIZE / 4) / 2;

        points[5] = new Point(z, x);

      }
      if (t == 6)
      {
        x = (0 - Game.TILE_SIZE / 2) / 2;
        z = (0 + Game.TILE_SIZE / 4) / 2;

        points[6] = new Point(z, x);

      }
      if (t == 7)
      {
        x = (0 - Game.TILE_SIZE / 4) / 2;
        z = (0 + Game.TILE_SIZE / 2) / 2;

        points[7] = new Point(z, x);
      }
      if (debug)
      {
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
        xform.getChildren().add(newBox);
      }
    }
  }
}
