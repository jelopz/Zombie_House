/*
 * ZombieBuilder imports a .stl model
 * Method: getZombie returns a group consisting of the triangle
 * mesh generated from the .stl file
 * */

package ZombieBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import application.Game;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class ZombieBuilder
{
  private static final double MODEL_SCALE_FACTOR = 1;

  static MeshView[] loadMeshViews()
  {
    //load file
    URL in = Game.class.getResource("Zombie.stl");
    File file = null;
    try
    {
      file = new File(in.toURI());
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    //imports as triangle mesh
    StlMeshImporter importer = new StlMeshImporter();
    importer.read(file);
    Mesh mesh = importer.getImport();

    return new MeshView[] { new MeshView(mesh) };
  }

  public static Group getZombie(int i, int j, double TILE_SIZE, char zombieType)
  {
	PhongMaterial zombieMaterial;
    MeshView[] meshViews = loadMeshViews();
    for (int t = 0; t < meshViews.length; t++)
    {
      meshViews[t].setTranslateX(i);
      meshViews[t].setTranslateZ(j);
      meshViews[t].setTranslateY(.5);
      meshViews[t].setScaleX(MODEL_SCALE_FACTOR);
      meshViews[t].setScaleY(MODEL_SCALE_FACTOR);
      meshViews[t].setScaleZ(MODEL_SCALE_FACTOR);

      if(zombieType == 'R')
      {
        zombieMaterial = new PhongMaterial(Color.DARKGREEN);
        zombieMaterial.setSpecularColor(Color.LIGHTGREEN);
      }
      else //zombieType == 'L'
      {
        zombieMaterial = new PhongMaterial(Color.DARKRED);
        zombieMaterial.setSpecularColor(Color.RED);
      }
      zombieMaterial.setSpecularPower(16);
      meshViews[t].setMaterial(zombieMaterial);

      meshViews[t].getTransforms().setAll(new Rotate(90, Rotate.X_AXIS));
    }
   
    return  new Group(meshViews);
  }
}
