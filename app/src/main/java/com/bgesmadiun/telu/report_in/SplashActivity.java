package com.bgesmadiun.telu.report_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    // Wait for 3 Sec
                    sleep(3000);

                    // Moving to MenuActivity
                    Intent intent = new Intent (getApplicationContext(),MenuActivity.class);
                    startActivity(intent);
                    finish();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}