package Lecture.Project_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChessGUI extends JFrame {
    private ChessBoard board;
    private JPanel boardPanel;
    private JButton[][] squares;
    private JPanel capturedPanel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JButton saveButton, loadButton, replayButton;
    
    private int selectedRow = -1, selectedCol = -1;
    private List<Character> whiteCaptured = new ArrayList<>();
    private List<Character> blackCaptured = new ArrayList<>();
    
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(0, 255, 0, 100);
    private static final Color ILLEGAL_COLOR = new Color(255, 0, 0, 100);
    private static final Color LEGAL_COLOR = new Color(0, 255, 0, 50);

    public ChessGUI() {
        board = new ChessBoard();
        initializeGUI();
        updateCapturedPieces();
        updateStatus();
    }

    private void initializeGUI() {
        setTitle("Chess Game - GUI Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 800);

        // Main panel with board and sidebar
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Chess Board
        boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Right sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Menu bar
        createMenuBar();
    }
    //new JPanel
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 8, 1, 1));
        panel.setPreferredSize(new Dimension(640, 640));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        squares = new JButton[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = createSquare(row, col);
                squares[row][col] = square;
                panel.add(square);
            }
        }
        return panel;
    }
    //create square and Jbutton
    private JButton createSquare(int row, int col) {
        JButton square = new JButton();
        square.setFocusable(false);
        square.setFont(new Font("Serif", Font.BOLD, 32));
        square.setContentAreaFilled(false);
        square.setBorderPainted(false);
        
        // Set initial square color
        boolean isLight = (row + col) % 2 == 0;
        square.setBackground(isLight ? LIGHT_SQUARE : DARK_SQUARE);
        
        square.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleSquareClick(row, col);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedRow >= 0 && selectedCol >= 0) {
                    highlightLegalMoves();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                clearHighlights();
                repaintBoard();
            }
        });
        
        return square;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 640));
        sidebar.setBorder(BorderFactory.createTitledBorder("Game Info"));
        
        // Status labels
        statusLabel = new JLabel("Status: Active", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel = new JLabel("Turn: White", JLabel.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Save, Load, and Replay Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        saveButton = new JButton("Save Game");
        loadButton = new JButton("Load Game");
        replayButton = new JButton("Replay Game");
        
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        replayButton.addActionListener(e -> replayGame());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(replayButton);
        
        // captured pieces shown on side
        capturedPanel = new JPanel(new BorderLayout());
        updateCapturedPieces();
        
        sidebar.add(statusLabel, BorderLayout.NORTH);
        sidebar.add(turnLabel, BorderLayout.CENTER);
        sidebar.add(buttonPanel, BorderLayout.SOUTH);
        sidebar.add(capturedPanel, BorderLayout.WEST);
        
        return sidebar;
    }
    //menu bar
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newGameItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }

    private void handleSquareClick(int row, int col) {
        if (selectedRow == -1) {
            // select piece to move
            if (board.board[row][col] != ' ') {
                PieceColor currentTurn = board.getCurrentTurn();
                boolean isWhiteTurn = currentTurn == PieceColor.WHITE;
                boolean isCorrectColor = (Character.isUpperCase(board.board[row][col]) == isWhiteTurn);
                
                if (isCorrectColor) {
                    selectedRow = row;
                    selectedCol = col;
                    repaintBoard();
                }
            }
        } else {
            // try to move piece
            if (board.isValidMove(selectedRow, selectedCol, row, col)) {
                // Valid move - execute it
                char capturedPiece = board.board[row][col];
                if (capturedPiece != ' ') {
                    if (Character.isUpperCase(capturedPiece)) {
                        whiteCaptured.add(capturedPiece);
                    } else {
                        blackCaptured.add(capturedPiece);
                    }
                }
                
                board.playerMove(selectedRow, selectedCol, row, col);
                clearHighlights();
                selectedRow = -1;
                selectedCol = -1;
                repaintBoard();
                updateStatus();
                updateCapturedPieces();
                
                if (board.gameStatus != ChessBoard.GameStatus.ACTIVE) {
                    JOptionPane.showMessageDialog(this, 
                        "Game Over! " + board.gameStatus + "!", 
                        "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // move is illegal, try a new move
                if (board.board[row][col] != ' ' && 
                    Character.isUpperCase(board.board[row][col]) == 
                    (board.getCurrentTurn() == PieceColor.WHITE)) {
                    selectedRow = row;
                    selectedCol = col;
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
                repaintBoard();
            }
        }
    }

    private void repaintBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = squares[row][col];
                char piece = board.board[row][col];
                
                // base square color
                boolean isLight = (row + col) % 2 == 0;
                Color baseColor = isLight ? LIGHT_SQUARE : DARK_SQUARE;
                
                // Piece display
                if (piece == ' ') {
                    square.setText("");
                } else {
                    square.setText(String.valueOf(piece));
                }
                
                // Highlights
                if (row == selectedRow && col == selectedCol) {
                    square.setBackground(SELECTED_COLOR);
                } else {
                    square.setBackground(baseColor);
                }
                
                square.repaint();
            }
        }
    }

    private void highlightLegalMoves() {
        clearHighlights();
        repaintBoard();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board.isValidMove(selectedRow, selectedCol, row, col)) {
                    squares[row][col].setBackground(LEGAL_COLOR);
                }
            }
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = squares[row][col];
                boolean isLight = (row + col) % 2 == 0;
                square.setBackground(isLight ? LIGHT_SQUARE : DARK_SQUARE);
            }
        }
    }

    private void updateCapturedPieces() {
        capturedPanel.removeAll();
        
        JPanel whitePanel = new JPanel();
        whitePanel.setBorder(BorderFactory.createTitledBorder("White Captured"));
        whitePanel.setBackground(Color.WHITE);
        
        JPanel blackPanel = new JPanel();
        blackPanel.setBorder(BorderFactory.createTitledBorder("Black Captured"));
        blackPanel.setBackground(Color.BLACK);
        blackPanel.setForeground(Color.WHITE);
        
        for (char piece : whiteCaptured) {
            JLabel label = new JLabel(String.valueOf(piece), JLabel.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 20));
            whitePanel.add(label);
        }
        
        for (char piece : blackCaptured) {
            JLabel label = new JLabel(String.valueOf(piece), JLabel.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 20));
            blackPanel.add(label);
        }
        
        capturedPanel.add(whitePanel, BorderLayout.NORTH);
        capturedPanel.add(blackPanel, BorderLayout.SOUTH);
        capturedPanel.revalidate();
        capturedPanel.repaint();
    }

    private void updateStatus() {
        statusLabel.setText("Status: " + board.gameStatus);
        turnLabel.setText("Turn: " + board.getCurrentTurn());
    }
        //save game
    private void saveGame() {
        String filename = JOptionPane.showInputDialog(this, "Enter save filename:", "chessgame.dat");
        if (filename != null && !filename.trim().isEmpty()) {
            try {
                board.saveGame(filename.trim());
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
            }
        }
    }
        //load game
    private void loadGame() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                board.loadGame(chooser.getSelectedFile().getName());
                whiteCaptured.clear();
                blackCaptured.clear();
                updateCapturedPieces();
                repaintBoard();
                updateStatus();
                JOptionPane.showMessageDialog(this, "Game loaded successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage());
            }
        }
    }
        //replay game
    private void replayGame() {
        SwingUtilities.invokeLater(() -> {
            JFrame replayFrame = new JFrame("Game Replay");
            replayFrame.setSize(800, 600);
            
            JTextArea replayText = new JTextArea();
            replayText.setEditable(false);
            replayText.setFont(new Font("Monospaced", Font.PLAIN, 14));
            
            for (int i = 0; i < board.moveHistory.size(); i++) {
                replayText.append("Move " + (i + 1) + ": " + board.moveHistory.get(i) + "\n");
            }
            
            replayFrame.add(new JScrollPane(replayText));
            replayFrame.setVisible(true);
        });
    }
        //new game
    private void newGame() {
        int confirm = JOptionPane.showConfirmDialog(this, "Start new game?", "New Game", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            board = new ChessBoard();
            whiteCaptured.clear();
            blackCaptured.clear();
            selectedRow = -1;
            selectedCol = -1;
            repaintBoard();
            updateCapturedPieces();
            updateStatus();
        }
    }
        //main method to start the game (with UI)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}
