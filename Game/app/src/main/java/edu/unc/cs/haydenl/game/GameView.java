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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by hayden on 10/9/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameView extends View {

    int width, height;
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
        Paint p = new Paint();
        drawOcean(c,p);
        drawTiles(c,p);
        drawSettlements(c,p);
        drawPlayerBoxes(c,p);
        drawPorts(c,p);
    }

    public void onTouch(float x, float y){
        double min = 100000;
        Spot minSpot = null;
        for(Tile t: game.tiles){
            for(Spot s: t.spots){
                double distance = Math.sqrt(Math.pow(x-s.x,2) + Math.pow(y-s.y,2));
                if(distance < min){
                    minSpot = s;
                    min = distance;
                }
            }
        }
        minSpot._player = 1;
        this.invalidate();

    }

    private void drawOcean(Canvas c, Paint p){
        p.setColor(Color.rgb(0,119,190));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawRect(0, 0, width, height, p);
    }

    private void drawTiles(Canvas c, Paint p){
        game.counter = 0;
        //top row
        for(int i = 0; i < 6; i+= 2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(5* width / 16 + (i + 1) * (width / 16), height / 10 + height/15);
                t.storeCoordinates(5* width / 16 + (i + 2) * (width / 16), height / 6 + height/15);
                t.storeCoordinates(5* width / 16 + (i + 2) * (width / 16), 7 * height / 30 + height/15);
                t.storeCoordinates(5* width / 16 + (i + 1) * (width / 16), 3 * height / 10 + height/15) ;
                t.storeCoordinates(5* width / 16 + i * (width / 16), 7 * height / 30 + height/15) ;
                t.storeCoordinates(5* width / 16 + i * (width / 16), height / 6 + height/15);
            }
            Path path = new Path();
            path.moveTo(t.spots[0].x,t.spots[0].y);
            path.lineTo(t.spots[1].x,t.spots[1].y);
            path.lineTo(t.spots[2].x,t.spots[2].y);
            path.lineTo(t.spots[3].x,t.spots[3].y);
            path.lineTo(t.spots[4].x,t.spots[4].y);
            path.lineTo(t.spots[5].x,t.spots[5].y);
            path.lineTo(t.spots[0].x,t.spots[0].y);

            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path,p);

            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3*(t.spots[2].y -t.spots[1].y)/4;
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(centerX, centerY - 20, 60, p);
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0)c.drawText("" + t.number, centerX, centerY, p);
        }
        //second row
        for(int i =0; i < 8; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 4 + (i + 1) * (width / 16), 7 * height / 30 + height/15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 16), 7 * height / 30 + 2 * height / 15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 16), 7 * height / 30 + 3 * height / 15);
                t.storeCoordinates(width / 4 + (i + 1) * (width / 16), 7 * height / 30 + 4 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 16), 7 * height / 30 + 3 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 16), 7 * height / 30 + 2 * height / 15);
            }

            Path path  = new Path();
            path.moveTo(t.spots[0].x,t.spots[0].y);
            path.lineTo(t.spots[1].x,t.spots[1].y);
            path.lineTo(t.spots[2].x,t.spots[2].y);
            path.lineTo(t.spots[3].x,t.spots[3].y);
            path.lineTo(t.spots[4].x,t.spots[4].y);
            path.lineTo(t.spots[5].x,t.spots[5].y);
            path.lineTo(t.spots[0].x,t.spots[0].y);

            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path,p);

            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3*(t.spots[2].y -t.spots[1].y)/4;
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(centerX, centerY-20, 60, p);
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0)c.drawText("" + t.number, centerX, centerY, p);
        }

        //third row
        for(int i =0; i < 10; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(3*width / 16 + (i + 1) * (width / 16), 7 * height / 30 + 3 * height / 15);
                t.storeCoordinates(3*width / 16 + (i + 2) * (width / 16), 7 * height / 30 + 4 * height / 15);
                t.storeCoordinates(3*width / 16 + (i + 2) * (width / 16), 7 * height / 30 + 5 * height / 15);
                t.storeCoordinates(3*width / 16 + (i + 1) * (width / 16), 7 * height / 30 + 6 * height / 15);
                t.storeCoordinates(3*width / 16 + i * (width / 16), 7 * height / 30 + 5 * height / 15);
                t.storeCoordinates(3*width / 16 + i * (width / 16), 7 * height / 30 + 4 * height / 15);
            }
            Path path  = new Path();
            path.moveTo(t.spots[0].x,t.spots[0].y);
            path.lineTo(t.spots[1].x,t.spots[1].y);
            path.lineTo(t.spots[2].x,t.spots[2].y);
            path.lineTo(t.spots[3].x,t.spots[3].y);
            path.lineTo(t.spots[4].x,t.spots[4].y);
            path.lineTo(t.spots[5].x,t.spots[5].y);
            path.lineTo(t.spots[0].x,t.spots[0].y);

            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path,p);

            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3*(t.spots[2].y -t.spots[1].y)/4;
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(centerX, centerY-20, 60, p);
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0)c.drawText("" + t.number, centerX, centerY, p);
        }

        //fourth row
        for(int i =0; i < 8; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 4 + (i + 1) * (width / 16), 7 * height / 30 + 5 * height / 15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 16), 7 * height / 30 + 6 * height / 15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 16), 7 * height / 30 + 7 * height / 15);
                t.storeCoordinates(width / 4 + (i + 1) * (width / 16), 7 * height / 30 + 8 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 16), 7 * height / 30 + 7 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 16), 7 * height / 30 + 6 * height / 15);
            }
            Path path  = new Path();
            path.moveTo(t.spots[0].x,t.spots[0].y);
            path.lineTo(t.spots[1].x,t.spots[1].y);
            path.lineTo(t.spots[2].x,t.spots[2].y);
            path.lineTo(t.spots[3].x,t.spots[3].y);
            path.lineTo(t.spots[4].x,t.spots[4].y);
            path.lineTo(t.spots[5].x,t.spots[5].y);
            path.lineTo(t.spots[0].x,t.spots[0].y);

            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path,p);

            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3*(t.spots[2].y -t.spots[1].y)/4;
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(centerX, centerY-20, 60, p);
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0)c.drawText("" + t.number, centerX, centerY, p);
        }

        //fifth row
        for(int i =0; i < 6; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(5*width / 16 + (i + 1) * (width / 16), 7 * height / 30 + 7 * height / 15);
                t.storeCoordinates(5*width / 16 + (i + 2) * (width / 16), 7 * height / 30 + 8 * height / 15);
                t.storeCoordinates(5*width / 16 + (i + 2) * (width / 16), 7 * height / 30 + 9 * height / 15);
                t.storeCoordinates(5*width / 16 + (i + 1) * (width / 16), 7 * height / 30 + 10 * height / 15);
                t.storeCoordinates(5*width / 16 + i * (width / 16), 7 * height / 30 + 9 * height / 15);
                t.storeCoordinates(5*width / 16 + i * (width / 16), 7 * height / 30 + 8 * height / 15);
                if(i == 4)setup = true;
            }

            Path path  = new Path();
            path.moveTo(t.spots[0].x,t.spots[0].y);
            path.lineTo(t.spots[1].x,t.spots[1].y);
            path.lineTo(t.spots[2].x,t.spots[2].y);
            path.lineTo(t.spots[3].x,t.spots[3].y);
            path.lineTo(t.spots[4].x,t.spots[4].y);
            path.lineTo(t.spots[5].x,t.spots[5].y);
            path.lineTo(t.spots[0].x,t.spots[0].y);

            int color = t.color;
            p.setColor(color);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawPath(path, p);
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            c.drawPath(path,p);

            int centerX = t.spots[0].x;
            int centerY = t.spots[1].y + 3*(t.spots[2].y -t.spots[1].y)/4;
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(centerX, centerY-20, 60, p);
            p.setColor(Color.BLACK);
            if(t.number == 6 || t.number == 8) p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(64);
            if(t.number != 0) c.drawText("" + t.number, centerX, centerY, p);
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
            c.drawCircle(prt.left.x, prt.left.y, 10, p);
            c.drawCircle(prt.right.x, prt.right.y, 10, p);
        }
    }

}
