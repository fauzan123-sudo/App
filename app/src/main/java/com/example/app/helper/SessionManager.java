package com.example.app.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.app.Dashboard;
import com.example.app.Login;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME    = "LOGIN";
    private static final String LOGIN        = "IS_LOGIN";
    public static final String  NAME         = "NAMA";
    public static final String  EMAIL        = "USERNAME";
    public static final String  ID           = "ID";
    public static final String  IMAGE        = "IMAGE";
    public static final String  JABATAN        = "JABATAN";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }

    public void createSession(String name, String email, String id, String image, String jabatan){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(IMAGE, image);
        editor.putString(JABATAN, jabatan);
        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){

        if (!this.isLoggin()){
            Intent i = new Intent(context, Login.class);
            context.startActivity(i);
            ((Dashboard) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(IMAGE, sharedPreferences.getString(IMAGE, null));
        user.put(JABATAN, sharedPreferences.getString(JABATAN, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, Login.class);
        context.startActivity(i);
        ((Dashboard) context).finish();

    }

}
