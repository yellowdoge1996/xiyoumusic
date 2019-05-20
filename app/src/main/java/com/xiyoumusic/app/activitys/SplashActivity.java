package com.xiyoumusic.app.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.xiyoumusic.app.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

public class SplashActivity extends BaseActivity {
    ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        mImageView = findViewById(R.id.splash);

        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                    int centerX = mImageView.getMeasuredWidth() / 2;//获取组件的宽的一半
                    int centerY = mImageView.getMeasuredHeight();//获取组件的高
                    Float startRadius = ((Double)Math.sqrt(mImageView.getMeasuredWidth()*mImageView.getMeasuredWidth()+
                            mImageView.getMeasuredHeight()*mImageView.getMeasuredHeight())).floatValue();
                    Animator animator = ViewAnimationUtils.createCircularReveal(mImageView, centerX, centerY, startRadius, 0);
                    animator.setDuration(2000);
                    animator.setInterpolator(new LinearOutSlowInInterpolator());//out到in
                    animator.start();
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mImageView.setVisibility(View.GONE);
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade,0);
                            finish();
                        }
                    });
            }
        },1000);

    }
}
