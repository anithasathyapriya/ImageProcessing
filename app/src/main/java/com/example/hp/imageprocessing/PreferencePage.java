package com.example.hp.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class PreferencePage extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences pref;
    Spinner spinner;
    String item,userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_page_layout);
        Bundle b = getIntent().getExtras();
        userid = b.get("Uid").toString();

        spinner=(Spinner) findViewById(R.id.pref_spinner);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Button button=(Button) findViewById(R.id.btn_preference);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int i =  spinner.getSelectedItemPosition();
        item=String.valueOf(i);

        SharedPreferences.Editor editor=pref.edit();
        editor.putString("preference",item);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Uid",userid);
        startActivity(intent);

    }
}
