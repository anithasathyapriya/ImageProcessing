package com.example.hp.imageprocessing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HP on 27/10/2017.
 */

//  class for handling the spinner options actions

public class SpinnerClass  extends AsyncTask<String,Void,JSONArray> {


    Context context;
    ListView listView;
    ProgressBar pb;
    String monthFlag;


    SpinnerClass(Context context, ListView listView, ProgressBar progressBar, String monthFlag) {
        this.monthFlag = monthFlag;
        this.context = context;
        this.listView = listView;
        this.pb = progressBar;
    }


    @Override
    protected void onPreExecute() {
        pb.setVisibility(View.VISIBLE);
        pb.setProgress(0);
        pb.setIndeterminate(true);
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray a = JSONParser.getJSONArrayFromUrl(params[0]);
        return (a);
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        Bitmap bng;
        ArrayList<ImageClass>list=new ArrayList<>();
        try {
            for (int j = 0; j < result.length(); j++)
            {   ImageClass c = new ImageClass();
                try {
                    JSONArray a = result.getJSONObject(j).getJSONArray("images");
                    String name = result.getJSONObject(j).getString("fNames");
                    c.name = name;
                    for (int i = 0; i < a.length(); i++) {
                        JSONArray jByte = a.getJSONObject(i).getJSONArray("Data");
                        byte[] by = new byte[jByte.length()];
                        for (int k = 0; k < jByte.length(); k++) {
                            by[k] = (byte) (((int) jByte.get(k)) & 0xFF);
                        }
                        bng = BitmapFactory.decodeByteArray(by, 0, by.length);
                        c.indexes.add(i);
                        saveImages(c.name, String.valueOf(i), bng);
                    }
                } catch (Exception e) { }
                list.add(c);
            }

        } catch (Exception e) {  }
        ImageAdapter_List adapter = new ImageAdapter_List(context,R.layout.activity_main, list, monthFlag);
        listView.setAdapter(adapter);
        pb.setVisibility(View.INVISIBLE);
    }

    public void saveImages(String folder, String imgIndex, Bitmap bng){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bng.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(context.getCacheDir(),folder);
            if (!file.exists()) {
                file.mkdirs();
            }
            File f = new File(file, imgIndex);
            if (f.exists()) {
                f.delete();
            }
            outputStream = new FileOutputStream(f);
            outputStream.write(temp.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
