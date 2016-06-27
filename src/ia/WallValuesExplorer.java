package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;
import bluetooth.BluetoothRobot;
import captors.Movement;

public class WallValuesExplorer extends AbstractExplorer {

	public static final int MAX_DIST = 200;
	
	protected char tileValues[][];

	private char[][] manhattanDistances;
	private List<Point> parkoor;

	public WallValuesExplorer(Position position, Movement movement, Grid grid) {
 		super(position, movement, grid);
 		this.tileValues = new char[this.XMax][this.YMax];
		this.manhattanDistances = new char[this.XMax][this.YMax];
 	}
 

	@Override
	public void explore () {
		this.computeScores(this.position.getPoint());
		while (!this.isAllDiscovered()){
			this.nextMove();
			this.computeScores(this.position.getPoint());
		}/**/
		//Phase 1
		this.endExploration();
		//Phase 2 ou 3
//		this.endExplorationToBigginingCorner();
	}

	
	public void endExploration(){
		Point currentPoint = this.position.getPoint();
		Point corners[] = {new Point(0,0),new Point(0,YMax-1),new Point(XMax-1,0), new Point(XMax-1,YMax-1)};
		List<Point> tmpParkoor = new ArrayList<Point>();
		this.parkoor= null;
		
		for (Point destination : corners){
			
			// Si le robot est déja dans un coin
			if (currentPoint.equals(destination)) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return;
			}
			
			BluetoothRobot.bt.send("DEBUG;" + currentPoint + " " + destination);
			char[][] distances = this.getManhattanDistances(currentPoint,destination);
			tmpParkoor = this.dummyTraceback(distances, currentPoint, destination);
			
			/*List<List<Point>> parkoors = this.tracebackDijktsra(distances, destination, currentPoint);
			tmpParkoor = this.chooseParkoor(parkoors);/**/
			
			if (this.parkoor == null || tmpParkoor.size()<this.parkoor.size())
				this.parkoor = tmpParkoor;
		}
		this.movement.followPath(this.parkoor, this.grid);/**/
	
	}
	
	//Fin de l'exploration dans un coin donné
	public void endExplorationToBigginingCorner(){
		Point currentPoint = this.position.getPoint();
		this.parkoor= null;
			
			// Si le robot est déja arrivé à destination
			if (currentPoint.equals(BluetoothRobot.bt.beginningCorner)) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				return;
			}
			
		char[][] distances = this.getManhattanDistances(currentPoint,BluetoothRobot.bt.beginningCorner);
		this.parkoor = this.dummyTraceback(distances, currentPoint, BluetoothRobot.bt.beginningCorner);
		
		/*List<List<Point>> parkoors = this.tracebackDijktsra(distances, BluetoothRobot.bt.beginningCorner, currentPoint);
		this.parkoor = this.chooseParkoor(parkoors);/**/
			
		this.movement.followPath(this.parkoor, this.grid);
	
	}
	
	
	/** tells if the grid is all discovered or not
	 * @return if all tiles values are equal to 0
	 */
	public boolean isAllDiscovered(){
		for (int x=0; x<this.XMax; x++){
			for (int y=0; y<this.YMax; y++){
				if (this.tileValues[x][y]!=0)
					return false;
			}
		}
		return true;
	}


	public void nextMove(){
		Point currentPoint = this.position.getPoint();
		Point destination = this.findHighestScore();
		
		// Si pas de déplacements
		if (currentPoint.equals(destination)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		char[][] distances = this.getManhattanDistances(currentPoint,destination);
		this.parkoor = this.dummyTraceback (distances, currentPoint, destination);
		
		String path = "";
		for (Point p : this.parkoor)
			path += "" + p.x + "," + p.y + " "; 
		BluetoothRobot.bt.send("DEBUG;"+path);
		this.movement.followPath(this.parkoor, this.grid);/**/
	}


	// --------------------------------------------------------------
	// ----------------------- Path selection -----------------------
	// --------------------------------------------------------------
	
	private List<Point> dummyTraceback(char[][] distances, Point start, Point dest) {
		int dist = (int)distances[dest.x][dest.y];
		Point[] path = new Point[dist+1];
		path[dist] = dest;
		
		
		while (dist > 0) {
			Tile current = this.grid.getTile(path[dist]);
			Tile[] neis = this.grid.getNeighbors(current);
			for (Tile nei : neis) {
				if (nei == null)
					continue;
				
				if (distances[nei.getLine()][nei.getCol()] == dist-1) {
					path[dist-1] = new Point(nei.getLine(), nei.getCol());
					break;
				}
			}
					
			dist--;
		}
		
		List<Point> list = new ArrayList<Point>(path.length);
		for (Point p : path)
			list.add(p);
		return list;
	}
	
	
	// --------------------------------------------------------------
	// ------------------ Distances computation ---------------------
	// --------------------------------------------------------------
	
	public char[][] getManhattanDistances (Point current) {
		return this.getManhattanDistances(current, null);
	}
	
	public char[][] getManhattanDistances (Point start, Point destination) {
		char[][] dists = this.manhattanDistances;
		for (int x=0; x<this.XMax; x++){
			for (int y=0; y<this.YMax; y++)
				dists[x][y]= 255;
		}
		dists[start.x][start.y] = 0;
		
		List<Point> nextPoints = new ArrayList<Point>();
		nextPoints.add(start);
		
		while (nextPoints.size() > 0) {
			Point current = nextPoints.remove(0);
			int val = dists[current.x][current.y];
			
			// Empeche les deplacements trop lointains quand pas de destination
			if (destination == null && val == MAX_DIST)
				continue;
			
			Tile[] neis = this.grid.getNeighbors(current);
			for (Tile nei : neis) {
				if (nei == null || this.tileValues[nei.getLine()][nei.getCol()]<0)
					continue;
				
				if (dists[nei.getLine()][nei.getCol()] > val+1) {
					dists[nei.getLine()][nei.getCol()] = (char) (val+1);
					nextPoints.add(new Point(nei.getLine(), nei.getCol()));
				}
			}
		}
		
		return dists;
	}

	

	/**search for the point on the grid witch has the greatest value
	 *
	 * @return the point with the highest value
	 */
	public List<Point> findHighScores(){
		List<Point> highPoints = new ArrayList<Point>();
		int highScore = 0;

		for (int y=0; y<this.YMax; y++){
			for (int x=0; x<this.XMax; x++){
				
				if (this.tileValues[x][y] == highScore){
					highPoints.add(new Point(x,y));
				}
				else if (this.tileValues[x][y]> highScore){
					highPoints.clear();
					highPoints.add(new Point(x,y));
					highScore= this.tileValues[x][y];
				}
			}
		}
		
		return highPoints;
	}

	public Point findHighestScore(){
		List<Point> highPoints = this.findHighScores();
		Point current = this.position.getPoint();

		Point dest = highPoints.get(0);
		int score = this.scoreDistanceModulation(dest,current, this.tileValues[dest.x][dest.y]);
		for (Point p : highPoints){
			int tmpScore = this.scoreDistanceModulation(p,current,this.tileValues[p.x][p.y]);
			if (tmpScore > score){
				score = tmpScore;
				dest = p;
			}
		}
		
		return dest;
	}


	/** affect a value to all tiles corresponding to the number of wall that can be detected
	 * use the distance to adjust the value of the tile
	 * @param p the point to compute scores from
	 * TODO : Changer pour ne pas faire de n^2
	 */
	public void computeScores(Point p){
		char[][] distances= this.getManhattanDistances(p);
		
		for(int x = 0; x < distances.length; x++){
			for(int y = 0; y < distances[x].length; y++){
				Tile t = this.grid.getTile(x, y);
				
				if (t.east == WallState.Undiscovered ||
						t.west == WallState.Undiscovered ||
						t.north == WallState.Undiscovered ||
						t.south == WallState.Undiscovered) {
					int score = this.tileScore(x, y);
					this.tileValues[x][y] =  (char) Math.max(1,score-distances[x][y]);
				} else
					this.tileValues[x][y] = 0;				
			}	
		}
		
		if (BluetoothRobot.bt.otherPosition.getX() != -2){
			this.tileValues[BluetoothRobot.bt.otherPosition.getPoint().x][BluetoothRobot.bt.otherPosition.getPoint().y] -= 5;
			for(Tile t : this.grid.getNeighbors(BluetoothRobot.bt.otherPosition.getPoint())){
				this.tileValues[t.getLine()][t.getCol()] -= 5;
			}
		}
	}

	public int scoreDistanceModulation(Point p1, Point p2, int score){
		int res=0;
		int dx = Math.abs(p1.x - p2.x);
		int dy = Math.abs(p1.y - p2.y);
		res = score -(dx+dy);
		// si le robot doit tourner
		if (dx!=0 && dy!=0)
			res-=2;
		return res;
	}


	/** for a given point get the number of undiscovered wall in all directions
	@return a mark corresponding to the max number of wall that can be discovered from this tile
	*/
	public int tileScore(int x, int y) {
		int mark = 0;
		Point point = new Point(x, y);
		for (Direction d : Direction.values()) {
			mark += getValuesByDirection(point, d);
		}
		return mark;


	}


	/*
	get the number of tiles without wall on the given direction from the given point
	*/
	public int getValuesByDirection(Point originPoint, Direction d) {
		int mark = 0;

		WallState state;
		Tile tile;

		tile = this.grid.getTile(originPoint);
		Point point = new Point(originPoint);
		if (tile == null)
			return mark;

		state = tile.getState(d);

		int offset = 0;
		while (offset < 6 && state != WallState.Wall){
			this.grid.translatePoint(point,d);

			if(state == WallState.Undiscovered){
				mark++;
			}

			offset++;
			tile = this.grid.getTile(point);
			if (tile == null)
				return mark;

			state = tile.getState(d);
		}

		return mark;
	}

}
