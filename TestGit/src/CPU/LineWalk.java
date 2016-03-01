package CPU;

import RoomGenerator.RoomGenerator;
import javafx.scene.Group;

public class LineWalk extends Zombie
{
  private double angleZ, angleX;
  private double translationZ, translationX;
  private boolean hasAngle; // has an angle he is currently walking in

  public LineWalk(int x, int y, Group m)
  {
    super(x, y, m);
    hasAngle = false;
  }

  @Override
  public void determineNextMove(RoomGenerator house)
  {
    if(hasAngle)
    {
      translationZ = model.getTranslateZ() + angleZ;
      translationX = model.getTranslateX() + angleX;
      
      hitbox.updateBoundaryPoints(translationZ, translationX);
      
      if(!hitbox.isCollision(house))
      {
        model.setTranslateZ(translationZ);
        model.setTranslateX(translationX);
      }
      else
      {
        hasAngle = false;
      }
    }
    else
    {
      findNextAngle(house);
    }

  }

  private void findNextAngle(RoomGenerator house)
  {
    angleZ = rand.nextDouble();
    angleX = Math.sqrt(1 - (angleZ * angleZ));

    if (rand.nextInt(1) == 0) // 50/50 chance of x being positive or negative
    {
      System.out.println(0);
      angleZ = -1 * angleZ;
    }
    else
    {
      System.out.println(1);
    }

    if (rand.nextInt(1) == 0) // 50/50 chance of y being positive or negative
    {
      angleX = -1 * angleX;
    }
    
    hasAngle = true;
    determineNextMove(house);
  }
}
