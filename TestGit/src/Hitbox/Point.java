/**
 * Helper class for Hitbox.java
 * A point class made specifically for our program using doubles instead of ints and with variables z and x
 */
package Hitbox;

/**
 * Helper class for Hitbox.java
 * A point class made specifically for our program using doubles instead of ints and with variables z and x
 */
public class Point
{
  
  /** The z value for the point in the hitbox. */
  public double z;
  
  /** The x value for the point in the hitbox. */
  public double x;

  /**
   * Instantiates a new point.
   *
   * @param z the z value for our hitbox
   * @param x the x value for our hitbox
   */
  public Point(double z, double x)
  {
    this.z = z;
    this.x = x;
  }
}
