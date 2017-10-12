package com.example.hp.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 4/10/2017.
 */

public class ImageAdapter_Grid  extends  BaseAdapter{

    Context mcontext;
   List<GridImageClass> list ;

    public ImageAdapter_Grid(Context c , List<GridImageClass> l)
    {
        mcontext=c;
        list=l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int iconColor = Color.GREEN;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.grid_layout,null);}

        ImageView img=(ImageView) convertView.findViewById(R.id.imageView2);//for displaying image
        ImageView imgview4=(ImageView) convertView.findViewById(R.id.imageView4);//for displaying color code
        img.setImageBitmap(GetImagesFromCache(mcontext, list.get(position).name));


        //setting the color based on face value
       if(list.get(position).relevance.compareTo("Max")== 0)
               iconColor = Color.GREEN;

            else
                iconColor = Color.RED;


        imgview4.setBackgroundColor(iconColor);
        return  convertView;
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
