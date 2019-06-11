package com.example.connectfour;

import com.example.connectfour.ConnectFour;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private ConnectFourView c4view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.c4view = new ConnectFourView(getApplicationContext());
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
