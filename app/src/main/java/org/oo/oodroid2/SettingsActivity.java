package org.oo.oodroid2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;


import java.util.List;


public class SettingsActivity extends PreferenceActivity {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static Context context = null;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        context = SettingsActivity.this;
        setupSimplePreferencesScreen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SettingsActivity.this, OoDroidActivity.class);
        startActivity(i);
        finish();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_ui);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(getString(R.string.title_video_settings));
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_video);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(getString(R.string.title_audio_settings));
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_audio);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("destination_IP"));
        bindPreferenceSummaryToValue(findPreference("video_encoding"));
        bindPreferenceSummaryToValue(findPreference("video_frame_rate"));
        bindPreferenceSummaryToValue(findPreference("video_resolution"));
        bindPreferenceSummaryToValue(findPreference("video_bitrate"));
        bindPreferenceSummaryToValue(findPreference("audio_encoding"));
        bindPreferenceSummaryToValue(findPreference("audio_bitrate"));
        bindPreferenceSummaryToValue(findPreference("audio_sampling_rate"));
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            String key = preference.getKey();
            System.out.println(preference.getKey());
            boolean valid = true;
            if(key.equals("destination_IP")) {
                if(!Utils.isIpAddress((String) value)){
                    Toast.makeText(context, "invalid IP address", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            } else if(key.equals("audio_bitrate")) {
                int val = Integer.parseInt((String) value);
                if(val < 8 || val > 128){
                    Toast.makeText(context, "invalid audio bitrate", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            } else if(key.equals("audio_sampling_rate")) {
                int val = Integer.parseInt((String) value);
                if(val < 8 || val > 48){
                    Toast.makeText(context, "invalid audio sampling rate", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            } else if(key.equals("video_bitrate")) {
                int val = Integer.parseInt((String) value);
                if(val < 8 || val > 4096){
                    Toast.makeText(context, "invalid video bitrate", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            } else if(key.equals("video_frame_rate")) {
                int val = Integer.parseInt((String) value);
                if(val < 7 || val > 30){
                    Toast.makeText(context, "invalid video frame rate", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            }
            return valid;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VideoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_video);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("video_encoding"));
            bindPreferenceSummaryToValue(findPreference("video_frame_rate"));
            bindPreferenceSummaryToValue(findPreference("video_resolution"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AudioPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_audio);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("audio_encoding"));
            bindPreferenceSummaryToValue(findPreference("audio_birate"));
            bindPreferenceSummaryToValue(findPreference("audio_sampling_rate"));
        }
    }

}
