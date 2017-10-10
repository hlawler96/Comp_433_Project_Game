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
import android.view.View;

/**
 * Created by hayden on 10/9/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameView extends View {

    int width, height;
    GameBoard game;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        game = new GameBoard();
    }

    public void onDraw(Canvas c){
        width = getWidth();
        height = getHeight();
        Paint p = new Paint();
        drawOcean(c,p);
        drawBoard(c,p);
    }

    private void drawOcean(Canvas c, Paint p){
        p.setColor(Color.rgb(0,119,190));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawRect(0, 0, width, height, p);
    }


    private void drawBoard(Canvas c, Paint p){
        int startX = width/3 , startY = getPaddingTop();

        Path path = new Path();
        path.moveTo(startX,startY);
        path.lineTo(2*width/3, height/10);
        path.lineTo(5*width/6, height/2);
        path.lineTo(2*width/3, 9*height/10);
        path.lineTo(width/3, 9*height/10);
        path.lineTo(width/6, height/2);
        path.lineTo(startX,startY);


        p.setColor(Color.RED);
        c.drawPath(path, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);
        c.drawPath(path,p);

    }

    private void drawTiles(Canvas c, Paint p){

    }

}
