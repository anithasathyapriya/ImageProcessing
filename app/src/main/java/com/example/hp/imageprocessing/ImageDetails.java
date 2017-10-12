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
    String name,imgDec,relevance,favourite, folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        Bundle b = getIntent().getExtras();
        final String imgName= b.get("iName").toString();
        folder = b.get("Id").toString();
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
    }

    @Override
    public   void onBackPressed(){
        Log.i( "onBackPressed: ","");
        Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
        intent.putExtra("Id", folder);
        startActivity(intent);
        return;
    }
}
