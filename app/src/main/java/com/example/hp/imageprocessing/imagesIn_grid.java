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

 String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc";
    String selectHost;

    //public ArrayList<String> bitmapNames = new ArrayList<String>();
    public ArrayList<GridImageClass> bitmapclass;
    public GridView gv;
    String name;
    String fol;
    private  ProgressBar pb;
    public ArrayList<String> bitmapNames;
    boolean selected = false;
    MenuItem itemCancel,itemSelect;
    Toolbar tb,tool;
    private Menu menu;
    ImageAdapter_Grid adapter;
    String selectedHost,flag,userid,Sitem,monthflag,selection,datestring=null;
    TextView tv;
    ListView listview;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        bitmapclass = new ArrayList<>();
        tb=(Toolbar)findViewById(R.id.toolbar);
        tv=(TextView) tb.findViewById(R.id.toolbar_title);
        Date d=null;
        pb=(ProgressBar) findViewById(R.id.progressbar2);
        final Bundle b = getIntent().getExtras();
        final String folder = b.get("Id").toString();
        fol = folder;
        flag = b.get("flag").toString();
        userid=b.get("Uid").toString();

        if (folder.compareTo("Favourite")==0)
        {
            selectHost=host+"/GetFavourites";// call to Favourites page
            tv.setText("My Favourites");

        }
        else if (folder.compareTo("SearchByPeople")==0)
        {
            selectHost=host+"/SearchByPeople/"+userid;
            tv.setText("Your People");
            flag="Related";
        }

        else if (flag.compareTo("false")==0){
            selectHost = host+"/folderImages/"+folder+"/"+userid;// call to dispaly all images of selected date

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
       /* else if (flag.compareTo("true")==0){
            selectHost = host+"/MonthallImages/" + folder+"/"+userid;// call to One whole month page
            //displaying the selected date as Title of Toolbar
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            try {
                d = sdf.parse(fol);
                sdf = new SimpleDateFormat(" MMM yyyy");
                datestring = sdf.format(d);
            } catch (Exception e) {
            }
            tv.setText(""+datestring);
            name = datestring;
            tv.setTextColor(Color.BLACK);
        }*/
        clear();

        // setting back button in the toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(flag.compareTo("true")==0)
                {
                    intent = new Intent(getApplicationContext(), MonthActivity.class);
                    intent.putExtra("Id",fol);
                    intent.putExtra("Uid",userid);
                    intent.putExtra("flag",flag);


                }
                else
                {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Uid",userid);
                    intent.putExtra("Id", folder);
                    intent.putExtra("flag",flag);
                }
                startActivity(intent);
                finish();
            }
        });

        gv = (GridView) findViewById(R.id.gridview1);
        GridTaskClass gridTask = new GridTaskClass(imagesIn_grid.this, gv, pb);
        gridTask.execute(selectHost);
        gv.setOnItemClickListener(imagesIn_grid.this);

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

                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames,userid, imagesIn_grid.this);
                    selectHost = host+"/DeleteSelected";
                    selection="Deleted";
                    gridSel.execute(selectHost);

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

                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames,userid, imagesIn_grid.this);
                    selectHost = host+"/MarkFavourite";
                    selection="Marked as Favourite";
                    gridSel.execute(selectHost);

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

                    GridSelectionClass gridSel = new GridSelectionClass(imagesIn_grid.this, pb, bitmapNames, userid,imagesIn_grid.this);
                    selectHost = host +"/ChangeRelavance";
                    selection="Relavance Changed";
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
            Intent intent = new Intent(getApplicationContext(),RelatedFacesActivity.class);
            bitmapclass = GridTaskClass.getBitmapclass();
            intent.putExtra("Id","SearchByPeople");
            intent.putExtra("Uid",userid);
            intent.putExtra("flag", bitmapclass.get(i).name); // image name set to flag
            startActivity(intent);

        }
        else {

            Intent intent = new Intent(getApplicationContext(), ImageDetails.class);
            bitmapclass = GridTaskClass.getBitmapclass();
            intent.putExtra("iName", bitmapclass.get(i).name);
            intent.putExtra("flag", flag);
            intent.putExtra("Id", fol);
            intent.putExtra("Uid",userid);
            startActivity(intent);
        }
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

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("Id",fol);
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
        intent.putExtra("Id",fol);
        intent.putExtra("flag",flag);
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                                startActivity(intent);
                            }
                        }, 2000);

    }

}

