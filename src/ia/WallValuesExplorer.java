package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;
import captors.Movement;

public class WallValuesExplorer extends AbstractExplorer {

	protected char tileValues[][];
	public String filename;

	private char[][] dijTab;
	private List<Point> parkoor;

	public WallValuesExplorer(Position position, Movement movement, Grid grid, String filename) {
 		super(position, movement, grid);
 		this.tileValues = new char[this.XMax][this.YMax];
		this.filename = filename;
		this.dijTab = new char[grid.getHeight()][grid.getWidth()];
 	}



	@Override
	public void explore () {
		this.computeScores(this.position.getPoint());
		while (!this.isAllDiscovered()){
			this.move();
			
			//this.computeScores(this.position.getPoint());
			break;
		}
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


	public void move(){
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
		
		char[][] grid = this.makeDijkstraGrid(currentPoint,destination);

		List<List<Point>> parkoors = this.solveDijktsra(grid, currentPoint, destination);
		this.parkoor = this.chooseParkoor(parkoors);
		parkoor.remove(0);
		
		System.out.println(parkoor.size());
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.movement.followPath(this.parkoor);
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

	/**
	 */
	public List<List<Point>> solveDijktsra(char[][] grid, Point begin, Point dest){
		List<List<Point>> possibleParkoors = new ArrayList<List<Point>>();
		List<Point> tmp = new ArrayList<Point>(grid[begin.x][begin.y]);
		
		tmp.add(begin);
		possibleParkoors.add(tmp);
		for (int dist=grid[begin.x][begin.y]; dist>0; dist--){
			for(int nblist=0; nblist< possibleParkoors.size(); nblist++ ){
				List<Point> list= possibleParkoors.get(nblist);
				Point p = list.get(list.size()-1);
				List<Point> pointsReached = new ArrayList<Point>(4);
				if (p.y-1>=0 && grid[p.x][p.y-1] == dist-1 && this.grid.getTile(p).west!=WallState.Wall)
					pointsReached.add(new Point(p.x,p.y-1));
				if (p.x-1>=0 && grid[p.x-1][p.y] == dist-1 && this.grid.getTile(p).north!=WallState.Wall)
					pointsReached.add(new Point(p.x-1,p.y));
				if (p.y+1<this.YMax && grid[p.x][p.y+1] == dist-1 && this.grid.getTile(p).east!=WallState.Wall)
					pointsReached.add(new Point(p.x,p.y+1));
				if (p.x+1<this.XMax && grid[p.x+1][p.y] == dist-1 && this.grid.getTile(p).south!=WallState.Wall)
					pointsReached.add(new Point(p.x+1,p.y));

				for (int size=pointsReached.size(); size>1;size--){
						@SuppressWarnings({ "unchecked", "rawtypes" })
						List<Point> listcopy = new ArrayList(list);
						listcopy.add(pointsReached.get(size-1));
						possibleParkoors.add(listcopy);
					}
				if (pointsReached.size()>=1)
					list.add(pointsReached.get(0));
				}
			}
		return possibleParkoors;
	}



 	public char[][] makeDijkstraGrid(Point begin,Point dest){
 		char[][] grid = this.dijTab; // Pas besoin de le re-créer plein de fois
		for (int x=0; x<this.XMax; x++){
			for (int y=0; y<this.YMax; y++)
				grid[x][y]= 255;
		}


		List<Point> prevPoints = new ArrayList<Point>(100);
		prevPoints.add(dest);
		char distance=0;
		grid[dest.x][dest.y]=distance;
		while (grid[begin.x][begin.y]==255){
			distance ++;
			List<Point> nextPoints = new ArrayList<Point>(255);
			for (Point p : prevPoints){
				List<Point> pointsReached= this.setNeighbourgValueDijkstra(p, grid, distance);
				for (Point point : pointsReached){
					nextPoints.add(point);
				}
			}
			prevPoints = new ArrayList<Point>(255);
			for (Point p: nextPoints){
				prevPoints.add(p);
			}
		}
		return grid;
	}

	public List<Point> setNeighbourgValueDijkstra(Point p,char[][] grid, char distance){

		Tile t = this.grid.getTile(p);
		List<Direction> emptyWalls = new ArrayList<Direction>(4);
		List<Point> pointsReached = new ArrayList<Point>(4);
		if (t.east != WallState.Wall)
			emptyWalls.add(Direction.EAST);
		if (t.west != WallState.Wall)
			emptyWalls.add(Direction.WEST);
		if (t.north != WallState.Wall)
			emptyWalls.add(Direction.NORTH);
		if (t.south != WallState.Wall)
			emptyWalls.add(Direction.SOUTH);

		for (Direction dir : emptyWalls){
			Point tmp= new Point(p);
			this.grid.translatePoint(tmp, dir);
			if (grid[tmp.x][tmp.y] > distance){
				grid[tmp.x][tmp.y]= distance;
				pointsReached.add(tmp);
			}
		}
		return pointsReached;
	}

	public char[][] getRealDistance(Point currentPoint){
		char[][] grid = new char[this.XMax][this.YMax];
		for (int x=0; x<this.XMax; x++){
			for (int y=0; y<this.YMax; y++)
				grid[x][y]= 255;
		}

		List<Point> prevPoints = new ArrayList<Point>(100);
		prevPoints.add(currentPoint);
		char distance=0;
		grid[currentPoint.x][currentPoint.y]=distance;
		while (distance<30){
			distance ++;
			List<Point> nextPoints = new ArrayList<Point>(255);
			for (Point p : prevPoints){
				List<Point> pointsReached= this.setNeighbourgValueDijkstra(p, grid, distance);
				for (Point point : pointsReached){
					nextPoints.add(point);
				}
			}
			prevPoints = new ArrayList<Point>(255);
			for (Point p: nextPoints){
				prevPoints.add(p);
			}
		}
		return grid;
	}

	/**search for the point on the grid witch has the greatest value
	 *
	 * @return the point with the highest value
	 */
	public List<Point> findHighScores(){
		List<Point> highPoints = new ArrayList<Point>(255);
		int highScore = 0;
		System.out.println("Scores");
		for (int y=0; y<this.YMax; y++){
			for (int x=0; x<this.XMax; x++){
				System.out.println(x + " " + y + "   " + (int)this.tileValues[x][y]);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
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
		char[][] realDistance= this.getRealDistance(p);
		for(int y = 0; y < this.YMax; y++){
			for(int x = 0; x < this.XMax; x++){
				int score = this.tileScore(x, y);
				if (score != 0)
					this.tileValues[x][y] =  (char) Math.max(1,score*3-new Double (Math.pow(realDistance[x][y],2)).intValue());
				else
					this.tileValues[x][y]= 0;
//				this.tileValues[x][y]= score;
			}
		}
	}

	public int scoreDistanceModulation(Point p1, Point p2, int score){
		int res=0;
		int dx = Math.abs(p1.x - p2.x);
		int dy = Math.abs(p1.y - p2.y);
		res = score -(dx+dy);
		if (dx!=0 && dy!=0)// si le robot doit tourner
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
