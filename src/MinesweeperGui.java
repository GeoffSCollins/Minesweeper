import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class MinesweeperGui extends JFrame {
    private Minesweeper minesweeper;
    private int rows, cols, numMines;

    private static final long serialVersionUID = 1L;
    //Filler text will never show due to updateText() being called in the minesweeper constructor
    private JLabel numsAndMinesText = new JLabel("Filler text", SwingConstants.CENTER);

    private Timer timer = new Timer();

    MinesweeperGui(int rows, int cols, int numMines) {
        super("Minesweeper");
        setSize(cols * 50, rows * 50 + 60);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Add text with bombs and time on top
        JPanel numsAndMinesPanel = new JPanel(new BorderLayout());
        numsAndMinesPanel.add(numsAndMinesText);

        //Add the game to the window
        JPanel gameGui = new JPanel();
        gameGui.setLayout(new GridLayout(rows, cols));
        JPanel complete = new JPanel(new BorderLayout());
        complete.add(numsAndMinesPanel, BorderLayout.PAGE_START);
        complete.add(gameGui, BorderLayout.PAGE_END);
        add(complete);

        setLocationRelativeTo(null);
        setVisible(true);

        // Now finally make the minesweeper game
        this.minesweeper = new Minesweeper(rows, cols, numMines, gameGui);

        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        // Make the timer start counting every 200 ms, but will only update when updateText is called
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        updateMinesandTimeText();
                    }
                },
                0,
                200
        );
    }

    //Changes the number of mines and shows the amount of time the user has been playing since the first click
    private void updateMinesandTimeText() {
        int gameTime = 0;

        if (minesweeper.getStartTime() != 0) {
            gameTime = (int) Math.floorDiv((System.nanoTime() - minesweeper.getStartTime()), 1000000000);
        }

        numsAndMinesText.setText("Mines left " + (this.numMines - minesweeper.getFlagsPlaced()) + "    Time: " + gameTime);

        if (minesweeper.getIsFinished()) {
            endGame();
        }
    }

    //Called if all the cells that are not mines are uncovered. Will get time and ask to play again.
    private void endGame() {
        long completionTime = (minesweeper.getEndTime() - minesweeper.getStartTime()) / 1000000000;
        timer.cancel();

        String resultString = "";
        if (minesweeper.getHasWon()) {
            resultString = "You have won!";
        } else {
            resultString = "You lost.";
        }

        resultString += " It took you " + completionTime + " seconds.";

        // Show the results string in a dialog window
        JOptionPane.showMessageDialog(null, resultString);
        saveScores(minesweeper.getHasWon(), completionTime);

        //Ask to play again and store the yes or no in a integer called playAgain
        int playAgain = JOptionPane.showConfirmDialog(null, "Play again?", "Play again?", JOptionPane.YES_NO_OPTION);

        if (playAgain == 0) {
            // Yes
            new MinesweeperGui(this.rows, this.cols, this.numMines);
            this.setVisible(false);
        } else {
            System.exit(0);
        }
    }

    //Save scores to the text file
    private void saveScores(boolean hasWon, long completionTime) {
        try {
            //Keep on asking name until it does not have a space.
            //Spaces are not wanted as they make reading the data difficult.
            String name;

            do {
                name = JOptionPane.showInputDialog("Enter your name if you want your score saved.\n"); //Don't allow spaces
            } while (name.contains(" "));

            //If there is a name
            if (name.length() > 1) {
                File file = new File("Scores.txt");
                // If the file does not exist, create it. Otherwise, the following line does nothing.
                file.createNewFile();

                FileWriter fw = new FileWriter(file, true);
                if (hasWon) {
                    fw.write("\n" + name + " W " + completionTime);
                    fw.close();
                } else {
                    fw.write("\n" + name + " L " + completionTime);
                    fw.close();
                }
            }
        } catch (Exception e) {
            //Needed for IO exception and null pointer exception
            System.out.println(e.getMessage());
        }
    }

}
