package com.example.hp.imageprocessing;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SetContactforPeople extends AppCompatActivity {

    Toolbar tb;
    Spinner spinner,spinnerContact;
    String folder,flag,userid,name;
    Button button;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        // setting the title
        tb=(Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("My contacts");
        spinner=(Spinner)findViewById(R.id.categorySpinner);
        spinner.setVisibility(View.INVISIBLE);
        spinnerContact=(Spinner)findViewById(R.id.spinnerContact);

        button=(Button) findViewById(R.id.button);
        tv=(TextView)findViewById(R.id.textView2);

        final Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        flag = b.get("flag").toString();
        userid=b.get("Uid").toString();


        // setting back button in the toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("Uid",userid);
                intent.putExtra("Id", folder);
                intent.putExtra("flag",flag);
            }
            });
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                loadcontacts();
            }
        });

    }

    private void loadcontacts(){
        StringBuilder builder=new StringBuilder();
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount()>0)
        {
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            builder.append(name);
        }
        cursor.close();

    }
}
