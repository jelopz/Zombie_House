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

  
//  @Override
//  public void determineNextMove(HouseBuilder house)
//  {
//    int z = rand.nextInt(4) + 1;
//
//    switch (z) // number 1 through 4
//    {
//      case 1:
//      {
//        translation = model.getTranslateX() + 1;
//        hitbox.updateBoundaryPoints(model.getTranslateZ(), translation);
//
//        // tests if the next move will cause a collision
////        if (!Hitbox.isCollision(house, hitbox))
//        if(!hitbox.isWallCollision(house))
//        {
//          model.setTranslateX(translation);
//        }
//        break;
//      }
//      case 2:
//      {
//        translation = model.getTranslateZ() + 1;
//        hitbox.updateBoundaryPoints(translation, model.getTranslateX());
//
//        if(!hitbox.isWallCollision(house))
//        {
//          model.setTranslateZ(translation);
//        }
//        break;
//      }
//      case 3:
//      {
//        translation = model.getTranslateX() - 1;
//        hitbox.updateBoundaryPoints(model.getTranslateZ(), translation);
//
//        if(!hitbox.isWallCollision(house))
//        {
//          model.setTranslateX(translation);
//        }
//        break;
//      }
//      default:
//      {
//        translation = model.getTranslateZ() - 1;
//        hitbox.updateBoundaryPoints(translation, model.getTranslateX());
//
//        if(!hitbox.isWallCollision(house))
//        {
//          model.setTranslateZ(translation);
//        }
//        break;
//      }
//    }
//  }

	public void move(HouseBuilder house) {
		translationZ = model.getTranslateZ() + angleZ;
		translationX = model.getTranslateX() + angleX;

		hitbox.updateBoundaryPoints(translationZ, translationX);

		if (!hitbox.isWallCollision(house)) {
			
	          model.setTranslateZ(translationZ);
	          model.setTranslateX(translationX);

//			for (Zombie z : Game.zombies) {
//				if ((!z.equals(this)) && zombieCollision(z)) {
//					model.setTranslateZ(translationZ - 2 * angleZ);
//					model.setTranslateX(translationX - 2 * angleX);
//				}
//			}
		}
	}

}
