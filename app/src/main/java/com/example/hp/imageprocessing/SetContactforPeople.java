package com.example.hp.imageprocessing;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.hp.imageprocessing.R.id.contact;

public class SetContactforPeople extends AppCompatActivity {

    final static String host ="http://192.168.48.247/EventTraceWebAppV1/Service1.svc";
    public Bitmap bitmapimage;
    Toolbar tb,toolbar;
    Spinner spinner;
    String cName,folder,fullName,flag,userid,imgName,personName,preferenceKey="null";
    ImageButton plusButon;
    EditText textAddName,textViewImages,contactName;
    ArrayList<String> contactList;
    Cursor cursor;
    int counter;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setcontact_layout);

        // setting the title
        tb = (Toolbar) findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        mListView = (ListView) findViewById(R.id.list_Contact);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("Add contacts");
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        spinner.setVisibility(View.INVISIBLE);
        textAddName = (EditText) findViewById(R.id.edxAddName);
        textViewImages = (EditText) findViewById(R.id.edxRelatedImages);

        //spinnerContact=(Spinner)findViewById(R.id.spinnerContact);

        final Bundle b = getIntent().getExtras();
        folder = b.get("Id").toString();
        flag=imgName= b.get("flag").toString();
        userid = b.get("Uid").toString();
        fullName=b.get("FName").toString();
        if (b.containsKey("Pkey"))
            preferenceKey = b.get("Pkey").toString();
        // setting back button in the toolbar
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject a = JSONParser.getJSONFromUrl(host+"/ViewPeopleImage/"+userid+"/"+imgName);
                return a;
            }
            @Override
            protected void onPostExecute(JSONObject a) {
                try {
                    imgName = a.getString("imgName");
                    personName=a.getString("name");
                    JSONArray img = a.getJSONArray("imgData");
                    byte[] by = new byte[img.length()];
                    for (int i = 0; i < img.length(); i++) {
                        by[i] = (byte) (((int) img.get(i)) & 0xFF);
                    }
                    bitmapimage = BitmapFactory.decodeByteArray(by, 0, by.length);
                    ImageView imgVIew = (ImageView) findViewById(R.id.imgContact);
                    imgVIew.setVisibility(View.VISIBLE);
                    imgVIew.setImageBitmap(bitmapimage);
                    if(personName.compareTo("null") != 0){
                        contactName.setVisibility(View.VISIBLE);
                        contactName.setText(personName);
                    }

                } catch (JSONException e) {
                    Log.i("JSON", e.getMessage());
                }
            }
        }.execute();

        contactName = (EditText) findViewById(R.id.edxContactName);
        contactName.setVisibility(View.VISIBLE);
        contactName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                contactName.setText("");
                return false;
            }
        });
        // plus button press event
        plusButon = (ImageButton) findViewById(R.id.btnPlus);
        plusButon.setVisibility(View.VISIBLE);
        plusButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setClickable(true);
                getContacts();
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String cont=(String )mListView.getItemAtPosition(i);
                        contactName.setText(cont);
                        /*cName=contactName.getText().toString();
                        cName=cName.trim().replace(" ","");*/
                    }
                } );
            }
        });

        // AddName button click event
        textAddName.setFocusableInTouchMode(false);
        textAddName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mListView.setVisibility(View.INVISIBLE);
            cName = contactName.getText().toString();
            cName=cName.trim();
            if(cName.contains(" "))
                cName=cName.replaceAll(" ","_");
           // contactName.setText(cName);
            if((cName.compareTo("null")==0)||(cName.compareTo("Enter Name")==0))
               Toast.makeText(getApplicationContext(), "Kindly enter a Name", Toast.LENGTH_LONG).show();
            else {
                new AsyncTask<Void, Void, JSONObject>() {
                    @Override
                    protected JSONObject doInBackground(Void... params) {
                        JSONParser.getJSONFromUrl(host +"/UpdateContact/"+userid+"/"+ imgName+"/"+cName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Contact updated Successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                        return null;
                    }
                }.execute();
            }
        }
        });

        textViewImages.setFocusableInTouchMode(false);
        textViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RelatedFacesActivity.class);
                intent.putExtra("Id", "SearchByPeople");
                intent.putExtra("Uid", userid);
                intent.putExtra("flag", flag);//image name set to flag
                intent.putExtra("FName",fullName);
                if (preferenceKey.compareTo("preference") == 0)
                    intent.putExtra("Pkey", "preference");
                startActivity(intent);
            }
        });

       tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
                intent.putExtra("Id", "SearchByPeople");
                intent.putExtra("Uid", userid);
                intent.putExtra("flag", "flag");
                intent.putExtra("FName",fullName);
                intent.putExtra("backkey","BackfromContact");
                if(preferenceKey.compareTo("preference")==0)
                    intent.putExtra("Pkey","preference");
                startActivity(intent);
            }
        });
    }


    public void getContacts() {
            contactList = new ArrayList<String>();
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            StringBuffer output;
            ContentResolver contentResolver = getContentResolver();
            cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
            // Iterate every contact in the phone
            if (cursor.getCount() > 0) {
                counter = 0;
                while (cursor.moveToNext()) {
                    output = new StringBuffer();
                   // String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                    String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                    if (hasPhoneNumber > 0) {
                        output.append("\n"+ name);
                      // Add the contact to the ArrayList
                    contactList.add(output.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetContactforPeople.this,android.R.layout.simple_list_item_1, android.R.id.text1,contactList);
                        mListView.setAdapter(adapter);
                    }
                });
            }
            }
    }
}

