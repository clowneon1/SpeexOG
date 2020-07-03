package com.clowneon1.speex.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.clowneon1.speex.R;

public class StartActivity extends AppCompatActivity {
    private static int TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        },TIME_OUT);
    }
}