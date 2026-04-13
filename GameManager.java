package Lecture.Project_1;

public class GameManager {
    private ChessBoard board;
    private UserInput input;

    public GameManager() {
        board = new ChessBoard();
        input = new UserInput();
    }
    //Run game method 
    public void runGame() {
        System.out.println("=== CHESS GAME - MOVE ANY PIECE! ===");
        System.out.println(" Format: FROM-TO (e.g., E2-E4, A1-A5, B1-C3)");
        System.out.println(" save mygame.dat | load mygame.dat | replay | exit");
        
        while (true) {
            board.showBoard(); 
            
            if (board.gameStatus != ChessBoard.GameStatus.ACTIVE) {
                System.out.println(" GAME OVER: " + board.gameStatus);
                board.saveGame("final.dat");
                break;
            }

            System.out.print(" Move any piece (e.g., E2-E4): ");
            String command = input.getCommand();
            
            if (command == null || command.equalsIgnoreCase("exit")) {
                break;
            } 
            //Replay Game
            if (command.equalsIgnoreCase("replay")) {
                board.replayGame();
                continue;
            } 
            //Save Game
            if (command.startsWith("save ")) {
                board.saveGame(command.substring(5).trim());
                continue;
            } 
            //Load Game
            if (command.startsWith("load ")) {
                board.loadGame(command.substring(5).trim());
                continue;
            }

            
            String[] parts = command.split("-");
            if (parts.length == 2) {
                int[] from = parsePosition(parts[0].trim().toUpperCase());
                int[] to = parsePosition(parts[1].trim().toUpperCase());
                
                if (from != null && to != null) {
                    char piece = board.board[from[0]][from[1]];
                    if (piece == ' ') {
                        System.out.println(" No piece at " + parts[0]);
                    } else if (board.isValidMove(from[0], from[1], to[0], to[1])) {
                        board.playerMove(from[0], from[1], to[0], to[1]);
                        System.out.println(" " + piece + " moved " + parts[0] + " → " + parts[1]);
                    } else {
                        System.out.println(" Illegal move for " + piece + " from " + parts[0]);
                    }
                } else {
                    System.out.println(" Invalid position format");
                }
            } else {
                System.out.println(" Use: FROM-TO (e.g., E2-E6)");
            }
        }
        input.close();
    }
//parse position
    private int[] parsePosition(String pos) {
        if (pos.length() != 2) return null;
        try {
            char colChar = pos.charAt(0);
            char rowChar = pos.charAt(1);
            
            if (colChar < 'A' || colChar > 'H' || rowChar < '1' || rowChar > '8') {
                return null;
            }
            
            int col = colChar - 'A';
            int row = 8 - (rowChar - '0');
            return new int[]{row, col};
        } catch (Exception e) {
            return null;
        }
    }
// Run Game
    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.runGame();
    }
}