package com.blackseapps.heskodkontrol.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackseapps.heskodkontrol.Modal.Login;

public class SharedPreference {

    SharedPreferences sharedpreferences;

    public SharedPreference(Context context) {
        sharedpreferences = (SharedPreferences) context.getSharedPreferences("HesCodeLogin", Context.MODE_PRIVATE);
    }


    public void setLoginInfo(String tc, String password,String username) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("tc", tc);
        editor.putString("password", password);
        editor.putString("username", username);

        editor.commit();
    }

    public Login getLoginInfo() {


        String tc = sharedpreferences.getString("tc", "");
        String password = sharedpreferences.getString("password", "");
        String username = sharedpreferences.getString("username", "");

        return new Login(tc, password,username);
    }
}



