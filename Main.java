import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JButton;

/*
 *  Upon click, make it a 0
 */

public class Main extends JFrame{
	
	private static final long serialVersionUID = 1L;
	JPanel JPan = new JPanel(new BorderLayout());
	Container pane;

	public Main()
	{
		super("Minesweeper_GC");
		setSize(500, 500);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		//Make button to start the game
		JButton game = new JButton("Start Minesweeper");
		game.setPreferredSize(new Dimension(500,230));
		JPan.add(game, BorderLayout.PAGE_START);
		
		//Make button to show statistics
		JButton stats = new JButton("Look at statistics");
		stats.setPreferredSize(new Dimension(500,230));
		JPan.add(stats, BorderLayout.PAGE_END);
		
		//If click start button, make a game with the desired rows, columns, and mines
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
	    	       do
	    	       {
	    	    	   int result = JOptionPane.showConfirmDialog(null, myPanel, "Please the following values", JOptionPane.OK_CANCEL_OPTION);
		    	       if (result == JOptionPane.OK_OPTION) 
		    	       {
		    	    	   try
		    	    	   {
		    	    		   int rows = Integer.parseInt(rowsField.getText());
		    	    		   int cols = Integer.parseInt(colsField.getText());
		    	    		   int mines = Integer.parseInt(mineField.getText());
		        	    	  
		    	    		   if(rows > 0 && cols > 0 && rows <= 16 && cols <= 30 && mines < (rows*cols))
		    	    		   {
		    	    			   new Minesweeper(rows, cols, mines);
		    	    			   finished = true;
		    	    		   }
		    	    	   }
		    	    	   catch(NumberFormatException numError)
		    	    	   {
		    	    		   System.out.println(numError.getMessage());
		    	    	   }
		    	       }	    	    	   
	    	       } while(!finished);
               	}
		});
		
		//If click statistics, make a new window displaying statistics
		stats.addMouseListener(new MouseAdapter() {
               @Override
               public void mousePressed(MouseEvent e) {
            	   readData();
               }
		});
		
		setLocationRelativeTo(null);
		add(JPan);
		setVisible(true);
	}
	
	public static void main(String args[])
	{	
		new Main();
	}
	
	public static void readData()
	{

		File file = new File("Scores.txt");
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> winLoss = new ArrayList<String>();
		ArrayList<Long> times = new ArrayList<Long>();
		
		long bestTime = 0;
		int bestTimePos = 0;
		double numWins = 0; //Double because of the calculation for win percentage
			
		try
		{
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine())
			{
				names.add(scanner.next());
				winLoss.add(scanner.next());
				times.add(scanner.nextLong());
			}
			
			scanner.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("There was an error: " + e.getMessage());
		}
		
		if(names.size() > 0)
		{
			bestTime = times.get(0);
			
			for(int i = 0; i < winLoss.size(); i++)
			{
				if(winLoss.get(i).equals("W"))
				{
					numWins++;
				}
			}
			
			for(int i = 0; i < times.size(); i++)
			{
				if(times.get(i) < bestTime && winLoss.get(i) == "W")
				{
					bestTime = times.get(i);
					bestTimePos = i;
				}
			}
			
			JOptionPane.showMessageDialog(null, "The fastest time was achieved by: " + names.get(bestTimePos) + " in " + (bestTime/1000000000) + " seconds.\n" + "The win rate of minesweeper is " + ((numWins / winLoss.size())*100) + "%");
		}
		else
		{
			JOptionPane.showMessageDialog(null, "There is no data.");
		}
	}
}
