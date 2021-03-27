package com.blackseapps.heskodkontrol.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Login.Login;
import com.blackseapps.heskodkontrol.ui.Result.Result;

public class WebApp {

    Context mContext;
    String[] data = new String[6];
    SharedPreference sharedPreference;
    Login webInterface;

    public WebApp(Context ctx) {
        this.mContext = ctx;

        webInterface = new Login();
    }


    @JavascriptInterface
    public void sendData(String tc, String name, String hescode, String status, String date) {

        sharedPreference = new SharedPreference(mContext);


        System.out.println("getdata:" + tc);

        data[0] = "https://heskodu.btbnext.com/api/heskodu/savelog";
        data[1] = tc;
        data[2] = name;
        data[3] = status;
        data[4] = date;
        data[5] = sharedPreference.getLoginInfo().getUsername();

        _postRequest(data);

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
        System.out.println("count:" + data);


        if (data.equals("0")) {
            Intent intent = new Intent(mContext, Result.class);
            mContext.startActivity(intent);
        }


    }

    @JavascriptInterface
    public void sendData(String data, String data2) {
        System.out.println("getdata_:" + data);
        Intent intent = new Intent(mContext, Result.class);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void login(String response) {
        System.out.println("response:" + response);
        Login.index = false;

        webInterface.LoginResponse(mContext, response.equals("1") ? false : true);
    }

    /**
     * SEND POST REQUEST
     * https://heskodu.btbnext.com/api/heskodu/savelog
     *
     * @param param
     */

    void _postRequest(String[] param) {

        final String[] params = param;

        String postUrl = params[0];
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest myReq = new StringRequest(Request.Method.POST,
                postUrl,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = "{\"tc\":\"" + params[1] + "\",\"adsoyad\":\"" + params[2] + "\"," +
                        "\"heskodu\":\"" + params[3] + "\",\"riskdurumu\":\"" + params[3] + "\"," +
                        "\"gecerlilikzamani\":\"" + params[4] + "\",\"kullaniciadi\":\"" + params[5] + "\"}";
                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        requestQueue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "Ski error connect - " + error);
            }
        };
    }


}


