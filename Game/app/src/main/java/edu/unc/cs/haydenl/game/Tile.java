package edu.unc.cs.haydenl.game;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by hayden on 10/9/17.
 */

public class Tile {

    public enum RESOURCE_TYPE {WHEAT, ROCK, BRICK, SHEEP, WOOD, DESERT}
    public Spot[] spots;
    public RESOURCE_TYPE type;
    public int color, counter, number;
    public boolean robbed;

    public Tile(RESOURCE_TYPE t) {
        spots = new Spot[6];
        type = t;
        for (int i = 0; i < spots.length; i++) {
            spots[i] = new Spot(t);
        }
        color = typeToColor(type);
        counter = 0;
        number = 0;
        robbed = false;
    }

    public boolean canSettle(int i) {
        if (i > 0) {
            if (spots[i - 1]._player == 0 && spots[i + 1]._player == 0) {
                return true;
            }
        } else {
            if (spots[1]._player == 0 && spots[5]._player == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean build(int i, int player) {
        if (canSettle(i)) {
            spots[i]._player = player;
            return true;
        } else {
            return false;
        }
    }

    public void storeCoordinates(int x, int y){
        spots[counter].x = x;
        spots[counter].y = y;
        counter++;
    }

    public static RESOURCE_TYPE random() {
        int i = (int) (Math.random() * 6);
        if (i == 0) return RESOURCE_TYPE.WHEAT;
        if (i == 1) return RESOURCE_TYPE.ROCK;
        if (i == 2) return RESOURCE_TYPE.BRICK;
        if (i == 3) return RESOURCE_TYPE.SHEEP;
        if (i == 4) return RESOURCE_TYPE.WOOD;
        if(i == 5) return RESOURCE_TYPE.DESERT;
        return null;
    }

    public int typeToColor(RESOURCE_TYPE t){
        int color;
        if(t == RESOURCE_TYPE.WOOD){
            color = Color.rgb(160,82,45);
        }else if(t == RESOURCE_TYPE.BRICK){
            color = Color.RED;
        }else if(t == RESOURCE_TYPE.WHEAT){
            color = Color.YELLOW;
        }else if(t == RESOURCE_TYPE.ROCK) {
            color = Color.GRAY;
        }else if(t == RESOURCE_TYPE.SHEEP) {
            color = Color.GREEN;
        }else if (t == RESOURCE_TYPE.DESERT){
            color = Color.rgb(255,222,173);
        }else{
            color = Color.WHITE;
        }
        return color;
    }

    public boolean inTile(float x, float y){
        int slope = (spots[0].y - spots[5].y) / (spots[0].x - spots[5].x);
        if(x > spots[5].x && x< spots[2].x && y > spots[0].y && y <  spots[3].y
                && y > slope * x + spots[0].y - slope*spots[0].x
                && y < slope * x + spots[3].y - slope*spots[3].x
                && y > -1*slope*x +spots[0].y + slope*spots[0].x
                && y < -1*slope*x + spots[4].y + slope*spots[4].x   ) {

            return true;
        }else{
            return false;
        }
    }
}

class Spot {

    int _player, x, y;
    boolean _city;
    Tile.RESOURCE_TYPE type;

    public Spot(Tile.RESOURCE_TYPE t){
        _player = 0;
        _city = false;
        x = 0;
        y = 0;
        type = t;
    }

    public void settle(int player){
        _player = player;
    }

    public void city(){
        _city = true;
    }

    public String toString(){
        return "Spot at (" + x + "," + y + ") and is owned by player " + _player;
    }

}

class Port {

    Spot left, right;
    Tile.RESOURCE_TYPE type;
    int _x, _y;

    public Port(Spot l, Spot r, Tile.RESOURCE_TYPE t){
        left = l;
        right = r;
        type = t;
    }

    public void setCoord(int x, int y){
        _x = x;
        _y = y;
    }




}
