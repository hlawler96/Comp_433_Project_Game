package edu.unc.cs.haydenl.game;

/**
 * Created by hayden on 10/16/17.
 */

public class GameLogic {

    public GameBoard game;
    public enum GAME_STATE{STEADY, MAIN_MENU, MENU_BUILD, MENU_TRADE, SETTINGS}
    public GAME_STATE state;
    public Player currentPlayer;
    public String message;


    public GameLogic(GameBoard g){
        game = g;
        state = GAME_STATE.STEADY;
        currentPlayer = game.players[0];
        message = "Place a settlement Player " + currentPlayer.id;
    }
}
