package CPU;

import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;

public class LineWalk extends Zombie
{
  private double translationZ, translationX;
  private boolean hasAngle; // has an angle he is currently walking in

  public LineWalk(int x, int y, Group m)
  {
    super(x, y, m);
    hasAngle = false;
  }

  @Override
  public void determineNextMove(HouseBuilder house)
  {
    if (!hasAngle) //has nowhere to go
    {
      super.findNextAngle(house);
      hasAngle = true;
    }

  }

//  private void findNextAngle(HouseBuilder house)
//  {
//    angleZ = rand.nextDouble();
//    angleX = Math.sqrt(1 - (angleZ * angleZ));
//
//    if (rand.nextInt(2) == 0) // 50/50 chance of x being positive or negative
//    {
//      angleZ = -1 * angleZ;
//    }
//
//    if (rand.nextInt(2) == 0) // 50/50 chance of y being positive or negative
//    {
//      angleX = -1 * angleX;
//    }
//  }

	public void move(HouseBuilder house) { //move based on current heading
      translationZ = model.getTranslateZ() + angleZ;
      translationX = model.getTranslateX() + angleX;

      hitbox.updateBoundaryPoints(translationZ, translationX);

      if (!hitbox.isWallCollision(house))
      {

        for (Zombie z : Game.zombies)
        {
          if ((!z.equals(this)) && zombieCollision(z))
          {
            hasAngle = false;

            model.setTranslateZ(translationZ - 2 * angleZ);
            model.setTranslateX(translationX - 2 * angleX);
          }
        }

        if (hasAngle)
        {
          model.setTranslateZ(translationZ);
          model.setTranslateX(translationX);
        }
      }
      else
      {
        hasAngle = false;
      }
	}
}