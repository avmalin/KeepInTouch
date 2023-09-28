package com.example.keepintouch.UI;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.keepintouch.R;

public class inAppTutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_in_app_tutorial);
        TextView tv_welcome = findViewById(R.id.tv_welcome);
        TextView tv_welcome3 = findViewById(R.id.tv_welcome3);
        Button button = findViewById(R.id.button);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(tv_welcome);
        YoYo.with(Techniques.RubberBand).duration(1000).repeat(2).playOn(tv_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn).duration(2000).playOn(button);
            }
        },2000);
        int[] colors = { Color.green(100),Color.GREEN};
        ValueAnimator colorAnimator = new ValueAnimator();
        colorAnimator.setIntValues(colors);
        colorAnimator.setDuration(3000);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setRepeatCount(0);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                int animatedColor = (int) animation.getAnimatedValue();
                tv_welcome3.setTextColor(animatedColor);
            }
        });
        colorAnimator.start();



    }
}