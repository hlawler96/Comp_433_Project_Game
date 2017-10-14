package edu.unc.cs.haydenl.game;

import android.content.Context;
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

/**
 * Created by hayden on 10/9/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameView extends View {

    int width, height, sideLength;
    ArrayList<Spot> settlements;
    GameBoard game;
    boolean setup;

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

    public void init(Context context
    ){
        game = new GameBoard();
        setup = false;
        settlements = new ArrayList<Spot>();


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
            sideLength = width/18;
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
        drawSettlements(c,p);
    }

    public void onTouch(float x, float y){
        double min = 100000;
        Spot minSpot = null;
        for(Tile t: game.tiles){
            for(Spot s: t.spots){
                double distance = Math.sqrt(Math.pow(x-s.x,2) + Math.pow(y-s.y,2));
                if(distance < min ){
                    minSpot = s;
                    min = distance;
                }
            }
        }
        if(min < sideLength*1.5) minSpot._player = 1;
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

}
