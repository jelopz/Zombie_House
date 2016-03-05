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

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import CPU.Zombie;
import Hitbox.Hitbox;
import RoomGenerator.HouseBuilder;
import Sound.Clip;
import Utilities.MapGen;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application
{
  public static boolean debug = false;

  public static final double TILE_SIZE = 56; // number of subdivisions in
  // tile

  public static final double WALL_HEIGHT = 64;
  private static final double CAMERA_INITIAL_DISTANCE = 0;
  private static final double CAMERA_INITIAL_X_ANGLE = 0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 2.0;
  private static final double TRACK_SPEED = 0.3;
  
  private final int MAP_HEIGHT = 51;
  private final int MAP_WIDTH = 41;

  private double scaleVal = 1;
  private double sprint = 3;
  private double walk = 2;
  private double speed = 1;
  private double windowX = 1024;
  private double windowY = 768;
  private double mousePosX;
  private double mousePosY;
  private double mouseOldX;
  private double mouseOldY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  private final Group root = new Group();

  private final Xform world = new Xform();
  private final Xform pauseXform = new Xform();
  private final Xform startXform = new Xform();
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();
  private final Xform playerXform = new Xform();
  private final Xform mapXform = new Xform();

  private final PerspectiveCamera camera = new PerspectiveCamera(true);

  private final PointLight light = new PointLight(Color.WHITE);

  public static ArrayList<Zombie> zombies; // List of Zombies

  private static final MapGen MG = new MapGen();

  private HouseBuilder house; // Our House

  private Scene theScene;

  private Worker walkThread;

  private Hitbox playerHitbox;

  private char[][] tiles;

  private boolean esc = false;
  private boolean first = true;
  private boolean front = false;
  private boolean back = false;
  private boolean left = false;
  private boolean right = false;
  private boolean running = true;
  private boolean holdMouse = true;
  public static boolean collisions = true; // made static for debugging

  private Clip walkClip;
  private Clip runClip;

  // private URL url = new URL("step.wav");

  private void buildCamera()
  {

    root.getChildren().add(cameraXform);
    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);// sets y up

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    if (debug)
    {
      camera.setTranslateZ(-1000);
      cameraXform.ry.setAngle(90);
    }
    else
    {
      camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
      cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    }
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    cameraXform.t.setZ(house.getPlayerSpawnPoint().x * TILE_SIZE);
    cameraXform.t.setX(house.getPlayerSpawnPoint().y * TILE_SIZE);
    cameraXform.setTranslateY(WALL_HEIGHT / 2);
  }

  private void buildStartMenu()
  {
    Text l1 = new Text("Start Game");
    l1.setScaleX(5);
    l1.setScaleY(5);
    l1.setTranslateY(-60);
    l1.setFill(Color.WHITE);
    Text l2 = new Text("Do A Thing!");
    l2.setScaleX(5);
    l2.setScaleY(5);
    l2.setFill(Color.WHITE);
    Text l3 = new Text("Exit Game");
    l3.setScaleX(5);
    l3.setScaleY(5);
    l3.setTranslateY(55);
    l3.setFill(Color.WHITE);
    startXform.setVisible(false);
    startXform.getChildren().addAll(l1, l2, l3);
    startXform.setRotateZ(180);
    startXform.t.setZ(-WALL_HEIGHT / 2 - 5);
    startXform.t.setX(25);
    startXform.setPickOnBounds(true);
    startXform.setVisible(true);
    cameraXform.getChildren().add(startXform);
    camera.setTranslateZ(-500);
    cameraXform.rx.setAngle(90);

  }

  private void buildPauseMenu()
  {
    Text l1 = new Text("Retry");
    l1.setScaleX(5);
    l1.setScaleY(5);
    l1.setTranslateY(-60);
    l1.setFill(Color.WHITE);
    Text l2 = new Text("New Map");
    l2.setScaleX(5);
    l2.setScaleY(5);
    l2.setFill(Color.WHITE);
    Text l3 = new Text("Quit");
    l3.setScaleX(5);
    l3.setScaleY(5);
    l3.setTranslateY(55);
    l3.setFill(Color.WHITE);
    pauseXform.setVisible(false);
    pauseXform.getChildren().addAll(l1, l2, l3);
    pauseXform.setRotateZ(180);
    pauseXform.t.setZ(-WALL_HEIGHT / 2 - 5);
    pauseXform.t.setX(25);
    pauseXform.setPickOnBounds(true);
    cameraXform.getChildren().add(pauseXform);

  }

  private void buildLight()
  {
    light.setTranslateZ(WALL_HEIGHT / 2);
    if (collisions)
    {
      if (debug)
      {
        light.setTranslateZ(-1000);
      }
      else
      {
        light.setTranslateZ(CAMERA_INITIAL_DISTANCE);
      }
    }
    light.setColor(Color.WHITE);
    // add light to camera so they move together
    cameraXform.getChildren().add(light);

  }

  private void handleMouse(Scene scene, final Node root)
  {

    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent event)
      {
        // r switches holdMouse, esc switches running if either is false
        // the mouse does not change the angle of the camera is not held in
        // center of screen//
        if (holdMouse && running)
        {
          Point p = MouseInfo.getPointerInfo().getLocation();
          int x = p.x;
          int y = p.y;
          // System.out.println(x + " " + y);
          // System.out.println();
          mousePosX = x;
          mousePosY = y;
          mouseOldX = 950;
          mouseOldY = 500;

          mouseDeltaX = (mousePosX - mouseOldX);
          mouseDeltaY = (mousePosY - mouseOldY);

          cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED);
          light.setRotationAxis(Rotate.Y_AXIS);
          light.setRotate(cameraXform.ry.getAngle());

          if (cameraXform.ry.getAngle() > 360)
          {
            cameraXform.ry.setAngle(0);
          }
          if (cameraXform.ry.getAngle() < 0)
          {
            cameraXform.ry.setAngle(360);

          }
          light.setRotationAxis(Rotate.X_AXIS);
          light.setRotate(cameraXform.ry.getAngle());
          cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED);

          try
          {
            new Robot().mouseMove(950, 500);
          }
          catch (AWTException e)
          {
            e.printStackTrace();
          }
        }
      }
    });
    scene.setOnMouseClicked((event) ->
    {
      PickResult res = event.getPickResult();
      if (pauseXform.getChildren().get(0) == res.getIntersectedNode())
      {
        System.out.println("Retry");
        resetMap();
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        running = true;

      }
      else if (pauseXform.getChildren().get(1) == res.getIntersectedNode())
      {
        System.out.println("New Map");
        makeNewMap();
        pauseXform.setVisible(false);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        running = true;

      }
      else if (pauseXform.getChildren().get(2) == res.getIntersectedNode())
      {
        System.out.println("Quit");
        pauseXform.setVisible(false);
        startXform.setVisible(true);
      }
      else if (startXform.getChildren().get(0) == res.getIntersectedNode())
      {
        System.out.println("Start Game");
        makeNewMap();
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
      }
      else if (startXform.getChildren().get(1) == res.getIntersectedNode())
      {

        System.out.println("Do A Thing!");
      }
      else if (startXform.getChildren().get(2) == res.getIntersectedNode())
      {
        System.out.println("Exit Game");
        System.exit(0);
      }
    });
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

        if (me.isPrimaryButtonDown())
        {

          // Moves the light Doesn't take into account up and down movements.
          // Which doesn't matter as I don't think up and down movements are a
          // requirement

          // Left and Right mouse movements

          cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
          light.setRotationAxis(Rotate.Y_AXIS);
          light.setRotate(cameraXform.ry.getAngle());

          if (cameraXform.ry.getAngle() > 360)
          {
            cameraXform.ry.setAngle(0);
          }
          if (cameraXform.ry.getAngle() < 0)
          {
            cameraXform.ry.setAngle(360);
          }
          // System.out.println(cameraXform.ry.getAngle()/45);
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
          cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
      }
    });

  }

  /*
   * Allows you to look up and down. The light doesn't follow but since being
   * able to look up and down isn't required it doesn't matter. Nice for
   * maneuvering around the map in it's current state.
   * 
   * Doesn't allow you to "fly." You're walking on the ground looking around.
   */
  private void handleUpDownRotation(double modifier)
  {
    light.setRotationAxis(Rotate.X_AXIS);
    light.setRotate(cameraXform.ry.getAngle());
    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
  }

  private void handleKeyboard(Scene scene, final Xform worldXform)
  {

    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {

        // pressing escape pauses the main game loop, frees the mouse and
        // changes the camera angle
        if (event.getCode() == KeyCode.ESCAPE)
        {
          if (running)
          {
            pauseXform.setVisible(true);
            esc = true;
            mapXform.getChildren().clear();
            playerXform.getChildren().clear();
            MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world,
                mapXform, playerXform);
            first = false;
            camera.setTranslateZ(-500);
            cameraXform.rx.setAngle(90);
            running = false;
          }
          else
          {
            pauseXform.setVisible(false);
            esc = false;
            mapXform.getChildren().clear();
            MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world,
                mapXform, playerXform);
            first = false;
            camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
            cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
            running = true;
          }
        }
        // pressing F12 resets the game with new Map, Zombies, and player
        // location
        if (event.getCode() == KeyCode.F12)
        {

          makeNewMap();
        }
        // pressing F11 resets the game with same Map, Zombies, and player
        // location
        if (event.getCode() == KeyCode.F11)
        {
          resetMap();
        }
        if (event.isControlDown())// does nothing right now
        {
        }
        if (event.isShiftDown())// adds to the player movement speed
        {
          speed = sprint;
        }
        else speed = walk;

        if (event.getCode() == KeyCode.W)
        {
          front = true;
        }
        if (event.getCode() == KeyCode.S)
        {
          back = true;
        }
        if (event.getCode() == KeyCode.A)
        {
          left = true;
        }
        if (event.getCode() == KeyCode.D)
        {
          right = true;
        }

        // hold and release mouse from center of screen by pressing c
        if (event.getCode() == KeyCode.R)
        {
          if (holdMouse == true) holdMouse = false;
          else holdMouse = true;
        }
        if (event.getCode() == KeyCode.Z) // puts the player on the "ground"
        {
          cameraXform2.t.setY(0);
        }
        if (event.getCode() == KeyCode.X) // turns on and off collision
                                          // detection
        {
          if (collisions)
          {
            collisions = false;
          }
          else
          {
            collisions = true;
          }
        }
      }
    });
    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        if (event.isShiftDown())// adds to the player movement speed
        {
          speed = sprint;
        }
        else
        {
          speed = walk;
        }
        if (event.getCode() == KeyCode.W)
        {
          front = false;
        }
        if (event.getCode() == KeyCode.S)
        {
          back = false;
        }
        if (event.getCode() == KeyCode.A)
        {
          left = false;
        }
        if (event.getCode() == KeyCode.D)
        {
          right = false;
        }

        walkClip.stopLoop();
      }
    });
  }

  private void makeSoundClips()
  {
    try
    {
      walkClip = new Clip(new URL("file:walkClip.wav"));
      runClip = new Clip(new URL("file:runClip.wav"));
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
  }

  private void resetMap()
  {
    startXform.getChildren().clear();
    pauseXform.getChildren().clear();
    world.getChildren().clear();
    playerXform.getChildren().clear();
    cameraXform.getChildren().clear();
    cameraXform2.getChildren().clear();
    cameraXform3.getChildren().clear();
    mapXform.getChildren().clear();
    root.getChildren().clear();

    root.getChildren().add(world);
    esc = false;
    first = true;
    running = true;
    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world,
        mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildStartMenu();
    startXform.setVisible(false);
    buildPauseMenu();

  }

  private void makeNewMap()
  {
    startXform.getChildren().clear();
    pauseXform.getChildren().clear();
    world.getChildren().clear();
    playerXform.getChildren().clear();
    cameraXform.getChildren().clear();
    cameraXform2.getChildren().clear();
    cameraXform3.getChildren().clear();
    mapXform.getChildren().clear();
    mapXform.getChildren().clear();
    root.getChildren().clear();

    esc = false;
    first = true;
    running = true;
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));
    house = new HouseBuilder(MAP_WIDTH, MAP_HEIGHT);
    tiles = house.getMap();

    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world,
        mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildStartMenu();
    startXform.setVisible(false);
    buildPauseMenu();

  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));

    // house = new RoomGenerator(mapW, mapH);
    house = new HouseBuilder(MAP_WIDTH, MAP_HEIGHT);
    tiles = house.getMap();

    zombies = new ArrayList<>();
    if (collisions)// sets 8 points around the player for testing collisions
    {
      playerHitbox = new Hitbox(playerXform);
    }
    esc = true;
    running = false;
    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world,
        mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildPauseMenu();
    buildStartMenu();
    makeSoundClips();

    theScene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    theScene.setFill(lg);
    theScene.setCamera(camera);

    handleMouse(theScene, world);
    handleKeyboard(theScene, world);

    primaryStage.setTitle("Zombie House");
    primaryStage.setScene(theScene);
    primaryStage.show();

    AnimationTimer gameLoop = new MainGameLoop();
    gameLoop.start();

  }

  class MainGameLoop extends AnimationTimer
  {

    public void handle(long now)
    {
      // pressing esc key changes this boolean, effectively pauses the game and
      // temporarily changes the camera angle;
      if (running)
      {

        walkThread = new Worker();
        walkThread.start();
        for (Zombie z : zombies) // tells zombies to figure out their next move
        {
          z.determineNextMove(house);
        }

      }
    }
  }

  public class Worker extends Thread
  {
    double nextX, nextZ;

    public void run()
    {
      double cos = Math.cos(Math.toRadians(cameraXform.ry.getAngle()));
      double sin = Math.sin(Math.toRadians(cameraXform.ry.getAngle()));
      /* Moves the camera around the world */

      // For each direction, before updating the player position, we take
      // where
      // the player would move, update the 8 collision detecting points to be
      // in
      // that position, and then test to see if either of those points are
      // found
      // ontop of a wall tile. If any of them are, the player does not move to
      // that spot. Else, if none of them are, we update the players position
      // to
      // that position.

      if (back)
      {
        nextZ = playerXform.t.getTz() - (speed * cos);
        nextX = playerXform.t.getTx() - (speed * sin);

        // sets the boundary points for the nextMove
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        // tests if the next move will not cause a collision
        if (!playerHitbox.isWallCollision(house))
        {
          if (playerHitbox.hasReachedGoal(house))
          {
            makeNewMap();
          }
          else
          {
            if (!walkClip.isLooped())
            {
              walkClip.setLoop();
            }

            // Update coordinates
            cameraXform.t.setX(cameraXform.t.getTx() - (speed * sin));
            cameraXform.t.setZ(cameraXform.t.getTz() - (speed * cos));

            playerXform.t.setX(nextX);
            playerXform.t.setZ(nextZ);
          }
        } // else do nothing if there IS a collision
      }

      if (front)
      {
        nextZ = playerXform.t.getTz() + (speed * cos);
        nextX = playerXform.t.getTx() + (speed * sin);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (!playerHitbox.isWallCollision(house))
        {
          if (playerHitbox.hasReachedGoal(house))
          {
            makeNewMap();
          }
          else
          {
            if (!walkClip.isLooped())
            {
              walkClip.setLoop();
            }

            cameraXform.t.setX(cameraXform.t.getTx() + (speed * sin));
            cameraXform.t.setZ(cameraXform.t.getTz() + (speed * cos));

            playerXform.t.setX(nextX);
            playerXform.t.setZ(nextZ);
          }
        }
      }

      if (right)
      {
        nextZ = playerXform.t.getTz() + (speed * sin);
        nextX = playerXform.t.getTx() - (speed * cos);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (!playerHitbox.isWallCollision(house))
        {
          if (playerHitbox.hasReachedGoal(house))
          {
            makeNewMap();
          }
          else
          {
            if (!walkClip.isLooped())
            {
              walkClip.setLoop();
            }

            cameraXform.t.setX(cameraXform.t.getTx() - (speed * cos));
            cameraXform.t.setZ(cameraXform.t.getTz() + (speed * sin));

            playerXform.t.setX(nextX);
            playerXform.t.setZ(nextZ);
          }
        }
      }

      if (left)
      {
        nextZ = playerXform.t.getTz() - (speed * sin);
        nextX = playerXform.t.getTx() + (speed * cos);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (!playerHitbox.isWallCollision(house))
        {
          if (playerHitbox.hasReachedGoal(house))
          {
            makeNewMap();
          }
          else
          {
            if (!walkClip.isLooped())
            {
              walkClip.setLoop();
            }

            cameraXform.t.setX(cameraXform.t.getTx() + (speed * cos));
            cameraXform.t.setZ(cameraXform.t.getTz() - (speed * sin));

            playerXform.t.setX(nextX);
            playerXform.t.setZ(nextZ);
          }
        }
      }
    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}