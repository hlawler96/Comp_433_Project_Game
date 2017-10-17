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
    public int[] numbers;
    public Port[] ports;
    public GameLogic gameLogic;

    public GameBoard(){
        tiles = new Tile[19];
        players  = new Player[4];
        for(int i = 0; i < tiles.length; i++ ){
            tiles[i] = assignType();
        }
        fillNumbers();
        counter = 0;
        for(int i = 1; i <= players.length; i++){
            players[i-1] = new Player(i);
        }
        fillPorts();
        gameLogic = new GameLogic(this);

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
            return new Tile(type);
        }else if( wheatCount != 4 && type == Tile.RESOURCE_TYPE.WHEAT){
            wheatCount++;
            return new Tile(type);
        }else if( brickCount != 3 && type == Tile.RESOURCE_TYPE.BRICK){
            brickCount++;
            return new Tile(type);
        }else if( rockCount != 3 && type == Tile.RESOURCE_TYPE.ROCK){
            rockCount++;
            return new Tile(type);
        }else if( sheepCount != 4 && type == Tile.RESOURCE_TYPE.SHEEP){
            sheepCount++;
            return new Tile(type);
        }else if (desertCount != 1 && type == Tile.RESOURCE_TYPE.DESERT){
            desertCount++;
            return new Tile(type);
        }else{
            return assignType();
        }
    }

    public void fillNumbers(){
        int rand = (int) (Math.random()*6);
        if(rand == 0) {
            int[] indicies = {0,1,2,6,11,15,18,17,16,12,7,3,4,5,10,14,13,8,9};
            giveTilesNumbers(indicies);
        }else if(rand == 1){
            int[] indicies = {2,6,11,15,18,17,16,12,7,3,0,1,5,10,14,13,8,4,9};
            giveTilesNumbers(indicies);
        }else if(rand == 2){
            int[] indicies = {11,15,18,17,16,12,7,3,0,1,2,6,10,14,13,8,4,5,9};
            giveTilesNumbers(indicies);
        }else if(rand == 3){
            int[] indicies = {18,17,16,12,7,3,0,1,2,6,11,15,14,13,8,4,5,10,9};
            giveTilesNumbers(indicies);
        }else if(rand == 4){
            int[] indicies = {16,12,7,3,0,1,2,6,11,15,18,17,13,8,4,5,10,14,9};
            giveTilesNumbers(indicies);
        }else{
            int[] indicies = {7,3,0,1,2,6,11,15,18,17,16,12,8,4,5,10,14,13,9};
            giveTilesNumbers(indicies);
        }
    }

    public void giveTilesNumbers(int[] indicies){
        int[] startNumbers = new int[18];
        numbers = new int[18];
        startNumbers[0] = 5;
        startNumbers[1] = 2;
        startNumbers[2] = 6;
        startNumbers[3] = 3;
        startNumbers[4] = 8;
        startNumbers[5] = 10;
        startNumbers[6] = 9;
        startNumbers[7] = 12;
        startNumbers[8] = 11;
        startNumbers[9] = 4;
        startNumbers[10] = 8;
        startNumbers[11] = 10;
        startNumbers[12] = 9;
        startNumbers[13] = 4;
        startNumbers[14] = 5;
        startNumbers[15] = 6;
        startNumbers[16] = 3;
        startNumbers[17] = 11;
        boolean hitDesert = false;

        for(int i = 0; i < indicies.length;i++){
            if(tiles[indicies[i]].type == Tile.RESOURCE_TYPE.DESERT){
                hitDesert = true;
                tiles[indicies[i]].number = 0;
            }else if(!hitDesert){
                tiles[indicies[i]].number = startNumbers[i];
            }else{
                tiles[indicies[i]].number = startNumbers[i-1];
            }

        }
    }

    public void fillPorts(){
        ports = new Port[9];
        ports[0] = new Port(tiles[0].spots[0], tiles[0].spots[5], Tile.RESOURCE_TYPE.WOOD);
        ports[1] = new Port(tiles[1].spots[1], tiles[1].spots[0], null);
        ports[2] = new Port(tiles[6].spots[1], tiles[6].spots[0], Tile.RESOURCE_TYPE.BRICK);
        ports[3] = new Port(tiles[11].spots[2], tiles[11].spots[1], null);
        ports[4] = new Port(tiles[15].spots[3], tiles[15].spots[2], Tile.RESOURCE_TYPE.SHEEP);
        ports[5] = new Port(tiles[17].spots[3], tiles[17].spots[2], null);
        ports[6] = new Port(tiles[16].spots[4], tiles[16].spots[3], Tile.RESOURCE_TYPE.ROCK);
        ports[7] = new Port(tiles[12].spots[5], tiles[12].spots[4], null);
        ports[8] = new Port(tiles[3].spots[5], tiles[3].spots[4], Tile.RESOURCE_TYPE.WHEAT);
    }

    public void givePortsCoords(){
        for(int i = 0; i < ports.length;i++) {
            int dy = Math.abs(ports[i].right.y - ports[i].left.y) / 2;
            int dx = Math.abs(ports[i].left.x - ports[i].right.x) / 2;
            int x,y;
            if(i == 0 || i == 7 || i == 8) {
                x = ports[i].right.x + dx - dy;
                y = ports[i].right.y - dy - dx;
            }else if (i == 1 || i == 2 || i == 3){
                x = ports[i].left.x - dx +dy;
                y = ports[i].left.y - dy - dx;
            }else if(i ==4 || i == 5){
                x = ports[i].left.x + dx + dy;
                y = ports[i].left.y - dy + dx;
            }else {
                x = ports[i].left.x + dx - dy;
                y = ports[i].left.y + dy + dx;
            }
            ports[i].setCoord(x, y);
        }


    }

    public void giveTilesCoords(int startX, int startY, int iter, int sideLength, int dx, int dy){

        for(int i = 0; i < iter*2; i+=2){
            Tile t = getTileForBoard();
            double length = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
            t.storeCoordinates(startX + i*dx,startY); //cos(30 deg)
            t.storeCoordinates(startX + dx + i*dx, startY + dy);
            t.storeCoordinates(startX + dx + i*dx, startY + dy + sideLength);
            t.storeCoordinates(startX + i*dx, startY + 2*dy + sideLength);
            t.storeCoordinates(startX - dx + i*dx, startY + dy + sideLength);
            t.storeCoordinates(startX - dx + i*dx, startY + dy);
        }

    }


}
