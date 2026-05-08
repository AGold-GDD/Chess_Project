package Lecture.Project_1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChessBoard {

    public char[][] board;
    public List<String> moveHistory;
    public GameStatus gameStatus;
    
  
    public enum GameStatus { ACTIVE, WHITE_WINS, BLACK_WINS, DRAW }
    
   
    private int whiteRookRow, whiteRookCol, blackRookRow, blackRookCol;

    public ChessBoard() {
        board = new char[8][8];
        moveHistory = new ArrayList<>();
        gameStatus = GameStatus.ACTIVE;
        initializeFullChessBoard();
        whiteRookRow = 7; whiteRookCol = 0;
        blackRookRow = 0; blackRookCol = 0;
    }

   
    private void initializeFullChessBoard() {
        initializeEmptyBoard();
        
        // BLACK PIECES
        board[0][0] = 'r'; board[0][1] = 'n'; board[0][2] = 'b';
        board[0][3] = 'q'; board[0][4] = 'k'; board[0][5] = 'b';
        board[0][6] = 'n'; board[0][7] = 'r';
        
        // BLACK PAWNS
        for (int i = 0; i < 8; i++) {
            board[1][i] = 'p';
        }
        
        // WHITE PAWNS 
        for (int i = 0; i < 8; i++) {
            board[6][i] = 'P';
        }
        
        // WHITE PIECES
        board[7][0] = 'R'; board[7][1] = 'N'; board[7][2] = 'B';
        board[7][3] = 'Q'; board[7][4] = 'K'; board[7][5] = 'B';
        board[7][6] = 'N'; board[7][7] = 'R';
    }

    
    private void initializeEmptyBoard() { 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ' ';
            }
        }
    }

   
    public void showBoard() {
    System.out.println("\n--- CHESS BOARD ---");
    System.out.println("  * A B C D E F G H * ");
    System.out.println("----------------------");
    for (int i = 0; i < 8; i++) {
        System.out.print((8 - i) + "| ");
        for (int j = 0; j < 8; j++) {
            char square = board[i][j];
            if (square == ' ') {
                square = (i + j) % 2 == 0 ? '_' : '#';
            }
            System.out.print(" " + square);
        }
        System.out.println(" |" + (8 - i));
    }
    System.out.println("----------------------");
    System.out.println("  * A B C D E F G H * ");
    
    // LEGEND
    System.out.println("\n PIECE LEGEND:");
    System.out.println(" K=King  Q=Queen  R=Rook  B=Bishop N=Knight  P=Pawn");
    System.out.println(" k= AltKing  q=AltQueen  r=AltRook  b=AltBishop  n=AltKnight  p=AltPawn");
    System.out.println("Status: " + gameStatus + " | Moves: " + moveHistory.size() + " | Turn: " + getCurrentTurn());
}

   
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Bounds checking
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }
        
        char piece = board[fromRow][fromCol];
        if (piece == ' ') return false;
        
       
        boolean isWhitePiece = Character.isUpperCase(piece);
        char targetPiece = board[toRow][toCol];
        if (targetPiece != ' ' && Character.isUpperCase(targetPiece) == isWhitePiece) {
            return false;
        }
        
      //checks valid moves
        return switch(Character.toLowerCase(piece)) {
            case 'p' -> isValidPawnMove(fromRow, fromCol, toRow, toCol, isWhitePiece);
            case 'r' -> isValidRookMove(fromRow, fromCol, toRow, toCol);
            case 'n' -> isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case 'b' -> isValidBishopMove(fromRow, fromCol, toRow, toCol);
            case 'q' -> isValidQueenMove(fromRow, fromCol, toRow, toCol);
            case 'k' -> isValidKingMove(fromRow, fromCol, toRow, toCol);
            default -> false;
        };
    }
//checks if position is valid
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
//check valid pawn moves
    private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        
   
        if (fromCol == toCol) {
            if (toRow == fromRow + direction && board[toRow][toCol] == ' ') {
                return true;
            }
            if (fromRow == startRow && toRow == fromRow + 2 * direction && 
                board[fromRow + direction][toCol] == ' ' && board[toRow][toCol] == ' ') {
                return true;
            }
        }
        // Diagonal capture
        else if (Math.abs(toCol - fromCol) == 1 && toRow == fromRow + direction) {
            return board[toRow][toCol] != ' ';
        }
        return false;
    }
//check valid rook moves
    private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) return false;
        return isPathClear(fromRow, fromCol, toRow, toCol);
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);
        int row = fromRow + rowStep;
        int col = fromCol + colStep;
        
        while (row != toRow || col != toCol) {
            if (board[row][col] != ' ') return false;
            row += rowStep;
            col += colStep;
        }
        return true;
    }
//check valid knight moves
    private boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
//check valid bishop moves
    private boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) return false;
        return isPathClear(fromRow, fromCol, toRow, toCol);
    }
//check valid queen moves
    private boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        return isValidRookMove(fromRow, fromCol, toRow, toCol) || 
               isValidBishopMove(fromRow, fromCol, toRow, toCol);
    }
//check valid king moves
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(toRow - fromRow) <= 1 && Math.abs(toCol - fromCol) <= 1;
    }

    // Records history of player moves
    public void playerMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isValidMove(fromRow, fromCol, toRow, toCol)) {
            // Record move in algebraic notation
            String moveNotation = String.format("%c%d-%c%d", 
                (char)('A' + fromCol), 8-fromRow,
                (char)('A' + toCol), 8-toRow);
            moveHistory.add(moveNotation);
            
            // Execute move
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = ' ';
            
            // Check win condition
            checkWinCondition();
        }
    }
//Checks win conditions
    private void checkWinCondition() {
        if (!hasKing(true)) {  // White king removed
            gameStatus = GameStatus.BLACK_WINS;
        } else if (!hasKing(false)) {  // Black king removed
            gameStatus = GameStatus.WHITE_WINS;
        }
    }
//Checks if player has king
    private boolean hasKing(boolean isWhite) {
        char king = isWhite ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == king) return true;
            }
        }
        return false;
    }

    public PieceColor getCurrentTurn() {
        return moveHistory.size() % 2 == 0 ? PieceColor.WHITE : PieceColor.BLACK;
    }

    // Save, Load, and Replay functions
    public void saveGame(String filename) {
        try {
            GameState.saveGame(this, filename);
        } catch (IOException e) {
            System.out.println(" Save failed: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        try {
            GameState.loadGame(this, filename);
        } catch (Exception e) {
            System.out.println(" Load failed: " + e.getMessage());
        }
    }

    public void replayGame() {
        if (moveHistory.isEmpty()) {
            System.out.println("No moves to replay!");
            return;
        }
        
        System.out.println(" REPLAYING " + moveHistory.size() + " MOVES (1s each):");
        ChessBoard replayBoard = new ChessBoard();
        replayBoard.initializeEmptyBoard();
        
        for (int i = 0; i < moveHistory.size(); i++) {
            try {
                Thread.sleep(1000);
                System.out.println("Move " + (i+1) + ": " + moveHistory.get(i));
                replayBoard.showBoard();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public GameStatus getGameStatus() { return gameStatus; }
}