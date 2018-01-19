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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar tb;
    private ProgressBar pb;
    Spinner spinner;
    ListView listview;
    String userid,Sitem,fullName,monthflag = "false",selectedHost = null;
    boolean isFirstBoolean=true;
    String host = "http://52.221.152.166/EventTrace/Service1.svc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb = (Toolbar) findViewById(toolbar);
        pb = (ProgressBar) findViewById(R.id.progressbar);
        //String[] temp=new String[3];

        Bundle b = getIntent().getExtras();
        userid = b.get("Uid").toString();
        fullName=b.get("FName").toString();
        if(b.containsKey("FName"))
        {
            fullName=b.get("FName").toString();
        }
         if (b.containsKey("key"))
         {
             monthflag=b.get("key").toString();
         }
         else if(b.containsKey("monthflag"))
         {
             monthflag=b.get("monthflag").toString();

         }

        // setting the title
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("My Moments");

        if (monthflag.compareTo("false")==0) //activate spinner
        {
            setspinner();
            selectedHost = host + "/ThreeImages/"+userid;

        }
        else if(monthflag.compareTo("backfrommonth")==0)
        {
            setspinner();
            selectedHost = host +"/Months/"+userid;
            monthflag="true";
        }
        else //activate back button
        {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            selectedHost = host + "/Months/"+userid;

        }

       // Default page loading(by Date)monthflag="false";

        listview = (ListView) findViewById(R.id.listView1);
        SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
        exe.execute(selectedHost);
        listview.setOnItemClickListener(MainActivity.this);

        SpinnerClick();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
            getMenuInflater().inflate(R.menu.action_menu, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.item1 :
                monthflag = "false";
                SpinnerClass exe = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
                selectedHost = host + "/ThreeImages/" + userid;
                listview = (ListView) findViewById(R.id.listView1);
                exe.execute(selectedHost);
                return  true;
            case R.id.item2 :
                monthflag = "true";
                selectedHost = host+"/Months/"+userid;
                SpinnerClass exe1 = new SpinnerClass(MainActivity.this, listview, pb, monthflag, MainActivity.this);
                listview = (ListView) findViewById(R.id.listView1);
                exe1.execute(selectedHost);
                listview.setOnItemClickListener(MainActivity.this);
                return  true;
            case R.id.item3 :
                Intent intent = new Intent(getApplicationContext(), SearchPeople.class);
                intent.putExtra("Id","SearchByPeople");
                intent.putExtra("Uid",userid);
                intent.putExtra("flag","flag");
                intent.putExtra("FName",fullName);
                startActivity(intent);
                return  true;
            case R.id.item4 :
                Intent intent1 = new Intent(getApplicationContext(), imagesIn_grid.class);
                intent1.putExtra("Id", "Favourite");
                intent1.putExtra("Uid", userid);
                intent1.putExtra("flag", "Favourite");
                intent1.putExtra("FName",fullName);
                startActivity(intent1);
                return  true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

 //on click of a row
    public void onItemClick(AdapterView<?> av, View v, int position, long id) {
        ImageClass item = (ImageClass) av.getItemAtPosition(position);
        Intent intent;
        if (monthflag.compareTo("true") == 0) {
            intent = new Intent(getApplicationContext(), MonthActivity.class);

        } else {
            intent = new Intent(getApplicationContext(), imagesIn_grid.class);
        }
        intent.putExtra("Id", item.getName());
        intent.putExtra("flag", monthflag);
        intent.putExtra("Uid", userid);
        intent.putExtra("FName",fullName);
        startActivity(intent);
    }

    public void SpinnerClick() {

        spinner = (Spinner) findViewById(R.id.categorySpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int position, long lng) {

                Sitem = spinner.getSelectedItem().toString();
                if (isFirstBoolean)// to differentiate the user click and default selection by spinner itself
                    isFirstBoolean = false;
                else if ((Sitem.compareTo("Set My Startup Page")) == 0) {
                    Intent intent = new Intent(getApplicationContext(), PreferencePage.class);
                    intent.putExtra("Uid", userid);
                    intent.putExtra("FName",fullName);
                    startActivity(intent);
                } else if ((Sitem.compareTo("Logout")) == 0) {
                    Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setspinner() {
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        spinner.setEnabled(true);
        String[] yourArray = getResources().getStringArray(R.array.items);
        yourArray[0] = fullName;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, yourArray);
        spinner.setAdapter(dataAdapter);
    }
}
