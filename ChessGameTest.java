package Lecture.Project_1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChessGameTest {
    
    private ChessBoard board;

    @BeforeEach
    void setUp() {
        board = new ChessBoard();
    }

    @Test
    void testInitialBoardSetup() {
        // Verify starting positions
        assertEquals('R', board.board[7][0], "White rook A1");
        assertEquals('r', board.board[0][0], "Black rook A8");
        assertEquals('P', board.board[6][0], "White pawn A2");
        assertEquals('p', board.board[1][0], "Black pawn A7");
    }

    @Test
    void testPawnMovement() {
        // White pawn move E2-E4
        assertTrue(board.isValidMove(6, 4, 4, 4), "Pawn E2-E4 should be valid");
        assertFalse(board.isValidMove(6, 4, 5, 4), "Pawn cannot move backward");
        
        // Black pawn move E7-E5
        assertTrue(board.isValidMove(1, 4, 3, 4), "Black pawn E7-E5 should be valid");
    }

        @Test
    void testRookMovement() {
        // Clear path for white rook for move A1-A4
        board.board[5][0] = board.board[4][0] = board.board[3][0] = ' ';
        assertTrue(board.isValidMove(7, 0, 3, 0), "Rook A1-A4 should be valid");
        assertFalse(board.isValidMove(7, 0, 6, 2), "Rook cannot move diagonally");
    }

    @Test
    void testKnightMovement() {
        // Knight move B1-C3
        assertTrue(board.isValidMove(7, 1, 5, 2), "Knight B1-C3 should be valid");
    }

    @Test
    void testWinCondition() {
        // Remove white king which means Black wins
        board.board[7][4] = ' ';  // Remove K (white king)
        board.checkWinCondition();
        assertEquals(ChessBoard.GameStatus.BLACK_WINS, board.gameStatus);
    }


}