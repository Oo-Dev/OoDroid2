package org.oo.oodroid2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class LaunchActivity extends Activity{

    private final static int LAUNCH_ACTIVITY_DELAY = 500;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        View logo = findViewById(R.id.logo);
        Animation anim = new ScaleAnimation(0.0f, 10.0f, 0.0f, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1510);
        //logo.setAnimation(anim);
        //anim.startNow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this,OoDroidActivity.class));
                overridePendingTransition(R.anim.startanim, R.anim.exitanim);
                finish();
            }
        },LAUNCH_ACTIVITY_DELAY);
    }
}
