package com.example.h4app;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnImg;
    Button clearBtn;
    ArrayList<ImageView> imgs;
    ImageView iv;
    Float x, y, dx, dy;
    RelativeLayout relativelayout;
    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        imgs = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get the reference of Button's
        btnImg = (Button) findViewById(R.id.loadImgBtn);
        clearBtn = (Button) findViewById(R.id.clearImgsBtn);


        InitializeAddImgListener();
        InitializeClearImgsListener();
    }

    /*
    * Initialize clear image listener
    * Used for clearing all the images when clear button is pressed
    */
    private void InitializeClearImgsListener()
    {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < imgs.size(); i++) {
                    relativelayout.removeView(imgs.get(i));
                }
                imgs.clear();
            }
        });
    }

    /*
     * Initialize add image listener
     * Used for adding a image from api when add image button is clicked
     */
    private void InitializeAddImgListener()
    {
        // perform setOnClickListener event on First Button
        btnImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                iv = new ImageView(MainActivity.this);
                relativelayout = (RelativeLayout)findViewById(R.id.mainActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                InitializeDragEvent();
                DownloadImage image = new DownloadImage(MainActivity.this, iv);
                image.execute("http://192.168.0.10:5003/api/Image/GetImage");
                iv.setLayoutParams(params);
                relativelayout.addView(iv);
                imgs.add(iv);
            }
        });
    }

    /*
     * Reverse a buffer. Since this stupid language dosent have a Array.Reverse() i made one myself..
     * Used for reversing a buffer, should be made generic, but works for my needs
     */
    private byte[] ReverseBuffer(byte[] buffer)
    {
        byte[] temp = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i++){
            temp[i] = buffer[buffer.length - 1 - i];
        }
        return temp;
    }

    /*
     * Checks if mouse is over some rectangle
     * Used to check if the mouse x and y is within a rectangle
     * returns true if mouse is in a rectangle
     * else false
     */
    private boolean IsMouseOverRectangle(int mx, int my, int ix, int iy, int iw, int ih)
    {
        return ((mx >= ix && mx <= ix + iw) && (my >= iy && my <= iy + ih));
    }

    /*
     * Initialize Drag event listener
     * Used for dragging a ImageView
     */
    @SuppressLint("ClickableViewAccessibility")
    private void InitializeDragEvent()
    {
        iv.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    dx = motionEvent.getX() -x;
                    dy = motionEvent.getY() -y;

                    iv.setX(iv.getX() + dx);
                    iv.setY(iv.getY() + dy);
                }
                x = motionEvent.getX();
                y = motionEvent.getY();
                return true;
            }
        });
    }
}