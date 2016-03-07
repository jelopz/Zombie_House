package CPU;

import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;

public class RandomWalk extends Zombie
{
  private double translationZ, translationX;

  public RandomWalk(int x, int y, Group m)
  {
    super(x, y, m);
  }

  public void determineNextMove(HouseBuilder house, double playerZ, double playerX)
  {
    double d = pathfinder.findEucl(playerZ, playerX, model.getTranslateZ(), model.getTranslateX());
    
    if(d/Game.TILE_SIZE < 15)
    {
      System.out.println("CLOSE ENOUGH TO MAYBE SMELL");
    }
    
    findNextAngle(house);
    isStuck = false;
  }

  public void move(HouseBuilder house)
  {
    translationZ = model.getTranslateZ() + angleZ;
    translationX = model.getTranslateX() + angleX;

    hitbox.updateBoundaryPoints(translationZ, translationX);

    if (!hitbox.isWallCollision(house))
    {

      for (Zombie z : Game.zombies)
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

}
