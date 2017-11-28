package edu.unc.cs.haydenl.game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hayden on 11/22/17.
 */

public class DevCards {

    public enum CARD_TYPE {ROAD_BUILDING , MONOPOLY , KNIGHT, YEAR_OF_PLENTY, VICTORY_POINT}
    public ArrayList<CARD_TYPE> cards;
    public int size, road_counter;

    public DevCards(){
        cards = new ArrayList<>();
        cards.add(CARD_TYPE.ROAD_BUILDING);
        cards.add(CARD_TYPE.ROAD_BUILDING);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.KNIGHT);
        cards.add(CARD_TYPE.MONOPOLY);
        cards.add(CARD_TYPE.MONOPOLY);
        cards.add(CARD_TYPE.YEAR_OF_PLENTY);
        cards.add(CARD_TYPE.YEAR_OF_PLENTY);
        cards.add(CARD_TYPE.VICTORY_POINT);
        cards.add(CARD_TYPE.VICTORY_POINT);
        cards.add(CARD_TYPE.VICTORY_POINT);
        cards.add(CARD_TYPE.VICTORY_POINT);
        cards.add(CARD_TYPE.VICTORY_POINT);
        Collections.shuffle(cards);
        size = cards.size();
        road_counter = 1;
    }

    public CARD_TYPE getCard(){
        CARD_TYPE c = cards.get(0);
        cards.remove(0);
        size--;
        return c;
    }


}
