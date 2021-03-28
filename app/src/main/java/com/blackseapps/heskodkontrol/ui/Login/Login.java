package com.blackseapps.heskodkontrol.ui.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackseapps.heskodkontrol.Modal.Variables;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;
import com.blackseapps.heskodkontrol.ui.Result.Result;
import com.blackseapps.heskodkontrol.utils.KeyboardEvents;
import com.blackseapps.heskodkontrol.webview.WebApp;
import com.blackseapps.heskodkontrol.webview.WebInterface;
import com.blackseapps.heskodkontrol.webview.WebPostServis;

public class Login extends AppCompatActivity implements WebInterface {

    public static boolean index;

    private EditText tc, password, token;
    private SharedPreference sharedPreference;
    private String[] data;
    private WebView mWebView;
    private ProgressBar progressBar;
    private Button send;
    Handler handler = new Handler();
    ;

    public Login() {
        data = new String[5];
        index = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View container = findViewById(R.id.container);
        View container2 = findViewById(R.id.container2);
        tc = findViewById(R.id.tcid);
        password = findViewById(R.id.password);
        token = findViewById(R.id.token);
        send = findViewById(R.id.btnsend);
        mWebView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progress);

        sharedPreference = new SharedPreference(this);


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardEvents.hide(Login.this);
            }
        });
        container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardEvents.hide(Login.this);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                send.setEnabled(false);
                tc.setEnabled(false);
                password.setEnabled(false);
                token.setEnabled(false);

                index = true;

                data[0] = Variables.URL_TOKEN_SERVICE;
                data[1] = tc.getText().toString().trim();
                data[2] = password.getText().toString().trim();
                data[3] = token.getText().toString().trim();

                webviewLogin(data);
            }
        });

        singControl();
        initWeb();
    }

    private void singControl() {
        if (!sharedPreference.getLoginInfo().getTc().equals("")) {
            Intent intent = new Intent(this, Barkod.class);
            startActivity(intent);
        }
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


    void webviewLogin(final String[] data) {

        String url = Variables.URL_EDEVLET_SIGN;
        mWebView.loadUrl(url);

        final String giris = Variables.E_DEVLET_SIGN_JS_CODE(data[1], data[2]);

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(final WebView view, final String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(giris, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                            if (index == false) return;

                            mWebView.addJavascriptInterface(new WebApp(Login.this), "test");
                            mWebView.loadUrl("javascript:window.test.login(" +
                                    "document.getElementsByName(\"submitButton\").length,'" + data[3] + "'," +
                                    "'" + data[1] + "'," +
                                    "'" + data[2] + "');");

                        }
                    });
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(final WebView view, final String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(giris, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                            if (index == false) return;

                            if (url.equals("https://www.turkiye.gov.tr/")) {
                                LoadingEnd();
                                new WebPostServis(Login.this).postLoginInfo(data[3], data[1], data[2]);
                            }

                            mWebView.addJavascriptInterface(new WebApp(Login.this), "test");
                            mWebView.loadUrl("javascript:window.test.login(" +
                                    "document.getElementsByName(\"submitButton\").length,'" + data[3] + "'," +
                                    "'" + data[1] + "'," +
                                    "'" + data[2] + "');");

                            LoadingPost();
                        }
                    });
                }
            }
        });


    }

    void LoadingEnd() {
        tc.setEnabled(true);
        password.setEnabled(true);
        token.setEnabled(true);
        send.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    void LoadingPost() {
        handler.postDelayed(new Runnable() {
            public void run() {
                LoadingEnd();
            }
        }, 4000);
    }

    @Override
    public void LoginResponse(Context context, boolean respoonseStatus, String lisans, String tc, String password) {
        if (!respoonseStatus) {
            Toast.makeText(context, "Lütfen TC Kimlik ya da Şifrenizi Kontrol Ediniz.", Toast.LENGTH_LONG).show();
        } else {

            new WebPostServis(context).postLoginInfo(lisans, tc, password);
        }
    }
}

