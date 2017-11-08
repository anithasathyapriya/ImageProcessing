package com.example.hp.imageprocessing;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by HP on 6/11/2017.
 */

public class UserClass extends HashMap<String,String> {

    public  UserClass(String Fname,String Uname,String Pword)
    {
        put("Fname",Fname);
        put("Uname",Uname);
        put("Pword",Pword);
    }

    public  static String  createUser(String url, UserClass user){
        JSONObject juser=new JSONObject();
        try
        {
            juser.put("username", user.get("Uname"));
            juser.put("fullname", user.get("Fname"));
            juser.put("password", user.get("Pword"));
        }catch (Exception exe){
        }
        String result = JSONParser.postStream(url, juser.toString());
        return result;
    }
}
