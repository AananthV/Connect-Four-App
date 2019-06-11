package com.example.connectfour;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConnectFour {
    public int boardWidth;
    public int boardHeight;
    public int[][] board;
    private ArrayList<ArrayList<Integer>> moves;
    public int numMoves;
    private int numUndos;

    public ConnectFour(int bWidth, int bHeight, int nUndos) {
        this.boardWidth = bWidth;
        this.boardHeight = bHeight;
        this.numUndos = nUndos;
        this.board = new int[this.boardHeight][this.boardWidth];
        this.moves = new ArrayList<>();
        this.numMoves = 0;
    }

    public void resetGame() {
        this.board = new int[this.boardHeight][this.boardWidth];
        this.moves = new ArrayList<>();
        this.numMoves = 0;
    }

    public void undoMove() {
        if(this.numMoves > 0) {
            this.numMoves--;
            this.board[this.moves.get(numMoves).get(0)][this.moves.get(numMoves).get(1)] = 0;
            this.moves.remove(numMoves);
        }
    }

    private boolean isPlayable(int column) {
        return this.board[0][column] == 0;
    }

    public int getMoveY(int column) {
        int i;
        for(i = 0; i < this.boardHeight && this.board[i][column] == 0; i++);
        return i-1;
    }

    public int playMove(int column) {
        if(isPlayable((column))) {
            int i = 0;
            for(i = 0; i < this.boardHeight && this.board[i][column] == 0; i++);
            this.board[i-1][column] = this.numMoves%2 + 1;
            this.numMoves++;
            this.moves.add(new ArrayList<Integer>(Arrays.asList(i-1, column)));

            if(isConnected(i-1, column)) {
                return (this.numMoves-1) % 2 + 1;
            };
        }
        return 0;
    }

    public boolean isFull() {
        return this.numMoves == this.boardHeight*this.boardWidth;
    }

    public boolean isConnected(int x, int y) {
        int num = this.board[x][y];
        int count = 0;
        int i = y;

        //HORIZONTAL.
        while (i < this.boardWidth && this.board[x][i] == num) {
            count++;
            i++;
        }
        i = y - 1;
        while (i >= 0 && this.board[x][i] == num) {
            count++;
            i--;
        }
        if (count == 4)
            return true;

        //VERTICAL.
        count = 0;
        int j = x;
        while (j < this.boardHeight && this.board[j][y] == num) {
            count++;
            j++;
        }
        if (count == 4)
            return true;

        //SECONDARY DIAGONAL.
        count = 0;
        i = x;
        j = y;
        while (i < this.boardWidth && j < this.boardHeight && this.board[i][j] == num) {
            count++;
            i++;
            j++;
        }
        i = x - 1;
        j = y - 1;
        while (i >= 0 && j >= 0 && this.board[i][j] == num) {
            count++;
            i--;
            j--;
        }
        if (count == 4)
            return true;

        //LEADING DIAGONAL.
        count = 0;
        i = x;
        j = y;
        while (i < this.boardWidth && j >= 0 && this.board[i][j] == num) {
            count++;
            i++;
            j--;
        }
        i = x - 1;
        j = y + 1;
        while (i >= 0 && j < this.boardHeight && this.board[i][j] == num) {
            count++;
            i--;
            j++;
        }
        if (count == 4)
            return true;

        return false;
    }

    public int[][] getBoard() {
        return this.board;
    }
}