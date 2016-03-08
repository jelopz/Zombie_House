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

/**
 * The Class ZombieBuilder.
 */
public class ZombieBuilder
{
  
  /** The Constant MODEL_SCALE_FACTOR. */
  private static final double MODEL_SCALE_FACTOR = 1;

  /**
   * Load mesh views.
   *
   * @return the mesh view[]
   */
  static MeshView[] loadMeshViews()
  {
    // load file
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
    // imports as triangle mesh
    StlMeshImporter importer = new StlMeshImporter();
    importer.read(file);
    Mesh mesh = importer.getImport();

    return new MeshView[]
    { new MeshView(mesh) };
  }

  /**
   * Gets the zombie.
   *
   * @param i the i
   * @param j the j
   * @param TILE_SIZE the tile size
   * @param zombieType the zombie type
   * @return the zombie
   */
  public static Group getZombie(int i, int j, double TILE_SIZE, char zombieType)
  {
    PhongMaterial zombieMaterial;
    MeshView[] meshViews = loadMeshViews();
    for (int t = 0; t < meshViews.length; t++)
    {
      meshViews[t].setTranslateX(3 * TILE_SIZE / 4);
      // meshViews[t].setTranslateZ(j - TILE_SIZE/2);
      // meshViews[t].setTranslateY(.5);
      meshViews[t].setScaleX(MODEL_SCALE_FACTOR);
      meshViews[t].setScaleY(MODEL_SCALE_FACTOR);
      meshViews[t].setScaleZ(MODEL_SCALE_FACTOR);

      if (zombieType == 'R')
      {
        zombieMaterial = new PhongMaterial(Color.DARKGREEN);
        zombieMaterial.setSpecularColor(Color.LIGHTGREEN);
      }
      else // zombieType == 'L'
      {
        zombieMaterial = new PhongMaterial(Color.DARKRED);
        zombieMaterial.setSpecularColor(Color.RED);
      }
      zombieMaterial.setSpecularPower(16);
      meshViews[t].setMaterial(zombieMaterial);

      meshViews[t].getTransforms().setAll(new Rotate(90, Rotate.X_AXIS));
    }

    return new Group(meshViews);
  }
}
