package com.example.connectfour;

import android.content.pm.PackageManager;
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
    public int winner;
    public boolean isSinglePlayer;

    public ConnectFour(int bWidth, int bHeight, boolean isSinglePlayer) {
        this.boardWidth = bWidth;
        this.boardHeight = bHeight;
        this.isSinglePlayer = isSinglePlayer;
        this.board = new int[this.boardHeight][this.boardWidth];
        this.moves = new ArrayList<>();
        this.numMoves = 0;
        this.winner = 0;
    }

    public ConnectFour(int[][] board, int bWidth, int bHeight, int numMoves, boolean isSinglePlayer) {
        this.boardWidth = bWidth;
        this.boardHeight = bHeight;
        this.board = new int[this.boardHeight][this.boardWidth];
        for(int row = 0; row < this.boardHeight; row++) {
            for(int col = 0; col < this.boardWidth; col++) {
                this.board[row][col] = board[row][col];
            }
        }
        this.isSinglePlayer = isSinglePlayer;
        this.moves = new ArrayList<>();
        this.numMoves = numMoves;
        this.winner = 0;
    }

    public void resetGame() {
        this.board = new int[this.boardHeight][this.boardWidth];
        this.moves = new ArrayList<>();
        this.numMoves = 0;
        this.winner = 0;
    }

    public void undoMove() {
        if(this.numMoves > 0) {
            this.numMoves--;
            this.board[this.moves.get(numMoves).get(0)][this.moves.get(numMoves).get(1)] = 0;
            this.moves.remove(numMoves);
            if(this.isSinglePlayer) {
                this.numMoves--;
                this.board[this.moves.get(numMoves).get(0)][this.moves.get(numMoves).get(1)] = 0;
                this.moves.remove(numMoves);
            }
        }
    }

    public boolean isPlayable(int column) {
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

            if(isConnected(i-1, column, -1)) {
                this.winner = (this.numMoves - 1) % 2 + 1;
                return this.winner;
            };
        }
        return 0;
    }

    public boolean isFull() {
        return this.numMoves == this.boardHeight*this.boardWidth;
    }

    private void updatePlayerLines(int[] player1lines, int[] player2lines, int[] line) {
        if(line[0] > 0 && line[1] > 0) {
            line[2] = 0;
        }
        if(line[2] == 1) {
            if(line[0] > 0) {
                player1lines[line[0] - 1]++;
            }
            if(line[1] > 0) {
                player2lines[line[1] - 1]++;
            }
        }
    }

    public double getScore() {
        int[] player1lines = {0, 0, 0, 0};
        int[] player2lines = {0, 0, 0, 0};
        for(int row = 0; row < this.boardHeight; row++) {
            for(int col = 0; col < this.boardWidth; col++) {

                int[] lineRight = {0, 0, 1};
                int[] lineDown = {0, 0, 1};
                int[] lineDownLeft = {0, 0, 1};
                int[] lineDownRight = {0, 0, 1};

                for(int i = 0; i < 4; i++) {

                    // Right
                    if(col + 3 < this.boardWidth) {
                        if(this.board[row][col+i] == 1) {
                            lineRight[0]++;
                        } else if (this.board[row][col+i] == 2) {
                            lineRight[1]++;
                        }
                    } else {
                        lineRight[2] = 0;
                    }

                    // Down and diagonals
                    if(row + 3 < this.boardHeight) {
                        // Down
                        if(this.board[row + i][col] == 1) {
                            lineDown[0]++;
                        } else if (this.board[row + 1][col] == 2) {
                            lineDown[1]++;
                        }

                        // Down Left
                        if(col - 3 >= 0) {
                            if(this.board[row + i][col - i] == 1) {
                                lineDownLeft[0]++;
                            } else if (this.board[row + i][col - i] == 2) {
                                lineDownLeft[1]++;
                            }
                        } else {
                            lineDownLeft[2] = 0;
                        }

                        // DOwn Right
                        if(col + 3 < this.boardWidth) {
                            if(this.board[row + i][col + i] == 1) {
                                lineDownRight[0]++;
                            } else if (this.board[row + i][col + i] == 2) {
                                lineDownRight[1]++;
                            }
                        } else {
                            lineDownRight[2] = 0;
                        }
                    } else {
                        lineDown[2] = 0;
                        lineDownLeft[2] = 0;
                        lineDownRight[2] = 0;
                    }
                }

                updatePlayerLines(player1lines, player2lines, lineDown);
                updatePlayerLines(player1lines, player2lines, lineRight);
                updatePlayerLines(player1lines, player2lines, lineDownRight);
                updatePlayerLines(player1lines, player2lines, lineDownLeft);
            }
        }

        if(player1lines[3] > 0) {
            return Double.POSITIVE_INFINITY;
        }
        if(player2lines[3] > 0) {
            return Double.NEGATIVE_INFINITY;
        }

        double player1Score = 0;
        double player2Score = 0;

        for(int i = 0; i < 4; i++) {
            player1Score += Math.pow(10*i, i) * player1lines[i];
            player2Score -= Math.pow(10*i, i) * player2lines[i];
        }

        return player1Score + player2Score;
    }

    public boolean isConnected(int x, int y, int num) {
        if(num == -1) {
            num = board[x][y];
        }
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
        while (i < this.boardHeight && j < this.boardWidth && this.board[i][j] == num) {
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
        while (i < this.boardHeight && j >= 0 && this.board[i][j] == num) {
            count++;
            i++;
            j--;
        }
        i = x - 1;
        j = y + 1;
        while (i >= 0 && j < this.boardWidth && this.board[i][j] == num) {
            count++;
            i--;
            j++;
        }
        if (count == 4)
            return true;

        return false;
    }
}
