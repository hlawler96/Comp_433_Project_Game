package edu.unc.cs.haydenl.game;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by hayden on 10/9/17.
 */

public class GameBoard {

    public Tile[] tiles;
    public Player[] players;
    public int woodCount = 0, wheatCount = 0, brickCount = 0, rockCount = 0, sheepCount = 0, desertCount = 0, counter;

    public GameBoard(){
        tiles = new Tile[19];
        players  = new Player[4];
        for(int i = 0; i < tiles.length; i++ ){
            tiles[i] = assignType();
        }
        for(int i = 1; i <= players.length; i++){
            players[i-1] = new Player(i);
        }
        counter = 0;
    }

    public Tile getTileForBoard(){
        Tile t = tiles[counter];
        counter++;
        if(counter > tiles.length) return null;
        return t;
    }

    public Tile assignType(){
        Tile.RESOURCE_TYPE type = Tile.random();
        if(woodCount != 4 & type == Tile.RESOURCE_TYPE.WOOD){
            woodCount ++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + woodCount);
            return new Tile(type, Color.rgb(160,82,45));
        }else if( wheatCount != 4 && type == Tile.RESOURCE_TYPE.WHEAT){
            wheatCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + wheatCount);
            return new Tile(type, Color.YELLOW);
        }else if( brickCount != 3 && type == Tile.RESOURCE_TYPE.BRICK){
            brickCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + brickCount);
            return new Tile(type, Color.RED);
        }else if( rockCount != 3 && type == Tile.RESOURCE_TYPE.ROCK){
            rockCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + rockCount);
            return new Tile(type, Color.GRAY);
        }else if( sheepCount != 4 && type == Tile.RESOURCE_TYPE.SHEEP){
            sheepCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + sheepCount);
            return new Tile(type, Color.GREEN);
        }else if (desertCount != 1 && type == Tile.RESOURCE_TYPE.DESERT){
            desertCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + desertCount);
            return new Tile(type, Color.rgb(255,222,173));
        }else{
            return assignType();
        }
    }





}
