package org.oo.oodroid2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.io.IOException;

import oo.org.server.SDPDistributor;


public class OoDroidActivity extends ActionBarActivity implements View.OnClickListener,Session.Callback,SurfaceHolder.Callback{


    private final static String TAG = "OoDroidActivity";

    /** Key used in the SharedPreferences for IP in et_dst. */
    public final static String KEY_IP = "dstIP";

    private Button mPlayButton, mFlashButton;
    private SurfaceView mSurfaceView;
    private EditText mDstIPText;
    private Session mSession;
    private TextView mBitRate;
    SharedPreferences settings;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oo_droid);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPlayButton = (Button) findViewById(R.id.bt_play);
        mFlashButton = (Button) findViewById(R.id.bt_flash);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mDstIPText = (EditText) findViewById(R.id.et_dst);
        mBitRate = (TextView) findViewById(R.id.tv_bitrate_value);
        
        settings = this.getPreferences(MODE_PRIVATE);
        mEditor = settings.edit();
        mDstIPText.setText(settings.getString(KEY_IP,"239.1.1.1"));
        
        //set nessesary information of session
        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)//FIXME orientation is inappropriate
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .setTimeToLive(1)
                .build();

        mPlayButton.setOnClickListener(this);
        mFlashButton.setOnClickListener(this);
        mDstIPText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditor.putString("dstIP",s.toString());
                mEditor.commit();
            }
        });

        mSurfaceView.getHolder().addCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSession.isStreaming()) {
            mPlayButton.setText(R.string.stop);
        } else {
            mPlayButton.setText(R.string.start);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_play) {
            // Starts/stops streaming
            mSession.setDestination(mDstIPText.getText().toString());
            if (!mSession.isStreaming()) {
                mSession.configure();
            } else {
                mSession.stop();
            }
            mPlayButton.setEnabled(false);
        } else {
            // flash light
            mSession.toggleFlash();
        }
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        mBitRate.setText((int)(bitrate/1000) + "Kbps");
    }

    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        mPlayButton.setEnabled(true);
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
        /*try {
            OutputStream mDescriptionOutput = this.openFileOutput("session.sdp",MODE_MULTI_PROCESS);
            try {
                mDescriptionOutput.write(mSession.getSessionDescription().getBytes());
                mDescriptionOutput.flush();
                mDescriptionOutput.close();
            } catch (IOException e) {
                logError("Fail to save session.sdp");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "unable to save session description");
            logError("Fail to find session.sdp");
            e.printStackTrace();
        }*/
        try {
            new SDPDistributor(mSession.getSessionDescription()).start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Open SDP distributor error");
        }
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }   

    @Override
    public void onSessionStarted() {
        Log.d(TAG,"Session started.");
        mPlayButton.setEnabled(true);
        mPlayButton.setText(R.string.stop);
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG,"Session stopped.");
        mPlayButton.setEnabled(true);
        mPlayButton.setText(R.string.start);
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
