package com.example.hp.imageprocessing;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login_Activity extends AppCompatActivity{

    String host = "http://192.168.48.247/EventTraceWebAppV1/Service1.svc/Login";
    String Uname,Pword,Fname="Dummy";
    String[] Result;
    EditText user,pass;
    TextView name,textview;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        user=(EditText) findViewById(R.id.edxUsername);
        pass=(EditText) findViewById(R.id.edxPassword);
        textview = (TextView) findViewById(R.id.txtMessage);

        user.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                user.setText("");
                return false;
            }
        });
        pass.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                pass.setText("");
                pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                return false;
            }
        });

        Button login=(Button)findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uname=user.getText().toString();
                Pword=pass.getText().toString();

                UserClass User=new UserClass(Fname,Uname,Pword);

            new AsyncTask<UserClass,Void,String>()
            {
                 @Override
                 protected String doInBackground(UserClass... params)
                {
                    String a = UserClass.createUser(host, params[0]);
                    return (a);
                }

                @Override
                protected void onPostExecute(String result)
                {
                    Result=result.split("\n");
                    result=result.substring(1,8);
                    if (result.compareTo("Success")==0)
                    {
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        int start = Result[0].lastIndexOf("_")+1;
                        int end = Result[0].lastIndexOf("_")+5;
                        String userid=Result[0].substring(start, end);
                        intent.putExtra("Uid",userid);
                        startActivity(intent);
                    }
                    else {

                        textview.setText("Wrong Username and Password,Re Enter again");
                        textview.setVisibility(View.VISIBLE);
                        user.setText("Enter Username");
                        pass.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        pass.setText("Enter Password");
                    }
                }
            }.execute(User);
            }
        });

        //signup activity
        EditText signup=(EditText)findViewById(R.id.edxSignup);
        signup.setFocusableInTouchMode(false);
        signup.setOnClickListener(new View.OnClickListener()
        { @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getApplicationContext(),Signup_Activity.class);
                startActivity(intent);
            }
        });

        //Sending email for forget password
        EditText forget=(EditText)findViewById(R.id.edxFPass);
        final String to="anisatpri@gmail.com";
        forget.setOnClickListener(new View.OnClickListener()
        {@Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{to});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Requesting for New Password");
                intent.putExtra(Intent.EXTRA_TEXT,"Forget user name");
               // intent.setType("message/rfc822");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    //startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                    startActivity(intent);
                }
                textview.setText("Email sent to Admin Department,Thanks!");
            }
        });
    }
}
