package application;

public class CollisionMap
{

  public static boolean generateMap(double d, double speed, double cos, double sin, char[][] tiles, double TILE_SIZE,
      Xform playerXform)
  {
    double x = (playerXform.t.getTx() + TILE_SIZE / 2) / TILE_SIZE;
    double z = (playerXform.t.getTz() + TILE_SIZE / 2) / TILE_SIZE;

    if (Math.floor(x + .5) < tiles.length && Math.floor(z + .5) < tiles.length && Math.floor(x - .5) >= 0
        && Math.floor(z - .5) >= 0)
    {

      boolean point[] = new boolean[8];
      for (int i = 0; i < point.length; i++)
      {
        point[i] = false;
      }
      if (tiles[(int) Math.floor(x + .25)][(int) (Math.floor(z + .5))] == 'X')
      {
        point[0] = true;

      }
      if (tiles[(int) Math.floor(x - .25)][(int) (Math.floor(z + .5))] == 'X')
      {
        point[1] = true;

      }
      if (tiles[(int) Math.floor(x + .25)][(int) (Math.floor(z - .5))] == 'X')
      {
        point[2] = true;

      }
      if (tiles[(int) Math.floor(x - .25)][(int) (Math.floor(z - .5))] == 'X')
      {
        point[3] = true;

      }
      if (tiles[(int) Math.floor(x + .5)][(int) (Math.floor(z + .25))] == 'X')
      {
        point[4] = true;

      }
      if (tiles[(int) Math.floor(x + .5)][(int) (Math.floor(z - .25))] == 'X')
      {
        point[5] = true;

      }
      if (tiles[(int) Math.floor(x - .5)][(int) (Math.floor(z + .25))] == 'X')
      {
        point[6] = true;

      }
      if (tiles[(int) Math.floor(x - .5)][(int) (Math.floor(z - .25))] == 'X')
      {
        point[7] = true;

      }

      if (d == 'w')
      {
        if (point[0] == true || point[1] == true)
        {
          return true;
        }
      }
      if (d == 's')
      {
        if (point[2] == true || point[3])
        {
          return true;
        }

      }
      if (d == 'a')
      {
        if (point[6] == true || point[7] == true)
        {
          return true;
        }

      }
      if (d == 'd')
      {
        if (point[4] == true || point[5] == true)
        {
          return true;
        }

      }
      System.out.println((int) x + "   " + (int) z);
    }
    return false;

  }
}
