package application;

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
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application
{
  double scaleVal = 1;
  final double TILE_SIZE = 10; // number of subdivisions in tile
  final Group root = new Group();
  final Xform world = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final PointLight light = new PointLight(Color.WHITE);
  final Xform cameraXform = new Xform();
  final Xform cameraXform2 = new Xform();
  final Xform cameraXform3 = new Xform();

  final Xform lightXform = new Xform();
  final Xform lightXform2 = new Xform();
  final Xform lightXform3 = new Xform();
  final Group lightGroup = new Group();

  Xform mapXform = new Xform();

  private double windowX = 1024;
  private double windowY = 768;
  private boolean front = false;
  private boolean back = false;
  private boolean left = false;
  private boolean right = false;
  private double speed = 1;
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

  double mousePosX;
  double mousePosY;
  double mouseOldX;
  double mouseOldY;
  double mouseDeltaX;
  double mouseDeltaY;

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
    pathable.setDiffuseColor(Color.DARKGREEN);
    pathable.setSpecularColor(Color.ORANGE);

    // material for walls (this and one above may need to be the same, check
    // ruberic//
    PhongMaterial notPathable = new PhongMaterial();
    notPathable.setDiffuseColor(Color.LIGHTGREEN);
    notPathable.setSpecularColor(Color.ORANGE);

    Xform tileXform = new Xform();
    mapXform.getChildren().add(tileXform);

    // loops through a 2d array, generates rectangles of wall and floor tiles//
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {

        Box tile = new Box(TILE_SIZE, 1, TILE_SIZE);
        tile.setDrawMode(DrawMode.FILL);
        tile.setTranslateX(i * TILE_SIZE);
        tile.setTranslateZ(j * TILE_SIZE);
        if (i % 2 == 0)// make a floot tile//
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
          lightXform.ry.setAngle(lightXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
          cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);

          if (cameraXform.ry.getAngle() > 360 || cameraXform.ry.getAngle() < -360)
          {
            cameraXform.ry.setAngle(0);
          }

          System.out.println(cameraXform.ry.getAngle() + " " + Math.cos(cameraXform.ry.getAngle()) + " "
              + Math.sin(cameraXform.ry.getAngle()));
          // mapXform.setRotateY(mapXform.ry.getAngle() - mouseDeltaX *
          // MOUSE_SPEED * modifier * ROTATION_SPEED);
        }
        else if (me.isSecondaryButtonDown())// ignore done by wsad buttons
        {

          double cos = Math.cos(cameraXform.ry.getAngle());
          double sin = Math.sin(cameraXform.ry.getAngle());
          double z = mapXform.getTranslateZ();
          double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier * sin;

          double x = mapXform.getTranslateX();
          double newX = x + mouseDeltaX * MOUSE_SPEED * modifier * cos;

          System.out.println(Math.abs(cos) + Math.abs(sin));
          mapXform.setTranslateZ(newZ);
          mapXform.setTranslateX(newX);

        }
        else if (me.isMiddleButtonDown())// ignore, not needed
        {
          lightXform2.t.setX(lightXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
          lightXform2.t.setY(lightXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);

        }
      }
    });
  }

  private void handleKeyboard(Scene scene, final Node root)
  {
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        String s = event.getText();
        System.out.println(s);
        if (s.equals("w")) front = true;
        if (s.equals("s")) back = true;
        if (s.equals("a")) left = true;
        if (s.equals("d")) right = true;
        if (s.equals("p"))// print put sin and cos of angle
        {
          double cos = Math.cos(cameraXform.ry.getAngle());
          double sin = Math.sin(cameraXform.ry.getAngle());
          System.out.println("sin = " + sin);
          System.out.println("cos = " + cos);
          System.out.println();
        }
      }
    });
    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        String s = event.getText();
        if (s.equals("w")) front = false;
        if (s.equals("s")) back = false;
        if (s.equals("a")) left = false;
        if (s.equals("d")) right = false;

      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));

    // array{}{} = clas(120,150)

    buildCamera();
    buildLight();
    drawMap();

    Scene scene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
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
      /* Moves the camer around the world */
      if (back) cameraXform.setTranslateZ(cameraXform.getTranslateZ() - speed);
      if (front) cameraXform.setTranslateZ(cameraXform.getTranslateZ() + speed);
      if (right) cameraXform.setTranslateX(cameraXform.getTranslateX() - speed);
      if (left) cameraXform.setTranslateX(cameraXform.getTranslateX() + speed);

    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}