package com.example.hp.imageprocessing;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Signup_Activity extends AppCompatActivity {

    String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/Signup";
    //"http://192.168.48.247/EventTraceWebAppV1/Service1.svc/Login";
    String Result,Uname,Pword,cname;
    EditText pass,pass1,name,username;
    TextView error;
    UserClass user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        name=(EditText)findViewById(R.id.edxName);
        username=(EditText)findViewById(R.id.edxUname);
        error= (TextView)findViewById(R.id.txtError);
        pass=(EditText) findViewById(R.id.edxPass);
        pass1=(EditText) findViewById(R.id.edxPass1);
        name.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                name.setText("");
                return false;
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    username.setText("");
                }
            }
        });
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pass.setText("");
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        pass1.setOnFocusChangeListener(new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pass1.setText("");
                    pass1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        Button submit=(Button) findViewById(R.id.btnSubmit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                cname = name.getText().toString();
                Uname = username.getText().toString();
                if ((pass.getText().toString()).compareTo((pass1.getText().toString())) == 0) {
                    Pword = pass.getText().toString();

                    user=new UserClass(cname, Uname, Pword);


                    new AsyncTask<UserClass, Void, String>() {
                        @Override
                        protected String doInBackground(UserClass... params) {
                            String a = UserClass.createUser(host, params[0]);
                            return (a);
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            Result = result;
                            error.setText("Account Successfully Created");
                            error.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                            startActivity(intent);
                        }
                    }.execute(user);

                } else {
                    error.setText("Password not matching, re-type again");
                    error.setVisibility(View.VISIBLE);
                    pass.setText("");
                    pass1.setText("");
                    pass.requestFocus();
                }
            }
        });
    }
}

