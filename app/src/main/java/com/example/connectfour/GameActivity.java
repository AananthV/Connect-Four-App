package com.example.connectfour;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity {
    private ConnectFourView c4view;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.connectfour.gameSharedPrefs";
    private int boardHeight;
    private int boardWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        this.boardHeight = this.mPreferences.getInt("boardHeight", 6);
        this.boardWidth = this.mPreferences.getInt("boardWidth", 7);

        this.c4view = new ConnectFourView(getApplicationContext(), this.boardWidth, this.boardHeight);
        LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.addView(this.c4view);

        this.c4view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    c4view.previewMove(event.getX(), event.getY());
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    c4view.playMove(event.getX(), event.getY());
                }
                return true;
            }
        });

        Button undoButton = (Button) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c4view.undoMove();
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c4view.reset();
            }
        });
    }
}
