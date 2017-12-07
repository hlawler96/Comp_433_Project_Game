package edu.unc.cs.haydenl.game;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hayden on 10/10/17.
 */

public class Player {

    int points, settlements, cities, id, developmentCards, longestRoad, largestArmy, numResourceCards;
    ArrayList<Integer> numbers;
    boolean hasLongestRoad, hasLargestArmy, playedDevCardThisTurn;
    HashMap<Tile.RESOURCE_TYPE, Integer> cards;
    ArrayList<Tile.RESOURCE_TYPE> ports;
    int color;
    Trade trade;
    Dice roll;
    ArrayList<Road> roads;
    ArrayList<DevCards.CARD_TYPE> devCards, newDevCards;

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
        color = Color.rgb(255,255,102);
        if(id == 2)color = Color.rgb(70,130,180);
        if(id == 3)color = Color.rgb(193,47,47);
        if(id == 4) color = Color.rgb(34,139,34);
        developmentCards = 0;
        largestArmy = 0;
        longestRoad = 0;
        numResourceCards = 0;
        trade = null;
        roll = new Dice(this);
        devCards = new ArrayList<>();
        newDevCards = new ArrayList<>();
        ports = new ArrayList<>();
        roads = new ArrayList<>();
        playedDevCardThisTurn = false;
    }

    public void addResource(Tile.RESOURCE_TYPE type){
        if(type != Tile.RESOURCE_TYPE.DESERT) {
            cards.put(type, cards.get(type) + 1);
            int sum = 0;
            for(Tile.RESOURCE_TYPE t : cards.keySet()){
                sum += cards.get(t);
            }
            numResourceCards = sum;
        }
    }

    public void addDevCard(DevCards.CARD_TYPE card){
        newDevCards.add(card);
        developmentCards ++;
        cards.put(Tile.RESOURCE_TYPE.SHEEP, cards.get(Tile.RESOURCE_TYPE.SHEEP) - 1);
        cards.put(Tile.RESOURCE_TYPE.ROCK, cards.get(Tile.RESOURCE_TYPE.ROCK) - 1);
        cards.put(Tile.RESOURCE_TYPE.WHEAT, cards.get(Tile.RESOURCE_TYPE.WHEAT) - 1);
    }

    public void useDevCard(DevCards.CARD_TYPE card){
        devCards.remove(devCards.indexOf(card));
        playedDevCardThisTurn = true;
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
        return cards.get(Tile.RESOURCE_TYPE.ROCK) >= 3 && cards.get(Tile.RESOURCE_TYPE.WHEAT) >= 2 && settlements >= cities && cities < 4;
    }

    public boolean canBuildDevCard(){
        return cards.get(Tile.RESOURCE_TYPE.ROCK) >= 1 && cards.get(Tile.RESOURCE_TYPE.WHEAT) >= 1
                && cards.get(Tile.RESOURCE_TYPE.SHEEP) >= 1;
    }

    public boolean hasEnoughResources(Trade t){
        Log.v("DEBUG_TAG", "Trade: " + trade.toString() + " Player has : " + this.cards.toString());
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

    public void addPorts(Port[] ports){
        for(Port p: ports){
            if(p.left._player == id || p.right._player == id){
                if(!this.ports.contains(p.type)) {
                    this.ports.add(p.type);
                    Log.v("DEBUG_TAG", "Player " + id + " has a port of type " + p.type);
                }
            }
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
            if(trade.inverse().tradeRock % 2 == 1){
                return false;
            }else{
                neg += trade.tradeRock / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.inverse().tradeRock%3 != 0){
                return false;
            }else{
                neg += trade.tradeRock / 3;
            }
        }else{
            if(trade.inverse().tradeRock % 4 != 0){
                return false;
            }else {
                neg += trade.tradeRock / 4;
            }
        }

        if(trade.tradeBrick > 0){
            pos += trade.tradeBrick;
        }else if (ports.contains(Tile.RESOURCE_TYPE.BRICK)){
            if(trade.inverse().tradeBrick%2 == 1){
                return false;
            }else{
                neg += trade.tradeBrick / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.inverse().tradeBrick%3 != 0){
                return false;
            }else{
                neg += trade.tradeBrick / 3;
            }
        }else{
            if(trade.inverse().tradeBrick % 4 != 0){
                return false;
            }else {
                neg += trade.tradeBrick / 4;
            }
        }

        if(trade.tradeSheep > 0){
            pos += trade.tradeSheep;
        }else if (ports.contains(Tile.RESOURCE_TYPE.SHEEP)){
            if(trade.inverse().tradeSheep%2 == 1){
                return false;
            }else{
                neg += trade.tradeSheep / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.inverse().tradeSheep%3 != 0){
                return false;
            }else{
                neg += trade.tradeSheep / 3;
            }
        }else{
            if(trade.inverse().tradeSheep % 4 != 0){
                return false;
            }else {
                neg += trade.tradeSheep / 4;
            }
        }

        if(trade.tradeWood > 0){
            pos += trade.tradeWood;
        }else if (ports.contains(Tile.RESOURCE_TYPE.WOOD)){
            if(trade.inverse().tradeWood%2 == 1){
                return false;
            }else{
                neg += trade.tradeWood / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.inverse().tradeWood%3 != 0){
                return false;
            }else{
                neg += trade.tradeWood / 3;
            }
        }else{
            if(trade.inverse().tradeWood % 4 != 0){
                return false;
            }else {
                neg += trade.tradeWood / 4;
            }
        }

        if(trade.tradeWheat > 0){
            pos += trade.tradeWheat;
        }else if (ports.contains(Tile.RESOURCE_TYPE.WHEAT)){
            if(trade.inverse().tradeWheat%2 == 1){
                return false;
            }else{
                neg += trade.tradeWheat / 2;
            }
        }else if( ports.contains(Tile.RESOURCE_TYPE.DESERT)){
            if(trade.inverse().tradeWheat%3 != 0){
                return false;
            }else{
                neg += trade.tradeWheat / 3;
            }
        }else{
            if(trade.inverse().tradeWheat % 4 != 0){
                return false;
            }else {
                neg += trade.tradeWheat / 4;
            }
        }
        Log.v("DEBUG_TAG", "Pos: " + pos + " , Neg: " + neg);
        if(hasEnoughResources(trade.inverse()))
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

    public int getDevCardCount(DevCards.CARD_TYPE type){
        int count = 0;
        for(DevCards.CARD_TYPE t: devCards){
            if(t == type) count ++;
        }
        for(DevCards.CARD_TYPE t: newDevCards){
            if(t == type) count ++;
        }
        return count;
    }

    public int getUsableDevCardCount(DevCards.CARD_TYPE type){
        int count = 0;
        for(DevCards.CARD_TYPE t: devCards){
            if(t == type) count ++;
        }
        return count;
    }

    public void moveOldDevCards(){
        for(DevCards.CARD_TYPE type: newDevCards){
            devCards.add(type);
        }
        newDevCards.clear();
        playedDevCardThisTurn = false;
    }

    public int playerToImages(boolean city){
        if(id == 1){
            if(city){
                return R.drawable.yellowcity;
            }else{
                return R.drawable.yellowset;
            }

        }else if(id == 2){
            if(city){
                return R.drawable.bluecity;
            }else{
                return R.drawable.blueset;
            }

        }else if(id == 3){
            if(city){
                return R.drawable.redcity;
            }else{
                return R.drawable.redset;
            }

        }else{
            if(city){
                return R.drawable.greencity;
            }else{
                return R.drawable.greenset;
            }

        }
    }
    public boolean canBuildSettlementHere(Spot s, Tile[] tiles, int sideLength){
        for(Tile t: tiles){
            for(Spot spot: t.spots){
                if( Math.sqrt(Math.pow(s.x - spot.x, 2) + Math.pow(s.y - spot.y, 2)) <= sideLength && s._player != 0 ){
                    return false;
                }
            }
        }
        for(Road r: roads){
            if((r.one.x == s.x && r.one.y == s.y) || (r.two.x == s.x && r.two.y == s.y) ){
                return true;
            }
        }
        return false;
    }

    public boolean canBuildSettlement(Tile[] tiles, int sideLength){
        for(Tile t: tiles){
            for(Spot s: t.spots){
                if(canBuildSettlementHere(s, tiles, sideLength)) return true;
            }
        }
        return false;
    }

}
