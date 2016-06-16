package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;
import api.Observable;
import api.Observer;
import captors.Movement;

public class WallValuesExplorer extends AbstractExplorer implements Observer {

	public static final int MAX_DIST = 5;
	
	protected char tileValues[][];
	private String filename;

	private char[][] manhattanDistances;
	private List<Point> parkoor;

	public WallValuesExplorer(Position position, Movement movement, Grid grid, String filename) {
 		super(position, movement, grid);
 		this.tileValues = new char[this.XMax][this.YMax];
		this.filename = filename;
		this.manhattanDistances = new char[this.XMax][this.YMax];
 	}



	@Override
	public void explore () {
		this.computeScores(this.position.getPoint());
		int idx = 0;
		while (!this.isAllDiscovered()){
			this.nextStep();
			this.computeScores(this.position.getPoint());
			if (++idx>=1)
				break;
		}/**/
	}

	/** tells if the grid is all discovered or not
	 * TODO : Améliorer pour pas de n^2 !!!!
	 *
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


	public void nextStep(){
		Point currentPoint = this.position.getPoint();
		Point destination = this.findHighestScore();
		
		System.out.println(destination.x + " " + destination.y);
		
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

		List<List<Point>> parkoors = this.tracebackDijktsra(distances, currentPoint, destination);
		for (List<Point> path : parkoors) {
			for (Point p : path)
				System.out.print("("+p.x+";"+p.y+")");
			System.out.println();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.parkoor = this.chooseParkoor(parkoors);
		
		System.out.println(this.parkoor.size());
		for (Point p : this.parkoor)
			System.out.println(p.x + " " + p.y);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();

		//this.movement.followPath(this.parkoor);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Point currentTile = this.position.getPoint();
		Point nextTile = null;
		
		int currentIdx = this.parkoor.indexOf(currentTile);
		nextTile = currentIdx == -1 ? this.parkoor.get(0) : this.parkoor.get(currentIdx+1);
		
		Direction dir = Direction.getDirectionBetween(currentTile, nextTile);
		
		if (this.grid.getTile(currentTile).getState(dir) == WallState.Wall)
			this.movement.stopOnThisTile();
	}


	// --------------------------------------------------------------
	// ----------------------- Path selection -----------------------
	// --------------------------------------------------------------
	
	public List<List<Point>> tracebackDijktsra(char[][] dists, Point begin, Point dest){
		List<List<Point>> possibleParkoors = new ArrayList<List<Point>>();
		List<Point> tmp = new ArrayList<Point>();
		
		tmp.add(begin);
		possibleParkoors.add(tmp);
		
		for (int dist=dists[begin.x][begin.y]; dist>0; dist--){
			List<List<Point>> toAdd = new ArrayList<List<Point>>();
			
			for(List<Point> list : possibleParkoors){
				Point p = list.get(list.size()-1);
				List<Point> pointsReached = new ArrayList<Point>(4);
				
				for (Tile nei : this.grid.getNeighbors(p)) {
					if (nei == null || dists[nei.getLine()][nei.getCol()] != dist-1)
						continue;
					
					pointsReached.add(new Point(nei.getLine(), nei.getCol()));
				}

				int idx=0;
				for (Point reached : pointsReached) {
					List<Point> current = list;
					
					if (idx++ != pointsReached.size()-1) {
						current = new ArrayList<Point>(current);
						toAdd.add(current);
					}
					
					current.add(reached);
				}
			}
			
			possibleParkoors.addAll(toAdd);
		}
		
		return possibleParkoors;
	}
	
	public List<Point> chooseParkoor(List<List<Point>> possibleParkoors){
		List<Point> parkoor = new ArrayList<Point>();
		int value;
		int maxValue =-20000;
		for (List<Point> list : possibleParkoors){
			value = 0;
			
			int idxPoint = 0;
			for (Point p : list){
				value+= this.tileValues[p.x][p.y];
				if (idxPoint>2){
					if (!(list.get(idxPoint-1).x == list.get(idxPoint-2).x && list.get(idxPoint-1).x== p.x) &&
							(!(list.get(idxPoint-2).y == list.get(idxPoint-1).y && list.get(idxPoint-1).y== p.y)))
						value-=10;
				}
				
				idxPoint++;
			}
			if (value>maxValue){
				parkoor=list;
			}
		}


		return parkoor;
	}
	
	
	// --------------------------------------------------------------
	// ------------------ Distances computation ---------------------
	// --------------------------------------------------------------
	
	public char[][] getManhattanDistances (Point current) {
		return this.getManhattanDistances(current, null);
	}
	
	public char[][] getManhattanDistances (Point current, Point destination) {
		char[][] dists = this.manhattanDistances;
		for (int x=0; x<this.XMax; x++){
			for (int y=0; y<this.YMax; y++)
				dists[x][y]= 255;
		}
		dists[current.x][current.y] = 0;
		
		List<Point> nextPoints = new ArrayList<Point>();
		nextPoints.add(current);
		
		while (nextPoints.size() > 0) {
			current = nextPoints.remove(0);
			int val = dists[current.x][current.y];
			
			// Empeche les deplacements trop lointains quand pas de destination
			if (destination == null && val == MAX_DIST)
				continue;
			
			Tile[] neis = this.grid.getNeighbors(current);
			for (Tile nei : neis) {
				if (nei == null)
					continue;
				
				if (dists[nei.getLine()][nei.getCol()] > val+1) {
					dists[nei.getLine()][nei.getCol()] = (char) (val+1);
					nextPoints.add(current);
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
	 */
	public void computeScores(Point p){
		char[][] distances= this.getManhattanDistances(p);
		for(int x = 0; x < distances.length; x++){
			for(int y = 0; y < distances[x].length; y++){
				int score = this.tileScore(x, y);
				if (distances[x][y] != 0)
					this.tileValues[x][y] =  (char) Math.max(1,score/distances[x][y]);
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
