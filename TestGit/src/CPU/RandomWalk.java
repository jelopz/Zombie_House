package CPU;


import javafx.scene.Group;

public class RandomWalk extends Zombie
{
  private double translation;

  public RandomWalk(int x, int y, Group m)
  {
    super(x, y, m);
  }

  @Override
  public void determineNextMove()
  {
    int z = rand.nextInt(4) + 1;

    switch (z) // number 1 through 4
    {
      case 1:
      {
        translation = model.getTranslateX() + 1;
        model.setTranslateX(translation);
        break;
      }
      case 2:
      {
        translation = model.getTranslateZ() + 1;
        model.setTranslateZ(translation);
        break;
      }
      case 3:
      {
        translation = model.getTranslateX() - 1;
        model.setTranslateX(translation);
        break;
      }
      default:
      {
        translation = model.getTranslateZ() - 1;
        model.setTranslateZ(translation);
        break;
      }
    }
  }

}
