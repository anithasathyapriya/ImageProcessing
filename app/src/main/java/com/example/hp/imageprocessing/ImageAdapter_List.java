package com.example.hp.imageprocessing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * Created by HP on 28/9/2017.
 */

public class ImageAdapter_List extends ArrayAdapter<ImageClass>{


    Context context;
    int layoutResourceId;
    List<ImageClass> data= Collections.emptyList();

    public ImageAdapter_List(Context contextl ,int resourceId, ArrayList<ImageClass> items)
    {super(contextl, resourceId,items );
        context=contextl;
        layoutResourceId=resourceId;
        data=items;
    }

    private class ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        TextView txtDate;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        Date d=null;
        Date d2=null;
        String datestring=null;
        ArrayList<String> imageViews = new ArrayList<>();

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {

            convertView=inflater.inflate(R.layout.listview_layout,null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
            holder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
            imageViews.add("imageView1");
            imageViews.add("imageView2");
            imageViews.add("imageView3");
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        ImageClass rowItem = data.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try{
            d = sdf.parse(rowItem.getName());
            sdf=new SimpleDateFormat("dd MMM yyyy");
            datestring = sdf.format(d);
            d2=sdf.parse(datestring);

        }catch(Exception e){}

        Calendar d1 = Calendar.getInstance();
        String s  = sdf.format( d1.getTime());
        Date s1=new Date();
        try{
        s1=sdf.parse(s);}
        catch (Exception e){}
       /* if (datestring.compareTo(s)==0)
            holder.txtDate.setText("Today");*/
        int diff=(int)(s1.getTime()-d2.getTime())/(1000 * 60 * 60 * 24);
        if(diff==0)
            holder.txtDate.setText("Today");
        else if(diff==1)
            holder.txtDate.setText("Yesterday");
        else
             holder.txtDate.setText(""+datestring);

        if(rowItem.getIndexes().size()==3){
            holder.imageView1.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(0)));
            holder.imageView2.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(1)));
            holder.imageView3.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(2)));
        }
        else if (rowItem.getImg().size()==2){
            holder.imageView1.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(0)));
            holder.imageView2.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(1)));
            holder.imageView3.setImageBitmap(null);

        }
        else {
            holder.imageView1.setImageBitmap(GetImagesFromCache(context, rowItem.getName(), String.valueOf(0)));
            holder.imageView2.setImageBitmap(null);
            holder.imageView3.setImageBitmap(null);
        }
        return convertView;
    }

    public Bitmap GetImagesFromCache(Context c, String folder, String index) {
        BufferedReader input = null;
        File file = null, f = null;
        try {
            f = new File(c.getCacheDir(), folder); // Pass getFilesDir() and "MyFile" to read file
            file = new File(f, index);

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

