package edu.unc.cs.haydenl.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayden on 10/16/17.
 */

public class GameLogic {

    public GameBoard game;
    public enum GAME_STATE{STEADY, MAIN_MENU, MENU_BUILD, MENU_TRADE_PLAYERS,MENU_TRADE_CHEST,
        ARE_YOU_SURE, TRADE_PROPOSE, TRADE_OVER, ROBBING, MOVE_ROBBER, STEAL_FROM_PLAYER, GAME_START, PLACE_SETTLEMENT, PLACE_ROAD, PLACE_CITY, USE_DEV_CARD, GAME_OVER}
    public GAME_STATE state;
    public Player currentPlayer, playerToTradeWith, playerToStealFrom, longestRoad, largestArmy;
    public String message;
    Tile prevRobbed;
    public boolean builtSettlementNeedRoad;
    public DevCards devCards;

    ArrayList<Player> playersInTrade, playersToRob;
    public int tradeCounter, robCounter;

    public GameLogic(GameBoard g){
        game = g;
        state = GAME_STATE.GAME_START;
        currentPlayer = game.players[0];
        message = "Place a settlement Player " + currentPlayer.id;
        builtSettlementNeedRoad = false;
        devCards = new DevCards();
        longestRoad = null;
        largestArmy = null;

    }

    public List<Player> tradePropose(){
        playersInTrade =  new ArrayList<>();
        tradeCounter = 0;
        Trade trade = currentPlayer.trade;
        if((trade.tradeWheat <= 0 && trade.tradeWood <= 0 && trade.tradeBrick <= 0 && trade.tradeSheep <= 0 && trade.tradeRock <= 0) ||
                (trade.tradeWheat >= 0 && trade.tradeWood >= 0 && trade.tradeBrick >= 0 && trade.tradeSheep >= 0 && trade.tradeRock >= 0)){
            message = "Invalid Trade";
            return playersInTrade;
        }else {
            if ((-1) * currentPlayer.cards.get(Tile.RESOURCE_TYPE.WHEAT) <= trade.tradeWheat &&
                    (-1) * currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD) <= trade.tradeWood &&
                    (-1) * currentPlayer.cards.get(Tile.RESOURCE_TYPE.BRICK) <= trade.tradeBrick &&
                    (-1) * currentPlayer.cards.get(Tile.RESOURCE_TYPE.ROCK) <= trade.tradeRock &&
                    (-1) * currentPlayer.cards.get(Tile.RESOURCE_TYPE.SHEEP) <= trade.tradeSheep) {
                state = GAME_STATE.TRADE_PROPOSE;

                for (int i = 0; i < game.players.length; i++) {
                    Player player = game.players[i];
                    if (player.cards.get(Tile.RESOURCE_TYPE.WHEAT) >= trade.tradeWheat &&
                            player.cards.get(Tile.RESOURCE_TYPE.WOOD) >= trade.tradeWood &&
                            player.cards.get(Tile.RESOURCE_TYPE.BRICK) >= trade.tradeBrick &&
                            player.cards.get(Tile.RESOURCE_TYPE.ROCK) >= trade.tradeRock &&
                            player.cards.get(Tile.RESOURCE_TYPE.SHEEP) >= trade.tradeSheep &&
                            !player.equals(currentPlayer)) {
                        playersInTrade.add(game.players[i]);
                        game.players[i].trade = trade.inverse();
                    }
                }
                if (playersInTrade.size() == 0) {
                    message = "No one is available to trade :(";

                }else {
                    message = "Trade Proposal for Player" + playersInTrade.get(0).id;
                }

            } else {
                message = " You lack sufficient resources!";

            }

            return playersInTrade;
        }
    }

    public void getBestOffer(){

            for(Player offer: playersInTrade){
                if(offer.trade.equals(currentPlayer.trade)){
                    playerToTradeWith = offer;

                }
            }
            for(Player offer: playersInTrade){
                if(offer.trade.accept){
                    playerToTradeWith = offer;

                }
            }

    }

    public void trade(Player one, Player two){
        one.cards.put(Tile.RESOURCE_TYPE.WHEAT, one.cards.get(Tile.RESOURCE_TYPE.WHEAT) + one.trade.tradeWheat);
        one.cards.put(Tile.RESOURCE_TYPE.ROCK, one.cards.get(Tile.RESOURCE_TYPE.ROCK) + one.trade.tradeRock);
        one.cards.put(Tile.RESOURCE_TYPE.BRICK, one.cards.get(Tile.RESOURCE_TYPE.BRICK) + one.trade.tradeBrick);
        one.cards.put(Tile.RESOURCE_TYPE.SHEEP, one.cards.get(Tile.RESOURCE_TYPE.SHEEP) + one.trade.tradeSheep);
        one.cards.put(Tile.RESOURCE_TYPE.WOOD, one.cards.get(Tile.RESOURCE_TYPE.WOOD) + one.trade.tradeWood);
        two.cards.put(Tile.RESOURCE_TYPE.WHEAT, two.cards.get(Tile.RESOURCE_TYPE.WHEAT) + two.trade.tradeWheat);
        two.cards.put(Tile.RESOURCE_TYPE.ROCK, two.cards.get(Tile.RESOURCE_TYPE.ROCK) + two.trade.tradeRock);
        two.cards.put(Tile.RESOURCE_TYPE.BRICK, two.cards.get(Tile.RESOURCE_TYPE.BRICK) + two.trade.tradeBrick);
        two.cards.put(Tile.RESOURCE_TYPE.SHEEP, two.cards.get(Tile.RESOURCE_TYPE.SHEEP) + two.trade.tradeSheep);
        two.cards.put(Tile.RESOURCE_TYPE.WOOD, two.cards.get(Tile.RESOURCE_TYPE.WOOD) + two.trade.tradeWood);
    }

    public void robbery(){
        playersToRob = new ArrayList<>();
        for(Player p: game.players){
            if(p.numResourceCards > 7){
               playersToRob.add(p);
            }
        }
        if(playersToRob.size() > 0){
            state = GAME_STATE.ROBBING;
            robCounter = 0;
        }else{
            state = GAME_STATE.MOVE_ROBBER;
            message = "Player " + currentPlayer.id + " move the Robber";
        }
    }

    public void steal(){
        if(playerToStealFrom.numResourceCards > 0) {
            int cardToSteal = (int) (Math.random() * playerToStealFrom.numResourceCards + 1);

            Tile.RESOURCE_TYPE typeToSteal = Tile.RESOURCE_TYPE.BRICK;
            if (cardToSteal <= playerToStealFrom.cards.get(typeToSteal)) {
                    currentPlayer.cards.put(typeToSteal, currentPlayer.cards.get(typeToSteal) + 1);
                    playerToStealFrom.cards.put(typeToSteal, playerToStealFrom.cards.get(typeToSteal) - 1);
            }else {
                cardToSteal -= playerToStealFrom.cards.get(typeToSteal);
            }

            typeToSteal = Tile.RESOURCE_TYPE.SHEEP;
            if (cardToSteal <= playerToStealFrom.cards.get(typeToSteal)) {
                currentPlayer.cards.put(typeToSteal, currentPlayer.cards.get(typeToSteal) + 1);
                playerToStealFrom.cards.put(typeToSteal, playerToStealFrom.cards.get(typeToSteal) - 1);
            }else {
                cardToSteal -= playerToStealFrom.cards.get(typeToSteal);
            }

            typeToSteal = Tile.RESOURCE_TYPE.WHEAT;
            if (cardToSteal <= playerToStealFrom.cards.get(typeToSteal)) {
                currentPlayer.cards.put(typeToSteal, currentPlayer.cards.get(typeToSteal) + 1);
                playerToStealFrom.cards.put(typeToSteal, playerToStealFrom.cards.get(typeToSteal) - 1);
            }else {
                cardToSteal -= playerToStealFrom.cards.get(typeToSteal);
            }

            typeToSteal = Tile.RESOURCE_TYPE.WOOD;
            if (cardToSteal <= playerToStealFrom.cards.get(typeToSteal)) {
                currentPlayer.cards.put(typeToSteal, currentPlayer.cards.get(typeToSteal) + 1);
                playerToStealFrom.cards.put(typeToSteal, playerToStealFrom.cards.get(typeToSteal) - 1);
            }else {
                typeToSteal = Tile.RESOURCE_TYPE.ROCK;
                currentPlayer.cards.put(typeToSteal, currentPlayer.cards.get(typeToSteal) + 1);
                playerToStealFrom.cards.put(typeToSteal, playerToStealFrom.cards.get(typeToSteal) - 1);
            }

        }
        state = GAME_STATE.STEADY;
        message = "Its your turn Player " + currentPlayer.id;
        playerToStealFrom = null;
    }

    public boolean count3sec(long initTime){
       long elapsedTime = System.currentTimeMillis()- initTime;
        if(elapsedTime / 1000.0 > 3){
            return true;
        }else {
            return false;
        }
    }




}

