package org.oo.oodroid2;

import android.view.View;

/**
 * Created by ental_000 on 2015/3/7.
 */
public class UIUtils {
    public static void setImmersive(View mDecorView, boolean sticky){
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if(sticky)
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        else
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        mDecorView.setSystemUiVisibility(
                visibility
        );
    }
}
