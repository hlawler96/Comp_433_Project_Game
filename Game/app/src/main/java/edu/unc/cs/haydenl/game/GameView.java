package edu.unc.cs.haydenl.game;

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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by hayden on 10/9/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameView extends View {

    int width, height, sideLength;
    ArrayList<Spot> settlements;
    GameBoard game;
    boolean setup;
    Context context;

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
            int startY = (int) (height / 2 - (2*dy + 2.5*sideLength));
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
        drawPlayerBoxes(c,p);
        drawTiles(c,p);
        drawPorts(c,p);
        drawCurrentPlayerResources(c,p);
        drawMessageBar(c,p);
        drawSettlements(c,p);
        drawMenu(c);

    }

    public void onTouch(float x, float y){
        double min = 150;
        if(game.gameLogic.state == GameLogic.GAME_STATE.STEADY) {
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
            //check for button click
            double distance = Math.sqrt(Math.pow(x - (width-80),2) + Math.pow(y - height/2, 2));
            if(distance < min){
                game.gameLogic.state = GameLogic.GAME_STATE.MAIN_MENU;
            }else if (min < sideLength * 1.5) {
                minSpot._player = game.gameLogic.currentPlayer.id;
            }

        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MAIN_MENU){
            if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4){
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            }else if(x < 3*width/8){
                game.gameLogic.state = GameLogic.GAME_STATE.MENU_BUILD;
            }else if(x < 5*width/8){
                game.gameLogic.state = GameLogic.GAME_STATE.MENU_TRADE;
            }else{
                quit();
            }
        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MENU_BUILD){
            Player p = game.gameLogic.currentPlayer;
            if(x < width/8 || x > 7*width/8 || y < height/4 || y > 3*height/4){
                game.gameLogic.state = GameLogic.GAME_STATE.STEADY;
            }
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
            if(t.type!= Tile.RESOURCE_TYPE.DESERT){
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
        }


    }

    private void drawSettlements(Canvas c, Paint p){
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        for(Tile t: game.tiles) {
            for (Spot s : t.spots) {
                if(s._player > 0){
                    c.drawCircle(s.x,s.y,10, p);
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

        if(game.gameLogic.state == GameLogic.GAME_STATE.STEADY){
            c.drawCircle(width - height/6 - 10, height/2, height / 12, circlePaint);
            circlePaint.setColor(Color.WHITE);
            c.drawText("B", width-height/6 - 10, height/2 + 20 , circlePaint);

        }else if(game.gameLogic.state == GameLogic.GAME_STATE.MAIN_MENU){
            c.drawRect(width/8, height/4, 3*width/8, 3*height/4,fullBoxPaint);
            c.drawRect(width/8, height/4, 3*width/8, 3*height/4,boxOutline);
            c.drawText("BUILD", width/4, height/2,textPaint);

            c.drawRect(3*width/8, height/4, 5*width/8, 3*height/4,fullBoxPaint);
            c.drawRect(3*width/8, height/4, 5*width/8, 3*height/4,boxOutline);
            c.drawText("TRADE", width/2, height/2,textPaint);

            c.drawRect(5*width/8, height/4, 7*width/8, 3*height/4,fullBoxPaint);
            c.drawRect(5*width/8, height/4, 7*width/8, 3*height/4,boxOutline);
            c.drawText("QUIT", 3*width/4, height/2,textPaint);
        }else if (game.gameLogic.state == GameLogic.GAME_STATE.MENU_BUILD){
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

        c.drawRect(5*width/24, 11*height/12 ,19*width/24, height, p);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WHEAT));
        c.drawCircle(8*width/24 - width/48, 23*height/24, height/ 30, p);
        c.drawCircle(8*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WHEAT), 9*width/24- width/48 - 10, 23*height/24, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.WOOD));
        c.drawCircle(10*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(10*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.WOOD), 11*width/24- width/48 - 10, 23*height/24, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.ROCK));
        c.drawCircle(12*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(12*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.ROCK), 13*width/24- width/48 - 10, 23*height/24, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.BRICK));
        c.drawCircle(14*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(14*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.BRICK), 15*width/24- width/48 - 10, 23*height/24, textPaint);

        p.setColor(game.tiles[0].typeToColor(Tile.RESOURCE_TYPE.SHEEP));
        c.drawCircle(16*width/24- width/48, 23*height/24, height/ 30, p);
        c.drawCircle(16*width/24- width/48, 23*height/24, height/ 30, outline);

        c.drawText("" + game.gameLogic.currentPlayer.cards.get(Tile.RESOURCE_TYPE.SHEEP), 17*width/24- width/48 - 10, 23*height/24, textPaint);


    }

    private void drawMessageBar(Canvas c, Paint p){
        p.setStrokeWidth(2);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setTextSize(64);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.WHITE);
        c.drawText(game.gameLogic.message, width/2, height/18,p);

    }

    public void quit(){
        Intent homeIntent = new Intent(context, MainActivity.class);
        context.startActivity(homeIntent);
    }



}
