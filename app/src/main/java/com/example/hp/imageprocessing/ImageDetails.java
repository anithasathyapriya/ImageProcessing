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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageDetails extends AppCompatActivity {


    final static String host = "http://52.221.152.166/EventTrace/Service1.svc";
    public Bitmap bitmapimage;
    PhotoViewAttacher attacher;
    String name,imgDec,imgName,relevance,favourite,folder,flag,userid,comment,fullName,preferenceKey="null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        Bundle b = getIntent().getExtras();
        imgName = b.get("iName").toString();
        folder = b.get("Id").toString();
        flag = b.get("flag").toString();
        userid = b.get("Uid").toString();
        fullName=b.get("FName").toString();
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        TextView tv = (TextView) tb.findViewById(R.id.toolbar_title);
        tv.setText("" + imgName);
        tv.setTextColor(Color.BLACK);
        if (b.containsKey("Pkey"))
            preferenceKey = b.get("Pkey").toString();
            setSupportActionBar(tb);
            Spinner spinner = (Spinner) findViewById(R.id.categorySpinner);
            spinner.setVisibility(View.INVISIBLE);

            // back button in the tool bar

            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag.compareTo("Related") == 0) {
                        Intent intent = new Intent(getApplicationContext(), RelatedFacesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Id",folder);
                        intent.putExtra("Uid", userid);
                        intent.putExtra("flag", folder);
                        //intent.putExtra("FName",fullName);
                        if (preferenceKey.compareTo("preference") == 0)
                            intent.putExtra("Pkey", "preference");
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Id", folder);
                        intent.putExtra("Uid", userid);
                        intent.putExtra("flag", flag);
                        intent.putExtra("FName",fullName);
                        if (preferenceKey.compareTo("preference") == 0)
                            intent.putExtra("Pkey", "preference");
                        startActivity(intent);
                    }
                }
            });


            //Display selected  image
            new AsyncTask<Void, Void, JSONObject>() {

                @Override
                protected JSONObject doInBackground(Void... params) {
                    JSONObject a = JSONParser.getJSONFromUrl(host + "/viewImage/" + imgName + "/" + userid);
                    return a;
                }

                @Override
                protected void onPostExecute(JSONObject a) {
                    try {
                        name = a.getString("imgName");
                        imgDec = a.getString("imgDec");
                        relevance = a.getString("relevance");
                        favourite = a.getString("favourite");
                        JSONArray img = a.getJSONArray("image");
                        byte[] by = new byte[img.length()];
                        for (int i = 0; i < img.length(); i++) {
                            by[i] = (byte) (((int) img.get(i)) & 0xFF);
                        }
                        bitmapimage = BitmapFactory.decodeByteArray(by, 0, by.length);
                        ImageView imgVIew = (ImageView) findViewById(R.id.imageDetail);
                        Button relevant=(Button)findViewById(R.id.btnIrrelevant);
                        imgVIew.setVisibility(View.VISIBLE);
                        EditText tv = (EditText) findViewById(R.id.etComment);
                        imgVIew.setImageBitmap(bitmapimage);
                        attacher=new PhotoViewAttacher(imgVIew);
                        tv.setText((""));
                        if (imgDec.compareTo("null") != 0)
                            tv.setText(imgDec);
                        if(relevance.compareTo("Max")==0)
                                relevant.setText("Exclude Selected");
                        else
                             relevant.setText("Include Selected");
                    } catch (JSONException e) {
                        Log.i("JSON", e.getMessage());
                    }
                }
            }.execute();


            // Code for Relevant button
            Button bi = (Button) findViewById(R.id.btnIrrelevant);
            bi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            JSONParser.getJSONFromUrl(host + "/UpdateRelevance/" + imgName + "/" + userid);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Relevance Flag Updated", Toast.LENGTH_LONG).show();
                                }
                            });
                            return null;
                            //dummy
                        }
                    }.execute();
                }
            });
            // For Favourite button
            Button btf = (Button) findViewById(R.id.btnFavourite);
            btf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            JSONParser.getJSONFromUrl(host + "/UpdateFavourite/" + imgName + "/" + userid);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Image marked as Favourote", Toast.LENGTH_LONG).show();
                                }
                            });
                            return null;
                        }
                    }.execute();

                }
            });

            // Action for Delete button

            Button btd = (Button) findViewById(R.id.btnDelete);
            btd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            JSONParser.getJSONFromUrl(host + "/DeleteImage/" + imgName + "/" + userid);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Image Deleted", Toast.LENGTH_LONG).show();
                                }
                            });
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            Intent intent = new Intent(ImageDetails.this, imagesIn_grid.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Id", folder);
                            intent.putExtra("Uid", userid);
                            intent.putExtra("flag", flag);
                            startActivity(intent);
                            //((Activity)getApplicationContext()).finish();
                        }
                    }.execute();

                }
            });

            //for Updating the comments
            Button btu = (Button) findViewById(R.id.btnUpdate);
            btu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText e = (EditText) findViewById(R.id.etComment);
                    comment = e.getText().toString();
                    if(comment.contains(" "))
                        comment=comment.replaceAll(" ","_");
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            JSONParser.getJSONFromUrl(host+"/UpdateComment/"+ imgName + "/" + userid + "/" + comment);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Comments added", Toast.LENGTH_LONG).show();

                                }
                            });
                            return null;
                        }
                    }.execute();
                    //e.setText("");
                }
            });

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.imagedetails_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(android.view.MenuItem item) {
            int res_id = item.getItemId();
            if (res_id == R.id.action_Home)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Uid",userid);
                intent.putExtra("FName",fullName);
                startActivity(intent);
                finish();
            }
            return super.onOptionsItemSelected(item);
        }


    // code for mobile back button press
    @Override
    public  void onBackPressed() {
        Log.i("onBackPressed: ", "");
        Intent intent = new Intent(getApplicationContext(), imagesIn_grid.class);
        intent.putExtra("Id", folder);
        intent.putExtra("Uid",userid);
        intent.putExtra("flag",flag);
        startActivity(intent);
    }
}
