package com.blackseapps.heskodkontrol.ui.Result;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.blackseapps.heskodkontrol.R;
import com.blackseapps.heskodkontrol.ui.Barkod.Barkod;


public class Result extends AppCompatActivity {

    private String tc, name, hescodetxt, status, date;
    private Handler handler = new Handler();
    private MediaPlayer music;
    private EditText username, tcid, hescode;
    private TextView statustxt;
    private View header, footer;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        username = findViewById(R.id.username);
        tcid = findViewById(R.id.tcid);
        hescode = findViewById(R.id.hescode);
        statustxt = findViewById(R.id.status);
        header = findViewById(R.id.headerview);
        footer = findViewById(R.id.statusview);
        btn = findViewById(R.id.btn);

        StatusControl();
        Result();
        GoBackBarkod();
    }

    void GoBackBarkod() {
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Result.this, Barkod.class);
                intent.putExtra("devam", true);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    void StatusControl() {
        Intent intent = getIntent();

        if (intent.getStringExtra("tc") != null) {
            tc = intent.getStringExtra("tc");
            name = intent.getStringExtra("name");
            hescodetxt = intent.getStringExtra("hescode");
            status = intent.getStringExtra("status");
            date = intent.getStringExtra("date");
        } else if (intent.getStringExtra("status").equals("Risklidir")) {
            tc = "0000******";
            name = "0000****";
            status = "Risklidir";
            hescodetxt = "0000000000";
        } else if (intent.getStringExtra("status").equals("Risksizdir")) {
            tc = "1111******";
            name = "1111****";
            status = "Riskli Değil";
            hescodetxt = "1111111111";
        } else {
            tc = "Kod Geçersiz";
            name = "Kod Geçersiz";
            status = "Kod Geçersiz";
            hescodetxt = "Kod Geçersiz";
        }
    }

    void Result() {

        username.setText(name);
        tcid.setText(tc);
        hescode.setText(hescodetxt);
        statustxt.setText(status);

        if (status.equals("Riskli Değil") == true) {
            music = MediaPlayer.create(this, R.raw.risksizdir);
            music.start();
            header.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
            footer.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
            btn.setBackgroundColor(getResources().getColor(R.color.resultSuccess));
        } else if (status.equals("Kod Geçersiz") == true) {
            header.setBackgroundColor(getResources().getColor(R.color.resultnull));
            footer.setBackgroundColor(getResources().getColor(R.color.resultnull));
            btn.setBackgroundColor(getResources().getColor(R.color.resultnull));
        } else {
            music = MediaPlayer.create(this, R.raw.risklidir);
            music.start();
            header.setBackgroundColor(getResources().getColor(R.color.resultWarning));
            footer.setBackgroundColor(getResources().getColor(R.color.resultWarning));
            btn.setBackgroundColor(getResources().getColor(R.color.resultWarning));
        }
    }

    public void _goBack(View view) {
        Intent intent = new Intent(this, Barkod.class);
        startActivity(intent);
        finish();
    }

}
