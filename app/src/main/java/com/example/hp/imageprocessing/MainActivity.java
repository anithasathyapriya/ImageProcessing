package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import static com.example.hp.imageprocessing.R.id.toolbar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private Toolbar tb;
    private ProgressBar pb;
    Spinner spinner;
    ListView listview;
    String Sitem;
    String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc";
    String monthflag="false";
    String selectedHost=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=(Toolbar)findViewById(toolbar);
        tb.setTitle("My Moments");
        tb.setTitleTextColor(Color.BLACK);
        pb=(ProgressBar) findViewById(R.id.progressbar);
        String[] temp=new String[3];


         // setting the title
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("My Moments");

        spinner=(Spinner)findViewById(R.id.categorySpinner);
        spinner.setSelection(-1);
        //SpinnerClass(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
          @Override
          public void onItemSelected(AdapterView adapter, View v, int i, long lng)
          {
              //Toast.makeText(getApplicationContext(), (CharSequence) spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
              Sitem=(String) spinner.getSelectedItem();
              if((Sitem.compareTo("Search By Date"))==0)
              {
                  monthflag="false";
                  selectedHost=host+"/ThreeImages";
                  listview = (ListView)findViewById(R.id.listView1);
                  SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag);
                  exe.execute(selectedHost);
              }

              else if ((Sitem.compareTo("Search By Month"))==0)
               {
                   monthflag="true";
                   selectedHost=host+"/MonthImages";
                   listview = (ListView)findViewById(R.id.listView1);
                   SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag);
                   exe.execute(selectedHost);
                  /*Intent intent= new  Intent(getApplicationContext(),MainActivity.class);
                  startActivity(intent);*/
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
              Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
          }
        });

        // Default page loading(bY Date)monthflag="false";

        selectedHost=host+"/ThreeImages";
        listview = (ListView)findViewById(R.id.listView1);
        SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag);
        exe.execute(selectedHost);
        listview.setOnItemClickListener(MainActivity.this);

    }

    public void onItemClick(AdapterView<?> av, View v, int position, long id)
    {
        ImageClass item=(ImageClass) av.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
        intent.putExtra("Id",item.getName());
        intent.putExtra("flag", monthflag);
        startActivity(intent);

    }



}
