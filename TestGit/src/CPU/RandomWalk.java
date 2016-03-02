package CPU;

import Hitbox.Hitbox;
import RoomGenerator.RoomGenerator;
import javafx.scene.Group;

public class RandomWalk extends Zombie
{
  private double translation;

  public RandomWalk(int x, int y, Group m)
  {
    super(x, y, m);
  }

  @Override
  public void determineNextMove(RoomGenerator house)
  {
    int z = rand.nextInt(4) + 1;

    switch (z) // number 1 through 4
    {
      case 1:
      {
        translation = model.getTranslateX() + 1;
        hitbox.updateBoundaryPoints(model.getTranslateZ(), translation);

        // tests if the next move will cause a collision
//        if (!Hitbox.isCollision(house, hitbox))
        if(!hitbox.isWallCollision(house))
        {
          model.setTranslateX(translation);
        }
        break;
      }
      case 2:
      {
        translation = model.getTranslateZ() + 1;
        hitbox.updateBoundaryPoints(translation, model.getTranslateX());

        if(!hitbox.isWallCollision(house))
        {
          model.setTranslateZ(translation);
        }
        break;
      }
      case 3:
      {
        translation = model.getTranslateX() - 1;
        hitbox.updateBoundaryPoints(model.getTranslateZ(), translation);

        if(!hitbox.isWallCollision(house))
        {
          model.setTranslateX(translation);
        }
        break;
      }
      default:
      {
        translation = model.getTranslateZ() - 1;
        hitbox.updateBoundaryPoints(translation, model.getTranslateX());

        if(!hitbox.isWallCollision(house))
        {
          model.setTranslateZ(translation);
        }
        break;
      }
    }
  }

}
