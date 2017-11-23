package com.example.hp.imageprocessing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class RelatedFacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public ArrayList<GridImageClass> bitmapclass;
    public ArrayList<String> bitmapNames;
    String imgname,userid,folder,name;
    MenuItem itemCancel,itemSelect;
    ImageAdapter_Grid adapter;
    boolean selected = false;
    ProgressBar pb;
    Toolbar tb,tool;
    TextView tv;
    GridView gv;
    Spinner spinner;
    String host="http://192.168.0.177/EventTraceWebAppV1/Service1.svc/SearchRelatedImages";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_in_grid);
        bitmapclass = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        imgname = b.get("flag").toString();
        userid = b.get("Uid").toString();
        pb = (ProgressBar) findViewById(R.id.progressbar2);
         tb = (Toolbar) findViewById(R.id.toolbar);
        spinner=(Spinner)findViewById(R.id.categorySpinner);
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

                Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id","SearchByPeople");
                intent.putExtra("Uid",userid);
                intent.putExtra("flag","flag");
                startActivity(intent);

            }
            });


        gv = (GridView) findViewById(R.id.gridview1);
        GridTaskClass gridTask = new GridTaskClass(RelatedFacesActivity.this, gv, pb);
        host=host+"/"+userid+"/"+imgname;
        gridTask.execute(host);
        gv.setOnItemClickListener(RelatedFacesActivity.this);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {

        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
        bitmapclass = GridTaskClass.getBitmapclass();
        intent.putExtra("iName", bitmapclass.get(i).name);
        intent.putExtra("flag", "Related");
        intent.putExtra("Id", imgname);
        intent.putExtra("Uid",userid);
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
}


