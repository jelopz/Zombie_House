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
import java.util.ArrayList;

import ZombieBuilder.ZombieBuilder;
import CPU.RandomWalk;
import CPU.Zombie;
import Hitbox.Hitbox;
import RoomGenerator.RoomGenerator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application
{

  public static final double TILE_SIZE = 56; // number of subdivisions in
  // tile

  public static final double WALL_HEIGHT = 64;
  private static final double CAMERA_INITIAL_DISTANCE = -1000;
  private static final double CAMERA_INITIAL_X_ANGLE = 0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 90;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 2.0;
  private static final double TRACK_SPEED = 0.3;
  private static double cameraInitialX;
  private static double cameraInitialZ;
  // Our House
  RoomGenerator house;

  private double scaleVal = 1;
  private final Group root = new Group();
  private final Xform world = new Xform();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);
  private final PointLight light = new PointLight(Color.WHITE);
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();

  private final Xform playerXform = new Xform();
  private final Xform mapXform = new Xform();
  private Hitbox playerHitbox;

  private ArrayList<Zombie> zombies; // List of Zombies

  private char[][] tiles;
  private int mapH = 36;
  private int mapW = 36;

  private double windowX = 1024;
  private double windowY = 768;
  private boolean front = false;
  private boolean back = false;
  private boolean left = false;
  private boolean right = false;
  private boolean collisions = true;
  private boolean holdMouse = true;
  private double sprint = 3;
  private double walk = 2;
  private double speed = 1;

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
    cameraXform.t.setZ(house.getPlayerSpawnPoint().x * TILE_SIZE);
    cameraXform.t.setX(house.getPlayerSpawnPoint().y * TILE_SIZE);
    cameraXform.setTranslateY(WALL_HEIGHT / 2);
  }

  private void buildLight()
  {

    light.setTranslateZ(WALL_HEIGHT / 2);
    if (collisions)
      light.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    light.setColor(Color.WHITE);
    // add light to camera so they move together
    cameraXform.getChildren().add(light);

  }

  private void drawMap()
  {
    zombies = new ArrayList<>();
    // Material for floors and ceilings//
    PhongMaterial pathable = new PhongMaterial();
    pathable.setDiffuseColor(Color.WHITE);
    pathable.setSpecularColor(Color.ORANGE);

    // Material for denoting the tile which the player will spawn on
    PhongMaterial spawnPoint = new PhongMaterial();
    spawnPoint.setDiffuseColor(Color.LIGHTGREEN);
    spawnPoint.setSpecularColor(Color.WHITE);

    // Material for denoting the tile which the zombie will spawn on
    PhongMaterial zombieSpawn = new PhongMaterial();
    zombieSpawn.setDiffuseColor(Color.RED);
    zombieSpawn.setSpecularColor(Color.WHITE);

    // material for walls (this and one above may need to be the same, check
    // ruberic//

    PhongMaterial notPathable = new PhongMaterial();
    notPathable.setDiffuseColor(Color.LIGHTGREEN);
    notPathable.setSpecularColor(Color.BLACK);

    PhongMaterial zombieColor = new PhongMaterial();
    zombieColor.setDiffuseColor(Color.ORANGE);
    zombieColor.setSpecularColor(Color.RED);

    // creates a blue cylinder centered on the camera for testing//
    PhongMaterial playerColor = new PhongMaterial();
    playerColor.setDiffuseColor(Color.BLUE);
    playerColor.setSpecularColor(Color.DARKBLUE);

    PhongMaterial bricks = new PhongMaterial();
    bricks.setDiffuseMap(new Image(getClass().getResource("img.png").toExternalForm()));
    bricks.setDiffuseColor(Color.WHITE);
    bricks.setSpecularPower(0);

    Xform tileXform = new Xform();
    mapXform.getChildren().add(tileXform);

    // loops through a 2d array, generates rectangles of wall and floor tiles//
    for (int i = 0; i < mapH; i++)
    {
      for (int j = 0; j < mapW; j++)
      {
        Box tile = new Box(TILE_SIZE, 1, TILE_SIZE);
        tile.setDrawMode(DrawMode.FILL);
        tile.setTranslateX(i * TILE_SIZE);
        tile.setTranslateZ(j * TILE_SIZE);
        Box ceiling = new Box(TILE_SIZE, 1, TILE_SIZE);
        ceiling.setDrawMode(DrawMode.FILL);
        if (collisions)
        {
          ceiling.setDrawMode(DrawMode.LINE);
        }
        ceiling.setTranslateX(i * TILE_SIZE);
        ceiling.setTranslateZ(j * TILE_SIZE);
        ceiling.setMaterial(bricks);
        if (tiles[i][j] == 'O')// make a floor tile//
        {

          ceiling.setTranslateY(WALL_HEIGHT + .5);
          tileXform.getChildren().add(ceiling);

          tile.setTranslateY(0.5);
          tile.setMaterial(bricks);

        }
        else if (tiles[i][j] == 'P')
        {
          ceiling.setTranslateY(WALL_HEIGHT + .5);
          tileXform.getChildren().add(ceiling);

          tile.setTranslateY(0.5);
          tile.setMaterial(spawnPoint);

          cameraInitialX = (i * TILE_SIZE);
          cameraInitialZ = (j * TILE_SIZE);

          // Just doing this for testing collisions
          if (collisions)
          {
            Cylinder player = new Cylinder(TILE_SIZE / 4, WALL_HEIGHT);
            player.setTranslateX(0);
            player.setTranslateZ(0);
            player.setTranslateY(WALL_HEIGHT / 2);
            player.setMaterial(playerColor);
            playerXform.getChildren().add(player);
            world.getChildren().add(playerXform);
          }

        }
        else if (tiles[i][j] == 'Z')
        {
          ceiling.setTranslateY(WALL_HEIGHT + .5);
          tileXform.getChildren().add(ceiling);
          // Just doing this for testing collisions
          tile.setTranslateY(0.5);
          tile.setMaterial(zombieSpawn);
          
          //Code for making the zombie model
//          Group zomb = ZombieBuilder.getZombie(i, j, TILE_SIZE);
//
//          zombies.add(new RandomWalk(j, i, zomb));
//          world.getChildren().add(zomb);
          
          
          //Zombie model is a cylinder
          Cylinder c = new Cylinder(TILE_SIZE/4, WALL_HEIGHT);
          c.setMaterial(notPathable);
          Group zomb = new Group(c);
          zombies.add(new RandomWalk(j, i, zomb));
          
          zomb.setTranslateX(i*TILE_SIZE);
          zomb.setTranslateZ(j*TILE_SIZE);
          zomb.setTranslateY(WALL_HEIGHT / 2);
          
          world.getChildren().add(zomb);
        }
        else// make a wall tile//
        {
          tile.setScaleY(WALL_HEIGHT);
          tile.setTranslateY(WALL_HEIGHT / 2);
          tile.setMaterial(bricks);
        }
        tileXform.getChildren().add(tile);
      }
    }
    
    if (collisions)// sets 8 points around the player for testing collisions
    {
      playerHitbox = new Hitbox(playerXform);
    }
    
    world.getChildren().add(mapXform);

    // places playerXform on spawn point
    playerXform.t.setZ(house.getPlayerSpawnPoint().x * TILE_SIZE);
    playerXform.t.setX(house.getPlayerSpawnPoint().y * TILE_SIZE);
  }

  private void handleMouse(Scene scene, final Node root)
  {

    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent event)
      {
        if (holdMouse)
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

  private void handleKeyboard(Scene scene, final Node root)
  {

    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {

        if (event.isControlDown())// does nothing right now
        {
        }
        if (event.isShiftDown())// adds to the player movement speed
        {
          speed = sprint;
        }
        else
          speed = walk;

        String s = event.getText();
        if (s.equals("w"))
          front = true;
        if (s.equals("s"))
          back = true;
        if (s.equals("a"))
          left = true;
        if (s.equals("d"))
          right = true;

        // hold and release mouse from center of screen by pressing c
        if (s.equals("r"))
        {
          if (holdMouse == true)
            holdMouse = false;
          else
            holdMouse = true;
        }
        if (s.equals("z")) // puts the player on the "ground"
        {
          cameraXform2.t.setY(0);
        }
        if (s.equals("x")) // turns on and off collision detection
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
          speed = walk;
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

    house = new RoomGenerator(mapW, mapH);
    tiles = house.getMap();

    drawMap();
    buildCamera();
    buildLight();

    Scene scene = new Scene(root, windowX, windowY, true);
    Stop[] stops = new Stop[]
    { new Stop(0, Color.RED), new Stop(1, Color.ORANGE) };
    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    scene.setFill(lg);
    scene.setCamera(camera);

    handleMouse(scene, world);
    handleKeyboard(scene, world);

    primaryStage.setTitle("Zombie House");
    primaryStage.setScene(scene);
    primaryStage.show();

    AnimationTimer gameLoop = new MainGameLoop();
    gameLoop.start();

  }

  /*
   * Takes a look at each point on the octogon and determines what tile it's on.
   * it then checks to see if that tile is a legal tile to be on.
   */
  private boolean isCollision()
  {
    if (collisions)
    {
      int x, y;
      for (int i = 0; i < 8; i++) // get what tile the point is on.
      {

        x = (int) (playerHitbox.getPoint(i).z / TILE_SIZE); // z //x
        y = (int) (playerHitbox.getPoint(i).x / TILE_SIZE); // x //y

        if (!house.isPointLegal(x, y)) // is that tile not a legal move?
        {
          return true; // the point is colliding with something
        }
      }
    }
    return false; // no point is colliding
  }

  class MainGameLoop extends AnimationTimer
  {

    double nextX, nextZ;

    public void handle(long now)
    {

      double cos = Math.cos(Math.toRadians(cameraXform.ry.getAngle()));
      double sin = Math.sin(Math.toRadians(cameraXform.ry.getAngle()));

      for (Zombie z : zombies) // tells zombies to figure out their next move
      {
        z.determineNextMove();
      }

      /* Moves the camera around the world */

      // For each direction, before updating the player position, we take where
      // the player would move, update the 8 collision detecting points to be in
      // that position, and then test to see if either of those points are found
      // ontop of a wall tile. If any of them are, the player does not move to
      // that spot. Else, if none of them are, we update the players position to
      // that position.

      if (back)
      {
        nextZ = playerXform.t.getTz() - (speed * cos);
        nextX = playerXform.t.getTx() - (speed * sin);

        // sets the boundary points for the nextMove
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (isCollision()) // tests if the next move will cause a collision
        {
          // Do nothing
          System.out.println("Back Wall");
        }
        else
        {
          // Update coordinates
          cameraXform.t.setX(cameraXform.t.getTx() - (speed * sin));
          cameraXform.t.setZ(cameraXform.t.getTz() - (speed * cos));

          playerXform.t.setX(nextX);
          playerXform.t.setZ(nextZ);
        }
      }

      if (front)
      {
        nextZ = playerXform.t.getTz() + (speed * cos);
        nextX = playerXform.t.getTx() + (speed * sin);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (isCollision())
        {
          System.out.println("Front Wall");
        }
        else
        {
          cameraXform.t.setX(cameraXform.t.getTx() + (speed * sin));
          cameraXform.t.setZ(cameraXform.t.getTz() + (speed * cos));

          playerXform.t.setX(nextX);
          playerXform.t.setZ(nextZ);

        }
      }
      if (right)
      {
        nextZ = playerXform.t.getTz() + (speed * sin);
        nextX = playerXform.t.getTx() - (speed * cos);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (isCollision())
        {
          System.out.println("Right Wall");
        }
        else
        {
          cameraXform.t.setX(cameraXform.t.getTx() - (speed * cos));
          cameraXform.t.setZ(cameraXform.t.getTz() + (speed * sin));

          playerXform.t.setX(nextX);
          playerXform.t.setZ(nextZ);
        }
      }
      if (left)
      {
        nextZ = playerXform.t.getTz() - (speed * sin);
        nextX = playerXform.t.getTx() + (speed * cos);
        playerHitbox.updateBoundaryPoints(nextZ, nextX);

        if (isCollision())
        {
          System.out.println("Left Wall");
        }
        else
        {
          cameraXform.t.setX(cameraXform.t.getTx() + (speed * cos));
          cameraXform.t.setZ(cameraXform.t.getTz() - (speed * sin));

          playerXform.t.setX(nextX);
          playerXform.t.setZ(nextZ);
        }

      }

    }
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}