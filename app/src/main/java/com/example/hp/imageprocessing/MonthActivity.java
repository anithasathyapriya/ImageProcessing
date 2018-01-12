package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.hp.imageprocessing.R.id.toolbar;

// for getting all the days of a month

public class MonthActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private Toolbar tb;
    private ProgressBar pb;
    ListView listview;
    Spinner spinner;
    String userid,flag,folder,Sitem,selectedHost,datestring,fullName,monthflag="true";
    String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/GetAllDays/";
    Date d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);
        final Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        userid=b.get("Uid").toString();
        flag=b.get("flag").toString();
        fullName=b.get("FName").toString();
        monthflag="false";
        tb=(Toolbar)findViewById(toolbar);
        listview=(ListView)findViewById(R.id.monthListView);
        spinner=(Spinner)findViewById(R.id.categorySpinner);
        spinner.setVisibility(View.INVISIBLE);
        pb=(ProgressBar) findViewById(R.id.progressbar);
        TextView tv = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        try {
            d = sdf.parse(folder);
            sdf = new SimpleDateFormat("MMM yyyy");
            datestring = sdf.format(d);
        } catch (Exception e) {
        }
        tv.setText("" + datestring);
        tv.setTextColor(Color.BLACK);
        //mTitle.setText("My Album");

        //activate back button
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // code for back button click
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("Uid",userid);
                intent.putExtra("key","backfrommonth");
                intent.putExtra("FName",fullName);
                startActivity(intent);
                finish();
            }
        });

        host=host+folder+"/"+userid;
        listview = (ListView)findViewById(R.id.monthListView);
        SpinnerClass exe = new SpinnerClass(MonthActivity.this, listview, pb, monthflag);
        exe.execute(host);
        listview.setOnItemClickListener(MonthActivity.this);
       // SpinnerClick();
    }

    public void onItemClick(AdapterView<?> av, View v, int position, long id) {
        ImageClass item = (ImageClass) av.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
        intent.putExtra("Id",item.getName());
        intent.putExtra("flag","false");
        intent.putExtra("Uid",userid);
        intent.putExtra("FName",fullName);
        intent.putExtra("key","pagefrommonth");
        startActivity(intent);
    }

   /* public void SpinnerClick(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng)
            {
                Sitem=(String) spinner.getSelectedItem();
                if((Sitem.compareTo("Search By Date"))==0)
                {
                    monthflag="false";
                    selectedHost=host+"/ThreeImages";
                    listview = (ListView)findViewById(R.id.listView1);
                    SpinnerClass exe = new SpinnerClass(MonthActivity.this,listview,pb,monthflag);
                    exe.execute(selectedHost);
                }

                else if ((Sitem.compareTo("Search By Month"))==0)
                {
                    monthflag="true";
                    selectedHost=host+"/MonthImages";
                    listview = (ListView)findViewById(R.id.listView1);
                    SpinnerClass exe = new SpinnerClass(MonthActivity.this, listview,pb,monthflag);
                    exe.execute(selectedHost);
                    listview.setOnItemClickListener(MonthActivity.this);
                }
                else if ((Sitem.compareTo("Search By Favourites"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),imagesIn_grid.class);
                    intent.putExtra("Id","Favourite");
                    intent.putExtra("flag","Favourite");
                    startActivity(intent);
                }
                else if ((Sitem.compareTo("Set Preference"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),PreferencePage.class);
                    startActivity(intent);
                }

            }
            @Override
            public void onNothingSelected(AdapterView arg0)
            {
                Toast.makeText(getApplicationContext(),"Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}
