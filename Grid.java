import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Grid extends JPanel implements MouseListener{
	private static final long serialVersionUID = 1L;
	private BufferedImage gridImage;
	private Object[][] array;
	public static final int X_ORIGIN = 54;
	public static final int Y_ORIGIN = 56;
	public static final int TILE_SIZE = 47;
	public static final int BORDER_SIZE = 5;
	private volatile boolean isTurn;
	private boolean state;
	
	public Grid(){
		this(new Object[10][10], "bigGrid.png");
	}
	
	public Grid(Object[][] arr){
		this(arr, "bigGrid.png");
	}
	
	public Grid(Object[][] arr, String path){
		array = arr;
		isTurn = true;
		state = false;
		
		setBackground(Color.white);
		setPreferredSize(new Dimension((X_ORIGIN+ arr.length + 1 + ((TILE_SIZE+BORDER_SIZE)*array.length)), 
				Y_ORIGIN+ arr.length + 1 + ((TILE_SIZE+BORDER_SIZE)*array.length)));
		setSize(getPreferredSize());
		setLocation(0,0);

		try{
			gridImage = ImageIO.read(new File(path));
		} catch (IOException e){
			System.out.println("Failed to load image");
		}
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(gridImage, 0, 0, this);
		
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[i].length; j++){
				if(array[i][j].equals((Object) 1) || ((array[i][j]).getClass().getName().equals("ShipPiece")
						&& !((ShipPiece) array[i][j]).isDestroy())){
					g2.setColor(Color.white);
					g2.fillRect(X_ORIGIN + i + 1 + ((TILE_SIZE + BORDER_SIZE) * i), Y_ORIGIN + j + 1 + ((TILE_SIZE + BORDER_SIZE) * j),
							TILE_SIZE+(BORDER_SIZE/2)-1, TILE_SIZE+(BORDER_SIZE/2)-1);
				}
				else if((array[i][j]).getClass().getName().equals("ShipPiece")){
					g2.drawImage(((ShipPiece) array[i][j]).getShipImage(),
							X_ORIGIN + i + ((TILE_SIZE + BORDER_SIZE) * i) + BORDER_SIZE/2,
							Y_ORIGIN + j + ((TILE_SIZE + BORDER_SIZE) * j) + BORDER_SIZE/2, this);
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e){
		if(isTurn && e.getButton() == MouseEvent.BUTTON1){
			int value = e.getX();
			int counter1 = 0;
			while(X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter1) + BORDER_SIZE < value){
				counter1++;
			}
			counter1--;
			
			int value2 = e.getY() - (TILE_SIZE / 2);
			int counter2 = 0;
			while(Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter2) + BORDER_SIZE < value2){
				counter2++;
			}
			counter2--;
			
			if(counter1 < array.length && counter1 >= 0){
				if(counter2 < array[0].length && counter2 >= 0){
					if(array[counter1][counter2].equals((Object) 1)){
						array[counter1][counter2] = 0;
						repaint();
						isTurn = false;
					} else if((array[counter1][counter2]).getClass().getName().equals("ShipPiece")
							&& !((ShipPiece) array[counter1][counter2]).isDestroy()){
						((ShipPiece) array[counter1][counter2]).destroy();
						repaint();
						isTurn = false;
					}
					state = false;
				}
			}
		}else if(!isTurn && e.getButton() == MouseEvent.BUTTON1){
			state = true;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e){}
	
	public boolean isTurn(){
		return isTurn;
	}
	
	public void setTurn(boolean t){
		isTurn = t;
	}
	
	public Object[][] getArray(){
		return array;
	}
	
	public void setArray(Object[][] arr){
		array = arr;
	}
	
	public boolean getState(){
		return state;
	}
	
	public void setState(boolean s){
		state = s;
	}
}