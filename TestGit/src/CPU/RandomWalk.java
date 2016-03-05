package CPU;

import Hitbox.Hitbox;
import RoomGenerator.HouseBuilder;
import application.Game;
import javafx.scene.Group;

public class RandomWalk extends Zombie
{
  private double angleZ, angleX;
  private double translationZ, translationX;

  public RandomWalk(int x, int y, Group m)
  {
    super(x, y, m);
  }


	public void determineNextMove(HouseBuilder house)
	{
		findNextAngle(house);
		isStuck = false;
	}
	
	private void findNextAngle(HouseBuilder house) {
		angleZ = rand.nextDouble();
		angleX = Math.sqrt(1 - (angleZ * angleZ));

		if (rand.nextInt(2) == 0) // 50/50 chance of x being positive or
									// negative
		{
			angleZ = -1 * angleZ;
		}

		if (rand.nextInt(2) == 0) // 50/50 chance of y being positive or
									// negative
		{
			angleX = -1 * angleX;
		}
	}

	public void move(HouseBuilder house) {
		translationZ = model.getTranslateZ() + angleZ;
		translationX = model.getTranslateX() + angleX;

		hitbox.updateBoundaryPoints(translationZ, translationX);

		if (!hitbox.isWallCollision(house)) {
			
	        for (Zombie z : Game.zombies)
	        {
	          if ((!z.equals(this)) && zombieCollision(z))
	          {
	            isStuck = true;
	            model.setTranslateZ(translationZ - 2 * angleZ);
	            model.setTranslateX(translationX - 2 * angleX);
	          }
	        }
			
	        if(!isStuck)
	        {
	          model.setTranslateZ(translationZ);
	          model.setTranslateX(translationX);
	        }
//			for (Zombie z : Game.zombies) {
//				if ((!z.equals(this)) && zombieCollision(z)) {
//					model.setTranslateZ(translationZ - 2 * angleZ);
//					model.setTranslateX(translationX - 2 * angleX);
//				}
//			}
		}
	}

}
