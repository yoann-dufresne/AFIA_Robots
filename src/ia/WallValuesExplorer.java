package ia;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import captors.Movement;
import captors.WallDiscoverer;
import captors.WallDiscovererObserver;
import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;

public class WallValuesExplorer extends AbstractExplorer {

	protected char tileValues[][];
	public String filename;

	private char[][] dijTab;


	public WallValuesExplorer(Position position, Movement move, Grid grid, String filename) {
 		super(position, move, grid);
 		this.tileValues = new char[this.XMax][this.YMax];
		this.filename = filename;
		this.dijTab = new char[grid.getHeight()][grid.getWidth()];
 	}



	@Override
	public void explore () {
		this.computeScores(this.position.getPoint());
		int cpt=0;
		while (!this.isAllDiscovered()){
			cpt = this.moveTo(cpt);
			this.computeScores(this.position.getPoint());
		}
	}

	/** tells if the grid is all discovered or not
	 * TODO : Am�liorer pour pas de n^2 !!!!
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


	public int moveTo(int cpt){
		Point currentPoint = this.position.getPoint();
		Point destination = this.findHighestScore();
		char[][] grid = this.makeDijkstraGrid(currentPoint,destination);

		List<Point> parkoor = this.chooseParkoor(this.solveDijktsra(grid, currentPoint, destination,cpt));
		parkoor.remove(0);

		for(Point p: parkoor){
			int diff=0;
			boolean canMoove = false;
			if(p.x == this.position.getPoint().x){
				diff = p.y - this.position.getPoint().y;
				if (diff > 0 && this.grid.getTile(this.position.getPoint()).east == WallState.Empty)
						canMoove= true;
				else if (diff < 0 && this.grid.getTile(this.position.getPoint()).west == WallState.Empty)
						canMoove= true;
			}
			else {
				// same Y
				diff =  p.x - this.position.getPoint().x;
				if (diff>0 && this.grid.getTile(this.position.getPoint()).south == WallState.Empty)
						canMoove= true;
				else if (diff<0 && this.grid.getTile(this.position.getPoint()).north == WallState.Empty)
						canMoove= true;
			}
			if (canMoove){
				//this.move.moveTo(p);
				cpt++;
			}
			else
				break;
		}
		return cpt;
	}

	public List<Point> chooseParkoor(List<List<Point>> possibleParkoors){
		List<Point> parkoor = new ArrayList<Point>();
		int value;
		int maxValue =-20000;
		for (List<Point> list : possibleParkoors){
			value = 0;
			for (int indexP=0; indexP<list.size(); indexP++){
				Point p = list.get(indexP);
				value+= this.tileValues[p.x][p.y];
				if (indexP>2){
					if (!(list.get(indexP-1).x == list.get(indexP-2).x && list.get(indexP-1).x== p.x) &&
							(!(list.get(indexP-2).y == list.get(indexP-1).y && list.get(indexP-1).y== p.y)))
						value-=10;
				}
			}
			if (value>maxValue){
				parkoor=list;
			}
		}


		return parkoor;
	}

	/**
	 */
	public List<List<Point>> solveDijktsra(char[][] grid, Point begin, Point dest,int cpt){
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
 		char[][] grid = this.dijTab; // Pas besoin de le re-cr�er plein de fois
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
		for (int y=0; y<this.YMax; y++){
			for (int x=0; x<this.XMax; x++){
				if (this.tileValues[x][y] == highScore){
					highPoints.add(new Point(x,y));
				}
				else if (this.tileValues[x][y]>= highScore){
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
