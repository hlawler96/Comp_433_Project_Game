package edu.unc.cs.haydenl.game;

import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

/**
 * Created by hayden on 10/9/17.
 */

public class GameActivity extends Activity {

    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.game_activity);
        player = MediaPlayer.create(this, R.raw.harmony);
        player.setLooping(true); // Set looping
        player.setVolume(100, 100);
        player.start();

    }
    @Override
    public void onPause(){
        super.onPause();
        player.stop();

    }

    @Override
    public void onResume(){
        super.onResume();
        player.start();
    }
}
