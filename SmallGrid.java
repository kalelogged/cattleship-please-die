import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SmallGrid extends JPanel{
	private static final long serialVersionUID = 1L;
	private Object[][] array;
	private BufferedImage gridImage;
	public static final int X_ORIGIN = 23;
	public static final int Y_ORIGIN = 39;
	public static final int TILE_SIZE = 20;
	public static final int BORDER_SIZE = 3;
	public static final int PIECE_SIZE = 18;
	
	public SmallGrid(Object[][] array){
		this.array = array;
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension((X_ORIGIN + 2 + (TILE_SIZE+BORDER_SIZE)*array.length), 
				Y_ORIGIN+ 2 + ((TILE_SIZE+BORDER_SIZE)*array.length)));
		setSize(getPreferredSize());
		
		try{
			gridImage = ImageIO.read(new File("smallGrid.png"));
		}
		catch(IOException e){
			System.out.println("Failed to load image");
		}
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(gridImage, 0, 15, this);
		
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[i].length; j++){
				if((array[i][j]).getClass().getName().equals("ShipPiece")){
					g2.drawImage(((ShipPiece) array[i][j]).getShipImage(), 
							X_ORIGIN + 2 + ((TILE_SIZE + BORDER_SIZE) * i) + BORDER_SIZE/2,
							Y_ORIGIN + 2 + ((TILE_SIZE + BORDER_SIZE) * j) + BORDER_SIZE/2,
							PIECE_SIZE, PIECE_SIZE, this);
				}
			}
		}
	}
	
	public Object[][] getArray(){
		return array;
	}
	
	public void setArray(Object[][] array){
		this.array = array;
	}
}