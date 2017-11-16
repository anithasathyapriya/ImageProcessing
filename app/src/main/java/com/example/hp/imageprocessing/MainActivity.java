package com.example.hp.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
    SharedPreferences preference;
    String Sitem,pref;
    String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc";
    String monthflag="false";
    String selectedHost=null;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=(Toolbar)findViewById(toolbar);
         pb=(ProgressBar) findViewById(R.id.progressbar);
        //String[] temp=new String[3];

        Bundle b=getIntent().getExtras();
        userid= b.get("Uid").toString();

         // setting the title
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("My Moments");

        spinner=(Spinner)findViewById(R.id.categorySpinner);
        //spinner.setSelection(Integer.parseInt(pref));
        spinner.setEnabled(true);
        SpinnerClick();

        // Default page loading(bY Date)monthflag="false";

        //selectedHost = host + "/ThreeImages/"+userid;

        listview = (ListView)findViewById(R.id.listView1);
//        SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
//        exe.execute(selectedHost);
        listview.setOnItemClickListener(MainActivity.this);
//        spinner.setEnabled(true);
    }

    public void onItemClick(AdapterView<?> av, View v, int position, long id)
    {
        ImageClass item=(ImageClass) av.getItemAtPosition(position);
        Intent intent;
        if(monthflag.compareTo("true")==0){
            intent = new Intent(getApplicationContext(),MonthActivity.class);

        }
        else {
            intent = new Intent(getApplicationContext(), imagesIn_grid.class);
        }
        intent.putExtra("Id",item.getName());
        intent.putExtra("flag", monthflag);
        intent.putExtra("Uid",userid);
        startActivity(intent);
    }

    public void SpinnerClick( ){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng)
            {
                Sitem = spinner.getSelectedItem().toString();
                if((Sitem.compareTo("Search By Date"))==0 )
                {
                    monthflag="false";
                    selectedHost=host+"/ThreeImages/"+userid;
                    listview = (ListView)findViewById(R.id.listView1);
                    SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
                    exe.execute(selectedHost);
                }
                else if((Sitem.compareTo("Search By People"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),imagesIn_grid.class);
                    intent.putExtra("Id","SearchByPeople");
                    intent.putExtra("Uid",userid);
                    intent.putExtra("flag","flag");
                    startActivity(intent);
                }
                else if ((Sitem.compareTo("Search By Month"))==0)
                {
                    monthflag="true";
                    selectedHost=host+"/Months/"+userid;
                    listview = (ListView)findViewById(R.id.listView1);
                    SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
                    exe.execute(selectedHost);
                    listview.setOnItemClickListener(MainActivity.this);
                }
                else if ((Sitem.compareTo("Search By Favourites"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),imagesIn_grid.class);
                    intent.putExtra("Id","Favourite");
                    intent.putExtra("Uid",userid);
                    intent.putExtra("flag","Favourite");
                    startActivity(intent);
                }
                else if ((Sitem.compareTo("Set Preference"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),PreferencePage.class);
                    intent.putExtra("Uid",userid);
                    startActivity(intent);
                }
                else if  ((Sitem.compareTo("Logout"))==0)
                {
                    Intent intent= new  Intent(getApplicationContext(),Login_Activity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView arg0)
            {
                Toast.makeText(getApplicationContext(),"Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
