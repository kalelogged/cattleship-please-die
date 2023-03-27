
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GridCreator extends JPanel{
	private static final long serialVersionUID = 1L;
	private BufferedImage gridImage = null;
	private Object[][] gridArray;
	private Ship[] shipArray;
	private JPanel[] panelArray;
	private JButton endSetup, randomizeShipsBtn;
	private JFrame window;
	private volatile boolean setupOver = false;
	public static final int X_ORIGIN = 54;
	public static final int Y_ORIGIN = 56;
	public static final int TILE_SIZE = 47;
	public static final int BORDER_SIZE = 5;
	public static boolean currentlyPlacingShip = false;

	public GridCreator(Ship[] shipArray, JFrame app){
		this(shipArray, 10, app);
	}

	public GridCreator(Ship[] shipArray, int gridSize, JFrame app){
		this(shipArray, gridSize, "bigGrid.png", app);
	}

	public GridCreator(Ship[] shipArray, int gridSize, String path, JFrame app){
		setLayout(null);
		setBackground(Color.white);
		setLocation(0,0);
		window = app;

		Object[][] grid = new Object[gridSize][gridSize];
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[i].length; j++){
				grid[i][j] = 1;
			}
		}

		gridArray = grid;
		this.shipArray = shipArray;
		panelArray = new JPanel[shipArray.length];

		try{
			gridImage = ImageIO.read(new File(path));
		} catch (IOException e){
			System.out.println("Failed to load image");
		}

	}
	
	public void setup(){
		int largestShipSize = 0;
		for(int i = 0; i < shipArray.length; i++){
			int temp = shipArray[i].getShipPieces().length;
			if(temp > largestShipSize){
				largestShipSize = temp;
			}
		}
		
		int windowWidth = X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) + (2 * BORDER_SIZE) + 50
				+ ((largestShipSize + 1) * TILE_SIZE);
		int windowHeight = Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * (gridArray.length + 1));
		if(windowHeight < 2 * TILE_SIZE + (shipArray.length * (TILE_SIZE + BORDER_SIZE + 2))){
			windowHeight = 2 * TILE_SIZE + (shipArray.length * (TILE_SIZE + BORDER_SIZE + 2));
		}
		window.setPreferredSize(new Dimension(windowWidth, windowHeight));
		window.setMinimumSize(new Dimension(windowWidth, windowHeight));
		window.pack();
		setSize(window.getContentPane().getSize());
		
		JLabel gridLabel = new JLabel(new ImageIcon(gridImage));
		gridLabel.setSize(X_ORIGIN + gridArray.length + 1 + ((TILE_SIZE + BORDER_SIZE) * gridArray.length),
				Y_ORIGIN + gridArray.length + 1 + ((TILE_SIZE + BORDER_SIZE) * (gridArray.length)));
		gridLabel.setLocation(0, 0);
		gridLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gridLabel.setVerticalAlignment(SwingConstants.TOP);
		add(gridLabel);

		int buttonXPos = gridLabel.getWidth();

		randomizeShipsBtn = new JButton("Randomize Grid");
		randomizeShipsBtn.setBounds(buttonXPos, 0, window.getWidth() - buttonXPos, TILE_SIZE - 5);
		randomizeShipsBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				Random rand = new Random();
				for(int i = 0; i < panelArray.length; i++){
					panelArray[i].setLocation(shipArray[i].getStartingOffGridPosition());
				}
				for(int i = 0; i < panelArray.length; i++){
					int timeout = 0;
					while(timeout < 500
							&& shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())){
						int x = rand.nextInt(gridArray.length);
						int y = rand.nextInt(gridArray.length);
						timeout++;
						leftClick(i, x, y);
						if(rand.nextInt(2) == 0
								&& !shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())){
							rightClick(i, x, y);
						}
					}
				}
			}
		});
		add(randomizeShipsBtn);
		randomizeShipsBtn.setVisible(true);
		repaint();
		
		endSetup = new JButton("Finish");
		endSetup.setBounds(buttonXPos, TILE_SIZE - 5, window.getWidth() - buttonXPos, window.getHeight());
		endSetup.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setupOver = true;
			}
		});
		add(endSetup);
		
		endSetup.setVisible(false);
		
		for(int j = 0; j < shipArray.length; j++){
			final int shipNum = j;
			
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setOpaque(false);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(Box.createRigidArea(new Dimension(0, 0)));
			
			for(int i = 0; i < shipArray[j].getShipPieces().length; i++){
				ImageIcon icon = new ImageIcon(shipArray[j].getShipPieces()[i].getShipImage());
				JLabel label = new JLabel(icon);
				panel.add(label);
				panel.add(Box.createRigidArea(new Dimension(BORDER_SIZE + 2, 0)));

			}
			panel.setLocation(X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) + (2 * BORDER_SIZE) + 50,
					TILE_SIZE + BORDER_SIZE + 2 + (j * (TILE_SIZE + BORDER_SIZE + 2)));
			panel.setSize(shipArray[j].getShipPieces().length * (TILE_SIZE + BORDER_SIZE), TILE_SIZE);
			shipArray[shipNum].setStartingOffGridPosition(panel.getLocation());
			add(panel);
			panelArray[j] = panel;
			setComponentZOrder(panel, 0);

			panel.addMouseMotionListener(new MouseMotionAdapter(){

				@Override
				public void mouseDragged(MouseEvent e){
					if(SwingUtilities.isLeftMouseButton(e)){
						JPanel component = (JPanel) e.getComponent().getParent().getParent();
						Point pt = new Point(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), component));
						panel.setLocation((int) pt.getX() - (TILE_SIZE / 2), (int) pt.getY() - (TILE_SIZE / 2));
						currentlyPlacingShip = true;
					}
				}

			});
			panel.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseReleased(MouseEvent e){
					JPanel component = (JPanel) e.getComponent().getParent().getParent();
					Point pt = new Point(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), component));
					int counter1 = 0;
					int counter2 = 0;
					
					int value = (int) pt.getX();

					while(X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter1) < value){
						counter1++;
					}
					counter1--;

					int value2 = (int) (pt.getY());
					while(Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter2) < value2){
						counter2++;
					}
					counter2--;
					
					if(e.getButton() == MouseEvent.BUTTON1){
						// calls the left click method
						currentlyPlacingShip = false;
						leftClick(shipNum, counter1, counter2);
						// if right button released
					}
					else if(e.getButton() == MouseEvent.BUTTON3){
						rightClick(shipNum, counter1, counter2);
					}
					endSetup.repaint();
				}
			});
		}
	}
	
	private void rightClick(int shipNum, int x, int y){
		boolean isVertical = false;
		if(((BoxLayout) panelArray[shipNum].getLayout()).getAxis() == BoxLayout.Y_AXIS){
			isVertical = true;
		}
		removeShipFromGridArray(shipArray[shipNum], isVertical);
		if(rotatePanel(panelArray[shipNum]) && !currentlyPlacingShip){
			addShipToGridArray(shipArray[shipNum], new Point(x, y), !isVertical);
		} else if(!currentlyPlacingShip){
			panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
			rotatePanel(panelArray[shipNum]);
		}

		showFinishButton();
	}
	
	private void leftClick(int shipNum, int x, int y){
		if((((BoxLayout) panelArray[shipNum].getLayout()).getAxis() == BoxLayout.X_AXIS)){
			if(x < gridArray.length - panelArray[shipNum].getWidth() / TILE_SIZE + 1 && x >= 0){
				if(y < gridArray[0].length - panelArray[shipNum].getHeight() / TILE_SIZE + 1 && y >= 0){
					placeShipPanelOnGrid(x, y, shipNum, false);
				}
				else{
					panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
					removeShipFromGridArray(shipArray[shipNum], false);
				}
			}
			else{
				panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
				removeShipFromGridArray(shipArray[shipNum], false);
			}
		}
		else{
			if(x < gridArray.length - panelArray[shipNum].getWidth() / TILE_SIZE + 1 && x >= 0){
				if(y < gridArray[0].length - panelArray[shipNum].getHeight() / TILE_SIZE + 1 && y >= 0){
					placeShipPanelOnGrid(x, y, shipNum, true);
				}
				else{
					rotatePanel(panelArray[shipNum]);
					panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
					removeShipFromGridArray(shipArray[shipNum], true);
				}
			}
			else{
				rotatePanel(panelArray[shipNum]);
				panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
				removeShipFromGridArray(shipArray[shipNum], true);
			}
		}

		showFinishButton();
	}
	
	private void placeShipPanelOnGrid(int x, int y, int shipNum, boolean isVertical){
		panelArray[shipNum].setLocation(X_ORIGIN + x + (((TILE_SIZE + BORDER_SIZE) * x) + BORDER_SIZE / 2),
				Y_ORIGIN + y + ((TILE_SIZE + BORDER_SIZE) * y) + BORDER_SIZE / 2);
		if(isIntersection(panelArray[shipNum])){
			if(isVertical){
				rotatePanel(panelArray[shipNum]);
			}
			removeShipFromGridArray(shipArray[shipNum], false);
			
			panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
		}
		else{
			removeShipFromGridArray(shipArray[shipNum], isVertical);
			addShipToGridArray(shipArray[shipNum], new Point(x, y), isVertical);

		}
	}
	
	private void showFinishButton(){
		boolean showButton = true;
		if(!currentlyPlacingShip){
			for(int i = 0; i < shipArray.length; i++){
				if(shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())){
					showButton = false;
				}
			}
			endSetup.setVisible(showButton);
		}
	}
	
	private boolean isIntersection(JPanel p){
		for(int i = 0; i < panelArray.length; i++){
			if(p.getBounds().intersects(panelArray[i].getBounds()) && !p.equals(panelArray[i])){
				return true;
			}
		}
		return false;
	}
	
	private void removeShipFromGridArray(Ship ship, boolean isVertical){
		for(int i = 0; i < gridArray.length; i++){
			for(int j = 0; j < gridArray[i].length; j++){
				for(int k = 0; k < ship.getShipPieces().length; k++){
					if(gridArray[j][i] == (ShipPiece) ship.getShipPieces()[k]){
						gridArray[j][i] = 1;
					}
				}
			}
		}
	}
	
	private void addShipToGridArray(Ship ship, Point location, boolean isVertical){
		if(location.getX() < gridArray.length && location.getX() >= 0 && location.getY() < gridArray.length
				&& location.getY() >= 0){
			for(int i = 0; i < ship.getShipPieces().length; i++){
				if(isVertical){
					gridArray[(int) location.getX()][(int) location.getY() + i] = ship.getShipPieces()[i];
				}
				else{
					
					gridArray[(int) location.getX() + i][(int) location.getY()] = ship.getShipPieces()[i];
				}
			}
		}
	}
	
	private boolean rotatePanel(JPanel panel){
		
		if(((BoxLayout) panel.getLayout()).getAxis() == BoxLayout.X_AXIS){
			if(panel.getX() > X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) && !currentlyPlacingShip){
				return false;
			}
			
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			int temp = panel.getWidth();
			int temp2 = panel.getHeight();
			panel.setSize(temp2, temp);
			
			for(int i = 0; i < panel.getComponentCount(); i++){
				if(!panel.getComponent(i).getClass().toString().equals("JPanel")){
					panel.add(Box.createRigidArea(new Dimension(0, BORDER_SIZE + 2)), i);
					panel.remove(++i);
				}
			}
			panel.add(Box.createRigidArea(new Dimension(0, 0)), 0);
			panel.remove(1);
			
			panel.validate();
			
			panel.setLocation(panel.getX(), panel.getY());
			
			int counter = 0;
			while(Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter) < panel.getY() + panel.getWidth()){
				counter++;
			}
			counter--;
			
			if(!(counter <= gridArray[0].length - panel.getHeight() / TILE_SIZE && counter >= 0)
					|| isIntersection(panel)){
				return false;
			}
		} else if(((BoxLayout) panel.getLayout()).getAxis() == BoxLayout.Y_AXIS){
			
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			
			int temp = panel.getWidth();
			int temp2 = panel.getHeight();
			panel.setSize(temp2, temp);
			
			for(int i = 0; i < panel.getComponentCount(); i++){
				if(!panel.getComponent(i).getClass().toString().equals("JPanel")){
					panel.add(Box.createRigidArea(new Dimension(BORDER_SIZE + 2, 0)), i);
					panel.remove(++i);
				}
			}
			panel.add(Box.createRigidArea(new Dimension(0, 0)), 0);
			panel.remove(1);
			
			panel.validate();
			
			panel.setLocation(panel.getX(), panel.getY());
			
			int counter = 0;
			while(X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter) < panel.getX() + panel.getHeight()){
				counter++;
			}
			counter--;
			
			if(!(counter <= gridArray.length - panel.getWidth() / TILE_SIZE && counter >= 0)
					|| isIntersection(panel)){
				return false;
			}
		}
		return true;
	}
	
	public Object[][] getGridArray(){
		return gridArray;
	}
	public boolean isSetupOver(){
		return setupOver;
	}

	public JButton getButton(){
		return endSetup;
	}
}