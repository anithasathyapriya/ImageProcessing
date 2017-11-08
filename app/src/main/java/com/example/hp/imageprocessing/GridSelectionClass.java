package com.example.hp.imageprocessing;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HP on 30/10/2017.
 */

public class GridSelectionClass extends AsyncTask<String, Void, String> {

    ProgressBar pb;
    Context context;
    ArrayList<String> bitmapNames;
    imagesIn_grid activity;

    public GridSelectionClass(Context context, ProgressBar p, ArrayList<String> bitmapNames, imagesIn_grid activity){
        this.context = context;
        this.pb = p;
        this.bitmapNames = bitmapNames;
        this.activity = activity;
    }


    @Override
    protected void onPreExecute() {
        pb.setVisibility(View.VISIBLE);
        pb.setProgress(0);
        pb.setIndeterminate(true);
    }
    @Override
    //  getting all images in the folder
    protected String doInBackground(String... params) {
        String a = JSONParser.postStream(params[0], getImageNames());
        return (a);
    }
    @Override
    protected void onPostExecute(String result) {
        final int countImages = bitmapNames.size();
        activity.reloadActivity();
    }

    private String getImageNames(){
        StringBuffer names = new StringBuffer();
        for (String s:bitmapNames) {
            names.append(s+"__");
        }
        final String imgNames = names.toString();
        return imgNames;
    }
}