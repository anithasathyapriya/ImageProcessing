package com.example.hp.imageprocessing;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

public class RelatedFacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String host="http://52.221.152.166/EventTrace/Service1.svc/SearchRelatedImages";
    String host1="http://52.221.152.166/EventTrace/Service1.svc";
    public ArrayList<GridImageClass> bitmapclass;
    public ArrayList<String> bitmapNames;
    String imgname,fullName,userid,folder,name,selectHost,selection,imgFullName,preferenceKey="null";
    MenuItem itemCancel,itemSelect;
    ImageAdapter_Grid adapter;
    boolean selected = false;
    ProgressBar pb;
    Toolbar tb,tool;
    TextView tv;
    GridView gv;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        bitmapclass = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        imgname = b.get("flag").toString();
        userid = b.get("Uid").toString();
        fullName=b.get("FName").toString();
        if (b.containsKey("Pkey"))
            preferenceKey = b.get("Pkey").toString();
            pb = (ProgressBar) findViewById(R.id.progressbar2);
            tb = (Toolbar) findViewById(R.id.toolbar);
            tool=(Toolbar) findViewById(R.id.bottomtoolbar);
            spinner = (Spinner) findViewById(R.id.categorySpinner);
            spinner.setVisibility(View.INVISIBLE);

            // setting the title
            tv = (TextView) tb.findViewById(R.id.toolbar_title);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tv.setText("Related Images");

            // back button in the tool bar
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(),SearchPeople.class);
                    intent.putExtra("Id", "SearchByPeople");
                    intent.putExtra("Uid", userid);
                    intent.putExtra("flag", "flag");
                    intent.putExtra("FName",fullName);
                    if(preferenceKey.compareTo("preference")==0)
                        intent.putExtra("Pkey","preference");
                    startActivity(intent);

                }
            });


            gv = (GridView) findViewById(R.id.gridview1);
            GridTaskClass gridTask = new GridTaskClass(RelatedFacesActivity.this, gv, pb);
            host = host + "/" + userid + "/" + imgname;
            gridTask.execute(host);
            gv.setOnItemClickListener(RelatedFacesActivity.this);


        ImageView ivDel=(ImageView) tool.findViewById(R.id.imgDelete);
        // code for Delete button in Grid view
        ivDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (bitmapNames.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Kindly select atleast one image",Toast.LENGTH_LONG).show();
                }
                else {

                    GridSelectionClass gridSel = new GridSelectionClass(RelatedFacesActivity.this, pb, bitmapNames,userid, RelatedFacesActivity.this);
                    selectHost = host1+"/DeleteSelected";
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
                else
                {
                    GridSelectionClass gridSel = new GridSelectionClass(RelatedFacesActivity.this, pb, bitmapNames,userid, RelatedFacesActivity.this);
                    selectHost = host1+"/MarkFavourite";
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

                    GridSelectionClass gridSel = new GridSelectionClass(RelatedFacesActivity.this, pb, bitmapNames, userid,RelatedFacesActivity.this);
                    selectHost = host1 +"/ChangeRelavance";
                    selection="Relavance Changed";
                    gridSel.execute(selectHost);

                }
            }
        });
        }


    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {

        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
        bitmapclass = GridTaskClass.getBitmapclass();
        imgFullName=bitmapclass.get(i).name;
        intent.putExtra("iName", imgFullName);
        intent.putExtra("flag", "Related");
        intent.putExtra("Id", imgname);
        intent.putExtra("Uid",userid);
        intent.putExtra("FName",fullName);
        startActivity(intent);
    }

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
            });
        }
        else if(res_id == R.id.action_cancel){
            selected  = false;
            invalidateOptionsMenu();
            tool.setVisibility(View.INVISIBLE);
            bitmapNames.clear();
            tv.setText("Related Images");
            setGridViewAdapter();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setGridViewAdapter(){
        adapter = new ImageAdapter_Grid(RelatedFacesActivity.this, bitmapclass);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(RelatedFacesActivity.this);
    }


    //action for mobile back button pressed
    @Override
    public   void onBackPressed(){

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        return;
    }

    public void reloadActivity(){

        final int countImages = bitmapNames.size();
        Toast.makeText(RelatedFacesActivity.this, countImages +" Images" +selection, Toast.LENGTH_LONG).show();
        final Intent intent = new Intent(getApplicationContext(),RelatedFacesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Id",folder);
        intent.putExtra("flag",imgname);
        intent.putExtra("Uid",userid);
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                                startActivity(intent);
                            }
                        }, 2000);
    }
}


