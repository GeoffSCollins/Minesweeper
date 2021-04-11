import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

class Minesweeper {
    private int rows, cols, numMines, flagsPlaced, numPressed;
    private boolean firstPress, isFinished, hasWon;
    private long startTime, endTime;

    private Cell[][] cells = null;

    Minesweeper(int rows, int cols, int numMines, JPanel gameGui) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        this.flagsPlaced = 0;
        this.numPressed = 0;

        this.startTime = 0;
        this.endTime = 0;

        this.firstPress = true;
        this.isFinished = false;
        this.hasWon = false;

        makeGrid(gameGui);
        placeMines();
    }

    int getFlagsPlaced() {
        return flagsPlaced;
    }

    int getNumPressed() {
        return numPressed;
    }

    boolean getIsFinished() {
        return isFinished;
    }

    boolean getHasWon() {
        return hasWon;
    }

    long getStartTime() {
        return startTime;
    }

    long getEndTime() {
        return endTime;
    }

    private void makeGrid(JPanel gameGui) {
        cells = new Cell[rows][cols];

        //Make the grid have the correct number of cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //final ints are needed for the cells array indexing because they are objects
                final int r = i;
                final int c = j;

                //Preferred size makes the game look nice, but it can be resized according to user
                cells[i][j] = new Cell();
                cells[r][c].setPreferredSize(new Dimension(50, 50));

                //Add functionality to each cell depending on the right click vs left click
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        //If the right mouse is clicked, then flag or unflag the cell
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            cells[r][c].changeFlagged();
                            updateFlagsPlaced();
                        }
                        //If left clicked, show the contents of the cell
                        else if (!cells[r][c].isFlagged) {
                            cells[r][c].flaggedNeighbors = updateFlaggedNeighbors(r, c);

                            //If the cell has the correct number flagged, show all of the neighbors
                            if (cells[r][c].isPressed && (cells[r][c].flaggedNeighbors == cells[r][c].neighbors)) {
                                floodFillFlagged(r, c);
                            }

                            cells[r][c].isPressed = true;
                            cells[r][c].resetImage();

                            //If the user clicked a mine, show the board and end the game
                            if (cells[r][c].mine) {
                                showBoard();
                                endTime = System.nanoTime();
                                isFinished = true;
                            }
                            //If the user clicked a cell that has no mines next to it, clear all the cells until they are next to a mine
                            else if (cells[r][c].neighbors == 0) {
                                floodFill(r, c);
                            }
                        }

                        //If they did not win the game
                        if (!isFinished) {
                            updateNumPressed();
                        }
                    }
                });

                gameGui.add(cells[i][j]);
            }
        }
    }

    private void placeMines() {
        //Randomly place mines on the board

        // Make a list of size row * cols as numbers
        java.util.List<Integer> mineList = new ArrayList<>();
        for (int i = 0; i < rows * cols; i++) {
            mineList.add(i);
        }

        // Shuffle the array
        List<Integer> shuffledList = new ArrayList<>();
        while (mineList.size() != 0) {
            int arrayIndex = (int) (Math.random() * (mineList.size()));
            shuffledList.add(mineList.get(arrayIndex));
            mineList.remove(mineList.get(arrayIndex));
        }

        // Now place mines at the first numMines positions
        for (int i = 0; i < numMines; i++) {
            int row = shuffledList.get(i) / cols;
            int col = shuffledList.get(i) % cols;

            cells[row][col].mine = true;
        }

        // Also update the neighbor numbers for each cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].changeNeighbors(getNeighbors(i, j));
                cells[i][j].resetImage();
            }
        }
    }

    //Called once a cell was right clicked. Updates the amount of flags placed on the board to determine how many mines
    //the user thinks they have left.
    private void updateFlagsPlaced() {
        flagsPlaced = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j].isFlagged) {
                    flagsPlaced++;
                }
            }
        }
    }

    //Updates the amount of flags placed next to a cell. Used for the flood fill when left clicked with correct number of mines to flags.
    private int updateFlaggedNeighbors(int row, int col) {
        int flaggedNeighbors = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (row + i >= 0 && col + j >= 0 && row + i <= rows - 1 && col + j <= cols - 1) //If cell is on grid
                {
                    if (cells[row + i][col + j].isFlagged) {
                        flaggedNeighbors++;
                    }
                }
            }
        }

        return flaggedNeighbors;
    }

    //Shows all adjacent cells that are not flagged
    private void floodFillFlagged(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (row + i >= 0 && col + j >= 0 && row + i <= rows - 1 && col + j <= cols - 1) //If cell is on grid
                {
                    if (!cells[row + i][col + j].isFlagged) {
                        cells[row + i][col + j].isPressed = true;
                        cells[row][col].isFloodFilled = true;
                        cells[row + i][col + j].resetImage();
                    }

                    if (cells[row + i][col + j].isPressed && cells[row + i][col + j].mine) {
                        showBoard();
                    }

                    if (cells[row + i][col + j].neighbors == 0 && !cells[row + i][col + j].isFloodFilled) {
                        floodFill(row + i, col + j);
                    }
                }
            }
        }
    }

    //Flood fill algorithm to show all of the cells. The isFloodFilled boolean is needed to prevent an overflow exception.
    private void floodFill(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (row + i >= 0 && col + j >= 0 && row + i <= rows - 1 && col + j <= cols - 1) //If cell is on grid
                {

                    cells[row + i][col + j].isPressed = true;
                    cells[row][col].isFloodFilled = true;
                    cells[row + i][col + j].resetImage();

                    if (cells[row + i][col + j].neighbors == 0 && !cells[row + i][col + j].isFloodFilled) {
                        floodFill(row + i, col + j);
                    }
                }
            }
        }
    }

    //Counts the amount of mines that the current cell is next to.
    private int getNeighbors(int row, int col) {
        int neighbors = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (row + i >= 0 && col + j >= 0 && row + i <= rows - 1 && col + j <= cols - 1) //If cell is on grid
                {
                    if (cells[row + i][col + j].mine) {
                        neighbors++;
                    }
                }
            }
        }

        return neighbors;
    }

    //Called once a cell was left clicked. Counts the amount of cells that are shown to determine if the game is won or not.
    private void updateNumPressed() {
        //Starts collecting time if it is the first cell clicked
        if (firstPress) {
            firstPress = false;
            startTime = System.nanoTime();
        }

        numPressed = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j].isPressed) {
                    numPressed++;
                }
            }
        }

        //If the game is won, update the endTime and set isFinished to true
        if (numPressed == rows * cols - numMines) {
            endTime = System.nanoTime();
            isFinished = true;
            hasWon = true;
        }
    }

    private void showBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].isPressed = true;
                cells[i][j].resetImage();
            }
        }
    }

}