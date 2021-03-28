package com.blackseapps.heskodkontrol.ui.LoadingWeb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.blackseapps.heskodkontrol.Modal.Variables;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;
import com.blackseapps.heskodkontrol.webview.WebApp;

public class LoadingWeb extends AppCompatActivity {

    private WebView mWebView;

    public LoadingWeb() {
        Variables.LOADING_STATUS = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_web);

        mWebView = findViewById(R.id.webView);

        Intent intent = getIntent();
        if (intent.getStringExtra("hesCode") == null)
            return;

        String hesCode = intent.getStringExtra("hesCode");
        webView(hesCode);
    }

    void webView(String hesCode) {

        String url = Variables.URL_QUERY;

        /**
         *
         * Config WebView
         * */

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

        mWebView.loadUrl(url);

        final String sorgula = Variables.E_DEVLET_QUERY_JS_CODE(hesCode);
        final String sonuc = "";


        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(final WebView view, String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(sorgula, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            view.addJavascriptInterface(new WebApp(LoadingWeb.this), "compact");
                        }
                    });

                    if (url.equals(Variables.URL_QUERY_RESPONSE)) {
                        view.evaluateJavascript(sonuc, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                view.loadUrl(Variables.E_DEVLET_QUERY_RESPONSE_JS_CODE());
                                view.loadUrl(Variables.E_DEVLET_QUERY_NOT_RESPONSE_JS_CODE());
                            }
                        });
                    }
                }
            }
        });
    }

    public void _iptal(View view) {
        Variables.LOADING_STATUS = false;
        Intent intent = new Intent(this, Barkod.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
