package com.example.hp.imageprocessing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencePage extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences reader;
    Spinner spinner,sp;
    Toolbar tb;

    String item,userid,fullName;
    //private static final String MY_PREFERENCES = "my_preferences";
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_page_layout);

        Bundle b = getIntent().getExtras();
        userid = b.get("Uid").toString();
        fullName=b.get("FName").toString();
        spinner=(Spinner) findViewById(R.id.pref_spinner);
        tb=(Toolbar)findViewById(R.id.toolbar);
        sp=(Spinner)findViewById(R.id.categorySpinner);
        sp.setVisibility(View.INVISIBLE);
        // setting the title
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("Set Preference");
       // reader= getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        reader=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Button button=(Button) findViewById(R.id.btn_preference);
        button.setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {
       item =spinner.getSelectedItem().toString();
        editor = reader.edit();
        editor.putBoolean("is_first", false);
        editor.putString("preference",item);
        editor.commit();

        Toast.makeText(getApplicationContext(),"Your Home Page is set",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("Uid",userid);
        intent.putExtra("FName",fullName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {

        if (item.getItemId()== R.id.action_cancel) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Uid", userid);
            intent.putExtra("FName",fullName);
            startActivity(intent);
        }
        return  super.onOptionsItemSelected(item);

    }
}
