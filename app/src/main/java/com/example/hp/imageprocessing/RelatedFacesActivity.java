package com.example.hp.imageprocessing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class RelatedFacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public ArrayList<GridImageClass> bitmapclass;
    String imgname,userid,folder;
    ProgressBar pb;
    TextView tv;
    GridView gv;
    Spinner spinner;
    String host="http://192.168.48.247/EventTraceWebAppV1/Service1.svc/SearchRelatedImages";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatedfaces_layout);
        bitmapclass = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        imgname = b.get("flag").toString();
        userid = b.get("Uid").toString();
        pb = (ProgressBar) findViewById(R.id.progressbar2);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
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


        gv = (GridView) findViewById(R.id.gridview2);
        GridTaskClass gridTask = new GridTaskClass(RelatedFacesActivity.this, gv, pb);
        gridTask.execute(host+"/"+userid+"/"+imgname);
        gv.setOnItemClickListener(RelatedFacesActivity.this);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
        bitmapclass = GridTaskClass.getBitmapclass();
        intent.putExtra("iName", bitmapclass.get(i).name);
        intent.putExtra("flag", "Related");
        intent.putExtra("Id", imgname);
        intent.putExtra("Uid",userid);
        startActivity(intent);

    }


    //action for mobile back button pressed
    @Override
    public   void onBackPressed(){

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        return;
    }
}


