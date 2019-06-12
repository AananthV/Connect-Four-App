package com.example.connectfour;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class Settings extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.connectfour.gameSharedPrefs";
    private int boardWidth;
    private int boardHeight;

    private static final int maxDimension = 9;
    private static final int minDimension = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        this.boardHeight = this.mPreferences.getInt("boardHeight", 6);
        this.boardWidth = this.mPreferences.getInt("boardWidth", 7);

        Log.v("TAG", "Works till here");

        this.displayHeight();
        this.displayWidth();
    }

    private void displayWidth() {
        TextView widthTV = (TextView) findViewById(R.id.width_text_view);
        widthTV.setText("" + this.boardWidth);
    }

    private void displayHeight() {
        TextView heightTV = (TextView) findViewById(R.id.height_text_view);
        heightTV.setText("" + this.boardHeight);
    }

    public void increaseHeight(View view) {
        if(this.boardHeight >= maxDimension) {
            this.boardHeight = maxDimension;
        } else {
            this.boardHeight++;
        }
        displayHeight();
    }

    public void decreaseHeight(View view) {
        if(this.boardHeight <= minDimension) {
            this.boardHeight = minDimension;
        } else {
            this.boardHeight--;
        }
        displayHeight();
    }

    public void increaseWidth(View view) {
        if(this.boardWidth >= maxDimension) {
            this.boardWidth = maxDimension;
        } else {
            this.boardWidth++;
        }
        displayWidth();
    }

    public void decreaseWidth(View view) {
        if(this.boardWidth <= minDimension) {
            this.boardWidth = minDimension;
        } else {
            this.boardWidth--;
        }
        displayWidth();
    }

    public void saveSettings(View view) {
        SharedPreferences.Editor preferencesEditor = this.mPreferences.edit();
        preferencesEditor.putInt("boardWidth", this.boardWidth);
        preferencesEditor.putInt("boardHeight", this.boardHeight);
        preferencesEditor.apply();

        finish();
    }
}
