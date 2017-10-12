package edu.unc.cs.haydenl.game;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by hayden on 10/9/17.
 */

public class GameBoard {

    public Tile[] tiles;
    public Player[] players;
    public int woodCount = 0, wheatCount = 0, brickCount = 0, rockCount = 0, sheepCount = 0, desertCount = 0, counter;
    public ArrayList<Integer> numbers;
    public Port[] ports;
    public GameBoard(){
        tiles = new Tile[19];
        players  = new Player[4];
        fillNumbers();
        for(int i = 0; i < tiles.length; i++ ){
            tiles[i] = assignType();
            if(tiles[i].type != Tile.RESOURCE_TYPE.DESERT) {
                tiles[i].number = numbers.get(0);
                numbers.remove(0);
            }else{
                tiles[i].number = 0;
            }
        }

        for(int i = 1; i <= players.length; i++){
            players[i-1] = new Player(i);
        }
        counter = 0;

        fillPorts();

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

    public void fillNumbers(){
        numbers = new ArrayList<Integer>();
        numbers.add(2);
        numbers.add(3);
        numbers.add(3);
        numbers.add(4);
        numbers.add(4);
        numbers.add(5);
        numbers.add(5);
        numbers.add(6);
        numbers.add(6);
        numbers.add(8);
        numbers.add(8);
        numbers.add(9);
        numbers.add(9);
        numbers.add(10);
        numbers.add(10);
        numbers.add(11);
        numbers.add(11);
        numbers.add(12);
        Collections.shuffle(numbers);

    }

    public void fillPorts(){
        ports = new Port[9];
        ports[0] = new Port(tiles[0].spots[5], tiles[0].spots[0], Tile.RESOURCE_TYPE.WOOD);
        ports[1] = new Port(tiles[1].spots[0], tiles[1].spots[1], null);
        ports[2] = new Port(tiles[6].spots[0], tiles[6].spots[1], Tile.RESOURCE_TYPE.BRICK);
        ports[3] = new Port(tiles[11].spots[1], tiles[11].spots[2], null);
        ports[4] = new Port(tiles[15].spots[2], tiles[15].spots[3], Tile.RESOURCE_TYPE.SHEEP);
        ports[5] = new Port(tiles[17].spots[2], tiles[17].spots[3], null);
        ports[6] = new Port(tiles[16].spots[3], tiles[16].spots[4], Tile.RESOURCE_TYPE.ROCK);
        ports[7] = new Port(tiles[12].spots[4], tiles[12].spots[5], null);
        ports[8] = new Port(tiles[3].spots[4], tiles[3].spots[5], Tile.RESOURCE_TYPE.WHEAT);
    }


}
