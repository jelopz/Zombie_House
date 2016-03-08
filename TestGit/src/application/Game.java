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

import CPU.OurZombie;
import Hitbox.Hitbox;
import Pathfinding.Pathfinder;
import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import Sound.Clip;
import Utilities.MapGen;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
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

/**
 * The Main Game class. Handle's all the player controls as well as the main
 * game loop
 */
public class Game extends Application
{

  /** Initializes in debug mode of true */
  public static boolean debug = true;

  /** number of subdivisions in tile */
  public static final double TILE_SIZE = 56;

  /** number of subdivisions in Wall height */
  public static final double WALL_HEIGHT = 64;

  /** The Constant CAMERA_INITIAL_DISTANCE. */
  private static final double CAMERA_INITIAL_DISTANCE = 0;

  /** The Constant CAMERA_INITIAL_X_ANGLE. */
  private static final double CAMERA_INITIAL_X_ANGLE = 0;

  /** The Constant CAMERA_INITIAL_Y_ANGLE. */
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;

  /** The Constant CAMERA_NEAR_CLIP. */
  private static final double CAMERA_NEAR_CLIP = 0.1;

  /** The Constant CAMERA_FAR_CLIP. */
  private static final double CAMERA_FAR_CLIP = 10000.0;

  /** Variable to regulate how fast the mouse moves across screen */
  private static final double MOUSE_SPEED = 0.1;

  /** Variable to regulate how fast the camera rotates */
  private static final double ROTATION_SPEED = 2.0;

  /** The Constant TRACK_SPEED. */
  private static final double TRACK_SPEED = 0.3;

  /** The value for the height of the 2D represented map */
  private final int MAP_HEIGHT = 51;

  /** The value for the width of the 2D represented map */
  private final int MAP_WIDTH = 41;

  /** The number of levels beaten. Used to keep track when game is over */
  private int mapsBeaten;

  /** Player stamina */
  private double stamina;

  /**
   * Boolean to denote if the player is sprinting or not. If on and stamina is
   * >0, player speed is double
   */
  private boolean isSprinting;

  /** The scale val. */
  private double scaleVal = 1;

  /** The default player speed */
  private double speed = 2;

  /** The default non-fullscreen window width. */
  private double windowX = 1024;

  /** The default non-fullscreen window height. */
  private double windowY = 768;

  /** The mouse's x position. */
  private double mousePosX;

  /** The mouse's y position */
  private double mousePosY;

  /** The mouse's previous x position. */
  private double mouseOldX;

  /** The mouse's previous y position */
  private double mouseOldY;

  /** The change in the mouse's x position */
  private double mouseDeltaX;

  /** The change in the mouse's y position */
  private double mouseDeltaY;

  /** The root. */
  private final Group root = new Group();

  /** The world. */
  private final Xform world = new Xform();

  /** The pause screen xform. */
  private final Xform pauseXform = new Xform();

  /** The start screen xform. */
  private final Xform startXform = new Xform();

  /** The camera xform. */
  private final Xform cameraXform = new Xform();

  /** The camera xform2. */
  private final Xform cameraXform2 = new Xform();

  /** The camera xform3. */
  private final Xform cameraXform3 = new Xform();

  /** The player xform. */
  private final Xform playerXform = new Xform();

  /** The map xform. */
  private final Xform mapXform = new Xform();

  /** The camera. */
  private final PerspectiveCamera camera = new PerspectiveCamera(true);

  /** The light. */
  private final PointLight light = new PointLight(Color.WHITE);

  /** The list of all the zombies on the current map */
  public static ArrayList<OurZombie> zombies;

  /** The map generator */
  private static final MapGen MG = new MapGen();

  /** The house/Our current map. */
  private HouseBuilder house;

  /** The scene. */
  private Scene theScene;

  /** The player hitbox. */
  private Hitbox playerHitbox;

  /** The 2D array of tiles acquired from the Housebuilder */
  private Tile[][] tiles;

  /** Boolean to denote if the esc key is pressed */
  private boolean esc = false;

  /** The first. */
  private boolean first = true;

  /** Boolean to denote if the player is currently moving forward */
  private boolean front = false;

