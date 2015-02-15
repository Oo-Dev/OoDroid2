package org.oo.oodroid2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.Stream;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.H263Stream;
import net.majorkernelpanic.streaming.video.VideoQuality;
import net.majorkernelpanic.streaming.video.VideoStream;

import java.io.IOException;
import java.net.InetAddress;


public class OoDroidActivity extends ActionBarActivity implements View.OnClickListener,Session.Callback,SurfaceHolder.Callback{


    private final static String TAG = "OoDroidActivity";

    private Button mButton1, mButton2;
    private SurfaceView mSurfaceView;
    private EditText mEditText;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oo_droid);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mEditText = (EditText) findViewById(R.id.editText1);

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(270)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .build();

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSession.isStreaming()) {
            mButton1.setText("stop");
        } else {
            mButton1.setText("start");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            // Starts/stops streaming
            mSession.setDestination(mEditText.getText().toString());
            if (!mSession.isStreaming()) {
                mSession.configure();
            } else {
                mSession.stop();
            }
            mButton1.setEnabled(false);
        } else {
            // Switch between the two cameras
            mSession.switchCamera();
        }
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        //Log.d(TAG,"Bitrate: "+bitrate);
    }

    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        mButton1.setEnabled(true);
        if (e != null) {
            logError(e.getMessage());
        }
    }

    @Override

    public void onPreviewStarted() {
        Log.d(TAG,"Preview started.");
    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG,"Preview configured.");
        // Once the stream is configured, you can get a SDP formatted session description
        // that you can send to the receiver of the stream.
        // For example, to receive the stream in VLC, store the session description in a .sdp file
        // and open it with VLC while streaming.
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG,"Session started.");
        mButton1.setEnabled(true);
        mButton1.setText("stop");
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG,"Session stopped.");
        mButton1.setEnabled(true);
        mButton1.setText("start");
    }

    /** Displays a popup to report the error to the user */
    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(OoDroidActivity.this);
        builder.setMessage(error).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSession.stop();
    }


}
