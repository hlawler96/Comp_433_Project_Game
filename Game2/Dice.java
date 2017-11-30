package edu.unc.cs.haydenl.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by hayden on 11/15/17.
 */

public class Dice {

    int one, two, sideWidth;
    Player p;


    public Dice(Player owner){
        p = owner;
        one = (int) (Math.random()*6 + 1);
        two = (int) (Math.random()*6 + 1);


    }

    public void draw(Canvas c, int width, int height){
        sideWidth = width / 15;
        if(p.id == 1){
            drawDie(10, height/6 + 10, c, one);
            drawDie(20 + sideWidth, height/6 + 10, c, two);
        }else if(p.id == 2){
            drawDie(width - 20 - 2*sideWidth, height/6 + 10, c, one);
            drawDie(width - 10 - sideWidth, height/6 + 10, c, two);
        }else if(p.id == 4){
            drawDie(10, 5* height/6 - 10 - sideWidth, c, one);
            drawDie(20 + sideWidth, 5* height/6 - 10 - sideWidth, c, two);
        }else{
            drawDie(width - 20 - 2*sideWidth, 5* height/6 - 10 - sideWidth, c, one);
            drawDie(width - 10 - sideWidth, 5* height/6 - 10 - sideWidth, c, two);
        }
    }

    public void drawDie(int startX, int startY, Canvas c, int value){
        Paint white = new Paint();
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawRect(startX, startY, startX + sideWidth, startY + sideWidth, white);

        Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setStrokeWidth(2);
        c.drawRect(startX, startY, startX + sideWidth, startY + sideWidth, black);
        black.setStyle(Paint.Style.FILL_AND_STROKE);

        if(value == 1){
            c.drawCircle(startX + sideWidth /2, startY + sideWidth/2, sideWidth / 9, black);
        }else if(value == 2){
            c.drawCircle(startX + sideWidth / 4, startY + sideWidth/ 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
        }else if(value == 3){
            c.drawCircle(startX + sideWidth / 4, startY + 3 * sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth /2, startY + sideWidth/2, sideWidth / 9, black);
        }else if(value == 4){
            c.drawCircle(startX + sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
        }else if(value == 5){
            c.drawCircle(startX + sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth /2, startY + sideWidth/2, sideWidth / 9, black);
        }else{
            c.drawCircle(startX + sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + 3*sideWidth / 4, sideWidth / 9, black);
            c.drawCircle(startX + sideWidth / 4, startY + sideWidth / 2, sideWidth / 9, black);
            c.drawCircle(startX + 3*sideWidth / 4, startY + sideWidth / 2, sideWidth / 9, black);
        }



    }
}
