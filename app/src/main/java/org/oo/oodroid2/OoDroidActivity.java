package org.oo.oodroid2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.io.IOException;

import oo.org.server.SDPDistributor;


public class OoDroidActivity extends Activity implements Session.Callback,SurfaceHolder.Callback{


    private final static String TAG = "OoDroidActivity";

    SurfaceView surfaceView;
    Session mSession;
    SDPDistributor distributorServer;
    Button buttonPlay, buttonFlash, buttonSettings;
    String destinationIP;
    int w,h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oo_droid);
        Log.v(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        findViewById(R.id.panel).getBackground().setAlpha(50);
        buttonPlay = (Button) findViewById(R.id.button);
        buttonFlash = (Button) findViewById(R.id.button2);
        buttonSettings = (Button) findViewById(R.id.button3);



        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSession.isStreaming()){
                    mSession.configure();
                    mSession.start();
                } else {
                    mSession.stop();
                }
            }
        });

        buttonFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.toggleFlash();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OoDroidActivity.this, SettingsActivity.class);
                startActivity(i);
                //finish();
            }
        });

    }



    @Override
    public void onBitrateUpdate(long bitrate) {

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        Log.v(TAG, "onSessionError");
    }

    @Override
    public void onPreviewStarted() {
        Log.v(TAG, "onPreviewStarted");
    }

    @Override
    public void onSessionConfigured() {
    }

    @Override
    public void onSessionStarted() {
        Log.v(TAG, "onSessionStarted");
        Toast.makeText(OoDroidActivity.this, "Streaming start", Toast.LENGTH_SHORT).show();
        distributorServer = new SDPDistributor(mSession.getSessionDescription());
        Log.v(TAG, mSession.getSessionDescription());
        try {
            distributorServer.startServer();// Start or restart
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionStopped() {
        Log.v(TAG, "onSessionStopped");
        Toast.makeText(OoDroidActivity.this, "Streaming has stopped", Toast.LENGTH_SHORT).show();
        distributorServer.stopServer();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(OoDroidActivity.this);

        destinationIP = sp.getString("destination_IP", "239.1.1.1");
        String audio_encoding = sp.getString(getString(R.string.key_audio_encoding), "None");int audio_encoding_value;
        int audio_bitrate = Integer.parseInt(sp.getString(getString(R.string.key_audio_bitrate), "64"));
        int audio_sampling_rate = Integer.parseInt(sp.getString(getString(R.string.key_audio_sampling_rate), "48"));
        String video_encoding = sp.getString(getString(R.string.key_video_encoding), "H.263"); int video_encoding_value;
        String video_resolution = sp.getString(getString(R.string.key_video_resolution), "320x240 (4:3)");

        int video_frame_rate = Integer.parseInt(sp.getString(getString(R.string.key_video_frame_rate), "24"));
        int video_bitrate = Integer.parseInt(sp.getString(getString(R.string.key_video_bitrate), "500"));

        if(audio_encoding.equals("AAC"))
            audio_encoding_value = SessionBuilder.AUDIO_AAC;
        else if(audio_encoding.equals("AMRNB"))
            audio_encoding_value = SessionBuilder.AUDIO_AMRNB;
        else
            audio_encoding_value = SessionBuilder.AUDIO_NONE;

        if(video_encoding.equals("H.263"))
            video_encoding_value = SessionBuilder.VIDEO_H263;
        else if(video_encoding.equals("H.264"))
            video_encoding_value = SessionBuilder.VIDEO_H264;
        else
            video_encoding_value = SessionBuilder.VIDEO_NONE;

        if(video_resolution.startsWith("1920x1080")){
            w = 1920; h = 1080;
        } else if(video_resolution.startsWith("1280x720")){
            w = 1280; h = 720;
        } else if(video_resolution.startsWith("640x480")){
            w = 640; h = 480;
        } else {
            w = 320; h = 240;
        }

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(surfaceView)
                .setContext(getApplicationContext())
                .setAudioEncoder(audio_encoding_value)
                .setAudioQuality(new AudioQuality(audio_bitrate * 1000, audio_sampling_rate * 1000))
                .setVideoEncoder(video_encoding_value)
                .setVideoQuality(new VideoQuality(w,h,video_frame_rate,video_bitrate * 1000))
                .setTimeToLive(1)
                .build();
        mSession.setPreviewOrientation(0);
        mSession.setDestination(destinationIP);
        mSession.configure();

        surfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        if(mSession.isStreaming())
            mSession.stop();
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        Log.v(TAG, "onPause");
        if(mSession.isStreaming())
            mSession.stop();
    }


}
