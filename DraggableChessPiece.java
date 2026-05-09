package Lecture.Project_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DraggableChessPiece extends JLabel {
    private ChessBoard chessBoard;
    private ChessGUI parentGUI;
    private int fromRow, fromCol;
    private int dragOffsetX, dragOffsetY;
    private Point dragPosition;
    private boolean dragging = false;
    private char currentPiece;

    private static final int TILE_SIZE = 80;
    private static final Font PIECE_FONT = new Font("Serif", Font.BOLD, 44);

    public DraggableChessPiece(ChessBoard board, ChessGUI gui, int row, int col) {
        this.chessBoard = board;
        this.parentGUI = gui;
        this.fromRow = row;
        this.fromCol = col;
        this.currentPiece = board.board[row][col];
        //updated display
        updateDisplay();
        setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(false);

        MouseAdapter dragHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (canDrag()) {
                    startDrag(e);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    updateDrag(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging) {
                    endDrag(e);
                }
            }
        };
        //mouse listener
        addMouseListener(dragHandler);
        addMouseMotionListener(dragHandler);
    }
        //can be dragged
    private boolean canDrag() {
        PieceColor turn = chessBoard.getCurrentTurn();
        boolean isWhiteTurn = turn == PieceColor.WHITE;
        return Character.isUpperCase(currentPiece) == isWhiteTurn;
    }
        //drag start
    private void startDrag(MouseEvent e) {
        dragging = true;
        dragOffsetX = e.getX();
        dragOffsetY = e.getY();
        parentGUI.repaintBoard();
    }
        //drag duration
    private void updateDrag(MouseEvent e) {
        Point panelPoint = SwingUtilities.convertPoint(this, e.getPoint(), parentGUI.getBoardPanel());
        dragPosition = new Point(panelPoint.x - dragOffsetX, panelPoint.y - dragOffsetY);
        setLocation(dragPosition.x, dragPosition.y);
        parentGUI.repaintBoard();
    }
        // drag end
    private void endDrag(MouseEvent e) {
        Point panelPoint = SwingUtilities.convertPoint(this, e.getPoint(), parentGUI.getBoardPanel());
        int targetCol = panelPoint.x / TILE_SIZE;
        int targetRow = panelPoint.y / TILE_SIZE;

        targetRow = Math.max(0, Math.min(7, targetRow));
        targetCol = Math.max(0, Math.min(7, targetCol));

        if (chessBoard.isValidMove(fromRow, fromCol, targetRow, targetCol)) {
            char captured = chessBoard.board[targetRow][targetCol];
            chessBoard.playerMove(fromRow, fromCol, targetRow, targetCol);

            fromRow = targetRow;
            fromCol = targetCol;
            currentPiece = chessBoard.board[fromRow][fromCol];

            if (captured != ' ') {
                if (Character.isUpperCase(captured)) {
                    parentGUI.whiteCaptured.add(captured);
                } else {
                    parentGUI.blackCaptured.add(captured);
                }
            }

            parentGUI.updateCapturedPieces();
            parentGUI.updateStatus();
        }

        dragging = false;
        updateDisplay();
        parentGUI.repaintBoard();
    }
        //updated display after piece is moved
private void updateDisplay() {
    if (currentPiece == ' ') {
        setVisible(false);
        return;
    }
    
    setText(String.valueOf(currentPiece));
    setFont(PIECE_FONT);
    

    boolean isWhitePiece = Character.isUpperCase(currentPiece);
    setForeground(isWhitePiece ? Color.WHITE : Color.BLACK);
    
    setBounds(fromCol * TILE_SIZE, fromRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    setVisible(true);
}
        // painted pieces
   @Override
protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    int size = Math.min(getWidth(), getHeight());
    int x = (getWidth() - size) / 2;
    int y = (getHeight() - size) / 2;
    
  
    g2d.setStroke(new BasicStroke(3));
    g2d.setColor(Color.DARK_GRAY);
    g2d.drawOval(x + 2, y + 2, size - 4, size - 4);
    
    
    g2d.setColor(new Color(255, 255, 255, 50));
    g2d.fillOval(x + 1, y + 1, size - 2, size - 2);
    
   
    String pieceText = getText();
    if (pieceText != null && !pieceText.isEmpty()) {
        FontMetrics fm = g2d.getFontMetrics(PIECE_FONT);
        int textX = x + (size - fm.stringWidth(pieceText)) / 2;
        int textY = y + ((size - fm.getHeight()) / 2 + fm.getAscent());
        
       
        g2d.setFont(PIECE_FONT);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.setColor(Color.DARK_GRAY);
        
      
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        for (String dir : directions) {
            int offsetX = 0, offsetY = 0;
            if (dir.contains("N")) offsetY = -1;
            if (dir.contains("S")) offsetY = 1;
            if (dir.contains("E")) offsetX = 1;
            if (dir.contains("W")) offsetX = -1;
            g2d.drawString(pieceText, textX + offsetX, textY + offsetY);
        }
        
    
        g2d.setColor(getForeground());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString(pieceText, textX, textY);
    }
    
    if (dragging) {
       
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillOval(x + 8, y + 8, size - 16, size - 16);
        
      
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(0, 255, 0, 200));
        g2d.drawOval(x - 2, y - 2, size + 4, size + 4);
    }
    
    g2d.dispose();
}
        //refresh piece layout
    public void refreshPiece() {
        currentPiece = chessBoard.board[fromRow][fromCol];
        updateDisplay();
    }
}