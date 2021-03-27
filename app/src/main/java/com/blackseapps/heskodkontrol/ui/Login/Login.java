package com.blackseapps.heskodkontrol.ui.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;
import com.blackseapps.heskodkontrol.ui.LoadingWeb.LoadingWeb;
import com.blackseapps.heskodkontrol.utils.WebApp;
import com.blackseapps.heskodkontrol.utils.WebInterface;

public class Login extends AppCompatActivity implements WebInterface {

    EditText tc, password, token;
    SharedPreference sharedPreference;
    String[] data;
    String _response;
    WebView mWebView;
    public static boolean index;
    private Handler handler = new Handler();


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        index = true;

        View container = findViewById(R.id.container);
        View container2 = findViewById(R.id.container2);
        tc = findViewById(R.id.tcid);
        password = findViewById(R.id.password);
        token = findViewById(R.id.token);
        Button send = findViewById(R.id.btnsend);
        mWebView = findViewById(R.id.webView);
        sharedPreference = new SharedPreference(Login.this);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(Login.this);
            }
        });

        container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(Login.this);
            }
        });

        data = new String[5];

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                data[0] = tc.getText().toString().trim();
                data[1] = password.getText().toString().trim();

                index = true;

                webviewLogin(data[0], data[1], data);

                // _postRequest(data);


                // sharedPreference.setLoginInfo(tc.getText().toString(), password.getText().toString());

                //  Intent intent = new Intent(Login.this, Barkod.class);
                //startActivity(intent);

            }
        });


        if (!sharedPreference.getLoginInfo().getTc().equals("")) {
            Intent intent = new Intent(this, Barkod.class);
            startActivity(intent);
        }

        initWeb();
    }


    void initWeb() {

        WebSettings settings = mWebView.getSettings();
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                mWebView.setWebContentsDebuggingEnabled(true);
            }
        }

    }

    void _postRequest(String[] param) {

        final String[] params = param;

        String postUrl = params[0];
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST,
                postUrl,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = "{\"tc\":\"" + params[1] + "\",\"sifre\":\"" + params[2] + "\"," +
                        "\"lisans\":\"" + params[3] + "\",\"mobileid\":\"" + params[4] + "\"}";
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
                _response = response;

                if (!_response.trim().equals("\"hata\"")) {
                    System.out.println(response);

                    sharedPreference.setLoginInfo(tc.getText().toString(), password.getText().toString(), response.replace("\"", ""));

                    Intent intent = new Intent(Login.this, Barkod.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Girilen Lisans Anahtarı Geçerli değildir.", Toast.LENGTH_LONG).show();
                }


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

    void webviewLogin(String tc, String password, final String[] data) {

        String url = "https://giris.turkiye.gov.tr/Giris/gir";

        mWebView.loadUrl(url);


        final String giris = "javascript:document.getElementById('tridField').value='" + tc + "';" +
                "javascript:document.getElementById('egpField').value='" + password + "';" +
                "javascript:document.getElementsByName('submitButton')[0].click();" +
                "javascript:document.getElementsByName('submitButton')[0].click();";

        mWebView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(final WebView view, final String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(giris, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                            if (index == false) return;

                            tsd();
                        }
                    });
                }
            }
        });


    }

    void tsd() {
        handler.postDelayed(new Runnable() {
            public void run() {
                mWebView.addJavascriptInterface(new WebApp(Login.this), "test");
                mWebView.loadUrl("javascript:window.test.login(" +
                        "document.getElementsByName(\"submitButton\").length);");


                mWebView.stopLoading();
            }
        }, 2000);
    }

    @Override
    public void LoginResponse(Context context, boolean respoonseStatus) {

        if (!respoonseStatus) {
            Toast.makeText(context, "Lütfen TC Kimlik ya da Şifrenizi Kontrol Ediniz.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Başarılı", Toast.LENGTH_LONG).show();
             _postRequest(data);
        }


    }
}

