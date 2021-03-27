package com.blackseapps.heskodkontrol.ui.Result;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.TextView;

import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.sharedPreferences.SharedPreference;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;
import com.blackseapps.heskodkontrol.ui.Login.Login;
import com.blackseapps.heskodkontrol.utils.WebApp;

public class Result extends AppCompatActivity {

    private String tc, name, hescodetxt, status, date;
    private Handler handler = new Handler();
    MediaPlayer music;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        if (intent.getStringExtra("tc") != null) {
            tc = intent.getStringExtra("tc");
            name = intent.getStringExtra("name");
            hescodetxt = intent.getStringExtra("hescode");
            status = intent.getStringExtra("status");
            date = intent.getStringExtra("date");
        } else {
            tc = "Kod Geçersiz";
            name = "Kod Geçersiz";
            status = "Kod Geçersiz";
            hescodetxt = "Kod Geçersiz";
        }


        EditText username = findViewById(R.id.username);
        EditText tcid = findViewById(R.id.tcid);
        EditText hescode = findViewById(R.id.hescode);
        TextView statustxt = findViewById(R.id.status);
        View header = findViewById(R.id.headerview);
        View footer = findViewById(R.id.statusview);
        Button btn = findViewById(R.id.btn);

        username.setText(name);
        tcid.setText(tc);
        hescode.setText(hescodetxt);
        statustxt.setText(status);


        if (status.equals("Riskli Değil") == true) {
            music = MediaPlayer.create(this, R.raw.test);
            music.start();
            header.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
            footer.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
            btn.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
        } else if (status.equals("Kod Geçersiz") == true) {
            header.setBackgroundColor(getResources().getColor(R.color.resultnull));
            footer.setBackgroundColor(getResources().getColor(R.color.resultnull));
            btn.setBackgroundColor(getResources().getColor(R.color.resultnull));
        } else {
            music = MediaPlayer.create(this, R.raw.test);
            music.start();
            header.setBackgroundColor(getResources().getColor(R.color.resultWarning));
            footer.setBackgroundColor(getResources().getColor(R.color.resultWarning));
            btn.setBackgroundColor(getResources().getColor(R.color.resultWarning));
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Result.this, Barkod.class);
                intent.putExtra("devam", true);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    public void _goBack(View view) {
        Intent intent = new Intent(this, Barkod.class);
        startActivity(intent);
        finish();
    }

}
