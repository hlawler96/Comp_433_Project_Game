package com.farmerma.afinal;

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
    ArrayList<Tile.RESOURCE_TYPE> ports;
    int color;
    Trade trade;
    Dice roll;
    ArrayList<Road> roads;
    ArrayList<DevCards.CARD_TYPE> devCards;

    public Player(int playerNum){
        id = playerNum;
        points = 0;
        settlements = 0;
        cities = 0;
        numbers = new ArrayList<Integer>();
        hasLongestRoad = false;
        hasLargestArmy = false;
        cards = new HashMap<>();
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
        devCards = new ArrayList<>();
        ports = new ArrayList<>();
        roads = new ArrayList<>();
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

    public void addDevCard(DevCards.CARD_TYPE card){
        devCards.add(card);
        developmentCards ++;
        cards.put(Tile.RESOURCE_TYPE.SHEEP, cards.get(Tile.RESOURCE_TYPE.SHEEP) - 1);
        cards.put(Tile.RESOURCE_TYPE.ROCK, cards.get(Tile.RESOURCE_TYPE.ROCK) - 1);
        cards.put(Tile.RESOURCE_TYPE.WHEAT, cards.get(Tile.RESOURCE_TYPE.WHEAT) - 1);
    }

    public void useDevCard(DevCards.CARD_TYPE card){
        devCards.remove(devCards.indexOf(card));
    }

    public boolean hasDevCard(DevCards.CARD_TYPE card){
        return devCards.contains(card);
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
                && cards.get((Tile.RESOURCE_TYPE.WHEAT)) >= 1 && cards.get(Tile.RESOURCE_TYPE.SHEEP) >= 1 && settlements - cities < 5;
    }

    public boolean canBuildCity(){
        return cards.get(Tile.RESOURCE_TYPE.ROCK) >= 3 && cards.get(Tile.RESOURCE_TYPE.WHEAT) >= 2 && settlements > cities && cities < 4;
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

    public boolean canTradeChest(){
        int pos = 0 , neg = 0;
        if(trade.tradeRock > 0){
            pos += trade.tradeRock;
        }else if (ports.contains(Tile.RESOURCE_TYPE.ROCK)){
            if(trade.tradeRock%2 == 1){
                return false;
            }else{
                neg += trade.tradeRock / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.tradeRock%3 != 0){
                return false;
            }else{
                neg += trade.tradeRock / 3;
            }
        }else{
            if(trade.tradeRock % 4 != 0){
                return false;
            }else {
                neg += trade.tradeRock / 4;
            }
        }

        if(trade.tradeBrick > 0){
            pos += trade.tradeBrick;
        }else if (ports.contains(Tile.RESOURCE_TYPE.BRICK)){
            if(trade.tradeBrick%2 == 1){
                return false;
            }else{
                neg += trade.tradeBrick / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.tradeBrick%3 != 0){
                return false;
            }else{
                neg += trade.tradeBrick / 3;
            }
        }else{
            if(trade.tradeBrick % 4 != 0){
                return false;
            }else {
                neg += trade.tradeBrick / 4;
            }
        }

        if(trade.tradeSheep > 0){
            pos += trade.tradeSheep;
        }else if (ports.contains(Tile.RESOURCE_TYPE.SHEEP)){
            if(trade.tradeSheep%2 == 1){
                return false;
            }else{
                neg += trade.tradeSheep / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.tradeSheep%3 != 0){
                return false;
            }else{
                neg += trade.tradeSheep / 3;
            }
        }else{
            if(trade.tradeSheep % 4 != 0){
                return false;
            }else {
                neg += trade.tradeSheep / 4;
            }
        }

        if(trade.tradeWood > 0){
            pos += trade.tradeWood;
        }else if (ports.contains(Tile.RESOURCE_TYPE.WOOD)){
            if(trade.tradeWood%2 == 1){
                return false;
            }else{
                neg += trade.tradeWood / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.tradeWood%3 != 0){
                return false;
            }else{
                neg += trade.tradeWood / 3;
            }
        }else{
            if(trade.tradeWood % 4 != 0){
                return false;
            }else {
                neg += trade.tradeWood / 4;
            }
        }

        if(trade.tradeWheat > 0){
            pos += trade.tradeWheat;
        }else if (ports.contains(Tile.RESOURCE_TYPE.WHEAT)){
            if(trade.tradeWheat%2 == 1){
                return false;
            }else{
                neg += trade.tradeWheat / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.tradeWheat%3 != 0){
                return false;
            }else{
                neg += trade.tradeWheat / 3;
            }
        }else{
            if(trade.tradeWheat % 4 != 0){
                return false;
            }else {
                neg += trade.tradeWheat / 4;
            }
        }

        if(pos == (-1*neg))return true;

        return false;
    }

    public void addRoad(Road r){
        int max = recursiveRoadCheck(roads, 1, r.one, r.two);
        roads.add(r);
        if(max > longestRoad){
            longestRoad = max;
        }


    }

    public int recursiveRoadCheck(ArrayList<Road> roads, int currentLength, Spot one, Spot two){

        int max = currentLength;
        int temp = 0;
        for(Road r: roads){
            if (r.one == one){
                ArrayList<Road> clone = (ArrayList<Road>) roads.clone();
                clone.remove(r);
                temp = recursiveRoadCheck(clone, currentLength + 1, r.two, two);
            }else if( r.one == two){
                ArrayList<Road> clone = (ArrayList<Road>) roads.clone();
                clone.remove(r);
                temp = recursiveRoadCheck(clone, currentLength+1, r.two, one);
            }else if( r.two == two){
                ArrayList<Road> clone = (ArrayList<Road>) roads.clone();
                clone.remove(r);
                temp = recursiveRoadCheck(clone, currentLength+1, r.one, one);
            }else if( r.two == one){
                ArrayList<Road> clone = (ArrayList<Road>) roads.clone();
                clone.remove(r);
                temp = recursiveRoadCheck(clone, currentLength+1, r.one, two);
            }
            if(temp > max) max = temp;
        }
        return max;
    }






}
