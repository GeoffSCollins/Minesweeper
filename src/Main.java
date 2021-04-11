import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JButton;

public class Main extends JFrame {
    Main() {
        super("Minesweeper_GC");
        setSize(500, 500);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Make the panel that holds everything
        JPanel panel = new JPanel(new BorderLayout());

        //Make button to start the game
        panel.add(addGameFunctionality(), BorderLayout.PAGE_START);

        //Make button to show statistics
        panel.add(addStatisticsFunctionality(), BorderLayout.PAGE_END);

        setLocationRelativeTo(null);
        add(panel);
        setVisible(true);
    }

    private JButton addGameFunctionality() {
        //If click start button, make a game with the desired rows, columns, and mines
        JButton game = new JButton("Start Minesweeper");
        game.setPreferredSize(new Dimension(500, 230));

        game.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setVisible(false);

                JTextField rowsField = new JTextField(5);
                rowsField.setText("16");
                JTextField colsField = new JTextField(5);
                colsField.setText("30");
                JTextField mineField = new JTextField(5);
                mineField.setText("99");

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Rows:"));
                myPanel.add(rowsField);
                myPanel.add(Box.createHorizontalStrut(15)); //Makes a space between inputs
                myPanel.add(new JLabel("Cols:"));
                myPanel.add(colsField);
                myPanel.add(Box.createHorizontalStrut(15)); //Make another space between inputs
                myPanel.add(new JLabel("Mines:"));
                myPanel.add(mineField);

                boolean finished = false;
                do {
                    int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter the following values", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            int rows = Integer.parseInt(rowsField.getText());
                            int cols = Integer.parseInt(colsField.getText());
                            int mines = Integer.parseInt(mineField.getText());

                            if (rows > 0 && cols > 0 && rows <= 16 && cols <= 30 && mines < (rows * cols)) {
                                new MinesweeperGui(rows, cols, mines);
                                finished = true;
                            }
                        } catch (NumberFormatException numError) {
                            System.out.println(numError.getMessage());
                        }
                    }
                } while (!finished);
            }
        });

        return game;
    }

    private JButton addStatisticsFunctionality() {
        JButton stats = new JButton("Look at statistics");
        stats.setPreferredSize(new Dimension(500, 230));

        //If click statistics, make a new window displaying statistics
        stats.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                readData();
            }
        });

        return stats;
    }

    public static void main(String[] args) {
        new Main();
    }

    private static void readData() {

        File file = new File("Scores.txt");
        ArrayList<String[]> data = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String name = scanner.next();
                String winOrLoss = scanner.next();
                String time = scanner.next();

                data.add(
                        new String[]{name, winOrLoss, time}
                );
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the scores file.");
        }

        if (data.size() > 0) {

            List<String[]> winData = data.stream()
                    .filter(t -> t[1].equals("W"))
                    .collect(Collectors.toList());

            double winPercentage = ( (double) winData.size() / data.size()) * 100;

            String bestPlayer = winData.get(0)[0];
            int bestTime = Integer.parseInt(winData.get(0)[2]);

            for (int i = 1; i < winData.size(); i++) {
                String currPlayer = winData.get(i)[0];
                int currTime = Integer.parseInt(winData.get(i)[2]);

                if (currTime < bestTime) {
                    bestTime = currTime;
                    bestPlayer = currPlayer;
                }
            }

            JOptionPane.showMessageDialog(null,
                    "The fastest time was achieved by: " + bestPlayer +
                            " in " + bestTime + " seconds.\n" +
                            "The win rate of minesweeper is " + winPercentage + "%"
            );

        } else {
            JOptionPane.showMessageDialog(null, "There is no data.");
        }
    }
}
