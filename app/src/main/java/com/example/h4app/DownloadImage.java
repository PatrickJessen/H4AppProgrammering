package com.example.h4app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;

 /*
 * This class is used to download a image from an API
 */
public class DownloadImage extends AsyncTask<String, Integer, Bitmap> {
    Context context;
    ImageView imageView;
    Bitmap bitmap;
    InputStream in = null;
    int responseCode = -1;
    //constructor.
    public DownloadImage(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }
    @Override
    protected void onPreExecute() {


    }
    @Override
    protected Bitmap doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(params[0])
                .build();

        try {
            Response response = client.newCall(request).execute();
            byte[] decodedImageBytes = Base64.decode(response.body().string(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Bitmap data) {
        imageView.setImageBitmap(data);
    }
}