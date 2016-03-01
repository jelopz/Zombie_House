package Hitbox;

import application.Game;
import application.Xform;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Hitbox
{
  private Point[] points;
  
  public Hitbox(Xform xform)
  {
    points = new Point[8];
    generateHitbox(xform);
  }
  
  public Point getPoint(int i)
  {
    return points[i];
  }
  
  /*
   * Updates the 8 points on the collision detecting octogon based on the
   * players location
   */
  public void updateBoundaryPoints(double nextZ, double nextX)
  {

    // updates the points on the octogon

    // I tried to simplify this further but apparently I don't know
    // how to do algebra and broke it so I'm leaving it like this
    // for now

    points[0].x = (nextX + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[0].z = (nextZ + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[1].x = (nextX + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[1].z = (nextZ + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[2].x = (nextX + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[2].z = (nextZ - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[3].x = (nextX + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[3].z = (nextZ - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[4].x = (nextX - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[4].z = (nextZ - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));

    points[5].x = (nextX - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[5].z = (nextZ - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[6].x = (nextX - Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
    points[6].z = (nextZ + Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));

    points[7].x = (nextX - Game.TILE_SIZE / 8 + (Game.TILE_SIZE / 2));
    points[7].z = (nextZ + Game.TILE_SIZE / 4 + (Game.TILE_SIZE / 2));
  }
  
  private void generateHitbox(Xform xform)
  {
    double z, x;

    PhongMaterial pathable = new PhongMaterial();
    pathable.setDiffuseColor(Color.WHITE);
    pathable.setSpecularColor(Color.ORANGE);

    for (int t = 0; t < 8; t++)
    {
      Box newBox = new Box(1, 100, 1);
      newBox.setMaterial(pathable);
      newBox.setTranslateY(Game.WALL_HEIGHT / 2);

      if (t == 0)
      {
        x = (0 + Game.TILE_SIZE / 4) / 2;
        z = (0 + Game.TILE_SIZE / 2) / 2;
        
        points[0] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 1)
      {
        x = (0 + Game.TILE_SIZE / 2) / 2;
        z = (0 + Game.TILE_SIZE / 4) / 2;

        points[1] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 2)
      {
        x = (0 + Game.TILE_SIZE / 2) / 2;
        z = (0 - Game.TILE_SIZE / 4) / 2;
        
        points[2] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 3)
      {
        x = (0 + Game.TILE_SIZE / 4) / 2;
        z = (0 - Game.TILE_SIZE / 2) / 2;

        points[3] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 4)
      {
        x = (0 - Game.TILE_SIZE / 4) / 2;
        z = (0 - Game.TILE_SIZE / 2) / 2;

        points[4] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 5)
      {
        x = (0 - Game.TILE_SIZE / 2) / 2;
        z = (0 - Game.TILE_SIZE / 4) / 2;

        points[5] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 6)
      {
        x = (0 - Game.TILE_SIZE / 2) / 2;
        z = (0 + Game.TILE_SIZE / 4) / 2;

        points[6] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      if (t == 7)
      {
        x = (0 - Game.TILE_SIZE / 4) / 2;
        z = (0 + Game.TILE_SIZE / 2) / 2;

        points[7] = new Point(z,x);
        
        newBox.setTranslateX(x);
        newBox.setTranslateZ(z);
      }
      xform.getChildren().add(newBox);
    }
  }
}