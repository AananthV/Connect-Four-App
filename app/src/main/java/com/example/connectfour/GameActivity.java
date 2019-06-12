package com.example.connectfour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private ConnectFourView c4view;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.connectfour.gameSharedPrefs";
    private int boardHeight;
    private int boardWidth;
    private boolean isSinglePlayer;
    private int botLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        this.isSinglePlayer = intent.getBooleanExtra("isSinglePlayer", false);

        this.mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        this.boardHeight = this.mPreferences.getInt("boardHeight", 6);
        this.boardWidth = this.mPreferences.getInt("boardWidth", 7);
        this.botLevel = this.mPreferences.getInt("botLevel", 3);

        this.c4view = new ConnectFourView(getApplicationContext(), this.boardWidth, this.boardHeight, this.isSinglePlayer, this.botLevel);
        LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.addView(this.c4view);

        this.c4view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    c4view.previewMove(event.getX(), event.getY());
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    c4view.playMove(event.getX());
                }
                return true;
            }
        });

        Button undoButton = (Button) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!c4view.moveInProgress) {
                    c4view.undoMove();
                } else {
                    Toast.makeText(GameActivity.this, "Wait for move to complete.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!c4view.moveInProgress) {
                    c4view.reset();
                } else {
                    Toast.makeText(GameActivity.this, "Wait for move to complete.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
