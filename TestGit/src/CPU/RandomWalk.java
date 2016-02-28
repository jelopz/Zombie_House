package CPU;

import java.util.Random;

import javafx.scene.shape.Cylinder;

public class RandomWalk extends Zombie
{
  private static Random rand;
  private double translation;

  public RandomWalk(int x, int y, Cylinder m)
  {
    super(x, y, m);
    rand = new Random();
  }

  @Override
  public void determineNextMove()
  {
    int z = rand.nextInt(4) + 1;

    switch (z) // number 1 through 4
    {
      case 1:
      {
        translation = super.model.getTranslateX() + 1;
        super.model.setTranslateX(translation);
        break;
      }
      case 2:
      {
        translation = super.model.getTranslateZ() + 1;
        super.model.setTranslateZ(translation);
        break;
      }
      case 3:
      {
        translation = super.model.getTranslateX() - 1;
        super.model.setTranslateX(translation);
        break;
      }
      default:
      {
        translation = super.model.getTranslateZ() - 1;
        super.model.setTranslateZ(translation);
        break;
      }
    }
  }

}
