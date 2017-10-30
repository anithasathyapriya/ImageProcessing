package com.example.hp.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class PreferencePage extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences pref;
    Spinner spinner;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_page_layout);

        spinner=(Spinner) findViewById(R.id.pref_spinner);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        item= spinner.getSelectedItem().toString();
        Button b=(Button) findViewById(R.id.btn_preference);
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("preference",item);
        editor.commit();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
}
