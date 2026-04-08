package Lecture.Project_1;

public class GameManager {
    //variables
    private ChessBoard board;
    private UserInput input;
    //constructor with instances for ChessBoard and UserInput
    public GameManager() {
        board = new ChessBoard();
        input = new UserInput();
    }
    //method to run the game
    public void runGame() {
        System.out.println("GameManager: Initializing engine...");
        while (true) {
            board.showBoard();
            System.out.println("GameManager: Ready for Rook's move.");
            int[] target = input.getPlayerPosition();
            if (target == null) {
                System.out.println("Exiting Game");
                break;
            }
            if (board.isValidMove(target[0], target[1])) {
                board.playerMove(target[0], target[1]);
                System.out.println("Rook has been moved!");
            } else {
                System.out.println("Illegal move, please try again");
            }
        }
        input.close();

    }
    //run game
    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.runGame();
    }
}