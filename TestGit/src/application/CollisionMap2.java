package application;

public class CollisionMap2
{
  public static boolean generateMap(double d, double speed, double cos, double sin, 
      char[][] tiles, double TILE_SIZE, Xform cameraXform)
  {
    System.out.println((int) ((cameraXform.t.getTx() - (speed * sin))/TILE_SIZE));
    if(tiles[(int) (((cameraXform.t.getTx() - (speed * sin))/TILE_SIZE)+.5)][(int) (((cameraXform.t.getTz() - (speed * cos))/TILE_SIZE)+.5)] == 'X')
      {
//      System.out.println("Wall");
      return true;
      }
    return false;
  }
}
