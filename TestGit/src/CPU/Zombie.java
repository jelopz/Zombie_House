package CPU;

import java.awt.Point;
import java.util.Random;
import Hitbox.Hitbox;
import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;

public abstract class Zombie
{
  protected final Point spawnPoint; // (x,y) coordinate of spawn Point
  protected Group model;
  protected static Random rand;
  protected Hitbox hitbox;
  private double radius;

  public Zombie(int x, int y, Group m)
  {
    spawnPoint = new Point(x, y);
    model = m;
    rand = new Random();
    hitbox = new Hitbox(model);
    radius = Game.TILE_SIZE / 4;
  }
  
  public boolean zombieCollision(Zombie z)
  {
    double zDistance = z.model.getTranslateZ() - model.getTranslateZ();
    double xDistance = z.model.getTranslateX() - model.getTranslateX();
    zDistance *= zDistance;
    xDistance *= xDistance;
    
    double distance = Math.sqrt(zDistance + xDistance);

    if(distance <= Game.TILE_SIZE / 2)
    {
      return true;
    }
    
    return false;
  }

  abstract public void determineNextMove(HouseBuilder house);
  
  abstract public void move(HouseBuilder house);

}
