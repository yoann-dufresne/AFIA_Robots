package captors;

import java.awt.Point;

import api.Observable;
import api.Observer;
import model.Direction;
import model.Grid;

public class WallObserver implements Observer{
	
	private Grid grid;
	private Movement move;
	private WallDetectors detectors;
	public static final int MAX_US_SENSOR_DIST = 250;
	public static final int TILE_SIZE_CM = 40;
	
	
	public WallObserver(Grid trueGrid, Movement move, WallDetectors wd){
		this.grid = trueGrid;
		this.move = move;
		this.detectors = wd;
	}
	
	public int getDistance(boolean front) {
		Direction dir = this.getOrientation(front);
		Point p = new Point(this.move.position.getPoint());
		Point arrive = null;
		
		switch (dir) {
		case EAST:
			arrive = this.grid.getFarthestEast(p);
			break;
		case WEST:
			arrive = this.grid.getFarthestWest(p);
			break;
		case NORTH:
			arrive = this.grid.getFarthestNorth(p);
			break;
		case SOUTH:
			arrive = this.grid.getFarthestSouth(p);
			break;	
		}
		
		Double distance = (p.distance(arrive) + 0.5) * TILE_SIZE_CM; // magic (0.5 : half tile because robot position) 
		if(distance > MAX_US_SENSOR_DIST)
			return MAX_US_SENSOR_DIST;
		
		return distance.intValue();
	}

	public Direction getOrientation(boolean frontDetector) {
		Direction robotHeading = this.move.position.getDirection();
		if (this.detectors.isInFrontPosition())
			if(frontDetector)
				return robotHeading;
			else 
				return robotHeading.turnLeft().turnLeft();
		else 
			if (frontDetector)
				return robotHeading.turnLeft();
			else 
				return robotHeading.turnRight();
	}
	
	public int getNbCase_A(){
		return this.getDistance(false) / TILE_SIZE_CM;
	}

	public int getNbCase_B(){
		return this.getDistance(true) / TILE_SIZE_CM;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		// /!\ Non fait
	}
	
	
}
