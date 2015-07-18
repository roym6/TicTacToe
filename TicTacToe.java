import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class TicTacToe extends JFrame {
	int movesLeft;                            //moves left in the game
	boolean gameNotOver;                      //true as long as game is not over
	boolean onePlayer;                        //true if single player mode
	String currPlayer, winPlayer, compPlayer; //keep track of winning, current, and computer players
	JButton modeOne;                          //button to choose single player mode
	JButton modeTwo;                          //button to choose two player mode
	JButton[][] buttons;                      //array of board buttons
	JLabel status;                            //displays status of current game
	JPanel board;                             //holds the buttons
	JPanel mode;                              //holds mode options
	
	public TicTacToe() {
		status = new JLabel("Choose a mode!", SwingConstants.CENTER);
		modeOne = new JButton("One Player");
		modeTwo = new JButton("Two Players");
		mode = new JPanel(new FlowLayout());
		mode.add(modeOne);
		mode.add(modeTwo);
		modeOne.addActionListener(new ModeListener());
		modeTwo.addActionListener(new ModeListener());
		setLayout(new BorderLayout(0, 0));
		add(mode, BorderLayout.CENTER);
		add(status, BorderLayout.NORTH);
		setSize(400,400);
		setTitle("TicTacToe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Resets the board and game.
	 */
	private void initialize() {
		currPlayer = "X";
		winPlayer = "";
		movesLeft = 9;
		gameNotOver = true;
		status.setText("X's move");
		for(int x = 0; x < 3; ++x){
			for(int y = 0; y < 3; ++y){
				buttons[y][x].setText("");
			}
		}
	}
	
	/**
	 * Checks to make sure the box is available to be used.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return true if move is possible, false if not
	 */
	private boolean isMoveAllowed(int x, int y) {
		if(buttons[y][x].getText() == ""){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Marks the box as the current players, decreases movesLeft
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private void makeMove(int x, int y) {
		buttons[y][x].setText(currPlayer);
		movesLeft--;
	}
	
	/**
	 * Changes the current player
	 */
	private void changePlayer() {
		if(currPlayer == "X"){
			currPlayer = "O";
			status.setText("O's move");
		} else {
			currPlayer = "X";
			status.setText("X's move");
		}
	}
	
	/**
	 * Checks if the game is over, sets the gameNotOver boolean
	 * @return true if game is over, otherwise false
	 */
	private boolean isGameOver() {
		if(playerWon() || noMovesLeft()){
			gameNotOver = false;
			return true;
		} else {
			gameNotOver = true;
			return false;
		}
	}
	
	/**
	 * Checks if a player has won.
	 * @return
	 */
	private boolean playerWon() {
		for(int x = 0; x < 3; ++x) {
			if(buttons[0][x].getText() == currPlayer &&
					buttons[1][x].getText() == currPlayer &&
					buttons[2][x].getText() == currPlayer) {
				winPlayer = currPlayer;
				return true;
			}
		}
		
		for(int y = 0; y < 3; ++y) {
			if(buttons[y][0].getText() == currPlayer &&
					buttons[y][1].getText() == currPlayer &&
					buttons[y][2].getText() == currPlayer) {
				winPlayer = currPlayer;
				return true;
			}
		}
		
		if(buttons[0][0].getText() == currPlayer &&
				buttons[1][1].getText() == currPlayer &&
				buttons[2][2].getText() == currPlayer) {
			winPlayer = currPlayer;
			return true;
		} else if (buttons[2][0].getText() == currPlayer &&
				buttons[1][1].getText() == currPlayer &&
				buttons[0][2].getText() == currPlayer) {
			winPlayer = currPlayer;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean noMovesLeft() {
		for(int x = 0; x < 3; ++x){
			for(int y = 0; y < 3; ++y){
				if(buttons[y][x].getText() == ""){
					return false;
				}
			}
		}
		return true;
	}
	
	private int[] getMove() {
		int[] result = minimax(2, compPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return new int[] {result[1], result[2]};
	}
	
	private int[] minimax(int depth, String player, int alpha, int beta) {
		ArrayList<int[]> moves = generateMoves();
		
		int currScore;
		int bestRow = -1;
		int bestCol = -1;
		String opponent = (player == compPlayer) ? "X" : compPlayer;
		
		if (moves.isEmpty() || depth == 0) {
			currScore = scoreBoard();
			return new int[] {currScore, bestRow, bestCol};
		} else {
			for(int[] move : moves) {
				buttons[move[0]][move[1]].setText(player);
	            if (player == compPlayer) {  // mySeed (computer) is maximizing player
	               currScore = minimax(depth - 1, opponent, alpha, beta)[0];
	               if (currScore > alpha) {
	                  alpha = currScore;
	                  bestRow = move[0];
	                  bestCol = move[1];
	               }
	            } else {  // oppSeed is minimizing player
	               currScore = minimax(depth - 1, opponent, alpha, beta)[0];
	               if (currScore < beta) {
	                  beta = currScore;
	                  bestRow = move[0];
	                  bestCol = move[1];
	               }
	            }
	            // Undo move
	            buttons[move[0]][move[1]].setText("");
	            if(alpha >= beta) break;
			}
		}
		System.out.println("end of ai move");
		return new int[] {(player == compPlayer) ? alpha : beta, bestRow, bestCol};
	}
	
	private ArrayList<int[]> generateMoves() {
		ArrayList<int[]> moveList = new ArrayList<int[]>();
		if (isGameOver()){
			System.out.println("game is over in generate moves");
			return moveList;
		}
		for(int x = 0; x < 3; ++x){
			for(int y = 0; y < 3; ++y){
				if(buttons[y][x].getText() == "") {
					moveList.add(new int[] {y, x});
					System.out.println("available moves" + x + y);
				}
			}
		}
		return moveList;
	}
	
	private int scoreBoard() {
		int score = 0;

        //Check all rows
        for (int y = 0; y < 3; ++y) {
            int X = 0;
            int O = 0;
            for (int x = 0; x < 3; ++x) {
                if (buttons[y][x].getText() == "O") {
                	O++;
                } else if (buttons[y][x].getText() == "X") {
                    X++;
                }
            } 
            score+=changeInScore(X, O); 
        }

        //Check all columns
        for (int x = 0; x < 3; ++x) {
            int X = 0;
            int O = 0;
            for (int y = 0; y < 3; ++y) {
                if (buttons[y][x].getText() == "") {
                	O++;
                } else if (buttons[y][x].getText() == "X") {
                    X++;
                }
            }
            score+=changeInScore(X, O);
        }

        int X = 0;
        int O = 0;

        //Check diagonal (first)
        for (int y = 0, x = 0; y < 3; ++y, ++x) {
            if (buttons[y][x].getText() == "X") {
                X++;
            } else if (buttons[y][x].getText() == "O") {
                O++;
            }
        }

        score+=changeInScore(X, O);

        X = 0;
        O = 0;

        //Check Diagonal (Second)
        for (int y = 2, x = 0; y > -1; --y, ++x) {
            if (buttons[y][x].getText() == "X") {
                X++;
            } else if (buttons[y][x].getText() == "O") {
                O++;
            }
        }

        score+=changeInScore(X, O);
        return score;
	}
	
	private int changeInScore(int X, int O){
        int change;
        if (O == 3) {
            change = 100;
        } else if (O == 2 && X == 0) {
            change = 10;
        } else if (O == 1 && X == 0) {
            change = 1;
        } else if (X == 3) {
            change = -100;
        } else if (X == 2 && O == 0) {
            change = -10;
        } else if (X == 1 && O == 0) {
            change = -1;
        } else {
            change = 0;
        } 
        return change;
    }
	
	private void checkAndMakeMove(int currX, int currY) {
		if(!isMoveAllowed(currX, currY) && gameNotOver) {
			status.setText("Invalid Move");
		} else if(gameNotOver){
			makeMove(currX, currY);
			if(isGameOver()){
				if(winPlayer != ""){
					status.setText("Player " + winPlayer + " wins!");
				} else {
					status.setText("No winner!");
				}
			} else {
				changePlayer();
			}
		}
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		TicTacToe tttBoard = new TicTacToe();
	}
	
	class TicTacToeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int currX = 0;
			int currY = 0;
			for(int x = 0; x < 3; ++x){
				for(int y = 0; y < 3; ++y){
					if(e.getSource() == buttons[y][x]){
						currX = x;
						currY = y;
					}
				}
			}
			
			checkAndMakeMove(currX, currY);
			revalidate();
			if(onePlayer == true && gameNotOver) {
				int[] compMove = getMove();
				currX = compMove[1];
				currY = compMove[0];
				System.out.println("making move: "+currY+currX);
				gameNotOver = true;
				checkAndMakeMove(currX, currY);
			}
		}
	}
	
	class ModeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			board = new JPanel(new GridLayout(3,3));
			buttons = new JButton[3][3];
			for(int x = 0; x < 3; ++x){
				for(int y = 0; y < 3; ++y){
					buttons[y][x] = new JButton();
					buttons[y][x].addActionListener(new TicTacToeListener());
					board.add(buttons[y][x]);
				}
			}
			remove(mode);
			add(board, BorderLayout.CENTER);
			add(new JButton(new AbstractAction("New Game") {
			    public void actionPerformed(ActionEvent e) {
			    	initialize();
			    }
			}), BorderLayout.SOUTH);
			initialize();
			if(e.getSource() == modeOne) {
				onePlayer = true;
				compPlayer = "O";
			} else {
				onePlayer = false;
			}
		}
	}
}