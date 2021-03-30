package com.blackseapps.heskodkontrol.ui.Barkod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackseapps.heskodkontrol.Modal.Login;
import com.blackseapps.heskodkontrol.Modal.Variables;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.LoadingWeb.LoadingWeb;

import com.blackseapps.heskodkontrol.ui.Result.Result;
import com.blackseapps.heskodkontrol.utils.KeyboardEvents;
import com.blackseapps.heskodkontrol.webview.WebApp;
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
    private boolean KEYCODE_ENTER = false;
    private int CameraClickCount = 0;

    View container, container2;
    String TC, PASSWORD, USERNAME, LAST;
    boolean CAMERA_FRONT = true;

    public Barkod() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null)
            cameraSource.stop();
    }

    void showKeyboard() {
        txtHesCode.post(new Runnable() {
            @Override
            public void run() {
                txtHesCode.requestFocus();
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtHesCode, InputMethodManager.SHOW_FORCED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod);

        container = findViewById(R.id.container);
        container2 = findViewById(R.id.container2);
        txtHesCode = findViewById(R.id.hescode);
        surfaceView = findViewById(R.id.surfaceView);
        imageView = findViewById(R.id.image);
        camera = findViewById(R.id.camera);
        barkod = findViewById(R.id.barkod);
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        mWebView = findViewById(R.id.webView);


        txtHesCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()}); //Txt Uppper
        txtHesCode.setOnKeyListener(keyListenerEnterCode); //Enter Code


        txtHesCode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // Toast.makeText(Barkod.this, "KEY 1: " + s.toString().substring(0, s.length() - 1), Toast.LENGTH_LONG).show();

                if (s.toString().indexOf(";") >= 0 || s.toString().indexOf("|") >= 0) {


                    boolean hes_1 = false, hes_2 = false;
                    if (s.toString().indexOf(";") >= 0)
                        if (s.toString().split(";").length > 1)
                            if (s.toString().split(";")[1].length() >= 10)
                                hes_1 = true;

                    if (s.toString().indexOf("|") >= 0)
                        if (s.toString().split("\\|").length > 1)
                        if (s.toString().split("\\|")[1].length() >= 10)
                            hes_2 = true;


                    if (hes_1 || hes_2) {
                        sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, "input");
                        if (s.toString().indexOf(";") >= 0)
                            sendhescode(s.toString().split(";")[1]);
                        else if (s.toString().indexOf("|") >= 0)
                            sendhescode(s.toString().split("\\|")[1]);
                    }
                }
            }
        });

        txtHesCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == KeyEvent.ACTION_DOWN ||
                        actionId == EditorInfo.IME_NULL ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == KeyEvent.KEYCODE_ENTER)) {


                    if (txtHesCode.getText().toString().equals("0000000000")) {
                        Intent intent = new Intent(Barkod.this, Result.class);
                        intent.putExtra("status", "Risklidir");
                        startActivity(intent);
                        txtHesCode.setText("");
                    } else if (txtHesCode.getText().toString().equals("1111111111")) {
                        Intent intent = new Intent(Barkod.this, Result.class);
                        intent.putExtra("status", "Risksizdir");
                        startActivity(intent);
                        txtHesCode.setText("");
                    } else if (txtHesCode.getText().toString().indexOf(';') < 0) {
                        sendhescode(txtHesCode.getText().toString());
                    } else {
                        sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, "input");
                        sendhescode(txtHesCode.getText().toString().split(";")[1]);
                    }
                }
                return false;
            }
        });

        sharedInit();
        CameraOrInput();
        SingControl();

        showKeyboard();

        handler.postDelayed(new Runnable() {
            public void run() {
                showKeyboard();
            }
        }, 3500);

    }

    void sharedInit() {
        sharedPreference = new SharedPreference(Barkod.this);
        TC = sharedPreference.getLoginInfo().getTc();
        PASSWORD = sharedPreference.getLoginInfo().getPassword();
        USERNAME = sharedPreference.getLoginInfo().getUsername();
        LAST = sharedPreference.getLoginInfo().getLast();

        System.out.println("LOG Last:" + LAST);

        if (LAST.indexOf("_") > 0)
            if (LAST.split("_")[1].equals("front"))
                CAMERA_FRONT = true;
            else
                CAMERA_FRONT = false;

    }

    private void CameraOrInput() {


        if (LAST.split("_")[0].equals("camera")) {
            CameraOpen(CAMERA_FRONT);
        } else if (LAST.equals("input"))
            BarkodOpen();
    }

    public void _clickCamera(View view) {

        BarkodOpen();
        LAST = sharedPreference.getLoginInfo().getLast();

        if (!LAST.equals("input")) {
            if (CAMERA_FRONT) {
                CameraOpen(false);
                CAMERA_FRONT = false;
                sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, "camera_back");

            } else {
                CAMERA_FRONT = true;
                sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, "camera_front");

                CameraOpen(true);
            }
        } else {
            CameraOpen(CAMERA_FRONT);
            sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, CAMERA_FRONT ? "camera_front" : "camera_back");
        }

        showKeyboard();


    }

    private void CameraOpen(boolean type) {
        initialiseDetectorsAndSources(type);
        imageView.setVisibility(View.GONE);
        surfaceView.setVisibility(View.VISIBLE);
        toggle = true;
        camera.setImageResource(R.drawable.kamera_buyuk);
        barkod.setImageResource(R.drawable.barkod_kucuk);
        camera.getLayoutParams().height = intToDp(84);
        camera.getLayoutParams().width = intToDp(84);
        barkod.getLayoutParams().height = intToDp(64);
        barkod.getLayoutParams().width = intToDp(64);
    }

    int intToDp(int px) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics()));
    }

    private void BarkodOpen() {
        if (!toggle && toggle2) return;

        surfaceView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        if (cameraSource != null)
            cameraSource.stop();

        toggle = false;
        toggle2 = false;


        barkod.setImageResource(R.drawable.barkod_buyuk);
        camera.setImageResource(R.drawable.kamera_kucuk);
        barkod.getLayoutParams().height = intToDp(84);
        barkod.getLayoutParams().width = intToDp(84);
        camera.getLayoutParams().height = intToDp(64);
        camera.getLayoutParams().width = intToDp(64);

        showKeyboard();

    }

    public void _clickBarkod(View view) {
        CameraClickCount = 0;
        sharedPreference.setLoginInfo(TC, PASSWORD, USERNAME, "input");
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

    public void sendhescode(String hescode) {
        Intent intent = new Intent(this, LoadingWeb.class);
        intent.putExtra("hesCode", hescode);
        startActivity(intent);
    }

    public void _signOut(View view) {
        sharedPreference.setLoginInfo("", "", "", "");
        Intent intent = new Intent(Barkod.this, Login.class);
        startActivity(intent);
        finish();
    }

    void SingControl() {
        if (Variables.REFRESH)
            webview(TC, PASSWORD, "R7G6-9154-15");
        else {
            handler.postDelayed(new Runnable() {
                public void run() {
                    webview(TC, PASSWORD, "R7G6-9154-15");
                    handler.postDelayed(this, ten_munite);
                }
            }, ten_munite);
        }
        Variables.REFRESH = false;
    }

    public void _iptal(View view) {

        Variables.LOADING_STATUS = false;
    }


    public View.OnKeyListener keyListenerEnterCode = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER)
                return true;
            return false;
        }
    };

    /**
     * WEB CONTROL AND BARKOD SCAN
     */

    void initConfigWebView() {
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

    void webview(String tc, String password, String hesCode) {
        String url = Variables.URL_EDEVLET_SIGN;

        initConfigWebView();

        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(new WebApp(Barkod.this), "compact");// JS INTERFACE METOD

        final String giris = Variables.E_DEVLET_SIGN_JS_CODE(tc, password);
        final String sorgula = Variables.E_DEVLET_QUERY_JS_CODE(hesCode);
        final String sonuc = "";


        mWebView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(final WebView view, final String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(giris, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if (index) {
                                index = false;
                                mWebView.loadUrl(Variables.URL_QUERY);
                            }
                        }
                    });

                    if (url.equals(Variables.URL_QUERY)) {
                        view.evaluateJavascript(sorgula, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                mWebView.evaluateJavascript("jquery:$('.loginLink')[0].click();", null);
                                view.loadUrl("javascript:window.compact.sendData(document.getElementsByClassName('compact').innerText);");
                            }
                        });
                    }

                    if (url.equals(Variables.URL_QUERY_RESPONSE)) {
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

    void webView(String hesCode) {

        String url = Variables.URL_QUERY;
        initConfigWebView();
        mWebView.loadUrl(url);

        final String sorgula = Variables.E_DEVLET_QUERY_JS_CODE(hesCode);
        final String sonuc = "";

        mWebView.addJavascriptInterface(new WebApp(Barkod.this), "compact");

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(final WebView view, String url) {
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(sorgula, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        BarkodOpen();
        CameraOpen(CAMERA_FRONT);


        System.out.println("CAllback");
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
                            System.out.println("LOG BARKOD:" + intentData);

                            if (intentData.indexOf("|") >= 0) {
                                txtBarcodeValue.setText(intentData.split("\\|")[1]);

                                Variables.TYPE = "camera";

                                // loading.setVisibility(View.VISIBLE);

                                //webView(intentData.split("\\|")[1]);

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
