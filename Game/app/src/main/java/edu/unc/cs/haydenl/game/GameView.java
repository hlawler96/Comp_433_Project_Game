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
import android.widget.Button;

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

    public void init(Context context){
        game = new GameBoard();
        setup = false;
        settlements = new ArrayList<Spot>();

        this.setOnTouchListener(new View.OnTouchListener(){
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
                t.storeCoordinates(width / 4 + (i + 1) * (width / 12), height / 10);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 12), height / 6);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 12), 7 * height / 30);
                t.storeCoordinates(width / 4 + (i + 1) * (width / 12), 3 * height / 10);
                t.storeCoordinates(width / 4 + i * (width / 12), 7 * height / 30);
                t.storeCoordinates(width / 4 + i * (width / 12), height / 6);
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
        }
        //second row
        for(int i =0; i < 8; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 6 + (i + 1) * (width / 12), 7 * height / 30);
                t.storeCoordinates(width / 6 + (i + 2) * (width / 12), 7 * height / 30 + height / 15);
                t.storeCoordinates(width / 6 + (i + 2) * (width / 12), 7 * height / 30 + 2 * height / 15);
                t.storeCoordinates(width / 6 + (i + 1) * (width / 12), 7 * height / 30 + 3 * height / 15);
                t.storeCoordinates(width / 6 + i * (width / 12), 7 * height / 30 + 2 * height / 15);
                t.storeCoordinates(width / 6 + i * (width / 12), 7 * height / 30 + height / 15);
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
        }

        //third row
        for(int i =0; i < 10; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 12 + (i + 1) * (width / 12), 7 * height / 30 + 2 * height / 15);
                t.storeCoordinates(width / 12 + (i + 2) * (width / 12), 7 * height / 30 + 3 * height / 15);
                t.storeCoordinates(width / 12 + (i + 2) * (width / 12), 7 * height / 30 + 4 * height / 15);
                t.storeCoordinates(width / 12 + (i + 1) * (width / 12), 7 * height / 30 + 5 * height / 15);
                t.storeCoordinates(width / 12 + i * (width / 12), 7 * height / 30 + 4 * height / 15);
                t.storeCoordinates(width / 12 + i * (width / 12), 7 * height / 30 + 3 * height / 15);
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
        }

        //fourth row
        for(int i =0; i < 8; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 6 + (i + 1) * (width / 12), 7 * height / 30 + 4 * height / 15);
                t.storeCoordinates(width / 6 + (i + 2) * (width / 12), 7 * height / 30 + 5 * height / 15);
                t.storeCoordinates(width / 6 + (i + 2) * (width / 12), 7 * height / 30 + 6 * height / 15);
                t.storeCoordinates(width / 6 + (i + 1) * (width / 12), 7 * height / 30 + 7 * height / 15);
                t.storeCoordinates(width / 6 + i * (width / 12), 7 * height / 30 + 6 * height / 15);
                t.storeCoordinates(width / 6 + i * (width / 12), 7 * height / 30 + 5 * height / 15);
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
        }

        //fifth row
        for(int i =0; i < 6; i+=2){
            Tile t = game.getTileForBoard();
            if(!setup) {
                t.storeCoordinates(width / 4 + (i + 1) * (width / 12), 7 * height / 30 + 6 * height / 15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 12), 7 * height / 30 + 7 * height / 15);
                t.storeCoordinates(width / 4 + (i + 2) * (width / 12), 7 * height / 30 + 8 * height / 15);
                t.storeCoordinates(width / 4 + (i + 1) * (width / 12), 7 * height / 30 + 9 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 12), 7 * height / 30 + 8 * height / 15);
                t.storeCoordinates(width / 4 + i * (width / 12), 7 * height / 30 + 7 * height / 15);
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




}
