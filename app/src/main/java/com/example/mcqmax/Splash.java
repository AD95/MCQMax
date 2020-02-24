package com.example.mcqmax;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mcqmax.utils.Common;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.fade_in_splash, R.anim.fade_out_splash);

        setContentView(R.layout.activity_splash);

        final Intent intent;
        intent = new Intent(Splash.this, MainScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        new Common(getApplicationContext());

        if (Common.getSharedPreferences(Common.cached).contains("true")) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                    } catch (Exception ignored) {
                    } finally {
                        startActivity(intent);
                    }
                }
            }.start();
        } else {
            Common.sync(Splash.this, intent);
        }
    }

}