package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.isaaco.campusdrive.R;

public class LoadingActivity extends AppCompatActivity {

    ImageView logo;
    TextView tv_campusdrive;
    TextView tv_campusdrive2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        logo = findViewById(R.id.introLogo);
        tv_campusdrive = findViewById(R.id.tv_campusDrive);
        tv_campusdrive2 = findViewById(R.id.tv_campusDrive2);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                logo,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        }, 3500);


        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                tv_campusdrive2.setVisibility(View.VISIBLE);
                tv_campusdrive2.startAnimation(animation2);
            }
        }, 600);
 Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lefttoright);
        Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            public void run() {
                tv_campusdrive.setVisibility(View.VISIBLE);
                tv_campusdrive.startAnimation(animation3);
            }
        }, 800);

        Window window = LoadingActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(LoadingActivity.this, R.color.white));

    }
}