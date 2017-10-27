package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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

import static com.example.hp.imageprocessing.R.id.bottomtoolbar;
import static com.example.hp.imageprocessing.R.id.progressbar;
import static com.example.hp.imageprocessing.R.id.toolbar;

public class imagesIn_grid extends AppCompatActivity implements AdapterView.OnItemClickListener{

 String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/";

    //public ArrayList<String> bitmapNames = new ArrayList<String>();
    public ArrayList<GridImageClass> bitmapclass;
    public GridView gv;
    String name;
    String fol;
    private  ProgressBar pb;
    public ArrayList<String> bitmapNames;
    boolean selected = false;
    MenuItem itemCancel;
    MenuItem itemSelect;
    Toolbar tool;
    private Menu menu;
    ImageAdapter_Grid adapter;
    String datestring=null;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        Toolbar tb=(Toolbar)findViewById(R.id.toolbar);
        tv=(TextView) tb.findViewById(R.id.toolbar_title);
        //String datestring=null;
        Date d=null;
        pb=(ProgressBar) findViewById(R.id.progressbar2);
        final Bundle b = getIntent().getExtras();
        final String folder = b.get("Id").toString();
        fol = folder;
        Spinner spinner=(Spinner) findViewById(R.id.categorySpinner);
        spinner.setVisibility(View.INVISIBLE);
        if (folder.compareTo("Favourite")==0)
        {
            host=host+"/GetFavourites";
            tv.setText("My Favourites");
        }
        else {
            host = host+ "folderImages/" + folder;
            //displaying the selected date as Title of Toolbar
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                d = sdf.parse(fol);
                sdf = new SimpleDateFormat("dd MMM");
                datestring = sdf.format(d);
            } catch (Exception e) {
            }
            tv.setText("" + datestring);
            name = datestring;
            tv.setTextColor(Color.BLACK);
        }
        clear();

        // setting back button in the toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("Id", folder);
                startActivity(intent);
                finish();
            }
        });

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
                JSONArray a = JSONParser.getJSONArrayFromUrl(host );
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
                setGridViewAdapter();
                pb.setVisibility(View.INVISIBLE);
            }
        }.execute();
        Toolbar tool=(Toolbar) findViewById(R.id.bottomtoolbar);
        ImageView  ivDel=(ImageView) tool.findViewById(R.id.imgDelete);
        // code for Delete button in Grid view
        ivDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Kindly select atleast one image",Toast.LENGTH_LONG).show();
                }
                else {
                    final int countImages = bitmapNames.size();
                    StringBuffer names = new StringBuffer();
                    for (String s:bitmapNames) {
                        names.append(s+"__");
                    }
                    final String imgNames = names.toString();
                    new AsyncTask<Void, Void, String>() {
                        //progress bar activation
                        @Override
                        protected void onPreExecute() {
                            pb.setVisibility(View.VISIBLE);
                            pb.setProgress(0);
                            pb.setIndeterminate(true);
                        }
                        @Override
                        //  getting all images in the folder
                        protected String doInBackground(Void... params) {
                            String a = JSONParser.postStream(host + "DeleteSelected", imgNames);
                            return (a);
                        }
                        @Override
                        protected void onPostExecute(String result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), countImages+" Images deleted", Toast.LENGTH_LONG).show();
                                }
                            });

                            final Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Id", folder);

                            /*(new Handler())
                                    .postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    startActivity(intent);
                                                }
                                            }, 2000);*/
                        }
                    }.execute();
                }
            }
        });

        ImageView  ivFav=(ImageView) tool.findViewById(R.id.imgHeart);
        // code for Favourite button in Grid view
        ivFav.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Kindly select atleast one image",Toast.LENGTH_LONG).show();
                }
                else {
                    final int countImages = bitmapNames.size();
                    StringBuffer names = new StringBuffer();
                    for (String s:bitmapNames) {
                        names.append(s+"__");
                    }
                    final String imgNames = names.toString();
                    new AsyncTask<Void, Void, String>() {
                        //progress bar activation
//                        @Override
//                        protected void onPreExecute() {
//                            pb.setVisibility(View.VISIBLE);
//                            pb.setProgress(0);
//                            pb.setIndeterminate(true);
//                        }
                        @Override
                        //  getting all images in the folder
                        protected String doInBackground(Void... params) {
                            String a = JSONParser.postStream(host + "MakeFavourite", imgNames);
                            return (a);
                        }
                        @Override
                        protected void onPostExecute(String result) {
//                            Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("Id", folder);
//                            startActivity(intent);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), countImages+" Images added to favourites", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }.execute();
                }
            }
        });

        ImageView  ivRel=(ImageView) tool.findViewById(R.id.imgRelavant);
        // code for Relavance button in Grid view
        ivRel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Kindly select atleast one image",Toast.LENGTH_LONG).show();
                }
                else {
                    final int countImages = bitmapNames.size();
                    StringBuffer names = new StringBuffer();
                    for (String s:bitmapNames) {
                        names.append(s+"__");
                    }
                    final String imgNames = names.toString();
                    new AsyncTask<Void, Void, String>() {
                        //progress bar activation
                        @Override
                        protected void onPreExecute() {
                            pb.setVisibility(View.VISIBLE);
                            pb.setProgress(0);
                            pb.setIndeterminate(true);
                        }
                        @Override
                        //  getting all images in the folder
                        protected String doInBackground(Void... params) {
                            String a = JSONParser.postStream(host + "ChangeRelavance", imgNames);
                            return (a);
                        }
                        @Override
                        protected void onPostExecute(String result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), countImages+" Images marked", Toast.LENGTH_LONG).show();
                                }
                            });
                            final Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Id", folder);
                            (new Handler())
                                    .postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    startActivity(intent);
                                                }
                                            }, 2000);
                        }
                    }.execute();
                }
            }
        });

    }
    // for  click event of one image in grid view
      @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
        intent.putExtra("iName",bitmapclass.get(i).name);
        //intent.putExtra("iName",bitmapNames.get(i));
        intent.putExtra("Id",fol);
        startActivity(intent);
    }

    //adding "select" to toolbar as a menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_menu, menu);
        itemCancel = menu.findItem(R.id.action_cancel);
        itemSelect = menu.findItem(R.id.action_select);
        itemCancel.setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(selected){
            itemSelect.setVisible(false);
            itemCancel.setVisible(true);
        }
        else{
            itemSelect.setVisible(true);
            itemCancel.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int res_id = item.getItemId();
        if (res_id == R.id.action_select)
        {
            selected  = true;
            tool = (Toolbar) findViewById(R.id.bottomtoolbar);
            tool.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
            bitmapNames = new ArrayList<>();
            //final ImageAdapter_Grid adapter = (ImageAdapter_Grid) gv.getAdapter();
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getApplicationContext(),"select  pressed "+i ,Toast.LENGTH_LONG).show();
                    if(!bitmapNames.contains(bitmapclass.get(i).name)) {
                        bitmapNames.add(bitmapclass.get(i).name);
                        ((CheckableGridView) view).selectImages();
                        tv.setText(" "+bitmapNames.size()+" selected");
                    }
                    else{
                        bitmapNames.remove(bitmapclass.get(i).name);
                        ((CheckableGridView) view).unSelectImages(bitmapclass.get(i).relevance);
                        tv.setText(" "+bitmapNames.size()+" selected");
                    }
                }
            }

        );
        }
        else if(res_id == R.id.action_cancel){
            selected  = false;
            invalidateOptionsMenu();
            tool.setVisibility(View.INVISIBLE);
            bitmapNames.clear();
            tv.setText(name);
            setGridViewAdapter();
        }
        return super.onOptionsItemSelected(item);
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
        //action for mobile back button pressed
    @Override
    public   void onBackPressed(){
        Log.i( "onBackPressed : ","");
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        //intent.putExtra("Id", folder);
        startActivity(intent);
        return;
    }

    public void setGridViewAdapter(){
        adapter = new ImageAdapter_Grid(imagesIn_grid.this, bitmapclass);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(imagesIn_grid.this);
    }

}

