//package application;
//
//public class CollisionMap
//{
//
//  public static boolean generateMap(double d, double speed, double cos, double sin, char[][] tiles, double TILE_SIZE,
//      Xform cameraXform)
//  {
////  double x = Math.floor(((cameraXform.t.getTx() /*+ (speed * sin)*/)+(TILE_SIZE/2))/TILE_SIZE);
////  double z = Math.floor(((cameraXform.t.getTz() /*+ (speed * cos)*/)+(TILE_SIZE/2))/TILE_SIZE);
//    
//    double angle = cameraXform.ry.getAngle()/45;
//    double anglePlus = angle+1;
//    double angleMin = angle-1;
//    if(anglePlus > 7) anglePlus = 0;
//    if(angleMin <0) angleMin = 7;
//    double x = (cameraXform.t.getTx() + TILE_SIZE / 2) / TILE_SIZE;
//    double z = (cameraXform.t.getTz() + TILE_SIZE / 2) / TILE_SIZE;
//    if (Math.floor(x + .5) < tiles.length && Math.floor(z + .5) < tiles.length && Math.floor(x - .5) >= 0
//        && Math.floor(z - .5) >= 0)
//    {
//
//      boolean point[] = new boolean[8];
//      for (int i = 0; i < point.length; i++)
//      {
//        point[i] = false;
//      }
//      if (tiles[(int) Math.floor(x + .25)][(int) (Math.floor(z + .5))] == 'X')
//      {
//        point[0] = true;
//
//      }
//      if (tiles[(int) Math.floor(x - .25)][(int) (Math.floor(z + .5))] == 'X')
//      {
//        point[1] = true;
//
//      }
//      if (tiles[(int) Math.floor(x + .25)][(int) (Math.floor(z - .5))] == 'X')
//      {
//        point[2] = true;
//
//      }
//      if (tiles[(int) Math.floor(x - .25)][(int) (Math.floor(z - .5))] == 'X')
//      {
//        point[3] = true;
//
//      }
//      if (tiles[(int) Math.floor(x + .5)][(int) (Math.floor(z + .25))] == 'X')
//      {
//        point[4] = true;
//
//      }
//      if (tiles[(int) Math.floor(x + .5)][(int) (Math.floor(z - .25))] == 'X')
//      {
//        point[5] = true;
//
//      }
//      if (tiles[(int) Math.floor(x - .5)][(int) (Math.floor(z + .25))] == 'X')
//      {
//        point[6] = true;
//
//      }
//      if (tiles[(int) Math.floor(x - .5)][(int) (Math.floor(z - .25))] == 'X')
//      {
//        point[7] = true;
//
//      }
//
//      if (d == 'w')
//      {
////        0 -45
////        45 -90
////        90 -135
////        135-180
////        180 -225
////        225 - 270
////        270 - 315
////        315 - 360
//        
////        System.out.println((int) angle + "   " + (int) (anglePlus));
//        if (point[(int) angleMin] == true || point[(int) angle] == true /*|| point[(int) (anglePlus)] == true*/)
//        {
//          return true;
//        }
//      }
//      if (d == 's')
//      {
//        if (point[2] == true || point[3])
//        {
//          return true;
//        }
//
//      }
//      if (d == 'a')
//      {
//        if (point[6] == true || point[7] == true)
//        {
//          return true;
//        }
//
//      }
//      if (d == 'd')
//      {
//        if (point[4] == true || point[5] == true)
//        {
//          return true;
//        }
//
//      }
////      System.out.println((int) x + "   " + (int) z);
//    }
//    return false;
//
//  }
//}
