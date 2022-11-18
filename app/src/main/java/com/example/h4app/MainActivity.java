package com.example.h4app;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button btnImg;
    Button clearBtn;
    Button postBtn;
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
        postBtn = (Button) findViewById(R.id.postImage);


        InitializeAddImgListener();
        InitializeClearImgsListener();
        PostImage("http://10.108.137.182:5003/api/Image/SaveImage");
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
                image.execute("http://10.108.137.182:5003/api/Image/GetImage");
                iv.setLayoutParams(params);
                relativelayout.addView(iv);
                imgs.add(iv);
                /*try {
                    SaveImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void PostImage(String... params)
    {
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder().add("base64", encoded).build();
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        if (response.isSuccessful()){
                        }
                    }
                });
            }
        });
    }

    private Bitmap.CompressFormat getCompressionFormat(BitmapFactory.Options options) {
        if (options == null || options.outMimeType == null) return Bitmap.CompressFormat.JPEG;

        if (options.outMimeType.endsWith("/png")) {
            return Bitmap.CompressFormat.PNG;
        } else if (options.outMimeType.endsWith("/webp")) {
            return Bitmap.CompressFormat.WEBP;
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }

    private void SaveImage() throws IOException {
        BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/drawable");
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        outStream = new FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        outStream.flush();
        outStream.close();
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