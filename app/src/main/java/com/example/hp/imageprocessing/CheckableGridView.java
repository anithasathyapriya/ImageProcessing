package com.example.hp.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by viswath on 20-10-2017.
 */

public class CheckableGridView extends FrameLayout {
    private boolean mChecked;
    private ImageView imgView;
    Color color;

    public CheckableGridView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.grid_layout, this);
        imgView = (ImageView)getRootView().findViewById(R.id.imageView2);
    }

    public void display(GridImageClass image) {
        imgView.setImageBitmap(GetImagesFromCache(super.getContext(), image.name));
        GradientDrawable drawable = (GradientDrawable) imgView.getBackground();
        if (image.relevance.compareTo("Max") == 0)
            drawable.setStroke(6, Color.GREEN);
        else
            drawable.setStroke(6, Color.RED);
    }

    public void selectImages(){
        GradientDrawable drawable = (GradientDrawable) imgView.getBackground();
        drawable.setStroke(24, Color.BLUE);
    }

    public void unSelectImages(String rel){
        GradientDrawable drawable = (GradientDrawable) imgView.getBackground();
        if (rel.compareTo("Max") == 0)
            drawable.setStroke(6, Color.GREEN);
        else
            drawable.setStroke(6, Color.RED);
    }

    // Getting the image from the cache memory
    public Bitmap GetImagesFromCache(Context c, String imgName){
        BufferedReader input = null;
        File file = null;
        try {
            file = new File(c.getCacheDir(), imgName); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
            String i = buffer.toString();
            try {
                byte[] encodeByte = Base64.decode(i, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch (Exception e) {
                e.getMessage();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
