package edu.unc.cs.haydenl.game;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayden on 10/9/17.
 *
 * TODO:
 *
 * Mason
 * - Get images for tiles
 * - Get images for Ports
 * - Get images for settlements/cities
 * - Add settings menu before game starts
 * - Make home screen look better
 * - Add music
 * - Change color schemes to be more readable
 * - Change player boxes to be less wordy
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameView extends View {

    int width, height, sideLength;
    ArrayList<Spot> settlements;
    GameBoard game;
    boolean setup;
    Context context;
    long time;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context c){
        game = new GameBoard();
        setup = false;
        settlements = new ArrayList<Spot>();
        context = c;
        time = 0;

        this.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                ((GameView) v).onTouch(event.getX(), event.getY());
                return false;

            }
        });


    }

    public void onDraw(Canvas c){
        width = getWidth();
        height = getHeight();

        if(!setup){
            //Setup done here so the value of width is right, given 0 when called in init()
            sideLength = width/19;
            game.counter = 0;
            int dx = (int) Math.round(sideLength*Math.cos(0.523599));
            int dy = (int) Math.round(sideLength*Math.sin(0.523599));
            int startX = width/2 - 2*dx;
            int startY = (int) (height / 2 - (3*dy + 2.5*sideLength));
            game.giveTilesCoords(startX,startY,3,sideLength,dx,dy);
            game.giveTilesCoords(startX-dx, startY + dy + sideLength,4, sideLength,dx,dy);
            game.giveTilesCoords(startX-2*dx, startY + 2*dy + 2*sideLength,5, sideLength,dx,dy);
            game.giveTilesCoords(startX-dx, startY + 3*dy + 3*sideLength,4, sideLength,dx,dy);
            game.giveTilesCoords(startX, startY + 4*dy + 4*sideLength,3, sideLength,dx,dy);
            setup = true;
            game.givePortsCoords();
        }
        Paint p = new Paint();
        drawOcean(c,p);
        drawCurrentPlayerResources(c,p);
        drawPlayerBoxes(c,p);
        drawTiles(c,p);
        drawPorts(c,p);
        if(game.gameLogic.state != GameLogic.GAME_STATE.TRADE_OVER) drawMessageBar(c,p);
        drawRoads(c, p);
        drawSettlements(c,p);
        drawMenu(c);
    }

    public void onTouch(float x, float y){

        if(game.gameLogic.state == GameLogic.GAME_STATE.STEADY) {
            onTouchSteady(x,y);

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MAIN_MENU){
            onTouchMainMenu(x,y);

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MENU_BUILD){
            if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4){
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            }else if(x > width / 2 && y < height / 2 && game.gameLogic.currentPlayer.canBuildCity()){
                game.gameLogic.state = GameLogic.GAME_STATE.PLACE_CITY;
                game.gameLogic.message = "Place a City";
            }else if(x < width / 2 && y < height / 2 && game.gameLogic.currentPlayer.canBuildSettlement()){
                game.gameLogic.state = GameLogic.GAME_STATE.PLACE_SETTLEMENT;
                game.gameLogic.message = "Place a Settlement";
            }else if(x > width / 2 && y > height / 2 && game.gameLogic.currentPlayer.canBuildRoad()){
                game.gameLogic.state = GameLogic.GAME_STATE.PLACE_ROAD;
                game.gameLogic.message = "Place a Road";
            }else if (game.gameLogic.currentPlayer.canBuildDevCard() && game.gameLogic.devCards.size > 0){
                DevCards.CARD_TYPE type = game.gameLogic.devCards.getCard();
                game.gameLogic.currentPlayer.addDevCard(type);
                game.gameLogic.devCards.cards.remove(0);
                if(type == DevCards.CARD_TYPE.VICTORY_POINT){
                    game.gameLogic.currentPlayer.points += 1;
                    if(game.gameLogic.currentPlayer.points >= 10){
                        game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                    }
                }
            }

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MENU_TRADE_PLAYERS){

            onTouchMenuTradePlayers(x,y);

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MENU_TRADE_CHEST){

            onTouchMenuTradeChest(x,y);

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.ARE_YOU_SURE){

            if(x > width/4 && x < 7*width/16 && y > height/2 + 50 && y < height/2 + 50 +height/10){
                quit();
            }else if (x > 9*width/16 && x < 3*width/4 && y > height/2 + 50 && y < height/2 + 50 +height/10){
                game.gameLogic.state = GameLogic.GAME_STATE.MAIN_MENU;
            }
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.TRADE_PROPOSE){

            onTouchMenuProposal(x,y);

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.TRADE_OVER){
            onTouchTradeEnded(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.ROBBING){
            onTouchRobbery(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.MOVE_ROBBER){
            onTouchMoveRobber(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.STEAL_FROM_PLAYER){
            onTouchStealFromPlayer(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.GAME_START){
            onTouchGameStart(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.PLACE_SETTLEMENT){
            onTouchPlaceSettlement(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.PLACE_CITY){
            onTouchPlaceCity(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.PLACE_ROAD){
            onTouchPlaceRoad(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.USE_DEV_CARD){
            onTouchUseDevCard(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.YEAR_OF_PLENTY){
            onTouchYearOfPlenty(x,y);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.MONOPOLY){
            onTouchMonopoly(x,y);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.ROAD_BUILDING){
            onTouchRoadBuilding(x,y);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.GAME_OVER){
            onTouchGameOver(x,y);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.ASK_KNIGHT){
            onTouchAskKnight(x,y);
        }
        this.invalidate();

    }

    private void drawOcean(Canvas c, Paint p){
        p.setColor(Color.rgb(0,119,190));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawRect(0, 0, width, height, p);
    }

    private void drawTiles(Canvas c, Paint p){

        for(Tile t: game.tiles){
            //Represent Tiles as paths for now,
            //TODO Add Graphics for Tiles

            Path path = new Path();
            path.moveTo(t.spots[0].x, t.spots[0].y);
            path.lineTo(t.spots[1].x, t.spots[1].y);
            path.lineTo(t.spots[2].x, t.spots[2].y);
            path.lineTo(t.spots[3].x, t.spots[3].y);
            path.lineTo(t.spots[4].x, t.spots[4].y);
            path.lineTo(t.spots[5].x, t.spots[5].y);
            path.lineTo(t.spots[0].x, t.spots[0].y);
            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);

            //Draws outline Of tiles
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path, p);

            //Draws circles for numbers if they arent the desert
            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3 * (t.spots[2].y - t.spots[1].y) / 4;
            if(t.type!= Tile.RESOURCE_TYPE.DESERT ){
                p.setColor(Color.WHITE);
                p.setStyle(Paint.Style.FILL_AND_STROKE);
                c.drawCircle(centerX, centerY - 20, sideLength / 2, p);
            }

            //draws numbers associated with tiles
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0)c.drawText("" + t.number, centerX, centerY, p);

            if(t.robbed == true) {
                p.setColor(Color.BLACK);
                p.setStyle(Paint.Style.FILL_AND_STROKE);
                c.drawCircle(centerX, centerY - 20, sideLength / 3, p);
            }
        }


    }

    private void drawSettlements(Canvas c, Paint p){
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint outline = new Paint();
        outline.setColor(Color.BLACK);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(3);
        p.setStrokeWidth(3);

        for(Tile t: game.tiles) {
            for (Spot s : t.spots) {
                if(s._player > 0){
                    p.setColor(game.players[s._player - 1].color);
                    if(s._city){
                        c.drawCircle(s.x,s.y,15, p);
                        c.drawCircle(s.x,s.y,15, outline);
                    }else{
                        c.drawCircle(s.x,s.y,10, p);
                        c.drawCircle(s.x,s.y,10, outline);
                    }

                }
            }
        }
    }

    private void drawPlayerBoxes(Canvas c, Paint p){

        //Player 1
        p.setColor(game.players[0].color);
        c.drawRect(0,0, 5* width / 24, height / 6,p);

        p.setColor(Color.BLACK);
        p.setTextSize(24);
        p.setStrokeWidth(1);
        p.setTextAlign(Paint.Align.CENTER);
        c.drawText("Player 1", 5*width/48 - 5, 30, p);
        p.setTextAlign(Paint.Align.LEFT);
        c.drawText("Resource Cards: " + game.players[0].numResourceCards, 10, 65,p);
        c.drawText("Development Cards: " + game.players[0].developmentCards, 10, 90,p);
        c.drawText("Points: " + game.players[0].points, 10, 115,p);
        c.drawText("Longest Road: " + game.players[0].longestRoad, 10, 140,p);
        c.drawText("Largest Army: " + game.players[0].largestArmy,10,165,p);



        //Player 2
        p.setColor(game.players[1].color);
        c.drawRect(19*width/24,0, width , height / 6,p);

        p.setColor(Color.BLACK);
        p.setTextSize(24);
        p.setTextAlign(Paint.Align.CENTER);
        c.drawText("Player 2", 43*width/48-5 , 30, p);
        p.setTextAlign(Paint.Align.LEFT);
        c.drawText("Resource Cards: " + game.players[1].numResourceCards, 19*width/24 + 10, 65,p);
        c.drawText("Development Cards: " + game.players[1].developmentCards, 19*width/24 + 10, 90,p);
        c.drawText("Points: " + game.players[1].points, 19*width/24 + 10, 115,p);
        c.drawText("Longest Road: " + game.players[1].longestRoad, 19*width/24 +10, 140,p);
        c.drawText("Largest Army: " + game.players[1].largestArmy,19*width/24 +10,165,p);

        //Player 3
        p.setColor(game.players[2].color);
        c.drawRect(19*width/24 ,5*height/6, width, height,p);

        p.setColor(Color.BLACK);
        p.setTextSize(24);
        p.setTextAlign(Paint.Align.CENTER);
        c.drawText("Player 3", 43*width/48-5 , 5* height/6 + 30, p);

        p.setTextAlign(Paint.Align.LEFT);
        c.drawText("Resource Cards: " + game.players[2].numResourceCards, 19*width/24 + 10, 5*height/6 + 65,p);
        c.drawText("Development Cards: " + game.players[2].developmentCards, 19*width/24 + 10, 5*height/6 +90,p);
        c.drawText("Points: " + game.players[2].points, 19*width/24 + 10, 5*height/6 +115,p);
        c.drawText("Longest Road: " + game.players[2].longestRoad, 19*width/24 +10, 5*height/6 +140,p);
        c.drawText("Largest Army: " + game.players[2].largestArmy,19*width/24 +10,5*height/6 + 165,p);

        //Player 4
        p.setColor(game.players[3].color);
        c.drawRect(0,5*height/6, 5*width / 24, height,p);

        p.setColor(Color.BLACK);
        p.setTextSize(24);
        p.setTextAlign(Paint.Align.CENTER);
        c.drawText("Player 4", 5*width/48 -5, 5*height/6 + 30, p);

        p.setTextAlign(Paint.Align.LEFT);
        c.drawText("Resource Cards: " + game.players[3].numResourceCards, 10, 5*height/6 +65,p);
        c.drawText("Development Cards: " + game.players[3].developmentCards, 10, 5*height/6 +90,p);
        c.drawText("Points: " + game.players[3].points, 10, 5*height/6 +115,p);
        c.drawText("Longest Road: " + game.players[3].longestRoad, 10, 5*height/6 +140,p);
        c.drawText("Largest Army: " + game.players[3].largestArmy,10,5*height/6 +165,p);


    }

    private void drawPorts(Canvas c, Paint p){
        Port[] ports = game.ports;
        p.setColor(Color.CYAN);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        for(Port prt: ports){
            p.setStrokeWidth(10);
            p.setColor(Color.rgb(139,69,19));
            c.drawLine(prt.left.x,prt.left.y,prt._x, prt._y,p);
            c.drawLine(prt.right.x,prt.right.y,prt._x, prt._y,p);
            p.setColor(game.tiles[0].typeToColor(prt.type));
            if(prt.type == Tile.RESOURCE_TYPE.DESERT) p.setColor(Color.WHITE);
            c.drawCircle(prt._x,prt._y,sideLength/6,p);

        }

    }

    private void drawMenu(Canvas c){

        Paint fullBoxPaint = new Paint();
        fullBoxPaint.setColor(Color.rgb(222,184,135));
        fullBoxPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(2);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(64);
        textPaint.setTextAlign(Paint.Align.CENTER);


        Paint boxOutline = new Paint();
        boxOutline.setStyle(Paint.Style.STROKE);
        boxOutline.setStrokeWidth(10);
        boxOutline.setColor(Color.BLACK);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setTextSize(64);
        circlePaint.setTextAlign(Paint.Align.CENTER);
        if(game.gameLogic.state != GameLogic.GAME_STATE.GAME_START) {
            game.gameLogic.currentPlayer.roll.draw(c, width, height);
        }

        if(game.gameLogic.state == GameLogic.GAME_STATE.STEADY){
            c.drawCircle(width - height/6 - 10, height/2, height / 12, circlePaint);
            circlePaint.setColor(Color.WHITE);
            c.drawText("B", width-height/6 - 10, height/2 + 20 , circlePaint);

            circlePaint.setColor(Color.BLACK);
            c.drawCircle(height/6 + 10, height/2, height /12, circlePaint);
            circlePaint.setColor(Color.WHITE);
            c.drawText("D", height/6 + 10, height / 2 + 20, circlePaint);


        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MAIN_MENU){

            c.drawRect(width/8, height/4, width/2, height/2,fullBoxPaint);
            c.drawRect(width/8, height/4, width/2, height/2,boxOutline);
            c.drawText("BUILD", 5*width/16, 3*height/8,textPaint);

            c.drawRect(width/8, height/2, 4*width/8, 3*height/4,fullBoxPaint);
            c.drawRect(width/8, height/2, 4*width/8, 3*height/4,boxOutline);
            c.drawText("TRADE", 5*width/16, 5*height/8,textPaint);

            c.drawRect(width/2, height/4, 7*width/8, height/2,fullBoxPaint);
            c.drawRect(width/2, height/4, 7*width/8, height/2,boxOutline);
            c.drawText("END TURN", 11*width/16, 3*height/8,textPaint);

            c.drawRect(width/2, height/2, 7*width/8, 3*height/4,fullBoxPaint);
            c.drawRect(width/2, height/2, 7*width/8, 3*height/4,boxOutline);
            c.drawText("QUIT", 11*width/16, 5*height/8,textPaint);

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MENU_BUILD){

            drawMenuBuild(fullBoxPaint, boxOutline, textPaint, c);

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MENU_TRADE_PLAYERS){

            drawTradePlayers(fullBoxPaint, boxOutline, textPaint, c);

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MENU_TRADE_CHEST){

            drawTradeChest(fullBoxPaint, boxOutline, textPaint, c);

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.ARE_YOU_SURE){

            c.drawRect(width/8, height/4,7*width/8,3*height/4,fullBoxPaint);
            c.drawRect(width/8, height/4,7*width/8,3*height/4,boxOutline);
            c.drawText("Are you Sure?", width/2, height/2 - 100, textPaint);
            c.drawText("All of your progress will be lost", width/2, height/2 , textPaint);
            c.drawRect(width/4, height/2 + 50, 7*width / 16, height/2 + 50 + height/10, fullBoxPaint);
            c.drawRect(width/4, height/2 + 50, 7*width / 16, height/2 + 50 + height/10, boxOutline);
            c.drawRect(9*width/16, height/2 + 50, 3*width / 4, height/2 + 50 + height/10, fullBoxPaint);
            c.drawRect(9*width/16, height/2 + 50, 3*width / 4, height/2 + 50 + height/10, boxOutline);
            c.drawText("Ok", 11*width/32, height/2 + height/15 + 50, textPaint);
            c.drawText("NO!", 21*width/32, height/2 + height/15 + 50, textPaint);
        }else if (game.gameLogic.state == GameLogic.GAME_STATE.TRADE_PROPOSE){

            drawTradeProposal(fullBoxPaint, boxOutline, textPaint, c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.TRADE_OVER){

            drawTradeEnded(fullBoxPaint, boxOutline, textPaint, c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.ROBBING){
            drawRobbery(fullBoxPaint, boxOutline, textPaint, c);
        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MOVE_ROBBER){
            drawMoveRobber(fullBoxPaint, boxOutline, textPaint, c);
        }else if( game.gameLogic.state == GameLogic.GAME_STATE.STEAL_FROM_PLAYER){
            drawStealFromPlayer(textPaint,c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.USE_DEV_CARD){
            drawUseDevCard(fullBoxPaint, boxOutline, textPaint, c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MONOPOLY){
            drawMonopoly(fullBoxPaint,boxOutline,textPaint,c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.YEAR_OF_PLENTY){
            drawYearOfPlenty(fullBoxPaint,boxOutline,textPaint,c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.GAME_OVER){
            drawGameOver(fullBoxPaint, boxOutline, textPaint,c);
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.ASK_KNIGHT){
            drawAskKnight(fullBoxPaint, boxOutline, textPaint, c);
        }

    }

    private void drawCurrentPlayerResources(Canvas c, Paint p){

        Paint textPaint = new Paint();
        textPaint.setStrokeWidth(2);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(40);

        Paint outline = new Paint();
        outline.setColor(Color.BLACK);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(10);

        p.setColor(game.gameLogic.currentPlayer.color);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(10);

        c.drawRect(5*width/24, 11*height/12 ,19*width/24, height, p);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(8*width/24 - width/48, 23*height/24, height/ 30, p);
        c.drawCircle(8*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WHEAT), 9*width/24- width/48 - 10, 93*height/96, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(10*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(10*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD), 11*width/24- width/48 - 10, 93*height/96, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(12*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(12*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.ROCK), 13*width/24- width/48 - 10, 93*height/96, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(14*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(14*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.BRICK), 15*width/24- width/48 - 10, 93*height/96, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(16*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(16*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.SHEEP), 17*width/24- width/48 - 10, 93*height/96, textPaint);


    }

    private void drawMessageBar(Canvas c, Paint p){
        p.setStrokeWidth(2);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setTextSize(64);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.WHITE);
        c.drawText(game.gameLogic.message, width/2, height/16,p);

    }

    public void quit(){
        Intent homeIntent = new Intent(context, MainActivity.class);
        context.startActivity(homeIntent);
    }

    public void onTouchSteady(float x, float y){

        if(Math.sqrt(Math.pow(x - (width - height / 6 - 10), 2) + Math.pow(y - height / 2, 2)) <= height / 12){
            game.gameLogic.state = GameLogic.GAME_STATE.MAIN_MENU;
        }else if(Math.sqrt(Math.pow(x - (height / 6 + 10), 2) + Math.pow(y - height / 2, 2)) <= height / 12){
            game.gameLogic.state = GameLogic.GAME_STATE.USE_DEV_CARD;
        }
    }

    public void onTouchMainMenu(float x, float y){

        if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        }else if(x > width/8 && x < width / 2 && y > height / 4 && y < height/2){
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_BUILD;
        }else if(x > width/8 && x < width / 2 && y > height / 2 && y < 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_PLAYERS;
            game.gameLogic.currentPlayer.trade = new Trade();
        }else if(x > width/2 && x < 7 * width / 8 && y > height / 2 && y < 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.ARE_YOU_SURE;
        }else{
            int index = game.gameLogic.currentPlayer.id;
            if(index == 4){
                index = 0;
            }
            game.gameLogic.currentPlayer = game.players[index];
            if(game.gameLogic.currentPlayer.devCards.contains(DevCards.CARD_TYPE.KNIGHT)){
                game.gameLogic.state = GameLogic.GAME_STATE.ASK_KNIGHT;
            }else {
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
                game.gameLogic.currentPlayer.rollDice();
                int roll = game.gameLogic.currentPlayer.roll.one + game.gameLogic.currentPlayer.roll.two;
                if (roll != 7) {
                    game.giveOutResources(roll);
                } else {
                    time = System.currentTimeMillis();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            redraw();
                                        }
                                    });
                                    if (game.gameLogic.count3sec(time)) {
                                        return;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();


                    game.gameLogic.robbery();
                    if (game.gameLogic.state == GameLogic.GAME_STATE.ROBBING) {
                        game.gameLogic.playersToRob.get(game.gameLogic.robCounter).trade = new Trade();
                    }


                }
            }
        }
    }

    public void onTouchMenuTradePlayers(float x, float y){
        Player p = game.gameLogic.currentPlayer;
        if(x> 3*width/8 && x< 5*width/8 && y > height/8 && y < height / 4){
            game.gameLogic.tradePropose();
        }else if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4) {
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        }else if(x > 5*width/8 && y > height/2){
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_CHEST;
            p.trade = new Trade();
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeWheat += 1;
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeWheat -= 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeWood += 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeWood -= 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
           p.trade.tradeRock += 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
           p.trade.tradeRock -= 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeBrick += 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeBrick -= 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeSheep += 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeSheep -= 1;
        }
    }

    public void onTouchMenuTradeChest(float x, float y){
        Player p = game.gameLogic.currentPlayer;
        if(x> 3*width/8 && x< 5*width/8 && y > height/8 && y < height / 4  && game.gameLogic.currentPlayer.canTradeChest()){
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.BRICK, game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.BRICK)+ game.gameLogic.currentPlayer.trade.tradeBrick);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.ROCK, game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.ROCK)+ game.gameLogic.currentPlayer.trade.tradeRock);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.WHEAT, game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WHEAT)+ game.gameLogic.currentPlayer.trade.tradeWheat);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.SHEEP, game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.SHEEP)+ game.gameLogic.currentPlayer.trade.tradeSheep);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.WOOD, game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD)+ game.gameLogic.currentPlayer.trade.tradeWood);
            game.gameLogic.state = GameLogic.GAME_STATE.TRADE_PROPOSE;
        }else if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4) {
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        }else if(x > 5*width/8 && y < height/2){
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_PLAYERS;
            game.gameLogic.currentPlayer.trade = new Trade();
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeWheat += 1;
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeWheat -= 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeWood += 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeWood -= 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeRock += 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeRock -= 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeBrick += 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeBrick -= 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeSheep += 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeSheep -= 1;
        }
    }

    public void drawTradePlayers(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        Player p = game.gameLogic.currentPlayer;
        game.gameLogic.message = "Propose a Trade";
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,boxOutline);

        c.drawRect(5*width/8, height/4, 7*width/8, height/2,fullBoxPaint);
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,boxOutline);
        c.drawText("Players", 6*width/8,3*height/8,textPaint);

        fullBoxPaint.setColor(Color.GRAY);
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Chest", 6*width/8,5*height/8,textPaint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL_AND_STROKE);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(width / 8 + width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWheat, width/8 + width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 1*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 1*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWood, width/8 + 2*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 2*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 2*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeRock, width/8 + 3*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 3*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 3*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeBrick, width/8 + 4*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 4*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 4*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeSheep, width/8 + 5*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 5*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 5*width/12, 5*height/8 + 20, textPaint);

        fullBoxPaint.setColor(Color.rgb(222,184,135));
        c.drawRect(3*width/8, height / 8,5*width/8, height/4, fullBoxPaint);
        c.drawRect(3*width/8, height / 8,5*width/8, height/4, boxOutline);
        c.drawText("Propose" , width/2 , 3*height/16 + 20, textPaint);
    }

    public void drawTradeChest(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        Player p = game.gameLogic.currentPlayer;
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,boxOutline);

        fullBoxPaint.setColor(Color.GRAY);
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,fullBoxPaint);
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,boxOutline);
        c.drawText("Players", 6*width/8,3*height/8,textPaint);

        fullBoxPaint.setColor(Color.rgb(222,184,135));
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Chest", 6*width/8,5*height/8,textPaint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL_AND_STROKE);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(width / 8 + width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWheat, width/8 + width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 1*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 1*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWood, width/8 + 2*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 2*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 2*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeRock, width/8 + 3*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 3*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 3*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeBrick, width/8 + 4*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 4*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 4*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeSheep, width/8 + 5*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 5*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 5*width/12, 5*height/8 + 20, textPaint);

        fullBoxPaint.setColor(Color.rgb(222,184,135));
        c.drawRect(3*width/8, height / 8,5*width/8, height/4, fullBoxPaint);
        c.drawRect(3*width/8, height / 8,5*width/8, height/4, boxOutline);
        c.drawText("Propose" , width/2 , 3*height/16 + 20, textPaint);

    }

    public void drawMenuBuild(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        if(game.gameLogic.currentPlayer.canBuildSettlement()){
            fullBoxPaint.setColor(Color.rgb(222,184,135));
        }else{
            fullBoxPaint.setColor(Color.GRAY);
        }
        c.drawRect(width/8, height/4, width/2, height/2,fullBoxPaint);
        c.drawRect(width/8, height/4, width/2, height/2,boxOutline);
        c.drawText("Settlement", 5*width/16, 3*height/8,textPaint);

        if(game.gameLogic.currentPlayer.canBuildCity()){
            fullBoxPaint.setColor(Color.rgb(222,184,135));
        }else{
            fullBoxPaint.setColor(Color.GRAY);
        }
        c.drawRect(width/2, height/4, 7*width/8, height/2,fullBoxPaint);
        c.drawRect(width/2, height/4, 7*width/8, height/2,boxOutline);
        c.drawText("City", 11 * width/16, 3*height/8,textPaint);

        if(game.gameLogic.currentPlayer.canBuildDevCard()){
            fullBoxPaint.setColor(Color.rgb(222,184,135));
        }else {
            fullBoxPaint.setColor(Color.GRAY);
        }

        c.drawRect(width/8, height/2, width/2, 3*height/4,fullBoxPaint);
        c.drawRect(width/8, height/2, width/2, 3*height/4,boxOutline);
        c.drawText("Dev. Card", 5*width/16, 5*height/8,textPaint);

        if(game.gameLogic.currentPlayer.canBuildRoad()){
            fullBoxPaint.setColor(Color.rgb(222,184,135));
        }else{
            fullBoxPaint.setColor(Color.GRAY);
        }
        c.drawRect(width/2, height/2, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(width/2, height/2, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Road", 11*width/16, 5*height/8,textPaint);
    }

    public void onTouchMenuProposal(float x, float y){
        Player p =  game.gameLogic.playersInTrade.get(game.gameLogic.tradeCounter);
       if(x > width / 8 && x < 3*width / 8 && y < height / 4 && y > height / 8){
           if(p.trade.isValid(game.gameLogic.currentPlayer)) {
               p.trade.accept = true;
           }
           game.gameLogic.tradeCounter++;

           if(game.gameLogic.tradeCounter == game.gameLogic.playersInTrade.size()){
               game.gameLogic.state = GameLogic.GAME_STATE.TRADE_OVER;
           }else {
               game.gameLogic.message = "Trade Proposal for Player " + game.gameLogic.playersInTrade.get(game.gameLogic.tradeCounter).id;
           }
       }else if(x > 3*width / 8 && x < 5*width/ 8 && y < height / 4 && y > height / 8){
           p.trade.accept = false;
           game.gameLogic.tradeCounter++;
           if(game.gameLogic.tradeCounter == game.gameLogic.playersInTrade.size()){
               game.gameLogic.state = GameLogic.GAME_STATE.TRADE_OVER;

           }else {
               game.gameLogic.message = "Trade Proposal for Player " + game.gameLogic.playersInTrade.get(game.gameLogic.tradeCounter).id;
           }
       } else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
        p.trade.tradeWheat += 1;
       }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
        p.trade.tradeWheat -= 1;
       }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
        p.trade.tradeWood += 1;
       }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
          p.trade.tradeWood -= 1;
       }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeRock += 1;
       }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
           p.trade.tradeRock -= 1;
       }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeBrick += 1;
       }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeBrick -= 1;
       }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20){
            p.trade.tradeSheep += 1;
       }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20){
            p.trade.tradeSheep -= 1;
       }

    }

    public void drawTradeProposal(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        Player p = game.gameLogic.playersInTrade.get(game.gameLogic.tradeCounter);
        Player temp = game.gameLogic.currentPlayer;
        game.gameLogic.currentPlayer = p;
        drawCurrentPlayerResources(c,new Paint());
        drawPlayerBoxes(c, new Paint());
        game.gameLogic.currentPlayer = temp;
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,boxOutline);

        c.drawRect(5*width/8, height/4, 7*width/8, height/2,fullBoxPaint);
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,boxOutline);
        c.drawText("Players", 6*width/8,3*height/8,textPaint);

        fullBoxPaint.setColor(Color.GRAY);
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(5*width/8, height/2, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Chest", 6*width/8,5*height/8,textPaint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL_AND_STROKE);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(width / 8 + width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWheat, width/8 + width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 1*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 1*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWood, width/8 + 2*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 2*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 2*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeRock, width/8 + 3*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 3*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 3*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeBrick, width/8 + 4*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 4*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 4*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeSheep, width/8 + 5*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 5*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 5*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(Color.GREEN);
        c.drawRect(width/8, height / 8, 3*width/8, height / 4, fill);
        c.drawRect(width/8, height / 8, 3*width/8, height / 4, boxOutline);
        c.drawText("Accept", width / 4,3 * height / 16, textPaint);

        fill.setColor(Color.RED);
        c.drawRect(3*width/8, height / 8, 5 * width / 8, height / 4, fill);
        c.drawRect(3*width/8, height / 8, 5 * width / 8, height / 4, boxOutline);
        c.drawText("Reject", width / 2,3 * height / 16, textPaint);



    }

    public void drawTradeEnded(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c) {
        Log.v("DEBUG_TAG", "Proposed Trade was " + game.gameLogic.currentPlayer.trade.toString());
        if(game.gameLogic.playerToTradeWith == null){
                game.gameLogic.getBestOffer();
            }
            Player p = game.gameLogic.playerToTradeWith;
            if(p == null){
                game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_PLAYERS;
                drawTradePlayers(fullBoxPaint,boxOutline,textPaint,c);
            }else {
                Paint fill = new Paint();
                fill.setStyle(Paint.Style.FILL_AND_STROKE);
                int counter = 0;
                for(int i = 0; i < 4; i++){
                    if(game.players[i].equals(game.gameLogic.currentPlayer)){
                        i ++;
                    }
                    if(game.players[i].trade.accept && game.players[i].trade.isValid(game.gameLogic.currentPlayer)){
                        textPaint.setStrokeWidth(2);
                        if(game.players[i].trade.equals(game.gameLogic.currentPlayer.trade.inverse())) {
                            fill.setColor(Color.GREEN);
                        }else{
                            Log.v("DEBUG_TAG", game.players[i].trade.toString());
                            fill.setColor(Color.YELLOW);
                        }
                    }else {
                        textPaint.setStrokeWidth(1);
                        fill.setColor(Color.RED);
                    }
                    c.drawRect(width / 8 + counter * width/6, height / 8,  width / 8 + (counter + 1) *width / 6, height / 4, fill);
                    c.drawRect(width / 8 + counter * width/6, height / 8,  width / 8 + (counter + 1) *width / 6, height / 4, boxOutline);
                    c.drawText("Player " + game.players[i].id, width / 8 + width / 12 + counter * width / 6,  3 * height / 16 + 20, textPaint);
                    counter ++;
                }
                game.gameLogic.message = "Offer from Player " + p.id;
                drawMessageBar(c, new Paint());
                c.drawRect(width / 8, height / 4, 5 * width / 8, 3 * height / 4, fullBoxPaint);
                c.drawRect(width / 8, height / 4, 5 * width / 8, 3 * height / 4, boxOutline);

                c.drawRect(5*width/8, height/4, 7*width/8, height/2,fullBoxPaint);
                c.drawRect(5*width/8, height/4, 7*width/8, height/2,boxOutline);
                c.drawText("Accept", 6*width/8,3*height/8 - 10,textPaint);
                c.drawText("Player " + game.gameLogic.playerToTradeWith.id, 6*width/8, 3*height/8 + textPaint.getTextSize() - 10, textPaint);


                c.drawRect(5 * width / 8, height / 2, 7 * width / 8, 3 * height / 4, fullBoxPaint);
                c.drawRect(5 * width / 8, height / 2, 7 * width / 8, 3 * height / 4, boxOutline);
                c.drawText("Reject All", 6 * width / 8, 5 * height / 8, textPaint);

                fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
                c.drawCircle(width / 8 + width / 12, height / 2, height / 20, fill);
                c.drawCircle(width / 8 + width / 12, height / 2, height / 20, boxOutline);
                c.drawText("" + p.trade.inverse().tradeWheat, width / 8 + width / 12, height / 2 + 20, textPaint);
                c.drawRect(width / 8 + 1 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 1 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 1 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
                c.drawRect(width / 8 + 1 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
                c.drawText("+1", width / 8 + 1 * width / 12, 3 * height / 8 + 20, textPaint);
                c.drawText("-1", width / 8 + 1 * width / 12, 5 * height / 8 + 20, textPaint);

                fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
                c.drawCircle(width / 8 + 2 * width / 12, height / 2, height / 20, fill);
                c.drawCircle(width / 8 + 2 * width / 12, height / 2, height / 20, boxOutline);
                c.drawText("" + p.trade.inverse().tradeWood, width / 8 + 2 * width / 12, height / 2 + 20, textPaint);
                c.drawRect(width / 8 + 2 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 2 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 2 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
                c.drawRect(width / 8 + 2 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
                c.drawText("+1", width / 8 + 2 * width / 12, 3 * height / 8 + 20, textPaint);
                c.drawText("-1", width / 8 + 2 * width / 12, 5 * height / 8 + 20, textPaint);

                fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
                c.drawCircle(width / 8 + 3 * width / 12, height / 2, height / 20, fill);
                c.drawCircle(width / 8 + 3 * width / 12, height / 2, height / 20, boxOutline);
                c.drawText("" + p.trade.inverse().tradeRock, width / 8 + 3 * width / 12, height / 2 + 20, textPaint);
                c.drawRect(width / 8 + 3 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 3 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 3 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
                c.drawRect(width / 8 + 3 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
                c.drawText("+1", width / 8 + 3 * width / 12, 3 * height / 8 + 20, textPaint);
                c.drawText("-1", width / 8 + 3 * width / 12, 5 * height / 8 + 20, textPaint);

                fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
                c.drawCircle(width / 8 + 4 * width / 12, height / 2, height / 20, fill);
                c.drawCircle(width / 8 + 4 * width / 12, height / 2, height / 20, boxOutline);
                c.drawText("" + p.trade.inverse().tradeBrick, width / 8 + 4 * width / 12, height / 2 + 20, textPaint);
                c.drawRect(width / 8 + 4 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 4 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 4 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
                c.drawRect(width / 8 + 4 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
                c.drawText("+1", width / 8 + 4 * width / 12, 3 * height / 8 + 20, textPaint);
                c.drawText("-1", width / 8 + 4 * width / 12, 5 * height / 8 + 20, textPaint);

                fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
                c.drawCircle(width / 8 + 5 * width / 12, height / 2, height / 20, fill);
                c.drawCircle(width / 8 + 5 * width / 12, height / 2, height / 20, boxOutline);
                c.drawText("" + p.trade.inverse().tradeSheep, width / 8 + 5 * width / 12, height / 2 + 20, textPaint);
                c.drawRect(width / 8 + 5 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 5 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
                c.drawRect(width / 8 + 5 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
                c.drawRect(width / 8 + 5 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
                c.drawText("+1", width / 8 + 5 * width / 12, 3 * height / 8 + 20, textPaint);
                c.drawText("-1", width / 8 + 5 * width / 12, 5 * height / 8 + 20, textPaint);


            }
    }

    public void onTouchTradeEnded(float x, float y){
        if(x > width / 8 && x < width / 8 + width / 6 && y > height / 8 && y < height / 4){
            if(game.gameLogic.currentPlayer.equals(game.players[0]) && game.players[1].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[1];
            }else if(game.players[0].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[0];
            }
        }else if(x > width / 8  + width/6 && x < width / 8 + width / 3 && y > height / 8 && y < height / 4){
            if((game.gameLogic.currentPlayer.equals(game.players[1]) || game.gameLogic.currentPlayer.equals(game.players[0])) && game.players[2].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[2];
            }else if(game.players[1].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[1];
            }
        }else if(x > width / 8  + width/3 && x < width / 8 + width / 2 && y > height / 8 && y < height / 4){
            if(game.gameLogic.currentPlayer.equals(game.players[3]) && game.players[2].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[2];
            }else if(game.players[3].trade.accept){
                game.gameLogic.playerToTradeWith = game.players[3];
            }
        }else if(x > 5*width/8 && y >  height/4 && x <  7*width/8 && y <  height/2){
            game.gameLogic.trade(game.gameLogic.currentPlayer, game.gameLogic.playerToTradeWith);
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_PLAYERS;
            for(Player p: game.players){
                p.trade = new Trade();
                p.countCards();
            }
            game.gameLogic.playerToTradeWith = null;
        }else if(x > 5*width/8 && y >  height/2 && x <  7*width/8 && y <  3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE_PLAYERS;
            for(Player p: game.players){
                p.trade = new Trade();
            }
            game.gameLogic.playerToTradeWith = null;
        }
    }

    public void drawRobbery(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){

        if(game.gameLogic.count3sec(time)) {
            Player p = game.gameLogic.playersToRob.get(game.gameLogic.robCounter);
            Player temp = game.gameLogic.currentPlayer;
            game.gameLogic.currentPlayer = p;
            drawCurrentPlayerResources(c, new Paint());
            drawPlayerBoxes(c, new Paint());
            game.gameLogic.message = "Player " + p.id + " must get rid of " + p.numResourceCards / 2 + " cards";
            game.gameLogic.currentPlayer = temp;

            c.drawRect(width / 8, height / 4, 5 * width / 8, 3 * height / 4, fullBoxPaint);
            c.drawRect(width / 8, height / 4, 5 * width / 8, 3 * height / 4, boxOutline);

            Paint fill = new Paint();
            fill.setStyle(Paint.Style.FILL_AND_STROKE);

            fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
            c.drawCircle(width / 8 + width / 12, height / 2, height / 20, fill);
            c.drawCircle(width / 8 + width / 12, height / 2, height / 20, boxOutline);
            c.drawText("" + p.trade.tradeWheat, width / 8 + width / 12, height / 2 + 20, textPaint);
            c.drawRect(width / 8 + 1 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 1 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 1 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
            c.drawRect(width / 8 + 1 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 1 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
            c.drawText("+1", width / 8 + 1 * width / 12, 3 * height / 8 + 20, textPaint);
            c.drawText("-1", width / 8 + 1 * width / 12, 5 * height / 8 + 20, textPaint);

            fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
            c.drawCircle(width / 8 + 2 * width / 12, height / 2, height / 20, fill);
            c.drawCircle(width / 8 + 2 * width / 12, height / 2, height / 20, boxOutline);
            c.drawText("" + p.trade.tradeWood, width / 8 + 2 * width / 12, height / 2 + 20, textPaint);
            c.drawRect(width / 8 + 2 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 2 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 2 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
            c.drawRect(width / 8 + 2 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 2 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
            c.drawText("+1", width / 8 + 2 * width / 12, 3 * height / 8 + 20, textPaint);
            c.drawText("-1", width / 8 + 2 * width / 12, 5 * height / 8 + 20, textPaint);

            fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
            c.drawCircle(width / 8 + 3 * width / 12, height / 2, height / 20, fill);
            c.drawCircle(width / 8 + 3 * width / 12, height / 2, height / 20, boxOutline);
            c.drawText("" + p.trade.tradeRock, width / 8 + 3 * width / 12, height / 2 + 20, textPaint);
            c.drawRect(width / 8 + 3 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 3 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 3 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
            c.drawRect(width / 8 + 3 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 3 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
            c.drawText("+1", width / 8 + 3 * width / 12, 3 * height / 8 + 20, textPaint);
            c.drawText("-1", width / 8 + 3 * width / 12, 5 * height / 8 + 20, textPaint);

            fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
            c.drawCircle(width / 8 + 4 * width / 12, height / 2, height / 20, fill);
            c.drawCircle(width / 8 + 4 * width / 12, height / 2, height / 20, boxOutline);
            c.drawText("" + p.trade.tradeBrick, width / 8 + 4 * width / 12, height / 2 + 20, textPaint);
            c.drawRect(width / 8 + 4 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 4 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 4 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
            c.drawRect(width / 8 + 4 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 4 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
            c.drawText("+1", width / 8 + 4 * width / 12, 3 * height / 8 + 20, textPaint);
            c.drawText("-1", width / 8 + 4 * width / 12, 5 * height / 8 + 20, textPaint);

            fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
            c.drawCircle(width / 8 + 5 * width / 12, height / 2, height / 20, fill);
            c.drawCircle(width / 8 + 5 * width / 12, height / 2, height / 20, boxOutline);
            c.drawText("" + p.trade.tradeSheep, width / 8 + 5 * width / 12, height / 2 + 20, textPaint);
            c.drawRect(width / 8 + 5 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 3 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 5 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 5 * height / 8 + height / 20, fill);
            c.drawRect(width / 8 + 5 * width / 12 - height / 20, 3 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 3 * height / 8 + height / 20, boxOutline);
            c.drawRect(width / 8 + 5 * width / 12 - height / 20, 5 * height / 8 - height / 20, width / 8 + 5 * width / 12 + height / 20, 5 * height / 8 + height / 20, boxOutline);
            c.drawText("+1", width / 8 + 5 * width / 12, 3 * height / 8 + 20, textPaint);
            c.drawText("-1", width / 8 + 5 * width / 12, 5 * height / 8 + 20, textPaint);


            int tempColor = fullBoxPaint.getColor();
            if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2)) {
                fullBoxPaint.setColor(Color.GRAY);
            }
            c.drawRect(5 * width / 8, height / 4, 7 * width / 8, 3 * height / 4, fullBoxPaint);
            c.drawRect(5 * width / 8, height / 4, 7 * width / 8, 3 * height / 4, boxOutline);
            c.drawText("Accept", 6 * width / 8, height / 2, textPaint);
            fullBoxPaint.setColor(tempColor);
        }
    }

    public void onTouchRobbery(float x, float y){
        if(game.gameLogic.count3sec(time)) {
            Player p = game.gameLogic.playersToRob.get(game.gameLogic.robCounter);
            if (x > 5 * width / 8 && x < 7 * width / 8 && y > height / 4 && y < 3 * height / 4) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick == (-1 * p.numResourceCards / 2)) {
                    p.cards.put(Tile.RESOURCE_TYPE.BRICK, p.cards.get(Tile.RESOURCE_TYPE.BRICK) + p.trade.tradeBrick);
                    p.cards.put(Tile.RESOURCE_TYPE.ROCK, p.cards.get(Tile.RESOURCE_TYPE.ROCK) + p.trade.tradeRock);
                    p.cards.put(Tile.RESOURCE_TYPE.SHEEP, p.cards.get(Tile.RESOURCE_TYPE.SHEEP) + p.trade.tradeSheep);
                    p.cards.put(Tile.RESOURCE_TYPE.WOOD, p.cards.get(Tile.RESOURCE_TYPE.WOOD) + p.trade.tradeWood);
                    p.cards.put(Tile.RESOURCE_TYPE.WHEAT, p.cards.get(Tile.RESOURCE_TYPE.WHEAT) + p.trade.tradeWheat);
                    p.numResourceCards += p.trade.tradeBrick + p.trade.tradeRock + p.trade.tradeSheep + p.trade.tradeWood + p.trade.tradeWheat;

                    if (game.gameLogic.playersToRob.size() > game.gameLogic.robCounter + 1) {
                        game.gameLogic.robCounter++;
                        game.gameLogic.playersToRob.get(game.gameLogic.robCounter).trade = new Trade();
                    } else {
                        game.gameLogic.state = GameLogic.GAME_STATE.MOVE_ROBBER;
                        game.gameLogic.message = "Player " + game.gameLogic.currentPlayer.id + " move the Robber";


                    }
                }
            } else if (x > width / 8 + 1 * width / 12 - height / 20 && x < width / 8 + 1 * width / 12 + height / 20 && y > 3 * height / 8 - height / 20 && y < 3 * height / 8 + height / 20) {
                if (p.trade.tradeWheat < 0) p.trade.tradeWheat += 1;
            } else if (x > width / 8 + 1 * width / 12 - height / 20 && x < width / 8 + 1 * width / 12 + height / 20 && y > 5 * height / 8 - height / 20 && y < 5 * height / 8 + height / 20) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2) &&
                        p.cards.get(Tile.RESOURCE_TYPE.WHEAT) >= -1 * p.trade.tradeWheat + 1)
                    p.trade.tradeWheat -= 1;
            } else if (x > width / 8 + 2 * width / 12 - height / 20 && x < width / 8 + 2 * width / 12 + height / 20 && y > 3 * height / 8 - height / 20 && y < 3 * height / 8 + height / 20) {
                if (p.trade.tradeWood < 0) p.trade.tradeWood += 1;
            } else if (x > width / 8 + 2 * width / 12 - height / 20 && x < width / 8 + 2 * width / 12 + height / 20 && y > 5 * height / 8 - height / 20 && y < 5 * height / 8 + height / 20) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2) &&
                        p.cards.get(Tile.RESOURCE_TYPE.WOOD) >= -1 * p.trade.tradeWood + 1)
                    p.trade.tradeWood -= 1;
            } else if (x > width / 8 + 3 * width / 12 - height / 20 && x < width / 8 + 3 * width / 12 + height / 20 && y > 3 * height / 8 - height / 20 && y < 3 * height / 8 + height / 20) {
                if (p.trade.tradeRock < 0) p.trade.tradeRock += 1;
            } else if (x > width / 8 + 3 * width / 12 - height / 20 && x < width / 8 + 3 * width / 12 + height / 20 && y > 5 * height / 8 - height / 20 && y < 5 * height / 8 + height / 20) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2) &&
                        p.cards.get(Tile.RESOURCE_TYPE.ROCK) >= -1 * p.trade.tradeRock + 1)
                    p.trade.tradeRock -= 1;
            } else if (x > width / 8 + 4 * width / 12 - height / 20 && x < width / 8 + 4 * width / 12 + height / 20 && y > 3 * height / 8 - height / 20 && y < 3 * height / 8 + height / 20) {
                if (p.trade.tradeBrick < 0) p.trade.tradeBrick += 1;
            } else if (x > width / 8 + 4 * width / 12 - height / 20 && x < width / 8 + 4 * width / 12 + height / 20 && y > 5 * height / 8 - height / 20 && y < 5 * height / 8 + height / 20) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2) &&
                        p.cards.get(Tile.RESOURCE_TYPE.BRICK) >= -1 * p.trade.tradeBrick + 1)
                    p.trade.tradeBrick -= 1;
            } else if (x > width / 8 + 5 * width / 12 - height / 20 && x < width / 8 + 5 * width / 12 + height / 20 && y > 3 * height / 8 - height / 20 && y < 3 * height / 8 + height / 20) {
                if (p.trade.tradeSheep < 0) p.trade.tradeSheep += 1;
            } else if (x > width / 8 + 5 * width / 12 - height / 20 && x < width / 8 + 5 * width / 12 + height / 20 && y > 5 * height / 8 - height / 20 && y < 5 * height / 8 + height / 20) {
                if (p.trade.tradeRock + p.trade.tradeWheat + p.trade.tradeWood + p.trade.tradeSheep + p.trade.tradeBrick != (-1 * p.numResourceCards / 2) &&
                        p.cards.get(Tile.RESOURCE_TYPE.SHEEP) >= -1 * p.trade.tradeSheep + 1)
                    p.trade.tradeSheep -= 1;
            }
        }
    }

    public void onTouchMoveRobber(float x, float y){
        if(game.gameLogic.count3sec(time)) {
            Tile toRob = null;
            for (Tile t : game.tiles) {
                if (t.inTile(x, y) && t != game.gameLogic.prevRobbed) {
                    game.gameLogic.message = "Are you sure?";
                    toRob = t;
                }
            }
            if(toRob != null && toRob != game.gameLogic.prevRobbed){
                for(Tile t: game.tiles){
                    if(t != toRob){
                        t.robbed = false;
                    }
                }
                toRob.robbed = true;
                game.gameLogic.prevRobbed.robbed = false;

            }

            if (Math.sqrt(Math.pow(x - (width - height / 6 - 10), 2) + Math.pow(y - height / 2, 2)) <= height / 12 && toRob != game.gameLogic.prevRobbed) {
                game.gameLogic.prevRobbed = toRob;
                ArrayList<Player> playersToStealFrom = new ArrayList<>();
                for(Tile t: game.tiles){
                    if(t.robbed){
                        toRob = t;
                    }
                }
                for(Spot s: toRob.spots){
                    if(s._player != 0 && s._player != game.gameLogic.currentPlayer.id && game.players[s._player-1].numResourceCards > 0){
                        playersToStealFrom.add(game.players[s._player - 1]);
                    }
                }
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
                game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;

                if(playersToStealFrom.size() == 1){
                    game.gameLogic.playerToStealFrom = playersToStealFrom.get(0);
                    game.gameLogic.steal();
                    if(game.gameLogic.usedKnightBeforeRoll){
                            game.gameLogic.currentPlayer.rollDice();
                            int roll = game.gameLogic.currentPlayer.roll.one + game.gameLogic.currentPlayer.roll.two;
                            if (roll != 7) {
                                game.giveOutResources(roll);
                            } else {
                                time = System.currentTimeMillis();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (true) {
                                            try {
                                                Thread.sleep(1000);
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        redraw();
                                                    }
                                                });
                                                if (game.gameLogic.count3sec(time)) {
                                                    return;
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();


                                game.gameLogic.robbery();
                                if (game.gameLogic.state == GameLogic.GAME_STATE.ROBBING) {
                                    game.gameLogic.playersToRob.get(game.gameLogic.robCounter).trade = new Trade();
                                }



                        }
                        game.gameLogic.usedKnightBeforeRoll = false;
                    }
                }else {
                    for (Player p : playersToStealFrom) {
                        if (p.numResourceCards > 0) {
                            game.gameLogic.state = GameLogic.GAME_STATE.STEAL_FROM_PLAYER;
                            game.gameLogic.message = "Choose which player to steal from";
                        }
                    }
                }
                time = 0;
                for (Player p : game.players) {
                    p.trade = new Trade();
                }
            }
        }

    }

    public void drawMoveRobber(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        if(game.gameLogic.count3sec(time)) {
            for (Tile t : game.tiles) {
                if (t.robbed && t != game.gameLogic.prevRobbed) {
                    Paint circlePaint = new Paint();
                    circlePaint.setColor(Color.GREEN);
                    circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    c.drawCircle(width - height / 6 - 10, height / 2, height / 12, circlePaint);
                    c.drawText("✓", width - height / 6 - 10, height / 2 + 20, textPaint);
                }
            }

        }
    }

    public void redraw(){
        this.invalidate();
    }

    public void onTouchStealFromPlayer(float x, float y){


        if(game.gameLogic.playerToStealFrom != null && Math.sqrt(Math.pow(x - (width - height / 6 - 10), 2) + Math.pow(y - height / 2, 2)) <= height / 12){
            game.gameLogic.steal();
            if(game.gameLogic.usedKnightBeforeRoll){
                    game.gameLogic.currentPlayer.rollDice();
                    int roll = game.gameLogic.currentPlayer.roll.one + game.gameLogic.currentPlayer.roll.two;
                    if (roll != 7) {
                        game.giveOutResources(roll);
                    } else {
                        time = System.currentTimeMillis();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                redraw();
                                            }
                                        });
                                        if (game.gameLogic.count3sec(time)) {
                                            return;
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();


                        game.gameLogic.robbery();
                        if (game.gameLogic.state == GameLogic.GAME_STATE.ROBBING) {
                            game.gameLogic.playersToRob.get(game.gameLogic.robCounter).trade = new Trade();
                        }


                    }

                game.gameLogic.usedKnightBeforeRoll = false;
            }

        }else {
            for (Tile t : game.tiles) {
                if (t.robbed) {
                    Spot minSpot = null;
                    double minDistance = sideLength;
                    for (Spot s : t.spots) {
                        if (Math.sqrt(Math.pow(x - s.x, 2) + Math.pow(y - s.y, 2)) <= minDistance && s._player != 0 && game.players[s._player - 1] != game.gameLogic.currentPlayer) {
                            minDistance = Math.sqrt(Math.pow(x - s.x, 2) + Math.pow(y - s.y, 2));
                            minSpot = s;
                        }
                    }
                    if (minSpot != null) {
                        game.gameLogic.playerToStealFrom = game.players[minSpot._player - 1];
                        game.gameLogic.message = "Steal from Player " + minSpot._player + "?";
                    }
                }
            }

        }
    }

    public void drawStealFromPlayer(Paint textPaint, Canvas c){
        if(game.gameLogic.playerToStealFrom != null){
            Paint circlePaint = new Paint();
            circlePaint.setColor(Color.GREEN);
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(width - height / 6 - 10, height / 2, height / 12, circlePaint);
            c.drawText("✓", width - height / 6 - 10, height / 2 + 20, textPaint);
        }
    }

    public void onTouchGameStart(float x, float y){

        if(game.gameLogic.builtSettlementNeedRoad){
            Player p = game.gameLogic.currentPlayer;
            boolean closeEnough = false;
            for(Tile t: game.tiles){
                for(Spot s: t.spots){
                    if(s._player == p.id ){
                        if(Math.sqrt(Math.pow(x-s.x,2) + Math.pow(y-s.y,2)) < sideLength * 1.5){
                            closeEnough = true;
                        }
                    }
                }
            }
            if(closeEnough) {
                int wood = p.cards.get(Tile.RESOURCE_TYPE.WOOD);
                onTouchPlaceRoad(x, y);
                game.gameLogic.state = GameLogic.GAME_STATE.GAME_START;
                if (wood > p.cards.get(Tile.RESOURCE_TYPE.WOOD)) {
                    p.cards.put(Tile.RESOURCE_TYPE.BRICK, p.cards.get(Tile.RESOURCE_TYPE.BRICK) + 1);
                    p.cards.put(Tile.RESOURCE_TYPE.WOOD, p.cards.get(Tile.RESOURCE_TYPE.WOOD) + 1);
                    game.gameLogic.builtSettlementNeedRoad = false;
                    if(game.gameLogic.snake){
                        int counter = game.gameLogic.currentPlayer.id;
                        if(counter > 1){
                            game.gameLogic.currentPlayer = game.players[counter - 2];
                        }
                    }else{
                        int counter = game.gameLogic.currentPlayer.id;
                        if (counter == 4) {
                            game.gameLogic.snake = true;
                            counter = 3;
                        }
                        game.gameLogic.currentPlayer = game.players[counter];
                    }

                    game.gameLogic.message = "Place a settlement Player " + game.gameLogic.currentPlayer.id;
                    if (game.gameLogic.currentPlayer.points == 2) {
                        game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
                        game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;
                        game.gameLogic.currentPlayer.rollDice();
                        int roll = game.gameLogic.currentPlayer.roll.one + game.gameLogic.currentPlayer.roll.two;
                        if (roll != 7) {
                            game.giveOutResources(roll);
                        } else {
                            game.gameLogic.message = "Player " + game.gameLogic.currentPlayer.id + " Move the Robber";
                            game.gameLogic.state = GameLogic.GAME_STATE.MOVE_ROBBER;
                        }
                    }
                }
            }

        } else {
            double min = 250;
            Spot minSpot = null;
            for (Tile t : game.tiles) {
                for (Spot s : t.spots) {
                    double distance = Math.sqrt(Math.pow(x - s.x, 2) + Math.pow(y - s.y, 2));
                    if (distance < min) {
                        minSpot = s;
                        min = distance;
                    }
                }
            }
            ArrayList<Spot> spotsToSettle = new ArrayList<>();
            if (min < sideLength * 1.5) {
                for (Tile t : game.tiles) {
                    for (Spot s : t.spots) {
                        if (Math.sqrt(Math.pow(s.x - minSpot.x, 2) + Math.pow(s.y - minSpot.y, 2)) <= sideLength && s._player != 0) {
                            game.gameLogic.message = "Can't settle there";
                            return;
                        }
                        if (s.x == minSpot.x && s.y == minSpot.y) {
                            spotsToSettle.add(s);
                        }
                    }
                }

                for (Spot s : spotsToSettle) {
                    for(Port p: game.ports){
                        if (p._x == s.x && p._y == s.y && !game.gameLogic.currentPlayer.ports.contains(p.type)){
                            game.gameLogic.currentPlayer.ports.add(p.type);
                        }
                    }
                    s._player = game.gameLogic.currentPlayer.id;
                    if (game.gameLogic.currentPlayer.points == 1) {
                        game.gameLogic.currentPlayer.addResource(s.type);
                    }
                }
                game.gameLogic.currentPlayer.addSettlement();
                game.gameLogic.builtSettlementNeedRoad = true;
                game.gameLogic.message = "Place a Road Player "+ game.gameLogic.currentPlayer.id;

            }
        }
    }

    public void onTouchPlaceSettlement(float x, float y){

        double min = 250;
        Spot minSpot = null;
        for (Tile t : game.tiles) {
            for (Spot s : t.spots) {
                double distance = Math.sqrt(Math.pow(x - s.x, 2) + Math.pow(y - s.y, 2));
                if (distance < min) {
                    minSpot = s;
                    min = distance;
                }
            }
        }
        ArrayList<Spot> spotsToSettle = new ArrayList<>();
        if (min < sideLength * 1.5) {
            for (Tile t : game.tiles) {
                for (Spot s : t.spots) {
                    if (Math.sqrt(Math.pow(s.x - minSpot.x, 2) + Math.pow(s.y - minSpot.y, 2)) <= sideLength && s._player != 0) {
                        game.gameLogic.message = "Can't settle there";
                        return;
                    }
                    if (s.x == minSpot.x && s.y == minSpot.y) {
                        spotsToSettle.add(s);
                    }
                }
            }

            for (Spot s : spotsToSettle) {
                for(Port p: game.ports){
                    if (p._x == s.x && p._y == s.y && !game.gameLogic.currentPlayer.ports.contains(p.type)){
                        game.gameLogic.currentPlayer.ports.add(p.type);
                    }
                }
                s._player = game.gameLogic.currentPlayer.id;
                if (game.gameLogic.currentPlayer.points == 1) {
                    game.gameLogic.currentPlayer.addResource(s.type);
                }
            }
            game.gameLogic.currentPlayer.addSettlement();
            if(game.gameLogic.currentPlayer.points >= 10) {
                game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
            }else{
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            }
        }
    }

    public void onTouchPlaceRoad(float x, float y){
        double min = sideLength * 1.75;
        Spot minSpotOne = null, minSpotTwo = null;
        double minSpotOneDistance = 0, minSpotTwoDistance = 0;
        for(Tile t: game.tiles){
            for(Spot s: t.spots){
                if(minSpotOne == null){
                    minSpotOne = s;
                    minSpotOneDistance = Math.sqrt(Math.pow(s.x - x, 2) + Math.pow(s.y - y, 2));
                } else if(minSpotTwo == null) {
                    minSpotTwo = s;
                    minSpotTwoDistance =  Math.sqrt(Math.pow(s.x - x, 2) + Math.pow(s.y - y, 2));
                }
                double sDistance = Math.sqrt(Math.pow(s.x - x, 2) + Math.pow(s.y - y, 2));
                if(sDistance < minSpotOneDistance){
                    minSpotTwo = minSpotOne;
                    minSpotTwoDistance = minSpotOneDistance;
                    minSpotOne = s;
                    minSpotOneDistance = sDistance;
                }else if(sDistance < minSpotTwoDistance && sDistance != minSpotOneDistance){
                    minSpotTwo = s;
                    minSpotTwoDistance = sDistance;
                }
            }
        }

        if(minSpotOneDistance < min && minSpotTwoDistance < min){
            Road r = new Road(minSpotOne, minSpotTwo);
            for(Player p: game.players){
                for(Road road: p.roads){
                    if (road.one.x == r.one.x && road.one.y == r.one.y && road.two.x == r.two.x && road.two.y == r.two.y){
                        return;
                    }
                }
            }
            boolean canBuildRoad = false;
            for(Tile t: game.tiles){
                for(Spot s: t.spots){
                    if((s.x == minSpotOne.x && s.y == minSpotOne.y && s._player == game.gameLogic.currentPlayer.id) ||
                            (s.x == minSpotTwo.x && s.y ==minSpotTwo.y &&s._player == game.gameLogic.currentPlayer.id)){
                        canBuildRoad = true;
                    }
                }
            }

                for(Road road: game.gameLogic.currentPlayer.roads){
                    if((road.one.x == r.one.x && road.one.y == r.one.y) || (road.two.x == r.two.x && road.two.y == r.two.y) ){
                        canBuildRoad = true;
                    }
                }
            if(canBuildRoad) {
                Player p = game.gameLogic.currentPlayer;
                p.addRoad(r);
                p.cards.put(Tile.RESOURCE_TYPE.BRICK, p.cards.get(Tile.RESOURCE_TYPE.BRICK) - 1);
                p.cards.put(Tile.RESOURCE_TYPE.WOOD, p.cards.get(Tile.RESOURCE_TYPE.WOOD) - 1);
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
                game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;

                if (game.gameLogic.longestRoad != null) {
                    if (game.gameLogic.longestRoad.longestRoad < game.gameLogic.currentPlayer.longestRoad) {
                        game.gameLogic.longestRoad.hasLongestRoad = false;
                        game.gameLogic.longestRoad.points -= 2;
                        game.gameLogic.longestRoad = game.gameLogic.currentPlayer;
                        int points = game.gameLogic.longestRoad.addLongestRoad();
                        if(points >= 10){
                            game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                        }
                    }
                } else if (p.longestRoad == 5) {
                    game.gameLogic.longestRoad = p;
                    int points = game.gameLogic.longestRoad.addLongestRoad();
                    if(points >= 10){
                        game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                    }
                }
            }
        }
    }

    public void onTouchPlaceCity(float x, float y){
        double min = sideLength * 1.5;
        ArrayList<Spot> spotsToAdd = new ArrayList<>();
        for(Tile t: game.tiles){
            for(Spot s: t.spots){
                double distance = Math.sqrt(Math.pow(x - s.x,2) + Math.pow(y - s.y,2));
                if(distance < min){
                    min = distance;
                    spotsToAdd.clear();
                    spotsToAdd.add(s);
                }else if(distance == min){
                    spotsToAdd.add(s);
                }
            }
        }
        if(spotsToAdd.size() == 0) return;
        for(Spot s: spotsToAdd){
            if(s._player!= game.gameLogic.currentPlayer.id){
                return;
            }
            s._city = true;
        }
        Player p = game.gameLogic.currentPlayer;
        game.gameLogic.currentPlayer.addCity();
        p.cards.put(Tile.RESOURCE_TYPE.WHEAT, p.cards.get(Tile.RESOURCE_TYPE.WHEAT)-1);
        p.cards.put(Tile.RESOURCE_TYPE.WHEAT, p.cards.get(Tile.RESOURCE_TYPE.WHEAT)-1);
        p.cards.put(Tile.RESOURCE_TYPE.ROCK, p.cards.get(Tile.RESOURCE_TYPE.ROCK)-1);
        p.cards.put(Tile.RESOURCE_TYPE.ROCK, p.cards.get(Tile.RESOURCE_TYPE.ROCK)-1);
        p.cards.put(Tile.RESOURCE_TYPE.ROCK, p.cards.get(Tile.RESOURCE_TYPE.ROCK)-1);
        game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        if(p.points >= 10) game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
    }

    public void drawRoads(Canvas c, Paint roadPaint){
        roadPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for(Player p: game.players){
            roadPaint.setColor(p.color);
            for(Road r: p.roads){
                roadPaint.setStrokeWidth(10);
                roadPaint.setColor(Color.BLACK);
                c.drawLine(r.one.x, r.one.y, r.two.x, r.two.y, roadPaint);
                roadPaint.setStrokeWidth(8);
                roadPaint.setColor(p.color);
                c.drawLine(r.one.x, r.one.y, r.two.x, r.two.y, roadPaint);
            }
        }
    }

    public void drawUseDevCard(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        int yes = fullBoxPaint.getColor();
        int no = Color.GRAY;
        int count = game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.ROAD_BUILDING);
        if(count == 0){
            fullBoxPaint.setColor(no);
        }
        c.drawRect(width/8, height/4, 3*width/8, height/2,fullBoxPaint);
        c.drawRect(width/8, height/4, 3*width/8, height/2,boxOutline);
        c.drawText("Road Building", width / 4, 3*height / 8, textPaint);
        c.drawText("" + count, width / 4, 3*height / 8 + height / 16, textPaint);

        count = game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.MONOPOLY);
        if(count > 0){
            fullBoxPaint.setColor(yes);
        }else{
            fullBoxPaint.setColor(no);
        }
        c.drawRect(3*width/8, height/4, 5*width/8, height/2,fullBoxPaint);
        c.drawRect(3*width/8, height/4, 5*width/8, height/2,boxOutline);
        c.drawText("Monopoly", width / 2, 3*height / 8, textPaint);
        c.drawText("" + count, width / 2, 3*height / 8 + height / 16, textPaint);

        count = game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.YEAR_OF_PLENTY);
        if(count > 0){
            fullBoxPaint.setColor(yes);
        }else{
            fullBoxPaint.setColor(no);
        }
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,fullBoxPaint);
        c.drawRect(5*width/8, height/4, 7*width/8, height/2,boxOutline);
        c.drawText("Year Of Plenty", 3*width / 4, 3*height / 8, textPaint);
        c.drawText(""+ count, 3*width / 4, 3*height / 8 + height / 16, textPaint);


        count = game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.KNIGHT);
        if(count > 0){
            fullBoxPaint.setColor(yes);
        }else{
            fullBoxPaint.setColor(no);
        }
        c.drawRect(1*width/8, height/2, 4*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(1*width/8, height/2, 4*width/8, 3*height/4,boxOutline);
        c.drawText("Knight", 5*width / 16, 5*height / 8, textPaint);
        c.drawText(""+ count, 5*width / 16, 5*height / 8 + height / 16, textPaint);

        count = game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.VICTORY_POINT);
        if(count > 0){
            fullBoxPaint.setColor(yes);
        }else{
            fullBoxPaint.setColor(no);
        }
        c.drawRect(4*width/8, height/2, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(4*width/8, height/2, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Victory Points", 11*width / 16, 5*height / 8, textPaint);
        c.drawText(""+ count, 11*width / 16, 5*height / 8 + height / 16, textPaint);
    }

    public void onTouchUseDevCard(float x, float y){
        if(x < width / 8 || x > 7*width / 8 || y < height / 4 || y > 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        }else if(y < height / 2 && x < 3*width/8 && game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.ROAD_BUILDING) > 0){
            game.gameLogic.state = GameLogic.GAME_STATE.ROAD_BUILDING;
            game.gameLogic.message = "Player " + game.gameLogic.currentPlayer.id + " Build 2 Roads";
        }else if(y < height / 2 && x < 5*width/8 && game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.MONOPOLY) > 0){
            game.gameLogic.state = GameLogic.GAME_STATE.MONOPOLY;
            game.gameLogic.message = "Select a Resource to Monopolize";
        }else if(y < height / 2 && game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.YEAR_OF_PLENTY) > 0){
            game.gameLogic.state = GameLogic.GAME_STATE.YEAR_OF_PLENTY;
            game.gameLogic.message = "Pick 2 Resources";
            game.gameLogic.currentPlayer.trade = new Trade();
        }else if(y > height / 2 && x < width/2 && game.gameLogic.currentPlayer.getDevCardCount(DevCards.CARD_TYPE.KNIGHT) > 0){
            game.gameLogic.state = GameLogic.GAME_STATE.MOVE_ROBBER;
            game.gameLogic.message = "Player " + game.gameLogic.currentPlayer.id + " Move the Robber";
            game.gameLogic.currentPlayer.addToLargestArmy();
            boolean largestArmyExists = false;
            for(Player p: game.players){
                if(p.hasLargestArmy){
                    largestArmyExists = true;
                    if (game.gameLogic.currentPlayer.largestArmy > p.largestArmy){
                        p.hasLargestArmy = false;
                        int points = game.gameLogic.currentPlayer.addLargestArmy();
                        if(points >= 10){
                            game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                        }
                    }
                }
            }
            if(!largestArmyExists && game.gameLogic.currentPlayer.largestArmy == 3){
                int points = game.gameLogic.currentPlayer.addLargestArmy();
                if(points >= 10){
                    game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                }
            }


        }
    }

    public void drawMonopoly(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        fullBoxPaint.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawRect(5*width/40, height/4, 11*width/40, 3*height/4, fullBoxPaint);
        c.drawRect(5*width/40, height/4, 11*width/40, 3*height/4, boxOutline);
        c.drawText("Wheat",8*width/40, height/2, textPaint);

        fullBoxPaint.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawRect(11*width/40, height/4, 17*width/40, 3*height/4, fullBoxPaint);
        c.drawRect(11*width/40, height/4, 17*width/40, 3*height/4, boxOutline);
        c.drawText("Wood",14*width/40, height/2, textPaint);

        fullBoxPaint.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawRect(17*width/40, height/4, 23*width/40, 3*height/4, fullBoxPaint);
        c.drawRect(17*width/40, height/4, 23*width/40, 3*height/4, boxOutline);
        c.drawText("Rock",20*width/40, height/2, textPaint);

        fullBoxPaint.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawRect(23*width/40, height/4, 29*width/40, 3*height/4, fullBoxPaint);
        c.drawRect(23*width/40, height/4, 29*width/40, 3*height/4, boxOutline);
        c.drawText("Brick",26*width/40, height/2, textPaint);

        fullBoxPaint.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawRect(29*width/40, height/4, 35*width/40, 3*height/4, fullBoxPaint);
        c.drawRect(29*width/40, height/4, 35*width/40, 3*height/4, boxOutline);
        c.drawText("Brick",32*width/40, height/2, textPaint);
    }

    public void onTouchMonopoly(float x, float y){
        Tile.RESOURCE_TYPE t = Tile.RESOURCE_TYPE.DESERT;
        if (y < height / 4 || y > 3*height/4 || x < width/8 || x > 7*width/8){
            return;
        }else if(x < 11*width/40){
            t = Tile.RESOURCE_TYPE.WHEAT;
        }else if(x < 17*width/40){
            t = Tile.RESOURCE_TYPE.WOOD;
        }else if(x < 23*width/40){
            t = Tile.RESOURCE_TYPE.ROCK;
        }else if(x < 29*width/40){
            t = Tile.RESOURCE_TYPE.BRICK;
        }else{
            t = Tile.RESOURCE_TYPE.SHEEP;
        }
        int count = 0;
        for(Player p: game.players){
            int num = p.cards.get(t);
            p.numResourceCards -= num;
            count += num;
            p.cards.put(t, 0);
        }
        game.gameLogic.currentPlayer.cards.put(t, count);
        game.gameLogic.currentPlayer.numResourceCards += count;
        game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
        game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;

    }

    public void drawYearOfPlenty(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(width/8, height/4, 5*width/8, 3*height/4,boxOutline);
        fullBoxPaint.setColor(Color.GREEN);
        c.drawRect(5*width/8, height/4, 7*width/8, 3*height/4,fullBoxPaint);
        c.drawRect(5*width/8, height/4, 7*width/8, 3*height/4,boxOutline);
        c.drawText("Accept", 3*width/4, height/2, textPaint);

        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL_AND_STROKE);
        Player p = game.gameLogic.currentPlayer;

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(width / 8 + width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWheat, width/8 + width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 1*width/12 - height/20, 3*height/8 - height/20, width/8 + 1*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 1*width/12 - height/20, 5*height/8 - height/20, width/8 + 1*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 1*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 1*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 2*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeWood, width/8 + 2*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 2*width/12 - height/20, 3*height/8 - height/20, width/8 + 2*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 2*width/12 - height/20, 5*height/8 - height/20, width/8 + 2*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 2*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 2*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 3*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeRock, width/8 + 3*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 3*width/12 - height/20, 3*height/8 - height/20, width/8 + 3*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 3*width/12 - height/20, 5*height/8 - height/20, width/8 + 3*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 3*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 3*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 4*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeBrick, width/8 + 4*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 4*width/12 - height/20, 3*height/8 - height/20, width/8 + 4*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 4*width/12 - height/20, 5*height/8 - height/20, width/8 + 4*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 4*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 4*width/12, 5*height/8 + 20, textPaint);

        fill.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, fill);
        c.drawCircle(width / 8 + 5*width / 12, height/2, height/20, boxOutline);
        c.drawText("" + p.trade.tradeSheep, width/8 + 5*width/12, height/2 + 20, textPaint);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , fill);
        c.drawRect(width/8 + 5*width/12 - height/20, 3*height/8 - height/20, width/8 + 5*width/12 + height/20,   3*height/8 + height/20 , boxOutline);
        c.drawRect(width/8 + 5*width/12 - height/20, 5*height/8 - height/20, width/8 + 5*width/12 + height/20,   5*height/8 + height/20 , boxOutline);
        c.drawText("+1" , width/8 + 5*width/12, 3*height/8 + 20, textPaint);
        c.drawText("-1" , width/8 + 5*width/12, 5*height/8 + 20, textPaint);

    }

    public void onTouchYearOfPlenty(float x, float y){
        Player p = game.gameLogic.currentPlayer;
        int total = p.trade.tradeBrick + p.trade.tradeWheat + p.trade.tradeRock + p.trade.tradeSheep + p.trade.tradeWood;
        if(x> 5*width/8 && x< 7*width/8 && y > height/4 && y < 3 * height / 4 && total == 2){
            Trade t = p.trade;
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.WHEAT,game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WHEAT) + t.tradeWheat);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.WOOD,game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD) + t.tradeWood);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.BRICK,game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.BRICK) + t.tradeBrick);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.ROCK,game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.ROCK) + t.tradeRock);
            game.gameLogic.currentPlayer.cards.put(Tile.RESOURCE_TYPE.SHEEP,game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.SHEEP) + t.tradeSheep);
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20 && total < 2){
            p.trade.tradeWheat += 1;
        }else if(x > width/8 + 1*width/12 - height / 20 &&  x < width/8 + 1*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20 && p.trade.tradeWheat > 0){
            p.trade.tradeWheat -= 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20 && total < 2){
            p.trade.tradeWood += 1;
        }else if(x > width/8 + 2*width/12 - height / 20 &&  x < width/8 + 2*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20&& p.trade.tradeWood > 0){
            p.trade.tradeWood -= 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20 && total < 2){
            p.trade.tradeRock += 1;
        }else if(x > width/8 + 3*width/12 - height / 20 &&  x < width/8 + 3*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20 && p.trade.tradeRock > 0){
            p.trade.tradeRock -= 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20 && total < 2){
            p.trade.tradeBrick += 1;
        }else if(x > width/8 + 4*width/12 - height / 20 &&  x < width/8 + 4*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20 && p.trade.tradeBrick > 0){
            p.trade.tradeBrick -= 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >3*height/8 - height/20 && y < 3*height/8 + height/20 && total < 2){
            p.trade.tradeSheep += 1;
        }else if(x > width/8 + 5*width/12 - height / 20 &&  x < width/8 + 5*width/12 + height/20 && y >5*height/8 - height/20 && y < 5*height/8 + height/20 && p.trade.tradeSheep > 0){
            p.trade.tradeSheep -= 1;
        }
    }

    public void onTouchRoadBuilding(float x, float y){
        int wood = game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD);
        onTouchPlaceRoad(x,y);
        if(game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD) < wood){
            game.gameLogic.devCards.road_counter++;
        }
        if(game.gameLogic.devCards.road_counter > 2){
            game.gameLogic.devCards.road_counter = 1;
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            game.gameLogic.message = "Its your turn Player " + game.gameLogic.currentPlayer.id;
        }
    }

    public void onTouchGameOver(float x, float y){
        if(x > 5*width/12&& x <8*width/12 && y > height/2 && y < 3*height/4){
            Intent homeIntent = new Intent(context, MainActivity.class);
            context.startActivity(homeIntent);
        }
    }

    public void drawGameOver(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        Player winner = game.players[0];
        for(Player p: game.players){
            if(p.points > winner.points){
                winner = p;
            }
        }
        c.drawRect(width / 4, height / 4, 3*width/4, 3*height/4, fullBoxPaint);
        c.drawRect(width / 4, height / 4, 3*width/4, 3*height/4, boxOutline);
        c.drawText("Congratulations Player "+ winner.id + " Wins!", width/2, 3*height/8, textPaint);

        c.drawRect(5*width/12, height/2 , 8*width/12, 3*height/4, boxOutline);
        c.drawText("Ok", width/2, 5*height/8, textPaint);
    }

    public void drawAskKnight(Paint fullBoxPaint, Paint boxOutline, Paint textPaint, Canvas c){
        c.drawRect(width/4, height/4, 3*width/4, height/2, fullBoxPaint);
        c.drawRect(width/4, height/4, 3*width/4, height/2, boxOutline);
        c.drawText("Do you want to play your Knight?", width/2, 3*height/8, textPaint);

        c.drawRect(width/4, height/2, width/2, 3*height/4, fullBoxPaint);
        c.drawRect(width/4, height/2, width/2, 3*height/4, boxOutline);
        c.drawText("Yes", 3*width/8, 5*height/8, textPaint);

        c.drawRect(width/2, height/2, 3*width/4, 3*height/4, fullBoxPaint);
        c.drawRect(width/2, height/2, 3*width/4, 3*height/4, boxOutline);
        c.drawText("No", 5*width/8, 5*height/8, textPaint);

    }

    public void onTouchAskKnight(float x, float y){
        if(x < width / 2 && x > width/4 && y > height/2 && y < 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.MOVE_ROBBER;
            game.gameLogic.usedKnightBeforeRoll = true;
            game.gameLogic.message = "Player " + game.gameLogic.currentPlayer.id + " Move the Robber";
            game.gameLogic.currentPlayer.addToLargestArmy();
            boolean largestArmyExists = false;
            for(Player p: game.players){
                if(p.hasLargestArmy){
                    largestArmyExists = true;
                    if (game.gameLogic.currentPlayer.largestArmy > p.largestArmy){
                        p.hasLargestArmy = false;
                        int points = game.gameLogic.currentPlayer.addLargestArmy();
                        if(points >= 10){
                            game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                        }
                    }
                }
            }
            if(!largestArmyExists && game.gameLogic.currentPlayer.largestArmy == 3){
                int points = game.gameLogic.currentPlayer.addLargestArmy();
                if(points >= 10){
                    game.gameLogic.state = GameLogic.GAME_STATE.GAME_OVER;
                }
            }
        }else if(x > width / 2 && x < 3*width/4 && y > height/2 && y < 3*height/4){
            game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            game.gameLogic.currentPlayer.rollDice();
            int roll = game.gameLogic.currentPlayer.roll.one + game.gameLogic.currentPlayer.roll.two;
            if (roll != 7) {
                game.giveOutResources(roll);
            } else {
                time = System.currentTimeMillis();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        redraw();
                                    }
                                });
                                if (game.gameLogic.count3sec(time)) {
                                    return;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


                game.gameLogic.robbery();
                if (game.gameLogic.state == GameLogic.GAME_STATE.ROBBING) {
                    game.gameLogic.playersToRob.get(game.gameLogic.robCounter).trade = new Trade();
                }


            }
        }

    }
}
