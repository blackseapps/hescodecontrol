package com.blackseapps.heskodkontrol.ui.Barkod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackseapps.heskodkontrol.Modal.Type;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.LoadingWeb.LoadingWeb;
import com.blackseapps.heskodkontrol.ui.Login.Login;
import com.blackseapps.heskodkontrol.utils.WebApp;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Barkod extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String intentData = "";
    private TextView txtBarcodeValue;
    private EditText txtHesCode;
    private SharedPreference sharedPreference;
    private WebView mWebView;
    private Handler handler = new Handler();
    private int ten_munite = 1000 * 60 * 10;
    private ImageView imageView, barkod, camera;
    private boolean toggle = false;
    private boolean toggle2 = false;
    private boolean index = true;
    private boolean index2 = true;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod);

        View container = findViewById(R.id.container);
        View container2 = findViewById(R.id.container2);
        txtHesCode = findViewById(R.id.hescode);
        surfaceView = findViewById(R.id.surfaceView);
        imageView = findViewById(R.id.image);
        camera = findViewById(R.id.camera);
        barkod = findViewById(R.id.barkod);
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);

        sharedPreference = new SharedPreference(Barkod.this);


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(Barkod.this);
            }
        });

        container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(Barkod.this);
            }
        });


        txtHesCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() >= 10) {
                    Type.TYPE = "input";
                    hideKeyboard(Barkod.this);
                    _sendhescode(txtHesCode.getText().toString());
                }
            }
        });

        if (Type.TYPE == "camera") {
            Intent intent = getIntent();
            if (intent.getBooleanExtra("devam", false) == true)
                CameraOpen(Type.FRONT_CAMERA);
        } else if (Type.TYPE == "input")
            BarkodOpen();



        _SingControl();
    }


    public void _clickCamera(View view) {

        if (Type.FRONT_CAMERA)
            Type.FRONT_CAMERA = false;
        else
            Type.FRONT_CAMERA = true;

        BarkodOpen();

        CameraOpen(Type.FRONT_CAMERA);


    }

    void CameraOpen(boolean type) {
        initialiseDetectorsAndSources(type);
        imageView.setVisibility(View.GONE);
        surfaceView.setVisibility(View.VISIBLE);
        toggle = true;
        camera.setBackgroundResource(R.color.resultSuccess);
        barkod.setBackgroundResource(R.color.transparent);
    }

    void BarkodOpen() {
        if (!toggle && toggle2) return;
        hideKeyboard(this);
        surfaceView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        if (cameraSource != null)
            cameraSource.stop();

        toggle = false;
        toggle2 = false;

        txtHesCode.post(new Runnable() {
            @Override
            public void run() {
                txtHesCode.requestFocus();
            }
        });

        barkod.setBackgroundResource(R.color.resultSuccess);
        camera.setBackgroundResource(R.color.transparent);
    }

    public void _clickBarkod(View view) {
        BarkodOpen();
    }

    public void _openKeyboard(View view) {
        txtHesCode.post(new Runnable() {
            @Override
            public void run() {
                txtHesCode.requestFocus();
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null)
            cameraSource.stop();
    }


    public void _sendhescode(String hescode) {
        Intent intent = new Intent(this, LoadingWeb.class);
        intent.putExtra("hesCode", hescode);
        startActivity(intent);
    }

    public void _signOut(View view) {
        sharedPreference.setLoginInfo("", "", "");
        Intent intent = new Intent(Barkod.this, Login.class);
        startActivity(intent);
        finish();
    }


    void _SingControl() {
        handler.postDelayed(new Runnable() {
            public void run() {
                webview(sharedPreference.getLoginInfo().getTc(),
                        sharedPreference.getLoginInfo().getPassword(),
                        "R7G6-9154-15");

                handler.postDelayed(this, ten_munite);
            }
        }, 100);
    }


    /**
     * WEB CONTROL AND BARKOD SCAN
     */

    void webview(String tc, String password, String hesCode) {
        String url = "https://giris.turkiye.gov.tr/Giris/gir";
        mWebView = (WebView) findViewById(R.id.webView);

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


        final String giris = "javascript:document.getElementById('tridField').value='" + tc + "';" +
                "javascript:document.getElementById('egpField').value='" + password + "';" +
                "javascript:document.getElementsByName('submitButton')[0].click();" +
                "javascript:document.getElementsByName('submitButton')[0].click();" +
                "jquery:$('.loginLink')[0].click();";


        final String sorgula =
                "jquery:$('.loginLink')[0].click();" +
                        "javascript:document.getElementById('hes_kodu').value='" + hesCode + "';" +
                        "javascript:document.forms['mainForm'].submit();";


        final String sonuc = "";

        mWebView.addJavascriptInterface(new WebApp(Barkod.this), "compact");

        mWebView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(final WebView view, final String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(giris, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if (index) {
                                index = false;
                                mWebView.loadUrl("https://www.turkiye.gov.tr/saglik-bakanligi-hes-kodu-sorgulama");

                            }


                        }
                    });


                    if (url.equals("https://www.turkiye.gov.tr/saglik-bakanligi-hes-kodu-sorgulama")) {
                        view.evaluateJavascript(sorgula, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                                for (int i = 0; i < 10; i++)
                                    mWebView.evaluateJavascript("jquery:$('.loginLink')[0].click();", null);

                                view.loadUrl("javascript:window.compact.sendData(document.getElementsByClassName('compact').innerText);");

                            }
                        });

                    }

                    if (url.equals("https://www.turkiye.gov.tr/saglik-bakanligi-hes-kodu-sorgulama?sonuc=Goster")) {

                        view.evaluateJavascript(sonuc, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {


                            }
                        });
                    }
                }
            }
        });
    }

    private void initialiseDetectorsAndSources(Boolean type) {


        barcodeDetector = new BarcodeDetector.Builder(Barkod.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(Barkod.this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setFacing(type ? 1 : 0)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(Barkod.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(Barkod.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            if (!index2) return;
                            index2 = false;

                            intentData = barcodes.valueAt(0).displayValue;
                            System.out.println(intentData);

                            if (intentData.indexOf("|") >= 0) {
                                txtBarcodeValue.setText(intentData.split("\\|")[1]);

                                Type.TYPE = "camera";
                                Intent intent = new Intent(Barkod.this, LoadingWeb.class);
                                intent.putExtra("hesCode", (intentData.split("\\|")[1]));
                                startActivity(intent);
                            }
                        }
                    });

                }
            }
        });

    }

}
