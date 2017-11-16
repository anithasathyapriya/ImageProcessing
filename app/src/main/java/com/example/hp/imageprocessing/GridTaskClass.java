package com.example.hp.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HP on 30/10/2017.
 */

public class GridTaskClass extends AsyncTask<String,Void,JSONArray>  {

    Context context;
    GridView gridView;
    ProgressBar pb;
    ArrayList<GridImageClass> bitmapclass;
    ImageAdapter_Grid adapter;
    static ArrayList<GridImageClass> bitmaps = new ArrayList<>();


    GridTaskClass(Context context,  GridView gridView, ProgressBar progressBar) {

        this.context = context;
        this.gridView = gridView;
        this.pb = progressBar;
    }


    @Override
    protected  void onPreExecute(){
        pb.setVisibility(View.VISIBLE);
        pb.setProgress(0);
        pb.setIndeterminate(true);
    }
    @Override
    //  getting all images in the folder
    protected JSONArray doInBackground(String... params) {
        JSONArray a = JSONParser.getJSONArrayFromUrl(params[0]);
        return (a);
    }
    @Override
    protected void onPostExecute(JSONArray result) {
        bitmapclass = new ArrayList<GridImageClass>();
        try {
            for (int j = 0; j < result.length(); j++) {
                JSONArray jByte = result.getJSONObject(j).getJSONObject("img").getJSONArray("Data");
                String name = result.getJSONObject(j).getString("name");
                String rel=result.getJSONObject(j).getString("relevance");
                byte[] by = new byte[jByte.length()];
                for (int i = 0; i < jByte.length(); i++) {
                    by[i] = (byte) (((int) jByte.get(i)) & 0xFF);
                }
                Bitmap bng = null;
                bng = BitmapFactory.decodeByteArray(by, 0, by.length);
                GridImageClass c = new GridImageClass();
                c.img = bng;
                c.name = name;
                c.relevance=rel;
                bitmapclass.add(c);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bng.compress(Bitmap.CompressFormat.JPEG,100, baos);
                byte[] b=baos.toByteArray();
                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                File file;
                FileOutputStream outputStream;
                try {
                    file = new File(context.getCacheDir(),c.name);
                    outputStream = new FileOutputStream(file);
                    outputStream.write(temp.getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
        }
        bitmaps.clear();
        bitmaps = bitmapclass;
        //gv = (GridView) findViewById(R.id.gridview1);
        setGridViewAdapter();
        pb.setVisibility(View.INVISIBLE);
    }

    public void setGridViewAdapter(){
        adapter = new ImageAdapter_Grid(context, bitmapclass);
        gridView.setAdapter(adapter);
        //bitmaps = bitmapclass;
    }

    public static ArrayList<GridImageClass> getBitmapclass() {
        return bitmaps;
    }

}

