package Lecture.Project_1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private char[][] board;
    private List<String> moveHistory;
    private ChessBoard.GameStatus status;
    //Chess board starting state
    public GameState(ChessBoard chessBoard) {
        this.board = new char[8][8];
        
        for (int i = 0; i < 8; i++) {
            System.arraycopy(chessBoard.board[i], 0, this.board[i], 0, 8);
        }
        this.moveHistory = new ArrayList<>(chessBoard.moveHistory);
        this.status = chessBoard.gameStatus;
    }
//Save Game
    public static void saveGame(ChessBoard chessBoard, String filename) throws IOException {
        GameState state = new GameState(chessBoard);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(state);
        }
        System.out.println(" Game saved: " + filename);
    }
//Load Game
    public static void loadGame(ChessBoard chessBoard, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            GameState state = (GameState) ois.readObject();
            
            // Restore board
            for (int i = 0; i < 8; i++) {
                System.arraycopy(state.board[i], 0, chessBoard.board[i], 0, 8);
            }
            
            // Restore history and status of Chess Game
            chessBoard.moveHistory.clear();
            chessBoard.moveHistory.addAll(state.moveHistory);
            chessBoard.gameStatus = state.status;
            
            System.out.println(" Game loaded: " + state.moveHistory.size() + " moves restored");
        }
    }
}