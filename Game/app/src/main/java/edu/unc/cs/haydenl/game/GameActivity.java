package edu.unc.cs.haydenl.game;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by hayden on 10/9/17.
 */

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.game_activity);
    }
}
