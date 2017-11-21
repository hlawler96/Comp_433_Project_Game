package edu.unc.cs.haydenl.game;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hayden on 10/10/17.
 */

public class Player {

    int points, settlements, cities, id, developmentCards, longestRoad, largestArmy, numResourceCards;
    ArrayList<Integer> numbers;
    boolean hasLongestRoad, hasLargestArmy;
    HashMap<Tile.RESOURCE_TYPE, Integer> cards;
    int color;
    Trade trade;
    Dice roll;

    public Player(int playerNum){
        id = playerNum;
        points = 0;
        settlements = 0;
        cities = 0;
        numbers = new ArrayList<Integer>();
        hasLongestRoad = false;
        hasLargestArmy = false;
        cards = new HashMap<Tile.RESOURCE_TYPE,Integer>();
        cards.put(Tile.RESOURCE_TYPE.BRICK, 0);
        cards.put(Tile.RESOURCE_TYPE.WOOD, 0);
        cards.put(Tile.RESOURCE_TYPE.ROCK, 0);
        cards.put(Tile.RESOURCE_TYPE.WHEAT, 0);
        cards.put(Tile.RESOURCE_TYPE.SHEEP, 0);
        color = Color.YELLOW;
        if(id == 2)color = Color.BLUE;
        if(id == 3)color = Color.RED;
        if(id == 4) color = Color.GREEN;
        developmentCards = 0;
        largestArmy = 0;
        longestRoad = 0;
        numResourceCards = 0;
        trade = null;
        roll = new Dice(this);
    }

    public void addResource(Tile.RESOURCE_TYPE type){
        if(type != Tile.RESOURCE_TYPE.DESERT) {
            cards.put(type, cards.get(type) + 1);
            numResourceCards++;
        }
    }

    public void useResource(Tile.RESOURCE_TYPE type) {
        cards.put(type, cards.get(type) - 1);
        numResourceCards--;
    }

    public int addSettlement(){
        if(settlements < 6){
            settlements++;
            points++;
        }
        return points;
    }

    public int addCity(){
        if(cities < 5){
            cities++;
            points++;
            settlements--;
        }
        return points;
    }

    public int addLongestRoad(){
        points += 2;
        hasLongestRoad = true;
        return points;
    }

    public int addLargestArmy(){
        points+=2;
        hasLargestArmy = true;
        return points;
    }

    public void addToLongestRoad(){
        longestRoad++;
    }

    public void addToLargestArmy(){
        largestArmy++;
    }

    public void countCards(){
        numResourceCards = cards.get(Tile.RESOURCE_TYPE.WHEAT) + cards.get(Tile.RESOURCE_TYPE.ROCK) + cards.get(Tile.RESOURCE_TYPE.SHEEP) +
                    cards.get(Tile.RESOURCE_TYPE.WOOD) + cards.get(Tile.RESOURCE_TYPE.BRICK);
    }

    public boolean canBuildRoad(){
        return cards.get(Tile.RESOURCE_TYPE.WOOD) >= 1 && cards.get(Tile.RESOURCE_TYPE.BRICK) >= 1;
    }

    public boolean canBuildSettlement(){
        return cards.get(Tile.RESOURCE_TYPE.BRICK) >= 1&& cards.get(Tile.RESOURCE_TYPE.WOOD) >= 1
                && cards.get((Tile.RESOURCE_TYPE.WHEAT)) >= 1 && cards.get(Tile.RESOURCE_TYPE.SHEEP) >= 1;
    }

    public boolean canBuildCity(){
        return cards.get(Tile.RESOURCE_TYPE.ROCK) >= 3 && cards.get(Tile.RESOURCE_TYPE.WHEAT) >= 2;
    }

    public boolean canBuildDevCard(){
        return cards.get(Tile.RESOURCE_TYPE.ROCK) >= 1 && cards.get(Tile.RESOURCE_TYPE.WHEAT) >= 1
                && cards.get(Tile.RESOURCE_TYPE.SHEEP) >= 1;
    }

    public boolean hasEnoughResources(Trade t){
        if(t.tradeBrick <= cards.get(Tile.RESOURCE_TYPE.BRICK) &&
                t.tradeRock <= cards.get(Tile.RESOURCE_TYPE.ROCK) &&
                t.tradeWood <= cards.get(Tile.RESOURCE_TYPE.WOOD) &&
                t.tradeSheep <= cards.get(Tile.RESOURCE_TYPE.SHEEP) &&
                t.tradeWheat <= cards.get(Tile.RESOURCE_TYPE.WHEAT)){
            return true;
        }else {
            return false;
        }
    }

    public void rollDice(){
        roll = new Dice(this);
    }


}
