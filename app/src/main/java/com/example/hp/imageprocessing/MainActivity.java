package com.example.hp.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
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
    String Sitem;
    final static String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/ThreeImages";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=(Toolbar)findViewById(toolbar);
        tb.setTitle("My Moments");
        tb.setTitleTextColor(Color.BLACK);
        pb=(ProgressBar) findViewById(R.id.progressbar);
        String[] temp=new String[3];


         // setting the title
        TextView mTitle = (TextView) tb.findViewById(R.id.toolbar_title);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("My Moments");

        spinner=(Spinner)findViewById(R.id.categorySpinner);
        spinner.setSelection(-1);
        //SpinnerClass(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
          @Override
          public void onItemSelected(AdapterView adapter, View v, int i, long lng)
          {
              //Toast.makeText(getApplicationContext(), (CharSequence) spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
              Sitem=(String) spinner.getSelectedItem();
              if((Sitem.compareTo("Search By Date"))==0)
              {
                  Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                  startActivity(intent);
              }


              else if ((Sitem.compareTo("Search By Favourites"))==0)
              {
                    Intent intent= new  Intent(getApplicationContext(),imagesIn_grid.class);
                    intent.putExtra("Id","Favourite");
                    startActivity(intent);
              }

          }
          @Override
          public void onNothingSelected(AdapterView arg0)
          {
              Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
          }
        });


        //reading   first 3 images with date in all  available dates
        new AsyncTask<Void, Void, JSONArray>() {
            ImageClass c;

            //progress bar activation
            @Override
            protected  void onPreExecute(){
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(0);
                pb.setIndeterminate(true);
            }
            @Override
            protected JSONArray   doInBackground(Void... params) {
                JSONArray a = JSONParser.getJSONArrayFromUrl(host );
                return (a);
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                Bitmap bng;
                ListView listview;
                ArrayList<ImageClass>list=new ArrayList<>();
                try {
                    for (int j = 0; j < result.length(); j++)
                    {   c = new ImageClass();
                        try {
                            JSONArray a = result.getJSONObject(j).getJSONArray("images");
                            String name = result.getJSONObject(j).getString("fNames");
                            c.name = name;
                            for (int i = 0; i < a.length(); i++) {
                                JSONArray jByte = a.getJSONObject(i).getJSONArray("Data");
                                byte[] by = new byte[jByte.length()];
                                for (int k = 0; k < jByte.length(); k++) {
                                    by[k] = (byte) (((int) jByte.get(k)) & 0xFF);
                                }
                                bng = BitmapFactory.decodeByteArray(by, 0, by.length);
                                c.indexes.add(i);
                                saveImages(c.name, String.valueOf(i), bng);
                            }
                        } catch (Exception e) { }
                        list.add(c);
                    }

                } catch (Exception e) {  }

                 ImageAdapter_List adapter = new ImageAdapter_List(MainActivity.this,R.layout.activity_main,list);
                 listview=(ListView) findViewById(R.id.listView1);
                 listview.setAdapter(adapter);
                 listview.setOnItemClickListener(MainActivity.this);
                 pb.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    public void onItemClick(AdapterView<?> av, View v, int position, long id)
    {
        ImageClass item=(ImageClass) av.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(),imagesIn_grid.class);
        intent.putExtra("Id",item.getName());
        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if (res_id == R.id.action_search)
            Toast.makeText(getApplicationContext(),item+"selected",Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    public void saveImages(String folder, String imgIndex, Bitmap bng){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bng.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(getApplicationContext().getCacheDir(),folder);
            if (!file.exists()) {
                file.mkdirs();
            }
            File f = new File(file, imgIndex);
            if (f.exists()) {
                f.delete();
            }
            outputStream = new FileOutputStream(f);
            outputStream.write(temp.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
