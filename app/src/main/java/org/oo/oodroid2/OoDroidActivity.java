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
    Session session;
    SDPDistributor distributorServer;
    Button buttonPlay, buttonFlash, buttonSettings;
    String destinationIP;
    int audioEncodingValue, audioBitrate, audioSamplingRate;
    int videoEncodingValue, videoBitrate, videoFrameRate, videoResolutionW, videoResolutionH;
    String audioEncoding, videoEncoding, videoResolution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oo_droid);

        overridePendingTransition(R.anim.startanim, R.anim.exitanim);
        Log.v(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        findViewById(R.id.panel).getBackground().setAlpha(50);
        buttonPlay = (Button) findViewById(R.id.button);
        buttonFlash = (Button) findViewById(R.id.button2);
        buttonSettings = (Button) findViewById(R.id.button3);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(OoDroidActivity.this);

        destinationIP = sp.getString(getString(R.string.key_destination_IP), "239.1.1.1");
        audioEncoding = sp.getString(getString(R.string.key_audio_encoding), "None");
        audioBitrate = Integer.parseInt(sp.getString(getString(R.string.key_audio_bitrate), "64"));
        audioSamplingRate = Integer.parseInt(sp.getString(getString(R.string.key_audio_sampling_rate), "48"));
        videoEncoding = sp.getString(getString(R.string.key_video_encoding), "H.263");
        videoResolution = sp.getString(getString(R.string.key_video_resolution), "320x240 (4:3)");
        videoFrameRate = Integer.parseInt(sp.getString(getString(R.string.key_video_frame_rate), "24"));
        videoBitrate = Integer.parseInt(sp.getString(getString(R.string.key_video_bitrate), "500"));

        if(audioEncoding.equals("AAC"))
            audioEncodingValue = SessionBuilder.AUDIO_AAC;
        else if(audioEncoding.equals("AMRNB"))
            audioEncodingValue = SessionBuilder.AUDIO_AMRNB;
        else
            audioEncodingValue = SessionBuilder.AUDIO_NONE;

        if(videoEncoding.equals("H.263"))
            videoEncodingValue = SessionBuilder.VIDEO_H263;
        else if(videoEncoding.equals("H.264"))
            videoEncodingValue = SessionBuilder.VIDEO_H264;
        else
            videoEncodingValue = SessionBuilder.VIDEO_NONE;

        if(videoResolution.startsWith("1920x1080")){
            videoResolutionW = 1920; videoResolutionH = 1080;
        } else if(videoResolution.startsWith("1280x720")){
            videoResolutionW = 1280; videoResolutionH = 720;
        } else if(videoResolution.startsWith("640x480")){
            videoResolutionW = 640; videoResolutionH = 480;
        } else {
            videoResolutionW = 320; videoResolutionH = 240;
        }

        session = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(surfaceView)
                .setContext(getApplicationContext())
                .setAudioEncoder(audioEncodingValue)
                .setAudioQuality(new AudioQuality(audioSamplingRate * 1000, audioBitrate * 1000))
                .setVideoEncoder(videoEncodingValue)
                .setVideoQuality(new VideoQuality(videoResolutionW, videoResolutionH, videoFrameRate, videoBitrate * 1000))
                .setTimeToLive(1)
                .build();
        session.setPreviewOrientation(0);
        session.setDestination(destinationIP);
        session.configure();

        surfaceView.getHolder().addCallback(this);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!session.isStreaming()){
                    session.configure();
                    session.start();
                } else {
                    session.stop();
                }
            }
        });

        buttonFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.toggleFlash();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OoDroidActivity.this, SettingsActivity.class);
                startActivity(i);
                finish();
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
        distributorServer = new SDPDistributor(session.getSessionDescription());
        Log.v(TAG, session.getSessionDescription());
    }

    @Override
    public void onSessionStarted() {
        Log.v(TAG, "onSessionStarted");
        Toast.makeText(OoDroidActivity.this, "Streaming start", Toast.LENGTH_SHORT).show();
        buttonPlay.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
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
        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        distributorServer.stopServer();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        session.startPreview();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        Log.v(TAG, "onPause");
    }


}
