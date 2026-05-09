package Lecture.Project_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
    //JFrame board
public class ChessGUI extends JFrame {
    private ChessBoard board;
    JPanel boardPanel;
    private DraggableChessPiece[][] chessPieces;
    private JPanel capturedPanel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JButton saveButton, loadButton, replayButton;
    
    public List<Character> whiteCaptured = new ArrayList<>();
    public List<Character> blackCaptured = new ArrayList<>();
        //board square colors
    private static final Color WHITE_SQUARE = Color.WHITE;
    private static final Color BLACK_SQUARE = Color.BLACK;
    private static final Color SELECTED_COLOR = new Color(0, 255, 0, 120);
    private static final Color LEGAL_COLOR = new Color(0, 255, 0, 80);
    private static final int TILE_SIZE = 80;
        //new GUI for board
    public ChessGUI() {
        board = new ChessBoard();
        initializeGUI();
        updateCapturedPieces();
        updateStatus();
    }

    private void initializeGUI() {
        setTitle("Chess Game - Drag & Drop Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 800);

        // Window (JPanel) with board and sidebar
        JPanel mainPanel = new JPanel(new BorderLayout());
        
       
        boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        // right sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        createMenuBar();
    }

private JPanel createBoardPanel() {
   
    boardPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintChessBoard(g);
        }
    };
    
    boardPanel.setPreferredSize(new Dimension(640, 640));
    boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    boardPanel.setLayout(null);  
    
    // Create draggable pieces
    createDraggablePieces();
    
    return boardPanel;
}

private void paintChessBoard(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    int tileSize = 80;
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            // Black & white checkerboard
            g2d.setColor((row + col) % 2 == 0 ? WHITE_SQUARE : BLACK_SQUARE);
            g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            
            // Grid lines for checkerboard
            g2d.setColor(Color.GRAY.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
        }
    }
    
   
    g2d.setColor(Color.GRAY);
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    for (int i = 0; i < 8; i++) {
        g2d.drawString(String.valueOf(8 - i), 4, (i * tileSize) + 68);
        g2d.drawString(String.valueOf((char)('A' + i)), (i * tileSize) + 56, 660);
    }
}

private void createDraggablePieces() {
    chessPieces = new DraggableChessPiece[8][8];
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            if (board.board[row][col] != ' ') {
                chessPieces[row][col] = new DraggableChessPiece(board, this, row, col);
                boardPanel.add(chessPieces[row][col]);
            }
        }
    }
    boardPanel.revalidate();
    boardPanel.repaint();
}
        //sidebar with buttons
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 640));
        sidebar.setBorder(BorderFactory.createTitledBorder("Game Info"));
        
        statusLabel = new JLabel("Status: Active", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel = new JLabel("Turn: White", JLabel.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        saveButton = new JButton(" Save Game");
        loadButton = new JButton(" Load Game");
        replayButton = new JButton(" Replay");
        
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        replayButton.addActionListener(e -> replayGame());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(replayButton);
        
        capturedPanel = new JPanel(new BorderLayout());
        updateCapturedPieces();
        
        sidebar.add(statusLabel, BorderLayout.NORTH);
        sidebar.add(turnLabel, BorderLayout.SOUTH);
        sidebar.add(buttonPanel, BorderLayout.CENTER);
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
        //captured pieces
    public void updateCapturedPieces() {
        capturedPanel.removeAll();
        
        JPanel whitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        whitePanel.setBorder(BorderFactory.createTitledBorder("White Captured (" + whiteCaptured.size() + ")"));
        whitePanel.setBackground(Color.WHITE);
        
        JPanel blackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        blackPanel.setBorder(BorderFactory.createTitledBorder("Black Captured (" + blackCaptured.size() + ")"));
        blackPanel.setBackground(Color.BLACK);
        blackPanel.setForeground(Color.WHITE);
        
        for (char piece : whiteCaptured) {
            JLabel label = new JLabel(String.valueOf(piece), JLabel.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 24));
            label.setPreferredSize(new Dimension(30, 30));
            whitePanel.add(label);
        }
        
        for (char piece : blackCaptured) {
            JLabel label = new JLabel(String.valueOf(piece), JLabel.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 24));
            label.setPreferredSize(new Dimension(30, 30));
            blackPanel.add(label);
        }
        
        capturedPanel.add(whitePanel, BorderLayout.NORTH);
        capturedPanel.add(blackPanel, BorderLayout.SOUTH);
        capturedPanel.revalidate();
        capturedPanel.repaint();
    }
        //updated status
    public void updateStatus() {
        statusLabel.setText("Status: " + board.gameStatus);
        turnLabel.setText("Turn: " + board.getCurrentTurn() + " | Moves: " + board.moveHistory.size());
        
        if (board.gameStatus != ChessBoard.GameStatus.ACTIVE) {
            JOptionPane.showMessageDialog(this, 
                "Game Over! " + board.gameStatus + "!", 
                "Checkmate!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    //board repaint
public void repaintBoard() {
    boardPanel.repaint();
}
    //get boaard panels
public JPanel getBoardPanel() {
    return boardPanel;
}
        //save game option
    private void saveGame() {
        String filename = JOptionPane.showInputDialog(this, "Save as:", "chessgame.dat");
        if (filename != null && !filename.trim().isEmpty()) {
            board.saveGame(filename.trim());
            JOptionPane.showMessageDialog(this, " Game saved!");
        }
    }
        //load game option
    private void loadGame() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                board.loadGame(chooser.getSelectedFile().getName());
                resetBoard();
                JOptionPane.showMessageDialog(this, " Game loaded!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, " Load failed: " + e.getMessage());
            }
        }
    }
        //replay game option
    private void replayGame() {
        StringBuilder replay = new StringBuilder("Move History:\n\n");
        for (int i = 0; i < board.moveHistory.size(); i++) {
            replay.append((i + 1)).append(": ").append(board.moveHistory.get(i)).append("\n");
        }
        JOptionPane.showMessageDialog(this, replay.toString(), "Replay", JOptionPane.PLAIN_MESSAGE);
    }
        //start new game option
    private void newGame() {
        int confirm = JOptionPane.showConfirmDialog(this, "Start new game?", "New Game", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            board = new ChessBoard();
            whiteCaptured.clear();
            blackCaptured.clear();
            resetBoard();
            updateStatus();
            updateCapturedPieces();
        }
    }
    //reset board
private void resetBoard() {
    boardPanel.removeAll();
    createDraggablePieces();  
    boardPanel.revalidate();
    boardPanel.repaint();
}
    //main start game method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ChessGUI().setVisible(true);
        });
    }
}