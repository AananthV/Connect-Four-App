package com.example.connectfour;

/*
    Uses a Min Max Algorithm along with AlphaBeta pruning to determine the move.
 */

public class ConnectFourAI {
    private int difficulty;
    private int playerNumber;

    private class State {
        public int action;
        public ConnectFour game;
        public int score;
        public int depth;

        public State(int action, ConnectFour game, int score, int depth) {
            this.action = action;
            this.game = game;
            this.score = score;
            this.depth = depth;
        }
    }

    public ConnectFourAI(int botDifficulty, int playerNumber) {
        this.difficulty = botDifficulty;
        this.playerNumber = playerNumber;
    }

    public int getMove(ConnectFour game) {
        boolean maxPlayer = (this.playerNumber == 2);
        State result = this.alphabeta(new State(0, game, 0, 0), 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, maxPlayer);
        return result.action;
    }

    private State alphabeta(State state, int depth, double alpha, double beta, boolean maxPlayer) {
        if(depth == this.difficulty || state.game.winner != 0) {
            return new State(state.action, state.game, state.game.getScore(playerNumber), state.depth);
        }

        if(maxPlayer) {
            double highestScore = Double.NEGATIVE_INFINITY;
            int deepestDepth = 0;
            int bestAction = -1;

            for(int col = 0; col < state.game.boardWidth; col++) {
                if(state.game.isPlayable(col)) {
                    ConnectFour newGame = new ConnectFour(state.game.board, state.game.boardWidth, state.game.boardHeight, state.game.numMoves, state.game.isSinglePlayer);
                    newGame.playMove(col);
                    State nextState = this.alphabeta(new State(col, newGame, 0, depth + 1), depth, alpha, beta, false);

                    int score = nextState.score;
                    if (bestAction == -1 || score >= highestScore) {
                        if (score == highestScore) {
                            if (nextState.depth > deepestDepth) {
                                highestScore = score;
                                bestAction = col;
                                deepestDepth = nextState.depth;
                            }
                        } else {
                            highestScore = score;
                            bestAction = col;
                        }
                    }

                    alpha = Math.max(alpha, highestScore);

                    if (beta <= alpha) {
                        return new State(bestAction, state.game, (int) highestScore, depth);
                    }
                }
            }

            return new State(bestAction, state.game, (int) highestScore, depth);

        } else {
            double smallestScore = Double.POSITIVE_INFINITY;
            int deepestDepth = 0;
            int bestAction = -1;

            for(int col = 0; col < state.game.boardWidth; col++) {
                if(state.game.isPlayable(col)) {
                    ConnectFour newGame = new ConnectFour(state.game.board, state.game.boardWidth, state.game.boardHeight, state.game.numMoves, state.game.isSinglePlayer);
                    newGame.playMove(col);
                    State nextState = this.alphabeta(new State(col, newGame, 0, depth + 1), depth + 1, alpha, beta, true);

                    int score = nextState.score;
                    if (bestAction == -1 || score <= smallestScore) {
                        if (score == smallestScore) {
                            if (nextState.depth > deepestDepth) {
                                smallestScore = score;
                                bestAction = col;
                                deepestDepth = nextState.depth;
                            }
                        } else {
                            smallestScore = score;
                            bestAction = col;
                        }
                    }

                    beta = Math.min(beta, smallestScore);

                    if (beta <= alpha) {
                        return new State(bestAction, state.game, (int) smallestScore, depth);
                    }
                }
            }

            return new State(bestAction, state.game, (int) smallestScore, depth);

        }
    }
}
