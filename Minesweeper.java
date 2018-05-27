import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Minesweeper extends JFrame{

	private static final long serialVersionUID = 1L;
	int numOfMinesCONST, mineNums, numPressed = 0, rows, cols, flagsPlaced = 0, playAgain, maxRows = 16, maxCols = 30;
	JPanel complete = new JPanel(new BorderLayout());
	JPanel game = new JPanel();
	JPanel numsAndMines = new JPanel(new BorderLayout());
	Random rand = new Random();
	int flagNum[][] = new int[maxRows][maxCols];
	Cell cells[][] = new Cell[maxRows][maxCols];
	boolean isFinished = false, firstPress = true;
	long completionTime = 0, startTime = 0, endTime;
	String name = "";
	//Filler text will never show due to updateText() being called in the minesweeper constructor
	JLabel numsAndMinesText = new JLabel("Filler text", SwingConstants.CENTER);
	int gameTime = 0;
	Timer timer = new Timer();
	
	public Minesweeper(int rows, int cols, int numOfMines)
	{
		super("Minesweeper_GC");
		setSize(cols*50, rows*50+60);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Add text with bombs and time on top
		numsAndMines.add(numsAndMinesText);
		updateText();
		
		//Add the game to the window
		game.setLayout(new GridLayout(rows, cols));
		complete.add(numsAndMines, BorderLayout.PAGE_START);
		complete.add(game, BorderLayout.PAGE_END);
		add(complete);
		
		//Get the arguments from the main class into the size of the board
		this.rows = rows;
		this.cols = cols;
		this.numOfMinesCONST = numOfMines;
		this.mineNums = numOfMines;
		
		//Make the grid have the correct number of cells
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{		
				//final ints are needed for the cells array indexing because they are objects
				final int r = i;
				final int c = j;
				
				//Preferred size makes the game look nice, but it can be resized according to user
				cells[i][j] = new Cell();
				cells[r][c].setPreferredSize(new Dimension(50,50));
				
				//Add functionality to each cell depending on the right click vs left click
				cells[i][j].addMouseListener(new MouseAdapter() {
		               @Override
		               public void mousePressed(MouseEvent e) {
		            	  //If the right mouse is clicked, then flag or unflag the cell
		                  if (e.getButton() == MouseEvent.BUTTON3) {
		                	  flagNum[r][c]++;
		                	  if(flagNum[r][c]%2 == 1 && !cells[r][c].isPressed)
		                	  {
		                		  cells[r][c].isFlagged = true;
		                	  }
		                	  else
		                	  {
		                		  cells[r][c].isFlagged = false;
		                	  }
		                	  cells[r][c].resetImage();
		                	  updateFlagsPlaced();
		                	  updateText();
		                  }
		                  //If left clicked, show the contents of the cell
		                  else if(!cells[r][c].isFlagged)
		                  {
		                	  cells[r][c].flaggedNeighbors = updateFlaggedNeighbors(r, c);
		                	  
		                	  //If the cell has the correct number flagged, show all of the neighbors
		                	  if(cells[r][c].isPressed && (cells[r][c].flaggedNeighbors == cells[r][c].neighbors))
	                		  {
		                		  floodFillFlagged(r, c);
	                		  }
		                	  
	                		  cells[r][c].isPressed = true;
		                	  cells[r][c].resetImage();
		                	  
		                	  //If the user clicked a mine, show the board and end the game
		                	  if(cells[r][c].mine)
		                	  {
		                		  showBoard();
		                	  }
		                	  //If the user clicked a cell that has no mines next to it, clear all the cells until they are next to a mine
		                	  else if(cells[r][c].neighbors == 0)
		                	  {
		                		  floodFill(r, c);
		                	  }
		                  }
		                  
		                  //If they did not win the game
		                  if(!isFinished) 
		                  {
		                	  updateNumPressed();
		                  }
		               }
		            });
					
					game.add(cells[i][j]);
				}
			}
		
		int counter = 0;
		//Randomly place mines on the board
		while(this.mineNums > 0)
		{
			counter++;
			int selectionRow = (int)(Math.random()*rows);
			int selectionCol = (int)(Math.random()*cols);
			
			if(cells[selectionRow][selectionCol].mine == false)
			{
				cells[selectionRow][selectionCol].mine = true;
				this.mineNums--;
			}
			
			//If the user makes a grid with too many mines, quit and show the board
			if(counter > 10000)
			{
				break;
			}
		}
		
		//Loads each cell with neighbors number
		for(int i = 0; i < rows; i++) 
		{
			for(int j = 0; j < cols; j++)
			{
				cells[i][j].changeNeighbors(getNeighbors(i,j));
				cells[i][j].resetImage();
			}
		}
		
		setLocationRelativeTo(null);
		setVisible(true);
		
		//Make the timer start counting every second, but will only update when updateText is called
		timer.scheduleAtFixedRate(
			    new TimerTask()
			    {
			        public void run()
			        {
			            updateText();
			        }
			    },
			    0,      // run first occurrence immediately
			    1000);  // run every second
	}
	
	//Changes the number of mines and shows the amount of time the user has been playing since the first click
	public void updateText()
	{
		if(startTime != 0)
		{
			gameTime = (int) Math.floorDiv((System.nanoTime()-startTime), 1000000000);
		}
		numsAndMinesText.setText("Mines left " + (this.numOfMinesCONST-flagsPlaced) + "    Time: " + gameTime);
	}
	
	//Called once a cell was right clicked. Updates the amount of flags placed on the board to determine how many mines 
	//the user thinks they have left.
	public void updateFlagsPlaced()
	{
		flagsPlaced = 0;
		
		for(int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				int r = i;
				int c = j;
				if(cells[r][c].isFlagged)
				{
					flagsPlaced++;
				}
			}
		}
	}
	
	//Called once a cell was left clicked. Counts the amount of cells that are shown to determine if the game is won or not.
	public void updateNumPressed()
	{
		//Starts collecting time if it is the first cell clicked
		if(this.firstPress)
		{
			this.firstPress = false;
			this.startTime = System.nanoTime();
		}
		numPressed = 0;
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(cells[i][j].isPressed == true)
				{
					numPressed++;
				}
			}
		}
		
		//If the game is won, call endGame
		if(numPressed == rows*cols - this.numOfMinesCONST)
		{
			endGame();
		}
	}
	
	//Called if all the cells that are not mines are uncovered. Will get time and ask to play again.
	public void endGame()
	{
		this.endTime = System.nanoTime();
		this.isFinished = true;
		this.completionTime = this.endTime - this.startTime;
		timer.cancel();
		
		//Show time in a dialog window. completionTime is divided by 1*10^-9 because timer is in nanoseconds.
		JOptionPane.showMessageDialog(null, "You have won! It took you " + (this.completionTime / 1000000000) + " seconds");
		
		//If longer than one second -- trying to stop cheating
		if(completionTime > 1000000000) 
		{
			saveScores(true);
		}
		
		//Ask to play again and store the yes or no in a integer called playAgain
		playAgain = JOptionPane.showConfirmDialog(null,	"Play again?", "Play again?", JOptionPane.YES_NO_OPTION);
		
		if(playAgain == 0) //Yes
		{
			new Minesweeper(this.rows, this.cols, this.numOfMinesCONST);
			this.setVisible(false);
		}
		
		else //No
		{
			System.exit(0);
		}
	}
	
	//Called if the user clicked on a mine. Reveals all of the cells' contents. Asks the user to play again.
	public void showBoard()
	{
		this.endTime = System.nanoTime();
		this.isFinished = true;
		this.completionTime = this.endTime - this.startTime;
		timer.cancel();
		
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				cells[i][j].isPressed = true;
				cells[i][j].resetImage();
			}
		}
		
		if(completionTime > 1000000000) //If longer than one second
		{
			saveScores(false);
		}
		
		playAgain = JOptionPane.showConfirmDialog(null,	"You lost. Play again?", "You lost. Play again?", JOptionPane.YES_NO_OPTION);
		
		if(playAgain == 0) //Yes
		{
			new Minesweeper(this.rows, this.cols, this.numOfMinesCONST);
			this.setVisible(false);
		}
		
		else //No
		{
			System.exit(0);
		}
	}
	
	//Counts the amount of mines that the current cell is next to.
	public int getNeighbors(int row, int col)
	{
		int neighbors = 0;
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
				
			{
				if(row+i >= 0 && col+j >= 0 && row+i <= rows-1 && col+j <= cols-1) //If cell is on grid 
				{
					if(cells[row+i][col+j].mine)
					{
						neighbors++;
					}
				}
			}
		}
		
		return neighbors;
	}
	
	//Flood fill algorithm to show all of the cells. The isFloodFilled boolean is needed to prevent an overflow exception.
	public void floodFill (int row, int col)
	{
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if(row+i >= 0 && col+j >= 0 && row+i <= rows-1 && col+j <= cols-1) //If cell is on grid
				{
					
					cells[row+i][col+j].isPressed = true;
					cells[row][col].isFloodFilled = true;
					cells[row+i][col+j].resetImage();
					
					if(cells[row+i][col+j].neighbors == 0 && !cells[row+i][col+j].isFloodFilled)
					{
						floodFill(row+i, col+j);
					}
				}
			}
		}
	}
	
	//Updates the amount of flags placed next to a cell. Used for the flood fill when left clicked with correct number of mines to flags.
	public int updateFlaggedNeighbors(int row, int col)
	{
		int flaggedNeighbors = 0;
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
				
			{
				if(row+i >= 0 && col+j >= 0 && row+i <= rows-1 && col+j <= cols-1) //If cell is on grid 
				{
					if(cells[row+i][col+j].isFlagged)
					{
						flaggedNeighbors++;
					}
				}
			}
		}		
		
		return flaggedNeighbors;
	}
	
	//Shows all adjacent cells that are not flagged
	public void floodFillFlagged (int row, int col)
	{
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if(row+i >= 0 && col+j >= 0 && row+i <= rows-1 && col+j <= cols-1) //If cell is on grid
				{
					if(!cells[row+i][col+j].isFlagged)
					{
						cells[row+i][col+j].isPressed = true;
						cells[row][col].isFloodFilled = true;
						cells[row+i][col+j].resetImage();
					}
					
					if(cells[row+i][col+j].isPressed && cells[row+i][col+j].mine)
					{
						showBoard();
					}
					
					if(cells[row+i][col+j].neighbors == 0 && !cells[row+i][col+j].isFloodFilled)
					{
						floodFill(row+i, col+j);
					}
				}
			}
		}
	}
	
	//Save scores to the text file
	public void saveScores(boolean winLoss)
	{
		try 
		{
			//Keep on asking name until it does not have a space.
			//Spaces are not wanted as they make reading the data difficult.
			do
			{
				name = JOptionPane.showInputDialog("Enter your name if you want your score saved.\n"); //Don't allow spaces
			} while(name.contains(" "));

			//If there is a name
			if(name.length() > 1 && name != null)
			{
				File file = new File("Scores.txt");		
				FileWriter fw = new FileWriter(file, true);
				if(winLoss)
				{
					fw.write("\n" + this.name + " W " + this.completionTime);
					fw.close();
				}
				else
				{
					fw.write("\n" + this.name + " L " + this.completionTime);
					fw.close();
				}
			}
		}
		catch(Exception e) //Needed for IO exception and null pointer exception
		{
			System.out.println(e.getMessage());
		}
	}
}