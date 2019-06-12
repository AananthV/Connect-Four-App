package com.example.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchSingleplayerGame(View view) {
        Intent singleplayerGame = new Intent(getApplicationContext(), GameActivity.class);
        singleplayerGame.putExtra("isSinglePlayer", true);
        startActivity(singleplayerGame);
    }

    public void launchMultiplayerGame(View view) {
        Intent multiplayerGame = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(multiplayerGame);
    }

    public void launchSettings(View view) {
        Intent settings = new Intent(getApplicationContext(), Settings.class);
        startActivity(settings);
    }
}
