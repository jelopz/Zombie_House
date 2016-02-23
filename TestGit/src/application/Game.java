/*****************************************
 * Main Game Class
 * Methods:
 * -makeCamera
 *    generates the perspective camera and places it in initial position
 * -makeLight
 *    generates a pointSourceLight and pairs it with the cameraXform
 * - drawMap
 *    uses 2d Char array generated from RoomGenerator class to draw wall and floor tiles
 * -handleMouse
 *    primary button and drag = rotate the camera along the Y axis
 *    center button and drag = raise and lower camera along the Y axis
 *    secondary button and drag = no effect yet
 * -handleKeyboard
 *    'w' sets boolean front true while held
 *    's' sets boolean back true while held
 *    'a' sets boolean left true while held
 *    'd' sets boolean right true while held
 * Sub-Classes
 * -MainGameLoop
 *    translates map relative to the camera angle based on front,back,left, and right booleans.*/

package application;

import RoomGenerator.RoomGenerator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application
{

  private static final double TILE_SIZE = 10; // number of subdivisions in
  // tile
  private static final double WALL_HEIGHT = 16;
  private static final double CAMERA_INITIAL_DISTANCE = 0;
  private static final double CAMERA_INITIAL_X_ANGLE = 0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double CONTROL_MULTIPLIER = 0.1;
  private static final double SHIFT_MULTIPLIER = 10.0;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 2.0;
  private static final double TRACK_SPEED = 0.3;

  private double scaleVal = 1;

  private final Group root = new Group();
  private final Xform world = new Xform();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);
  private final PointLight light = new PointLight(Color.WHITE);
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();

  private final Xform lightXform = new Xform();
  private final Xform lightXform2 = new Xform();
  private final Xform lightXform3 = new Xform();
  private final Group lightGroup = new Group();

  private Xform mapXform = new Xform();

  private char[][] tiles;
  private int mapH = 36;
  private int mapW = 36;

  private double windowX = 1024;
  private double windowY = 768;
  private boolean front = false;
  private boolean back = false;
  private boolean left = false;
  private boolean right = false;
  private double speed = .5;

  private double mousePosX;
  private double mousePosY;
  private double mouseOldX;
  private double mouseOldY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  private void buildCamera()
  {
    root.getChildren().add(cameraXform);
    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);// sets y up

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    cameraXform.setTranslateY(WALL_HEIGHT / 2);
  }

  private void buildLight()
  {

    light.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    lightXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    lightXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

    light.setTranslateX(CAMERA_INITIAL_X_ANGLE);
    light.setTranslateY(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.getChildren().add(lightXform);// add light to camera so they
    // move together
    lightGroup.getChildren().add(light);

  }

  private void drawMap()
  {
    // Material for floors and ceilings//
    PhongMaterial pathable = new PhongMaterial();
    pathable.setDiffuseColor(Color.WHITE);
    pathable.setSpecularColor(Color.ORANGE);

    // material for walls (this and one above may need to be the same, check
    // ruberic//
    PhongMaterial notPathable = new PhongMaterial();
    notPathable.setDiffuseColor(Color.LIGHTGREEN);
    notPathable.setSpecularColor(Color.ORANGE);

    Xform tileXform = new Xform();
    mapXform.getChildren().add(tileXform);

    // loops through a 2d array, generates rectangles of wall and floor
    // tiles//
    for (int i = 0; i < mapH; i++)
    {
      for (int j = 0; j < mapW; j++)
      {

        Box tile = new Box(TILE_SIZE, 1, TILE_SIZE);
        tile.setDrawMode(DrawMode.FILL);
        tile.setTranslateX(i * TILE_SIZE);
        tile.setTranslateZ(j * TILE_SIZE);
        if (tiles[i][j] == 'O')// make a floot tile//
        {
          tile.setTranslateY(0.5);
          tile.setMaterial(pathable);
        }
        else// make a wall tile//
        {
          tile.setScaleY(WALL_HEIGHT);
          tile.setTranslateY(WALL_HEIGHT / 2);
          tile.setMaterial(notPathable);
        }
        tileXform.getChildren().add(tile);
      }
    }

    world.getChildren().add(mapXform);

  }

  private void handleMouse(Scene scene, final Node root)
  {
    scene.setOnMousePressed(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
      }
    });
    scene.setOnMouseDragged(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 1.0;

        if (me.isControlDown())
        {
          modifier = CONTROL_MULTIPLIER;
        }
        if (me.isShiftDown())
        {
          modifier = SHIFT_MULTIPLIER;
        }
        if (me.isPrimaryButtonDown())
        {

          // Moves the light Doesn't take into account up and down movements.
          // Which doesn't matter as I don't think up and down movements are a
          // requirement
          lightXform.ry.setPivotX(lightXform2.t.getTx());
          lightXform.ry.setPivotZ(lightXform2.t.getTz());
          lightXform.ry.setAngle(lightXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);

          // Left and Right mouse movements
          cameraXform.ry.setPivotX(cameraXform2.t.getTx());
          cameraXform.ry.setPivotZ(cameraXform2.t.getTz());
          cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);

          // Up and Down mouse movements. I don't think this is required but
          // helps maneuver around the map in it's current state
          handleUpDownRotation(modifier); // comment out if unwanted
        }
        else if (me.isSecondaryButtonDown())
        {
          System.out.println("No effect yet");// replace with effect
        }
        else if (me.isMiddleButtonDown())// raise and lower camera
        {
          lightXform2.t.setY(lightXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);

        }
      }
    });
  }

  /*
   * ***May not work exactly how you think. Suppose you're in a room and you
   * want to look down at your feet. The spot you will be looking at on the
   * floor will not be at your feet, but the floor some considerable length
   * forward
   * 
   * Allows you to look up and down. The light doesn't follow but since being
   * able to look up and down isn't required it doesn't matter. Nice for
   * maneuvering around the map in it's current state.
   * 
   * Doesn't allow you to "fly." You're walking on the ground looking around.
   */
  private void handleUpDownRotation(double modifier)
  {
    cameraXform.rx.setPivotY(cameraXform2.t.getTz());
    cameraXform.rx.setPivotZ(cameraXform2.t.getTz());
    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
  }

  private void handleKeyboard(Scene scene, final Node root)
  {
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        String s = event.getText();
        if (s.equals("w"))
          front = true;
        if (s.equals("s"))
          back = true;
        if (s.equals("a"))
          left = true;
        if (s.equals("d"))
          right = true;
        if (s.equals("z")) // puts the player on the "ground"
        {
          System.out.println(cameraXform2.t.getY());
          lightXform2.t.setY(0);
          cameraXform2.t.setY(0);
        }
        if (s.equals("x")) // shoots player up 50 units for debugging
        {
          lightXform2.t.setY(lightXform2.t.getY() + 50);
          cameraXform2.t.setY(lightXform2.t.getY() + 50);
        }
      }
    });
    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        String s = event.getText();
        if (s.equals("w"))
          front = false;
        if (s.equals("s"))
          back = false;
        if (s.equals("a"))
          left = false;
        if (s.equals("d"))
          right = false;

      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));

    RoomGenerator newRoom = new RoomGenerator(mapW, mapH);
    tiles = newRoom.getMap();

    buildCamera();
    buildLight();
    drawMap();

    Scene scene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[]
    { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    scene.setFill(lg);
    scene.setCamera(camera);

    handleMouse(scene, world);
    handleKeyboard(scene, world);

    primaryStage.setTitle("Application");
    primaryStage.setScene(scene);
    primaryStage.show();

    AnimationTimer gameLoop = new MainGameLoop();
    gameLoop.start();

  }

  class MainGameLoop extends AnimationTimer
  {

    public void handle(long now)
    {
      double cos = Math.cos(Math.toRadians(cameraXform.ry.getAngle()));
      double sin = Math.sin(Math.toRadians(cameraXform.ry.getAngle()));

      double z = mapXform.t.getTz();
      double x = mapXform.t.getTx();

      /* Moves the world in realtion to the camera */
      if (front)
      {
        mapXform.t.setX(x - (speed * sin));
        mapXform.t.setZ(z - (speed * cos));

      }
      if (back)
      {
        mapXform.t.setX(x + (speed * sin));
        mapXform.t.setZ(z + (speed * cos));

      }
      if (left)
      {
        mapXform.t.setX(x - (speed * cos));
        mapXform.t.setZ(z + (speed * sin));

      }
      if (right)
      {
        mapXform.t.setX(x + (speed * cos));
        mapXform.t.setZ(z - (speed * sin));

      }

    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}