class Trade{
    public int tradeWheat, tradeWood, tradeBrick, tradeRock, tradeSheep;
    public boolean accept;

    public Trade(){
        tradeBrick = 0;
        tradeRock = 0;
        tradeSheep = 0;
        tradeWheat = 0;
        tradeWood = 0;
        accept = false;
    }

    public Trade(int wheat, int wood, int brick, int rock, int sheep){
        tradeBrick = brick;
        tradeWood = wood;
        tradeRock = rock;
        tradeSheep = sheep;
        tradeWheat = wheat;
    }

    public Trade inverse(){
        return new Trade(-1 * tradeWheat, -1 * tradeWood, -1 * tradeBrick, -1 * tradeRock, -1 * tradeSheep);
    }

    public boolean equals(Trade t){
        if(t.tradeWheat == tradeWheat &&
                t.tradeSheep == tradeSheep &&
                t.tradeWood == tradeWood &&
                t.tradeRock == tradeRock &&
                t.tradeBrick == tradeBrick){
            return true;
        }else {
            return false;
        }
    }

    public boolean isValid(Player p){
        if(((tradeWheat <= 0 && tradeWood <= 0 && tradeBrick <= 0 && tradeSheep <= 0 && tradeRock <= 0) ||
                (tradeWheat >= 0 && tradeWood >= 0 && tradeBrick >= 0 && tradeSheep >= 0 && tradeRock >= 0)) &&
                p.hasEnoughResources(this)){
            return false;
        }else {
            return true;
        }
    }


}
