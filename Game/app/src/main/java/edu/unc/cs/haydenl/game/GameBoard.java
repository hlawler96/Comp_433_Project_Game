package edu.unc.cs.haydenl.game;

import android.util.Log;

/**
 * Created by hayden on 10/9/17.
 */

public class GameBoard {

    public Tile[] tiles;
    public int woodCount = 0, wheatCount = 0, brickCount = 0, rockCount = 0, sheepCount = 0, desertCount = 0;

    public GameBoard(){
        tiles = new Tile[19];
        for(Tile t: tiles){
            t = assignType();
        }
    }

    public Tile assignType(){
        Tile.RESOURCE_TYPE type = Tile.random();
        if(woodCount != 4 & type == Tile.RESOURCE_TYPE.WOOD){
            woodCount ++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + woodCount);
            return new Tile(type);
        }else if( wheatCount != 4 && type == Tile.RESOURCE_TYPE.WHEAT){
            wheatCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + wheatCount);
            return new Tile(type);
        }else if( brickCount != 3 && type == Tile.RESOURCE_TYPE.BRICK){
            brickCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + brickCount);
            return new Tile(type);
        }else if( rockCount != 3 && type == Tile.RESOURCE_TYPE.ROCK){
            rockCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + rockCount);
            return new Tile(type);
        }else if( sheepCount != 4 && type == Tile.RESOURCE_TYPE.SHEEP){
            sheepCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + sheepCount);
            return new Tile(type);
        }else if (desertCount != 1 && type == Tile.RESOURCE_TYPE.DESERT){
            desertCount++;
            Log.v("RESOURCE_TEST", "type: " + type + " count: " + desertCount);
            return new Tile(type);
        }else{
            return assignType();
        }
    }





}
