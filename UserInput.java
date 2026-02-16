package Lecture.Project_1;

import java.util.Scanner;

public class UserInput {
    private Scanner scanner;
//New constructor and scanner
    public UserInput() {
        scanner = new Scanner(System.in);
    }
    //get player input to move rook chess piece
    public int[] getPlayerPosition() {
        while (true) {
            System.out.println("ChessBoard.java");
            System.out.print("Enter move (e.g., E8) or 'exit': ");

            String input = scanner.nextLine().toUpperCase();

            if (input.equals("EXIT")) {
                return null;
            } else if (isValidFormat(input)) {
                System.out.println("Moving piece to: " + input);
            int targetRow = 8 - (input.charAt(1) - '0');
            int targetCol = input.charAt(0) - 'A';
            return new int[]{targetRow, targetCol};
            } else {
                System.out.println("Invalid coordinate. Try again.");
            }
        }
    }
    //boolean to determine proper format
    private static boolean isValidFormat(String s) {
        if (s.length() != 2)
            return false;
        char col = s.charAt(0);
        char row = s.charAt(1);
        return (col >= 'A' && col <= 'H') && (row >= '1' && row <= '8');
    }
    //close scanner
    public void close() {
        scanner.close();
    }
}
