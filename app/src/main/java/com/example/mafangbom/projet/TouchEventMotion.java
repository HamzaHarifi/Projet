package com.example.mafangbom.projet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mafangbom on 14/04/17.
 */

public class TouchEventMotion extends View {
    private Paint paint = new Paint();
    private Path path = new Path();

    public TouchEventMotion(Context ctx, AttributeSet attrs ){
        super(ctx,attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float xPos = event.getX() ;
        float yPos = event.getY() ;

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xPos, yPos);
                return true;
            case MotionEvent.ACTION_MOVE :
                path.lineTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP :
                break ;
            default:
                return false;
        }

        invalidate();
        return true ;

    }
}
