package edu.unc.cs.haydenl.game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {
    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        player = MediaPlayer.create(this, R.raw.harmony);
        player.setLooping(true); // Set looping
        player.setVolume(100, 100);
        player.start();
    }

    public void play(View v){
        Intent playIntent = new Intent(this, GameActivity.class);
        startActivity(playIntent);
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
