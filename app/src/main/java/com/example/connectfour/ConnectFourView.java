package com.example.connectfour;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ConnectFourView extends View {
    private ConnectFour game;
    private ConnectFourAI bot;

    private static final int canvasBackgroundColor = 0xFFEAEAEA;
    private static final int gridColor = Color.BLACK;
    private static final int circleColors[] = {Color.WHITE, Color.RED, Color.YELLOW};
    private static final int messageBoxColor = 0xCCFFFFFF;
    private static final int messageTextColor = Color.BLACK;

    private static final float messageTextSize = 320f;
    private static final float messageSubTextSize = 50f;
    private static final float playerIndicatorTextSize = 100f;
    private static final int playerIndicatorPadding = 16;
    private static final int playerIndicatorRadius = 8;

    private Paint backgroundPaint;
    private Paint gridPaint;
    private Paint[] circlePaints;
    private Paint messageBoxPaint;
    private Paint messageTextPaint;
    private Paint messageSubTextPaint;
    private Paint playerIndicatorTextPaint;

    private MediaPlayer soundPlayer;

    private int gridWidth;
    private int gridDrawWidth;
    private int gridHeight;
    private int gridDrawHeight;

    private int cellSize;
    private static final float cellCircleSize = 0.8f; // Relative to a cell
    private static final float cellBorderSize = 0.1f; // Relative to a cell

    private boolean previewAvailable;
    public boolean moveInProgress;
    private int previewX;
    private int previewY;

    private int moveX;
    private int moveY;
    private int numMoves;

    private int isGameOver;
    private boolean isSinglePlayer;
    private int singlePlayerCounter;

    private class MakeMove extends AsyncTask<String, Void, String> {
        private int move;
        @Override
        protected String doInBackground(String... params) {
            this.move = bot.getMove(game);
            return "" + this.move;
        }

        @Override
        protected void onPostExecute(String result) {
            moveInProgress = false;
            playMove((move + 0.5f) * cellSize);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public ConnectFourView(Context context, int bWidth, int bHeight, boolean isSinglePlayer, int botLevel) {
        super(context);
        this.game =  new ConnectFour(bWidth, bHeight, isSinglePlayer);
        this.gridWidth = this.game.boardWidth;
        this.gridHeight = this.game.boardHeight;
        this.isSinglePlayer = isSinglePlayer;
        if(this.isSinglePlayer) {
            this.bot = new ConnectFourAI(botLevel, 2);
        }
        Log.v("BotLevel", "" + botLevel);
        this.numMoves = 0;

        this.soundPlayer = MediaPlayer.create(getContext(), R.raw.blop);

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(canvasBackgroundColor);
        this.backgroundPaint.setStyle(Paint.Style.FILL);

        this.gridPaint = new Paint();
        this.gridPaint.setColor(gridColor);
        this.gridPaint.setStyle(Paint.Style.FILL);

        this.messageBoxPaint = new Paint();
        this.messageBoxPaint.setColor(messageBoxColor);
        this.messageBoxPaint.setStyle(Paint.Style.FILL);

        this.messageTextPaint = new Paint();
        this.messageTextPaint.setColor(messageTextColor);
        this.messageTextPaint.setTextSize(messageTextSize);
        this.messageTextPaint.setTextAlign(Paint.Align.CENTER);

        this.messageSubTextPaint = new Paint();
        this.messageSubTextPaint.setColor(messageTextColor);
        this.messageSubTextPaint.setTextSize(messageSubTextSize);
        this.messageSubTextPaint.setTextAlign(Paint.Align.CENTER);

        this.playerIndicatorTextPaint = new Paint();
        this.playerIndicatorTextPaint.setColor(messageTextColor);
        this.playerIndicatorTextPaint.setTextSize(playerIndicatorTextSize);
        this.playerIndicatorTextPaint.setTextAlign(Paint.Align.CENTER);

        this.circlePaints = new Paint[3];
        for(int i = 0; i < 3; i++) {
            this.circlePaints[i] = new Paint();
            this.circlePaints[i].setColor(circleColors[i]);
            this.circlePaints[i].setStyle(Paint.Style.FILL);
        }
    }

    public void previewMove(float x, float y) {
        if(this.moveInProgress || this.isGameOver > 0) return;
        int column = (int) Math.floor(Math.round(x)/ this.cellSize);
        this.previewX = Math.round((column+0.5f)*this.cellSize);
        this.previewY = Math.round(1.5f * this.cellSize);
        this.previewAvailable = true;
        this.invalidate();
    }

    private void updateMove() {
        int result = this.game.playMove(this.moveX);
        this.numMoves++;
        if(result > 0) {
            this.isGameOver = result;
        } else if (this.game.isFull()) {
            this.isGameOver = 3;
        }
        this.soundPlayer.start();
        this.invalidate();
    }

    public void playMove(float x) {
        if(this.isGameOver > 0) {
            this.isGameOver = 0;
            this.numMoves = 0;
            this.invalidate();
            return;
        }
        if(!(x > 0 && x < this.gridDrawWidth) || this.moveInProgress) return;
        this.moveX = (int) Math.floor(Math.round(x)/ this.cellSize);
        this.moveY = this.game.getMoveY(this.moveX);
        this.previewX = Math.round((this.moveX+0.5f)*this.cellSize);
        this.previewY = Math.round(1.5f * this.cellSize);
        this.moveInProgress = true;
        this.previewAvailable = true;
        this.singlePlayerCounter = 0;
        this.invalidate();
    }

    public void undoMove() {
        this.game.undoMove();
        this.invalidate();
    }

    public void reset() {
        this.game.resetGame();
        this.isGameOver = 0;
        this.numMoves = 0;
        this.invalidate();
    }

    public static String repeatString(String val, int count){
        StringBuilder buf = new StringBuilder(val.length() * count);
        while (count-- > 0) {
            buf.append(val);
        }
        return buf.toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.cellSize = Math.round(
                Math.min(getMeasuredWidth()/this.gridWidth, getMeasuredHeight()/(this.gridHeight+2))
        );
        this.gridDrawHeight = this.cellSize * this.gridHeight;
        this.gridDrawWidth = this.cellSize * this.gridWidth;

        setMeasuredDimension(this.gridDrawWidth, this.gridDrawHeight + 2*this.cellSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, this.gridDrawWidth, this.gridDrawHeight + 2*cellSize, this.backgroundPaint);

        // Draw Player Indicator
        canvas.drawRoundRect(
                playerIndicatorPadding/2,
                playerIndicatorPadding/2,
                this.gridDrawWidth - playerIndicatorPadding/2,
                this.cellSize - playerIndicatorPadding/2,
                this.playerIndicatorRadius,
                this.playerIndicatorRadius,
                gridPaint
        );
        canvas.drawRoundRect(
                playerIndicatorPadding,
                playerIndicatorPadding,
                this.gridDrawWidth - playerIndicatorPadding,
                this.cellSize - playerIndicatorPadding,
                playerIndicatorRadius,
                playerIndicatorRadius,
                this.circlePaints[this.game.numMoves % 2 + 1]
        );
        if(!this.isSinglePlayer) {
            canvas.drawText("Player " + (this.game.numMoves % 2 + 1), Math.round(this.gridDrawWidth/2), Math.round(this.cellSize/2) + playerIndicatorTextSize/3, playerIndicatorTextPaint);
        } else {
            if(this.numMoves % 2 == 0) {
                canvas.drawText("Player", Math.round(this.gridDrawWidth/2), Math.round(this.cellSize/2) + playerIndicatorTextSize/3, playerIndicatorTextPaint);
            } else {
                canvas.drawText("CPU" + this.repeatString(".", (int) (this.singlePlayerCounter++/10) % 3), Math.round(this.gridDrawWidth/2), Math.round(this.cellSize/2) + playerIndicatorTextSize/3, playerIndicatorTextPaint);
            }
        }


        // Draw Circles
        Path blankCircles = new Path();
        for(int row = 0; row < this.gridHeight; row++) {
            for(int col = 0; col < this.gridWidth; col++) {
                if(this.game.board[row][col] == 0) {
                    blankCircles.addCircle(
                            (col + 0.5f)*this.cellSize,
                            (row + 2.5f)*this.cellSize,
                            this.cellSize*cellCircleSize/2f,
                            Path.Direction.CW
                    );
                } else {
                    canvas.drawCircle(
                            Math.round((col + 0.5)*this.cellSize),
                            Math.round((row + 2.5)*this.cellSize),
                            Math.round(this.cellSize*cellCircleSize/2),
                            this.circlePaints[this.game.board[row][col]]
                    );
                }
            }
        }

        // Draw Preview if preview is available
        if(this.previewAvailable) {
            canvas.drawCircle(
                    this.previewX,
                    this.previewY,
                    Math.round(this.cellSize*(cellCircleSize + cellBorderSize)/2),
                    gridPaint
            );
            canvas.drawCircle(
                    this.previewX,
                    this.previewY,
                    Math.round(this.cellSize*cellCircleSize/2),
                    this.circlePaints[this.game.numMoves % 2 + 1]
            );
        }

        canvas.save();
        canvas.clipPath(blankCircles, Region.Op.DIFFERENCE);

        canvas.drawRect(0, 2*this.cellSize, this.gridDrawWidth, this.gridDrawHeight + 2*cellSize, this.gridPaint);

        for(int row = 0; row < this.gridHeight; row++) {
            for(int col = 0; col < this.gridWidth; col++) {
                if(this.game.board[row][col] > 0) {
                    canvas.drawCircle(
                            Math.round((col + 0.5) * this.cellSize),
                            Math.round((row + 2.5) * this.cellSize),
                            Math.round(this.cellSize * cellCircleSize / 2),
                            this.circlePaints[this.game.board[row][col]]
                    );
                }
            }
        }


        // Check if Game is Over
        if(this.isGameOver > 0) {
            canvas.restore();
            canvas.drawRect(0, 0, this.gridDrawWidth, this.gridDrawHeight + 2*this.cellSize, this.messageBoxPaint);
            if(this.isGameOver == 3) {
                canvas.drawText("DRAW", Math.round(this.gridDrawWidth/2), Math.round(this.gridDrawHeight/2 + this.cellSize), this.messageTextPaint);
            } else if (this.isGameOver < 3) {
                canvas.drawCircle(
                        Math.round(this.gridDrawWidth / 2),
                        Math.round(this.gridDrawHeight / 2 - this.cellSize / 2),
                        Math.round(this.cellSize * (cellCircleSize + cellBorderSize)),
                        gridPaint
                );
                canvas.drawCircle(
                        Math.round(this.gridDrawWidth / 2),
                        Math.round(this.gridDrawHeight / 2 - this.cellSize / 2),
                        Math.round(this.cellSize * (cellCircleSize)),
                        this.circlePaints[this.isGameOver]
                );
                canvas.drawText("WINS", Math.round(this.gridDrawWidth / 2), Math.round(this.gridDrawHeight / 2 + this.cellSize * 2.5), this.messageTextPaint);
            }
            canvas.drawText("Click to start New Game", Math.round(this.gridDrawWidth/2), Math.round(this.gridDrawHeight/2 + this.cellSize*4), this.messageSubTextPaint);
            this.game.resetGame();
        }

        if(this.moveInProgress && this.previewAvailable) {
            this.previewY+=25;
            if(this.previewY >= (this.moveY+2.5f)*this.cellSize) {
                this.updateMove();
                this.moveInProgress = false;
                this.previewAvailable = false;
            }
        }

        if(this.moveInProgress) {
            this.invalidate();
        }

        if(!(this.moveInProgress || this.previewAvailable) && this.isSinglePlayer && this.numMoves % 2 == 1) {
            this.moveInProgress = true;
            MakeMove move = new MakeMove();
            move.execute();
        }
    }
}
