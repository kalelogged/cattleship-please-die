import java.awt.Point;
import java.util.ArrayList;

public class Ship{
	private ShipPiece[] pieces;
	private Point startingPosition;
	
	Ship(ShipPiece[] list){
		pieces = list;
		startingPosition = new Point(0,0);
	}
	
	Ship(ArrayList<ShipPiece> list){
		pieces = list.toArray(new ShipPiece[0]);
		startingPosition = new Point(0,0);
	}
	
	public boolean checkIfDead(){
		boolean isDead = true;
		for(int i = 0; i < pieces.length; i++){
			if(!pieces[i].isDestroy()){
				isDead = false;
			}
		}
		return isDead;
	}
	
	public ShipPiece[] getShipPieces(){
		return pieces;
	}
	
	public void setStartingOffGridPosition(Point sp){
		startingPosition = sp;
	}
	
	public Point getStartingOffGridPosition(){
		return startingPosition;
	}
}