  /** Boolean to denote if the player is currently moving backward */
  private boolean back = false;

  /** Boolean to denote if the player is currently moving left */
  private boolean left = false;

  /** Boolean to denote if the player is currently moving right */
  private boolean right = false;

  /** Boolean to denote if the application is running or paused */
  private boolean running = true;

  /** The hold mouse. */
  private boolean holdMouse = true;

  /** The collisions. */
  private boolean collisions = true;

  /** The walk clip. */
  private Clip walkClip;

  /** The run clip. Currently unused */
  private Clip runClip;

  /**
   * Builds the camera. Placed high above the map if debug mode is on, else, set
   * at the correct height on the player spawn
   */
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

  /**
   * Builds the start menu.
   */
  private void buildStartMenu()
  {
    Text l1 = new Text("Start Game");
    l1.setScaleX(5);
    l1.setScaleY(5);
    l1.setTranslateY(-60);
    l1.setFill(Color.WHITE);
    Text l2 = new Text("Continue");
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

  /**
   * Builds the pause menu.
   */
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

  /**
   * Builds the light then adds the light to the cameraXform so the camera and
   * the light move together
   */
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

  /**
   * Handle mouse.
   *
   * @param scene
   *          the scene
   * @param root
   *          the root
   */
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
          scene.setCursor(Cursor.NONE);
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
        if (debug)
        {
          camera.setTranslateZ(-1000);
        }
        else
        {
          camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
          cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        }
        running = true;

      }
      else if (pauseXform.getChildren().get(1) == res.getIntersectedNode())
      {
        System.out.println("New Map");
        makeNewMap();
        pauseXform.setVisible(false);
        if (debug)
        {
          camera.setTranslateZ(-1000);
        }
        else
        {
          camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
          cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        }
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
        if (debug)
        {
          camera.setTranslateZ(-1000);
        }
        else
        {
          camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
          cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        }
      }
      else if (startXform.getChildren().get(1) == res.getIntersectedNode())
      {

        System.out.println("Continue");
        pauseXform.setVisible(false);
        startXform.setVisible(false);
        esc = false;
        mapXform.getChildren().clear();
        MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
        first = false;
        running = true;
        if (debug)
        {
          camera.setTranslateZ(-1000);
        }
        else
        {
          camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
          cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        }
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

  /**
   * Allows you to look up and down. The light doesn't follow but since being
   * able to look up and down isn't required it doesn't matter. Nice for
   * maneuvering around the map in it's current state.
   * 
   * Doesn't allow you to "fly." You're walking on the ground looking around.
   *
   * @param modifier
   *          the modifier
   */
  private void handleUpDownRotation(double modifier)
  {
    light.setRotationAxis(Rotate.X_AXIS);
    light.setRotate(cameraXform.ry.getAngle());
    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
  }

  /**
   * Handle's keyboard input. WASD moves player. Esc key pauses. R "unlock" the
   * mouse cursor
   *
   * @param scene
   *          the scene
   * @param worldXform
   *          the world xform
   */
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
            scene.setCursor(Cursor.DEFAULT);
            pauseXform.setVisible(true);
            esc = true;
            mapXform.getChildren().clear();
            playerXform.getChildren().clear();
            MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
            first = false;
            camera.setTranslateZ(-500);
            cameraXform.rx.setAngle(90);
            running = false;
          }
          else
          {
            pauseXform.setVisible(false);
            startXform.setVisible(false);
            esc = false;
            mapXform.getChildren().clear();
            MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
            first = false;
            camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
            cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
            running = true;
          }
        }
        if (event.isShiftDown())// adds to the player movement speed
        {
          if (stamina > 0)
          {
            isSprinting = true;
          }
        }
        else
        {
          isSprinting = false;
        }

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
          if (holdMouse == true)
            holdMouse = false;
          else
            holdMouse = true;
          scene.setCursor(Cursor.DEFAULT);
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
          if (stamina > 0)
          {
            isSprinting = true;
          }
        }
        else
        {
          isSprinting = false;
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

  /**
   * Initialize the sound clips
   */
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

  /**
   * Reset's the map.
   */
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
    zombies.clear();
    stamina = 5;

    root.getChildren().add(world);
    esc = false;
    first = true;
    running = true;
    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildStartMenu();
    startXform.setVisible(false);
    buildPauseMenu();
  }

  /**
   * Make's a new map and starts the new stage.
   */
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
    zombies.clear();
    stamina = 5;

    esc = false;
    first = true;
    running = true;
    root.getChildren().add(world);
    world.getTransforms().add(new Scale(scaleVal, scaleVal, scaleVal));
    house = new HouseBuilder(MAP_WIDTH, MAP_HEIGHT);
    tiles = house.getMap();

    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildStartMenu();
    startXform.setVisible(false);
    buildPauseMenu();

  }

  /*
   * (non-Javadoc)
   * 
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
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
    if (!debug)
    {
      esc = true;
      running = false;
    }
    MG.drawMap(house, TILE_SIZE, WALL_HEIGHT, tiles, MAP_WIDTH, MAP_HEIGHT, zombies, first, esc, collisions, debug, world, mapXform, playerXform);
    first = false;
    buildCamera();
    buildLight();
    buildPauseMenu();
    buildStartMenu();
    makeSoundClips();
    if (debug)
      startXform.setVisible(false);
    theScene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[]
    { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    theScene.setFill(lg);
    theScene.setCamera(camera);

    handleMouse(theScene, world);
    handleKeyboard(theScene, world);

    primaryStage.setTitle("Zombie House");
    primaryStage.setScene(theScene);
    primaryStage.setFullScreen(true);
    primaryStage.show();

    AnimationTimer gameLoop = new MainGameLoop();
    gameLoop.start();

  }

  /**
   * The Class MainGameLoop.
   * 
   * For each direction, before updating the player position, we take where the
   * player would move, update the 8 collision detecting points to be in that
   * position, and then test to see if either of those points are found ontop of
   * a wall tile. If any of them are, the player does not move to that spot.
   * Else, if none of them are, we update the players position to that position.
   * 
   * If the shift button is down and the stamina meter isn't completely
   * depleted, the player moves twice as fast and the stamina value is depleted
   * equal to the time the shift button is spent down. While the palyer is not
   * sprinting, the stamina value is increased equal to the time spent not
   * sprinting. The value can not go below 0 or above 5.
   * 
   * Every 2 seconds, the zombie determines its next move. For every frame,
   * though, the zombie is moving based on what it determines every 2 seconds.
   * 
   * If the zombie and player collide, the map resets.
   */
  class MainGameLoop extends AnimationTimer
  {

    /** The last. */
    private long last = 0;

    /** The current speed. */
    private double currentSpeed;

    /*
     * (non-Javadoc)
     * 
     * @see javafx.animation.AnimationTimer#handle(long)
     */
    public void handle(long now)
    {
      // pressing esc key changes this boolean, effectively pauses the game and
      // temporarily changes the camera angle;
      if (running)
      {
        if (isSprinting && stamina > 0)
        {
          currentSpeed = 2 * speed;
        }
        else
        {
          currentSpeed = speed;
        }
        double nextX, nextZ;

        double cos = Math.cos(Math.toRadians(cameraXform.ry.getAngle()));
        double sin = Math.sin(Math.toRadians(cameraXform.ry.getAngle()));
        /* Moves the camera around the world */

        if (back)
        {
          nextZ = playerXform.t.getTz() - (currentSpeed * cos);
          nextX = playerXform.t.getTx() - (currentSpeed * sin);

          // sets the boundary points for the nextMove
          playerHitbox.updateBoundaryPoints(nextZ, nextX);

          // tests if the next move will not cause a collision
          if (!playerHitbox.isWallCollision(house))
          {
            if (playerHitbox.hasReachedGoal(house))
            {
              mapsBeaten++;
              if (mapsBeaten < 4)
              {
                makeNewMap();
              }
              else
              {
                // you beat the game.
              }
            }
            else
            {
              if (!walkClip.isLooped())
              {
                walkClip.setLoop();
              }

              // Update coordinates
              cameraXform.t.setX(cameraXform.t.getTx() - (currentSpeed * sin));
              cameraXform.t.setZ(cameraXform.t.getTz() - (currentSpeed * cos));

              playerXform.t.setX(nextX);
              playerXform.t.setZ(nextZ);
            }
          } // else do nothing if there IS a collision
        }

        if (front)
        {
          nextZ = playerXform.t.getTz() + (currentSpeed * cos);
          nextX = playerXform.t.getTx() + (currentSpeed * sin);
          playerHitbox.updateBoundaryPoints(nextZ, nextX);

          if (!playerHitbox.isWallCollision(house))
          {
            if (playerHitbox.hasReachedGoal(house))
            {
              mapsBeaten++;
              if (mapsBeaten < 4)
              {
                makeNewMap();
              }
              else
              {
                // you beat the game.
              }
            }
            else
            {
              if (!walkClip.isLooped())
              {
                walkClip.setLoop();
              }

              cameraXform.t.setX(cameraXform.t.getTx() + (currentSpeed * sin));
              cameraXform.t.setZ(cameraXform.t.getTz() + (currentSpeed * cos));

              playerXform.t.setX(nextX);
              playerXform.t.setZ(nextZ);
            }
          }
        }

        if (right)
        {
          nextZ = playerXform.t.getTz() + (currentSpeed * sin);
          nextX = playerXform.t.getTx() - (currentSpeed * cos);
          playerHitbox.updateBoundaryPoints(nextZ, nextX);

          if (!playerHitbox.isWallCollision(house))
          {
            if (playerHitbox.hasReachedGoal(house))
            {
              mapsBeaten++;
              if (mapsBeaten < 4)
              {
                makeNewMap();
              }
              else
              {
                // you beat the game.
              }
            }
            else
            {
              if (!walkClip.isLooped())
              {
                walkClip.setLoop();
              }

              cameraXform.t.setX(cameraXform.t.getTx() - (currentSpeed * cos));
              cameraXform.t.setZ(cameraXform.t.getTz() + (currentSpeed * sin));

              playerXform.t.setX(nextX);
              playerXform.t.setZ(nextZ);
            }
          }
        }

        if (left)
        {
          nextZ = playerXform.t.getTz() - (currentSpeed * sin);
          nextX = playerXform.t.getTx() + (currentSpeed * cos);
          playerHitbox.updateBoundaryPoints(nextZ, nextX);

          if (!playerHitbox.isWallCollision(house))
          {
            if (playerHitbox.hasReachedGoal(house))
            {
              mapsBeaten++;
              if (mapsBeaten < 4)
              {
                makeNewMap();
              }
              else
              {
                // you beat the game.
              }
            }
            else
            {
              if (!walkClip.isLooped())
              {
                walkClip.setLoop();
              }

              cameraXform.t.setX(cameraXform.t.getTx() + (currentSpeed * cos));
              cameraXform.t.setZ(cameraXform.t.getTz() - (currentSpeed * sin));

              playerXform.t.setX(nextX);
              playerXform.t.setZ(nextZ);
            }
          }
        }

        if ((System.currentTimeMillis() - last) > 2000)
        {
          if (isSprinting)
          {
            stamina -= ((System.currentTimeMillis() - last) / 1000);
            if (stamina < 0)
            {
              stamina = 0;
            }
          }
          else
          {
            stamina += ((System.currentTimeMillis() - last) / 1000);
            if (stamina > 5)
            {
              stamina = 5;
            }
          }
          if (debug)
          {
            System.out.println("------- new -----");
          }
          // tells zombies to figure out their next move
          for (OurZombie z : zombies)
          {
            z.determineNextMove(house, playerXform.t.getTz(), playerXform.t.getTx());
          }

          last = System.currentTimeMillis();
        }

        double distance;
        for (OurZombie z : zombies)
        {
          z.move(house);
          distance = Pathfinder.findEucl(playerXform.t.getTz(), playerXform.t.getTx(), z.getModel().getTranslateZ(), z.getModel().getTranslateX());
          if (distance < TILE_SIZE / 2)
          {
            // You've been hit by the zombie!
            resetMap();
          }
        }
      }
    }
  }

  /**
   * The main method.
   *
   * @param args
   *          the arguments
   */
  public static void main(String[] args)
  {
    launch(args);
  }

}