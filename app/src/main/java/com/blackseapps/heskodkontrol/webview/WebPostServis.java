package com.blackseapps.heskodkontrol.webview;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blackseapps.heskodkontrol.Modal.Variables;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;

public class WebPostServis {

    private Context context;
    private SharedPreference sharedPreference;

    public WebPostServis(Context context) {
        this.context = context;
        sharedPreference = new SharedPreference(context);
    }

    public void postLoginInfo(final String lisans, final String tc, final String password) {

        String postUrl = Variables.URL_TOKEN_SERVICE;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest myReq = new StringRequest(Request.Method.POST,
                postUrl,
                loginInfoListener(tc, password),
                errorListener()) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = "{\"lisans\":\"" + lisans + "\",\"mobileid\":\"" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "\"}";
                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        requestQueue.add(myReq);
    }

    void postSaveLog(String[] param) {

        final String[] params = param;

        String postUrl = params[0];
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest myReq = new StringRequest(Request.Method.POST,
                postUrl,
                saveLogListener(),
                errorListener()) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = "{\"tc\":\"" + params[1] + "\",\"adsoyad\":\"" + params[2] + "\"," +
                        "\"heskodu\":\"" + params[6] + "\",\"riskdurumu\":\"" + params[3] + "\"," +
                        "\"gecerlilikzamani\":\"" + params[4] + "\",\"kullaniciadi\":\"" + params[5] + "\"}";

                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        requestQueue.add(myReq);
    }

    private Response.Listener<String> saveLogListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("LOG: postSaveLog Success");
            }
        };
    }

    private Response.Listener<String> loginInfoListener(final String tc, final String password) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("LOG: "+response);

                if (!response.trim().equals("\"hata\"")) {

                    System.out.println("LOG: postLoginInfo Success");

                    String username = response.replace("\"", "");

                    sharedPreference.setLoginInfo(tc, password, username, "input");

                    Intent intent = new Intent(context, Barkod.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Girilen Lisans Anahtarı Geçerli değildir.", Toast.LENGTH_LONG).show();
                }


            }
        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "Ski error connect - " + error);
            }
        };
    }
}
