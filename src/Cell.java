import javax.swing.JButton;
import javax.swing.ImageIcon;

class Cell extends JButton {

    private static final long serialVersionUID = 1L;
    boolean mine;
    boolean isPressed = false;
    boolean isFlagged = false;
    boolean isFloodFilled = false;
    int neighbors = 0;
    int flaggedNeighbors = 0;

    Cell() {
        this.mine = false;
    }

    void changeNeighbors(int neighbors) {
        this.neighbors = neighbors;
    }

    private void setImage(String filename) {
        ImageIcon image = new ImageIcon(this.getClass().getResource("images/" + filename));
        setIcon(image);
    }

    void resetImage() {
        if (!isPressed && this.isFlagged) {
            this.setImage("Flag.jpg");
        } else if (!isPressed) {
            this.setImage("Clickable.png");
        } else if (this.mine) {
            this.setImage("Mine.png");
        } else if (this.neighbors == 0) {
            setIcon(null);
        } else {
            this.setImage(Integer.toString(this.neighbors) + ".png");
        }
    }

    void changeFlagged() {
        this.isFlagged = !this.isFlagged;
        this.resetImage();
    }
}