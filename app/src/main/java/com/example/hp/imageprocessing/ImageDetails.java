package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageDetails extends AppCompatActivity {


    final static String host = "http://10.217.138.174/EventTraceWebAppV1/Service1.svc/viewImage";
    public Bitmap bitmapimage;
    String name,imgDec,relevance,favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        Bundle b = getIntent().getExtras();
        final String imgName= b.get("iName").toString();
        Toolbar tb=(Toolbar)findViewById(R.id.toolbar);
        TextView tv=(TextView) tb.findViewById(R.id.toolbar_title);
        tv.setText(""+imgName);
        tv.setTextColor(Color.BLACK);


            //Display selected  image
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject a = JSONParser.getJSONFromUrl(host + "/" + imgName);
                return a;
            }

            @Override
            protected void onPostExecute(JSONObject a) {
                try {
                    name = a.getString("imgName");
                    imgDec=a.getString("imgDec");
                    relevance=a.getString("relevance");
                    favourite=a.getString("favourite");
                    JSONArray img = a.getJSONArray("image");
                    byte[] by = new byte[img.length()];
                    for (int i = 0; i < img.length(); i++) {
                        by[i] = (byte) (((int) img.get(i)) & 0xFF);
                    }
                    bitmapimage = BitmapFactory.decodeByteArray(by, 0, by.length);
                    ImageView imgVIew = (ImageView) findViewById(R.id.imageDetail);
                    //TextView tv = (TextView) findViewById(R.id.txtImgName);
                    imgVIew.setImageBitmap(bitmapimage);
                    //tv.setText(name);
                } catch (JSONException e) {
                    Log.i("JSON", e.getMessage());
                }
            }
        }.execute();

        // Code for Relevant button
        Button bi=(Button) findViewById(R.id.btnIrrelevant);
        bi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        JSONParser.getJSONFromUrl(host + "/UpdateRelevance/" + imgName);

                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                Toast.makeText(getApplicationContext(),"Relevance Flag Updated",Toast.LENGTH_LONG).show();
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
                        intent.putExtra("iName",imgName);
                        startActivity(intent);
                    }
                }.execute();
            }
        });
        // For Favourite button
        Button btf=(Button)findViewById(R.id.btnFavourite);
        btf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        JSONParser.getJSONFromUrl(host + "/UpdateFavourite" + imgName);

                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                Toast.makeText(getApplicationContext(),"Image marked as Favourote",Toast.LENGTH_LONG).show();
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Intent intent = new Intent(getApplicationContext(),ImageDetails.class);
                        intent.putExtra("iName",imgName);
                        startActivity(intent);
                    }
                }.execute();

            }
        });

        // Action for Delete button

        Button btd=(Button)findViewById(R.id.btnDelete);
        btd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        JSONParser.getJSONFromUrl(host + "/DeleteImage/" + imgName);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Image Deleted", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }.execute();
                Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
                intent.putExtra("Id",fname);
                startActivity(intent);
            }
        });

        //for Updating the comments
        Button btu=(Button)findViewById(R.id.btnUpdate);
        btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText e=(EditText)findViewById(R.id.etComment);
                final String comment=e.getText().toString();
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        JSONParser.getJSONFromUrl(host + "/UpdateComment/" + imgName+"/"+comment);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Comments added", Toast.LENGTH_LONG).show();
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
                        intent.putExtra("iName",imgName);
                        startActivity(intent);
                    }
                }.execute();
            }
        });

    }
    @Override
    public   void onBackPressed(){
        Log.i( "onBackPressed: ","");
        Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
        intent.putExtra("Id", folder);
        startActivity(intent);
        return;
}
