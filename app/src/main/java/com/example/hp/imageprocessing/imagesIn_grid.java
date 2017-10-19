package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.hp.imageprocessing.R.id.progressbar;
import static com.example.hp.imageprocessing.R.id.toolbar;

public class imagesIn_grid extends AppCompatActivity implements AdapterView.OnItemClickListener{

    final static String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/folderImages";

    //public ArrayList<String> bitmapNames = new ArrayList<String>();
    public ArrayList<GridImageClass> bitmapclass;
    GridView gv;
    String name;
    String fol;
    private  ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        Toolbar tb=(Toolbar)findViewById(R.id.toolbar);
        TextView tv=(TextView) tb.findViewById(R.id.toolbar_title);
        String datestring=null;
        Date d=null;
        pb=(ProgressBar) findViewById(R.id.progressBar2);
        final Bundle b = getIntent().getExtras();
        final String folder = b.get("Id").toString();
        fol = folder;
        //displaying the selected date as Title of Toolbar
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try{
             d = sdf.parse(fol);
            sdf=new SimpleDateFormat("dd MMM");
            datestring = sdf.format(d);
        }catch(Exception e){}
        tv.setText(""+ datestring);
        tb.setTitleTextColor(Color.BLACK);
        clear();



        new AsyncTask<Void, Void, JSONArray>() {
            //progress bar activation
            @Override
            protected  void onPreExecute(){
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(0);
                pb.setIndeterminate(true);
            }
            @Override
            //  getting all images in the folder
            protected JSONArray doInBackground(Void... params) {
                JSONArray a = JSONParser.getJSONArrayFromUrl(host + "/" + folder);
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
                        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                        bng.compress(Bitmap.CompressFormat.JPEG,100, baos);
                        byte[] b=baos.toByteArray();
                        String temp= Base64.encodeToString(b, Base64.DEFAULT);
                        File file;
                        FileOutputStream outputStream;
                        try {
                            file = new File(getApplicationContext().getCacheDir(),c.name);
                            outputStream = new FileOutputStream(file);
                            outputStream.write(temp.getBytes());
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                }
                gv = (GridView) findViewById(R.id.gridview1);
                ImageAdapter_Grid adapter = new ImageAdapter_Grid(imagesIn_grid.this, bitmapclass);
                gv.setAdapter(adapter);
                gv.setOnItemClickListener(imagesIn_grid.this);
                pb.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

      @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
        intent.putExtra("iName",bitmapclass.get(i).name);
        //intent.putExtra("iName",bitmapNames.get(i));
          intent.putExtra("Id",fol);
       // intent.putExtra("Index",i);
        //intent.putExtra("count", bitmapNames.size());
        startActivity(intent);
    }

    // clear the cache memory
    public void clear() {
        File[] directory = getCacheDir().listFiles();
        if(directory != null){
            for (File file : directory ){
                file.delete();
            }
        }
    }

    @Override
    public   void onBackPressed(){
        Log.i( "onBackPressed: ","");
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        //intent.putExtra("Id", folder);
        startActivity(intent);
        return;
    }
}

