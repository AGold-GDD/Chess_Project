package Lecture.Project_1;

public class ChessBoard {
   private char[][] board;
   private int rookRow, rookCol;
    //constructor for chess board
    public ChessBoard() {
        board = new char[8][8];
        initializeEmptyBoard();
        rookRow = 7;
        rookCol = 0;
       board[rookRow][rookCol] = 'R';
    }
//initialize an empty board
 private void initializeEmptyBoard() { 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ' ';
            }
        }
    }
//method to print board
public void showBoard() {
	    System.out.println("\n--- Current Chess Board ---");
        System.out.println("_ A B C D E F G H ");
        for (int i = 0; i < 8; i ++) {
            System.out.print((8 - i) + " ");
                        for (int j = 0; j < 8; j++) {
                char square = board[i][j];
                if (square == ' ') {
                    square = (i + j) % 2 == 0 ? '_' : '#';
                }
                System.out.print(square + " ");
            }
            System.out.println((8 - i));  
        }
         System.out.println("_ A B C D E F G H ");
    }

    //boolean to determine what moves are valid
    public boolean isValidMove(int targetRow, int targetCol) { 
        if (targetRow == rookRow && targetCol == rookCol) {
            return false;
        }
        if (targetRow != rookRow && targetCol != rookCol) {
            return false;
        } 
        return true;
    }
    //method to determine what move the player has made
    public void playerMove (int targetRow, int targetCol) {
        board[rookRow][rookCol] = ' ';
        rookRow = targetRow;
        rookCol = targetCol;
        board[rookRow][rookCol] = 'R';
    }
}