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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

 String host = "http://192.168.51.46/EventTraceWebAppV1/Service1.svc";
    String selectHost;

    public ArrayList<GridImageClass> bitmapclass;
    public GridView gv;
    String name,folder,backkey="null",flag,userid,selection,preferenceKey="null",datestring="null";
    private  ProgressBar pb;
    public ArrayList<String> bitmapNames;
    boolean selected = false;
    MenuItem itemCancel,itemSelect;
    Toolbar tb,tool;
    ImageAdapter_Grid adapter;
    String fullName;
    TextView tv;
    ListView listview;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        bitmapclass = new ArrayList<>();
        Date d = null;
        tb = (Toolbar) findViewById(R.id.toolbar);
        tv = (TextView) tb.findViewById(R.id.toolbar_title);
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        pb = (ProgressBar) findViewById(R.id.progressbar2);
        final Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        flag = b.get("flag").toString();
        userid = b.get("Uid").toString();
        fullName = b.get("FName").toString();
        if (b.containsKey("key")) {
            backkey = b.get("key").toString();
        } // for  the preference set by the user
        else if (b.containsKey("Pkey")) {
            preferenceKey = b.get("Pkey").toString();
            //fullName = b.get("FName").toString();
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tb.setTitle("");
            spinner.setVisibility(View.INVISIBLE);
        }

        if (flag.compareTo("login") == 0) {
            spinner.setVisibility(View.INVISIBLE);
        } else {
            spinner.setVisibility(View.INVISIBLE);
            // setting back button in the toolbar
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        if (folder.compareTo("Favourite") == 0) {
            selectHost = host + "/GetFavourites";// call to Favourites page
            tv.setText("My Favourites");
            name = "My Favourites";

        } else if (folder.compareTo("SearchByPeople") == 0) {
            selectHost = host + "/SearchByPeople/" + userid;
            tv.setText("Your People");
            if (backkey.compareTo("BackfromContact") > 0) {
                flag = "Related";
            }
            name = "Your People";
            fullName = b.get("FName").toString();
        } else if (flag.compareTo("false") == 0) {
            selectHost = host + "/folderImages/" + folder + "/" + userid;// call to dispaly all images of selected date

            //displaying the selected date as Title of Toolbar
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                d = sdf.parse(folder);
                sdf = new SimpleDateFormat("dd MMM");
                datestring = sdf.format(d);
            } catch (Exception e) {
            }
            tv.setText("" + datestring);
            name = datestring;
            tv.setTextColor(Color.BLACK);
        }


        clear();


        gv = (GridView) findViewById(R.id.gridview1);
        GridTaskClass gridTask = new GridTaskClass(imagesIn_grid.this, gv, pb);
        gridTask.execute(selectHost);
        gv.setOnItemClickListener(imagesIn_grid.this);

        // code for back button click
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (flag.compareTo("true") == 0 || (backkey.compareTo("pagefrommonth") == 0)) {
                    intent = new Intent(getApplicationContext(), MonthActivity.class);
                    if (folder.length() == 8) {
                        folder = folder.substring(0, 6);
                    }
                    intent.putExtra("Id", folder);
                    intent.putExtra("Uid", userid);
                    intent.putExtra("flag", "true");
                    intent.putExtra("FName",fullName);
                } else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Uid", userid);
                    intent.putExtra("FName",fullName);
                   /* intent.putExtra("Id", folder);
                    intent.putExtra("flag",flag);*/
                }
                startActivity(intent);
                finish();
            }
        });

        tool = (Toolbar) findViewById(R.id.bottomtoolbar);
        ImageView ivDel = (ImageView) tool.findViewById(R.id.imgDelete);
        // code for Delete button in Grid view
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kindly select atleast one image", Toast.LENGTH_LONG).show();
                } else {

                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames, userid, imagesIn_grid.this);
                    selectHost = host + "/DeleteSelected";
                    selection = "Deleted";
                    gridSel.execute(selectHost);

                }
            }
        });

        ImageView ivFav = (ImageView) tool.findViewById(R.id.imgHeart);
        // code for Favourite button in Grid view
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kindly select atleast one image", Toast.LENGTH_LONG).show();
                } else {
                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames, userid, imagesIn_grid.this);
                    selectHost = host + "/MarkFavourite";
                    selection = "Marked as Favourite";
                    gridSel.execute(selectHost);
                }
            }
        });

        ImageView ivRel = (ImageView) tool.findViewById(R.id.imgRelavant);
        // code for Relavance button in Grid view
        ivRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kindly select atleast one image", Toast.LENGTH_LONG).show();
                } else {

                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames, userid, imagesIn_grid.this);
                    selectHost = host + "/ChangeRelavance";
                    selection = "Relavance Changed";
                    gridSel.execute(selectHost);

                }
            }
        });
    }

    // for  click event of one image in grid view
      @Override

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (flag.compareTo("Related")==0)
        {
            Intent intent = new Intent(getApplicationContext(),SetContactforPeople.class);
            bitmapclass = GridTaskClass.getBitmapclass();
            intent.putExtra("Id","SearchByPeople");
            intent.putExtra("Uid",userid);
            intent.putExtra("flag", bitmapclass.get(i).name);//image name set to flag
            intent.putExtra("FName",fullName);
            if(preferenceKey.compareTo("preference")==0)
                intent.putExtra("Pkey","preference");
            startActivity(intent);

        }
        else {

            Intent intent = new Intent(getApplicationContext(), ImageDetails.class);
            bitmapclass = GridTaskClass.getBitmapclass();
            intent.putExtra("iName", bitmapclass.get(i).name);
            intent.putExtra("flag", flag);
            intent.putExtra("Id", folder);
            intent.putExtra("Uid",userid);
            intent.putExtra("FName",fullName);
            if(preferenceKey.compareTo("preference")==0)
                intent.putExtra("Pkey","preference");
            startActivity(intent);
        }
    }
    //adding "select" to toolbar as a menu item
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_menu, menu);

        itemCancel = menu.findItem(R.id.action_cancel);
        itemSelect = menu.findItem(R.id.action_select);
        itemCancel.setVisible(false);
        return true;
    }*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(preferenceKey.compareTo("preference")==0)
        {
           getMenuInflater().inflate(R.menu.imagedetails_menu,menu);
        }
        else if(flag.compareTo("Related")!=0)
        {

            getMenuInflater().inflate(R.menu.grid_menu, menu);
            itemCancel = menu.findItem(R.id.action_cancel);
            itemSelect = menu.findItem(R.id.action_select);
            itemCancel.setVisible(false);
            if (selected){
                itemSelect.setVisible(false);
                itemCancel.setVisible(true);
            } else{
                itemSelect.setVisible(true);
                itemCancel.setVisible(false);
            }
        }

        return super.onPrepareOptionsMenu(menu);
        //return true;
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
            bitmapclass = GridTaskClass.getBitmapclass();

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
            });
        }
        else if(res_id == R.id.action_cancel){
            selected  = false;

            invalidateOptionsMenu();
            tool.setVisibility(View.INVISIBLE);
            bitmapNames.clear();
            tv.setText(name);
            setGridViewAdapter();
        }
        else  if (res_id == R.id.action_Home)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Uid",userid);
            intent.putExtra("FName",fullName);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
       // return true;
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

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("Id",folder);
        intent.putExtra("Uid",userid);
        intent.putExtra("flag",flag);
        startActivity(intent);
        return;
    }

    public void setGridViewAdapter(){
        adapter = new ImageAdapter_Grid(imagesIn_grid.this, bitmapclass);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(imagesIn_grid.this);
    }

    public void reloadActivity(){

        final int countImages = bitmapNames.size();
        Toast.makeText(imagesIn_grid.this, countImages +" Images" +selection, Toast.LENGTH_LONG).show();
        final Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Id",folder);
        intent.putExtra("flag",flag);
        intent.putExtra("Uid",userid);
        intent.putExtra("FName",fullName);
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                                startActivity(intent);
                            }
                        }, 2000);
    }

}

