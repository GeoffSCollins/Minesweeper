import javax.swing.JButton;
import javax.swing.ImageIcon;

public class Cell extends JButton {
	
	private static final long serialVersionUID = 1L;
	boolean mine;
	boolean isPressed = false;
	boolean isFlagged = false;
	boolean isFloodFilled = false;
	int neighbors = 0;
	int flaggedNeighbors = 0;
	
	//All the image icons are the number of adjacent mines, a mine, or a flag. They are 100x100 px images.
	ImageIcon Mine = new ImageIcon(this.getClass().getResource("Mine.png"));
	ImageIcon one = new ImageIcon(this.getClass().getResource("1.png"));
	ImageIcon two = new ImageIcon(this.getClass().getResource("2.png"));
	ImageIcon three = new ImageIcon(this.getClass().getResource("3.png"));
	ImageIcon four = new ImageIcon(this.getClass().getResource("4.png"));
	ImageIcon five = new ImageIcon(this.getClass().getResource("5.png"));
	ImageIcon six = new ImageIcon(this.getClass().getResource("6.png"));
	ImageIcon seven = new ImageIcon(this.getClass().getResource("7.png"));
	ImageIcon eight = new ImageIcon(this.getClass().getResource("8.png"));
	ImageIcon Clickable = new ImageIcon(this.getClass().getResource("Clickable.png"));
	ImageIcon Flag = new ImageIcon(this.getClass().getResource("Flag.jpg"));
	
	public Cell()
	{
		this.mine = false;
	}
	
	
	public void changeNeighbors(int neighbors)
	{
		this.neighbors = neighbors;
	}
		
	public void resetImage()
	{
		if(!isPressed && this.isFlagged)
		{
			setIcon(Flag);
		}
		else if(!isPressed)
		{
			setIcon(Clickable);
		}
		else
		{
			if(this.mine)
			{
				setIcon(Mine);
			}
			else
			{
				switch (this.neighbors)
				{
					case 0:
						setIcon(null);
						break;
					case 1:
						setIcon(one);
						break;
					case 2:
						setIcon(two);
						break;
					case 3:
						setIcon(three);
						break;
					case 4:
						setIcon(four);
						break;
					case 5:
						setIcon(five);
						break;
					case 6:
						setIcon(six);
						break;
					case 7:
						setIcon(seven);
						break;
					case 8:
						setIcon(eight);
						break;
				}
			}
		}
	}
}