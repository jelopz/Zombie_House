//package CPU;
//
//import java.awt.Point;
//import java.util.Random;
//import Hitbox.Hitbox;
//import Pathfinding.Path;
//import Pathfinding.Pathfinder;
//import RoomGenerator.HouseBuilder;
//import RoomGenerator.Tile;
//import application.Game;
//import javafx.scene.Group;
//
//public abstract class Zombie
//{
//  protected final Point spawnPoint; // (x,y) coordinate of spawn Point
//  protected Group model;
//  protected static Random rand;
//  protected Hitbox hitbox;
//  protected double angleZ, angleX;
//  protected boolean isStuck;
//  protected boolean smellsPlayer;
//  protected Pathfinder pathfinder;
//
//  protected Path currentPath;
//
//  private double radius;
//
//  public Zombie(int x, int y, Group m)
//  {
//    spawnPoint = new Point(x, y);
//    model = m;
//    rand = new Random();
//    hitbox = new Hitbox(model);
//    radius = Game.TILE_SIZE / 4;
//    isStuck = false;
//    smellsPlayer = false;
//    pathfinder = new Pathfinder();
//  }
//
//  public boolean zombieCollision(Zombie z)
//  {
//    double zDistance = z.model.getTranslateZ() - model.getTranslateZ();
//    double xDistance = z.model.getTranslateX() - model.getTranslateX();
//    zDistance *= zDistance;
//    xDistance *= xDistance;
//
//    double distance = Math.sqrt(zDistance + xDistance);
//
//    if (distance <= Game.TILE_SIZE / 2)
//    {
//      return true;
//    }
//
//    return false;
//  }
//
//  abstract public void determineNextMove(HouseBuilder house, double playerZ, double playerX);
//
//  abstract public void move(HouseBuilder house);
//
//  protected void smell(Tile[][] house, double playerZ, double playerX)
//  {
//    int sX = (int) (model.getTranslateZ() / Game.TILE_SIZE); // starting X tile
//    int sY = (int) (model.getTranslateX() / Game.TILE_SIZE); // starting Y tile
//
//    int tX = (int) (playerZ / Game.TILE_SIZE); // target X tile
//    int tY = (int) (playerX / Game.TILE_SIZE); // target Y tile
//
//    pathfinder.init(new Point(sX,sY), new Point(tX, tY), house);
//  }
//
//  protected void findNextAngle(HouseBuilder house)
//  {
//    angleZ = rand.nextDouble();
//    angleX = Math.sqrt(1 - (angleZ * angleZ));
//
//    if (rand.nextInt(2) == 0) // 50/50 chance of x being positive or
//    // negative
//    {
//      angleZ = -1 * angleZ;
//    }
//
//    if (rand.nextInt(2) == 0) // 50/50 chance of y being positive or
//    // negative
//    {
//      angleX = -1 * angleX;
//    }
//  }
//
//}
