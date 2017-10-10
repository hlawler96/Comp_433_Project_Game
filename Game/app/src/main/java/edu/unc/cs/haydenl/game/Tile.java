package edu.unc.cs.haydenl.game;

/**
 * Created by hayden on 10/9/17.
 */

public class Tile {

    public enum RESOURCE_TYPE {WHEAT, ROCK, BRICK, SHEEP, WOOD, DESERT}

    ;
    public Spot[] spots;
    public RESOURCE_TYPE type;

    public Tile(RESOURCE_TYPE t) {
        spots = new Spot[6];
        type = t;
        for (Spot s : spots) {
            s = new Spot();
        }
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
}

class Spot {

    int _player;
    boolean _city;

    public Spot(){
        _player = 0;
        _city = false;
    }

    public void settle(int player){
        _player = player;
    }

    public void city(){
        _city = true;
    }

}
