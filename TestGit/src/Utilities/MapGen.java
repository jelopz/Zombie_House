/*MapGen
 * Handles the drawing resetting and general initialization of the 
 * visual elements of the game
 * 
 * Methods: 
 *  drawMap
 *    -generates all the tiles and models 
 *    -places all elements into 3d space*/

package Utilities;

import java.awt.Point;
import java.util.ArrayList;

import CPU.LineWalk;
import CPU.RandomWalk;
import CPU.Zombie;
import RoomGenerator.HouseBuilder;
import RoomGenerator.Tile;
import ZombieBuilder.ZombieBuilder;
import application.Game;
import application.Xform;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;

public class MapGen
{

  public void drawMap(HouseBuilder house, double TILE_SIZE, double WALL_HEIGHT, Tile[][] tiles, int mapW, int mapH, ArrayList<Zombie> zombies, boolean first, boolean esc, boolean collisions, boolean debug, Xform world, Xform mapXform, Xform playerXform)
  {

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

    PhongMaterial blueBricks = new PhongMaterial();
    blueBricks.setDiffuseMap(new Image(getClass().getResource("bluetile.png").toExternalForm()));
    blueBricks.setSpecularPower(0);

    PhongMaterial greenBricks = new PhongMaterial();
    greenBricks.setDiffuseMap(new Image(getClass().getResource("greentile.png").toExternalForm()));
    greenBricks.setDiffuseColor(Color.WHITE);
    greenBricks.setSpecularPower(0);

    PhongMaterial yellowBricks = new PhongMaterial();
    yellowBricks.setDiffuseMap(new Image(getClass().getResource("yellowtile.png").toExternalForm()));
    yellowBricks.setDiffuseColor(Color.WHITE);
    yellowBricks.setSpecularPower(0);

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
        if (esc)
        {
          ceiling.setDrawMode(DrawMode.LINE);
        }
        if (!debug)
        {
          ceiling.setTranslateX(i * TILE_SIZE);
          ceiling.setTranslateZ(j * TILE_SIZE);
          ceiling.setMaterial(bricks);
        }
        if (tiles[i][j].getTileType() == '-' || tiles[i][j].getTileType() == 'H' ||
            tiles[i][j].getTileType() == 'D')// make a floor tile
        {
          if (!debug)
          {
            ceiling.setTranslateY(WALL_HEIGHT + .5);
            mapXform.getChildren().add(ceiling);
          }

          tile.setTranslateY(0.5);

          if (j < mapW / 2 && i < mapH / 2)
          {
            tile.setMaterial(bricks);
          }
          else if (j < mapW / 2 && i > mapH / 2)
          {
            tile.setMaterial(yellowBricks);
          }
          else if (j > mapW / 2 && i > mapH / 2)
          {
            tile.setMaterial(greenBricks);
          }
          else if (j > mapW / 2 && i < mapH / 2)
          {
            tile.setMaterial(blueBricks);
          }

        }
         else if (tiles[i][j].getTileType() == 'E')
         {
         if (!debug)
         {
         ceiling.setTranslateY(WALL_HEIGHT + .5);
         mapXform.getChildren().add(ceiling);
         }
        
         tile.setTranslateY(0.5);
         tile.setMaterial(pathable);
         Game.endPointTile = new Point(j, i);
         }
        else if (tiles[i][j].getTileType() == 'P')
        {
          if (!debug)
          {
            ceiling.setTranslateY(WALL_HEIGHT + .5);
            mapXform.getChildren().add(ceiling);
          }

          tile.setTranslateY(0.5);
          tile.setMaterial(spawnPoint);

          // Just doing this for testing collisions
          if (esc)
          {
            Cylinder player = new Cylinder(TILE_SIZE / 4, WALL_HEIGHT);
            player.setTranslateX(0);
            player.setTranslateZ(0);
            player.setTranslateY(WALL_HEIGHT / 2);
            player.setMaterial(playerColor);
            playerXform.getChildren().add(player);
            mapXform.getChildren().add(playerXform);
          }

        }
        else if (tiles[i][j].getTileType() == 'R' || tiles[i][j].getTileType() == 'L')
        {
          if (!debug)
          {
            ceiling.setTranslateY(WALL_HEIGHT + .5);
            mapXform.getChildren().add(ceiling);
          }

          // Just doing this for testing collisions
          tile.setTranslateY(0.5);
          tile.setMaterial(zombieSpawn);
          if (first)
          {
            // Code for making the zombie model
            Group zomb = ZombieBuilder.getZombie(i, j, TILE_SIZE, tiles[i][j].getTileType());

            if (tiles[i][j].getTileType() == 'R')
            {
              zombies.add(new RandomWalk(j, i, zomb));
            }
            else // tiles[i][j] == 'L'
            {
              zombies.add(new LineWalk(j, i, zomb));
            }
            zomb.setTranslateX(i * TILE_SIZE);
            zomb.setTranslateZ(j * TILE_SIZE);
            zomb.setTranslateY(.5);
            zomb.setCache(true);
            zomb.setCacheHint(CacheHint.SPEED);

            // //Zombie model is a cylinder
            // Cylinder c = new Cylinder(TILE_SIZE / 4, WALL_HEIGHT);
            // Group zomb = new Group(c);
            //
            // if (tiles[i][j] == 'R')
            // {
            // c.setMaterial(notPathable);
            // zombies.add(new RandomWalk(j, i, zomb));
            // }
            // else // tiles[i][j] == 'L'
            // {
            // c.setMaterial(zombieSpawn);
            // zombies.add(new LineWalk(j, i, zomb));
            // }
            // zomb.setTranslateX(i * TILE_SIZE);
            // zomb.setTranslateZ(j * TILE_SIZE);
            // zomb.setTranslateY(WALL_HEIGHT / 2);

            world.getChildren().add(zomb);
          }
        }
        else// make a wall tile//
        {
          tile.setScaleY(WALL_HEIGHT);
          tile.setTranslateY(WALL_HEIGHT / 2);
          tile.setMaterial(bricks);
        }
        tile.setCache(true);
        tile.setCacheHint(CacheHint.SPEED);
        mapXform.getChildren().add(tile);
      }
    }

    if (first)
    {
      world.getChildren().add(mapXform);

      // places playerXform on spawn point

      playerXform.t.setZ(house.getPlayerSpawnPoint().x * TILE_SIZE);
      playerXform.t.setX(house.getPlayerSpawnPoint().y * TILE_SIZE);
    }

    mapXform.setVisible(true);
  }
}
