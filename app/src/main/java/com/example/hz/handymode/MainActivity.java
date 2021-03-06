package com.example.hz.handymode;

import android.app.Activity;
import android.app.Service;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Paint mRipplePaint;
    private TextView mLeftTV,mCenterTV,mRightTV;
    private  KeyButtonRipple mLeftKey,mCenterKey,mRightKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeftTV=(TextView)findViewById(R.id.left_tv);
        mCenterTV=(TextView)findViewById(R.id.center_tv);
        mRightTV=(TextView)findViewById(R.id.right_tv);

        mLeftTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftKey=new KeyButtonRipple(MainActivity.this,v);
                v.setBackground( mLeftKey);
            }
        });

        mCenterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground( new KeyButtonRipple(MainActivity.this,v));
            }
        });

        mRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground( new KeyButtonRipple(MainActivity.this,v));
//                Paint p=new Paint();
//                p.setColor(0xffcccccc);
//                Canvas canvas;
//                canvas.drawRoundRect(82f,0f,82f,53f,26f,26f,p);
            }
        });

        Display display = ((WindowManager)getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        Point po = new Point();
        display.getRealSize(po);
        Log.d(TAG,"onCreate-- 宽度po.x="+po.x+"高度po.y="+po.y );

    }

    public boolean onTouchEvent(MotionEvent ev){
        final int action = ev.getAction();
//        final View v=(View)ev.getDevice();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(false);
                break;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                break;
        }
        return true;
    }

    public void setPressed(boolean pressed) {
        mLeftKey.setPressedSoftware(pressed);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display = ((WindowManager)getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        Point po = new Point();
        display.getRealSize(po);
        Log.d(TAG,"onConfigurationChanged-- 宽度po.x="+po.x+"高度po.y="+po.y );

    }

    public void PrintScreenWidthHeight(){
        Display display = ((WindowManager)getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        Point po = new Point();
        display.getRealSize(po);
        Log.d(TAG,"宽度po.x="+po.x+"高度po.y="+po.y );
    }
}
