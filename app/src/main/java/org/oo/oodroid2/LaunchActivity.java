package org.oo.oodroid2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LaunchActivity extends Activity{

    private final static int LAUNCH_ACTIVITY_DELAY = 1500;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this,OoDroidActivity.class));
                finish();
            }
        },LAUNCH_ACTIVITY_DELAY);
    }
}
