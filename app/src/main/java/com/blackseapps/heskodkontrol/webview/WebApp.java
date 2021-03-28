package com.blackseapps.heskodkontrol.webview;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;


import com.blackseapps.heskodkontrol.Modal.Variables;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Login.Login;
import com.blackseapps.heskodkontrol.ui.Result.Result;

public class WebApp {

    Context mContext;
    String[] data = new String[7];
    SharedPreference sharedPreference;
    Login webInterface;
    boolean index = true;

    public WebApp(Context ctx) {
        this.mContext = ctx;
        webInterface = new Login();
    }


    @JavascriptInterface
    public void sendData(String tc, String name, String hescode, String status, String date) {

        if (!Variables.LOADING_STATUS) return;
        sharedPreference = new SharedPreference(mContext);

        Variables.LOADING_STATUS=false;

        System.out.println("LOG STATUS:" + status);

        data[0] = Variables.URL_TOKEN_SAVE_LOG;
        data[1] = tc;
        data[2] = name;
        data[3] = status;
        data[4] = date;
        data[5] = sharedPreference.getLoginInfo().getUsername();
        data[6] = hescode;

        new WebPostServis(mContext).postSaveLog(data);

        Intent intent = new Intent(mContext, Result.class);
        intent.putExtra("tc", tc);
        intent.putExtra("name", name);
        intent.putExtra("hescode", hescode);
        intent.putExtra("status", status);
        intent.putExtra("date", date);
        mContext.startActivity(intent);
    }


    @JavascriptInterface
    public void sendData(String data) {
        System.out.println("LOG: YÜKLENDİ");

        System.out.println("LOG COUNT:" + data);
        if (data.equals("0") && index) {
            index = false;
            Intent intent = new Intent(mContext, Result.class);
            mContext.startActivity(intent);
        }
    }

    @JavascriptInterface
    public void sendData(String data, String data2) {
        System.out.println("LOG GET DATA:" + data);
        Intent intent = new Intent(mContext, Result.class);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void login(String response, String lisans, String tc, String password) {
        System.out.println("LOG RESPONSE:" + response);
        Login.index = false;

        webInterface.LoginResponse(mContext, response.equals("1") ? false : true, lisans, tc, password);
    }
}


