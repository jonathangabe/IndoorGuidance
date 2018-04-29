package idev.com.drawline;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;

import idev.com.drawline.Database.TestAdapter;
import idev.com.drawline.Model.Room;

public class CustomView extends View {

    private Application application;
    Paint p;
    Path path;

    public CustomView(Context context, Application application) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(10);
        path = new Path();
        this.application = application;
    }

//    private final Point point1;
//    private final Point point2;

//    Pt[] myPath =
//
//            {
//                    new Pt(0.73f * 100, 4.46f * 100),
//
//                    new Pt(0.73f * 100, 4.58f * 100),
//
//                    new Pt(200, 500),
//
//                    new Pt(400, 500),
//
//                    new Pt(400, 200)
//
//            };


    @Override
    protected void onDraw(Canvas canvas) {

        // draw first vertex
        Pt[] myPath = new Pt[application.getRoomlist().size()];
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.GREEN);
        for (int i = 0; i < application.getRoomlist().size(); i++) {
            float x = Float.valueOf(String.valueOf(application.getRoomlist().get(i).getLongi()));
            float y = Float.valueOf(String.valueOf(application.getRoomlist().get(i).getLati()));
            myPath[i] = new Pt(x, y * 10);
        }
        path.moveTo(myPath[0].x, myPath[0].y);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.CYAN);
        canvas.drawCircle(myPath[0].x, myPath[0].y, 15, p);
        for (int i = 1; i < myPath.length; i++) {
            System.out.println("X : " + myPath[i].x + " Y : " + myPath[i].y);
            canvas.drawCircle(myPath[i].x, myPath[i].y, 15, p);
            System.out.println("================");
            path.lineTo(myPath[i].x, myPath[i].y);
        }
        canvas.drawPath(path, p);
    }

    public void refreshView(Application application) {
        this.application = application;
        invalidate(); // redraws the view calling onDraw()
    }

    class Pt {

        float x, y;


        Pt(float _x, float _y) {

            x = _x;

            y = _y;

        }

    }

}
