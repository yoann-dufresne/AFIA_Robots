package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bluetooth.BluetoothRobot;
import main.Config;
import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;

public class IA1Robot {

	private Position[] positions;
	private Grid grid;
	
	public IA1Robot(Position rob0, Position rob1, Grid grid) {
		this.positions = new Position[2];
		this.positions[0] = rob0;
		this.positions[1] = rob1;
		this.grid = grid;
	}
	
	/**
	 * Compute the path from the actual location (in the position attribute) to
	 * the point (row, col)
	 * @param row Goal row
	 * @param col Goal column
	 * @return The path to the goal represented by the intermediate points.
	 */
	public Solution goTo (int row, int col) {
		Solution sol = new Solution();
		Point destination = new Point(row, col);
		
		// calcul les distances pour 1 robot. 255 si pas possible
		sol.aloneParkoor0 = this.manhattanDistances(this.positions[0].getPoint());
		sol.aloneScore = sol.aloneParkoor0[destination.x][destination.y];
		
		if (sol.aloneScore != 255)
			sol.alonePath = this.traceback(sol.aloneParkoor0, destination);
		
		return sol;
	}
	
	/**
	 * Compute the path from the current location (in the position attribute) to
	 * the point (row, col)
	 * @param row Goal row
	 * @param col Goal column
	 * @return The path to the goal represented by the intermediate points.
	 */
	public List<Point> getPathTo (Point end, char[][] values) {
		List<Point> path = new ArrayList<Point>(1);
		if (values[end.x][end.y] != 255) {
			path = this.traceback(values, end);
		}
		
		return path;
	}
	
	/**
	 * A dijkstra exploration for finding all the possible destinations from the start point
	 * @param values
	 * @param start
	 * @return
	 */
	public char[][] manhattanDistances (Point start) {
		List<Point> positions = new ArrayList<Point>();
		char[][] values = new char[Config.GRID_HEIGHT][Config.GRID_WIDTH];
		for (int i=0 ; i<values.length ; i++)
			for (int j=0 ; j<values[i].length ; j++)
				values[i][j] = 255;
		values[start.x][start.y] = 0;
		positions.add(start);
		
		while (positions.size() != 0) {
			Point first = positions.remove(0);
			char firstVal = values[first.x][first.y];//values.get(first);
			
			Point[] tiles = this.grid.getAllFarthests(first);
			for (Point tile : tiles) {
				int dist = new Double(first.distance(tile)).intValue();
				if (firstVal + dist >= 255)
					continue;
				
				if (values[tile.x][tile.y] == 255 ||
						values[tile.x][tile.y] > firstVal + dist) {
					values[tile.x][tile.y] = (char) (firstVal + dist);
					this.insertInList(tile, positions, values);
				}
			}
		}
		
		return values;
	}

	public char[][] parkoor (Point start, Point destination) {
		return this.parkoor(start, destination, 255);
	}
	
	public char[][] parkoor (Point start, Point destination, int limit) {
		char[][] values = new char[Config.GRID_HEIGHT][Config.GRID_WIDTH];
		for (int i=0 ; i<values.length ; i++)
			for (int j=0 ; j<values[i].length ; j++)
				values[i][j] = 255;
		values[start.x][start.y] = 0;
		
		List<Point> positions = new ArrayList<Point>();
		positions.add(start);
		
		while (positions.size() != 0 && !positions.get(0).equals(destination)) {
			Point first = positions.remove(0);
			char prevVal = values[first.x][first.y];
			
			Point[] tiles = this.grid.getAllFarthests(first);
			for (Point tile : tiles) {
				int dist = new Double(first.distance(tile)).intValue();
				if (prevVal + dist >= limit)
					continue;
				
				if (values[tile.x][tile.y]==255 || values[tile.x][tile.y] > prevVal + dist) {
					values[tile.x][tile.y] = (char)(prevVal + dist);
					this.insertInList(tile, positions, values);
				}
			}
		}
		
		return values;
	}
	
	public void parkoor (Point[] points, Point destination, Solution sol, int limit) {
		char[][][] robotParkoors = new char[2][][];
		robotParkoors[0] = sol.aloneParkoor0;
		robotParkoors[1] = sol.aloneParkoor1;
		
		char[][] best = null;
		Point bestTile = null;
		char minVal = 255;
		int bestCoop = 0;
		
		for (int coop=0 ; coop<2 ; coop++) {
			for (int i=0 ; i<robotParkoors.length ; i++)
				for (int j=0 ; j<robotParkoors[i].length ; j++) {
					if (robotParkoors[coop][i][j] == 255)
						continue;
	
					Tile t = this.grid.getTile(i, j);
					
					List<Direction> emptyWalls = new ArrayList<Direction>(4);
					for (Direction dir : Direction.values())
						if (t.getState(dir) == WallState.Empty)
							emptyWalls.add(dir);
		
					for (Direction dir : emptyWalls)
						this.grid.setWall(i, j, dir);
					
					Point p = points[(coop+1) % 2];
					char[][] tmp = this.parkoor(p, destination, limit);
					char score = (char)Math.max(
							tmp[destination.x][destination.y],
							robotParkoors[coop][i][j]
					);
					// TODO : Modifier le score en fonction du temps d'arrivée de l'autre robot
					if (score < minVal) {
						best = tmp;
						minVal = score;
						bestTile = new Point(i, j);
						bestCoop = coop;
					}
					
					for (Direction dir : emptyWalls)
						this.grid.setEmpty(i, j, dir);
				}
		}
		sol.coopScore = 255;
		if (sol.coopTile == null)
			return;
		
		sol.coopTile = bestTile;
		
		sol.mainRobot = (bestCoop + 1) % 2;
		sol.secondRobot = bestCoop;
		
		// Ask for Yoann because it's shit !
		sol.myCoopParkoor = bestCoop == BluetoothRobot.bt.id ? robotParkoors[bestCoop] : best;
		sol.otherCoopParkoor = bestCoop == BluetoothRobot.bt.id ? best : robotParkoors[(bestCoop+1)%2];
	}

	private void insertInList(Point p, List<Point> positions, char[][] possibleDestinations) {
		int idx = 0;
		
		for (Point current : positions) {
			if (possibleDestinations[current.x][current.y] >= possibleDestinations[p.x][p.y]) {
				break;
			}
			
			idx++;
		}
				
		positions.add(idx, p);
	}
	
	public List<Point> traceback(char[][] values, Point destination) {
		Point currentPoint = destination;
		List<Point> path = new ArrayList<Point>();
		if (values[destination.x][destination.y] == 255)
			return path;
		
		path.add(destination);
		
		while (values[currentPoint.x][currentPoint.y] != 0) {
			int currentVal = values[currentPoint.x][currentPoint.y];
			
			List<Point> possibleProvenance = this.getProvenances(currentPoint);
			for (Point tile : possibleProvenance) {
				if (values[tile.x][tile.y] == 255 || path.contains(tile))
					continue;
				
				int val = values[tile.x][tile.y];
				int dist = new Double(currentPoint.distance(tile)).intValue();
				if (val != currentVal - dist)
					continue;
				
				path.add(0, tile);
				currentPoint = tile;
				break;
			}
		}
		
		return path;
	}

	private List<Point> getProvenances(Point currentPoint) {
		List<Point> prov = new ArrayList<Point>();
		
		Tile tile = this.grid.getTile(currentPoint.x, currentPoint.y);
		if (tile.east == WallState.Wall)
			prov.addAll(this.grid.getAllOnWestPath(currentPoint));
		if (tile.west  == WallState.Wall)
			prov.addAll(this.grid.getAllOnEastPath(currentPoint));
		if (tile.north  == WallState.Wall)
			prov.addAll(this.grid.getAllOnSouthPath(currentPoint));
		if (tile.south  == WallState.Wall)
			prov.addAll(this.grid.getAllOnNorthPath(currentPoint));
		
		return prov;
	}
	
	private List<Point> goBackHome(int robotID){
		char[][] values = this.manhattanDistances(this.positions[robotID].getPoint());
		Point dest= BluetoothRobot.bt.beginningCorner;
		List<Point> path = new ArrayList<Point>();
		if (dest!=this.positions[robotID].getPoint() && values[dest.x][dest.y]!=255)
			 path = this.getPathTo(dest, values);
		return path;
	}
	
	
	public List<Point> goBackToAnyCorner(){
		char[][] values = this.manhattanDistances(this.positions[BluetoothRobot.bt.id].getPoint());
		Point corners[] = {
				new Point(0,0),
				new Point(0,this.grid.getWidth()-1),
				new Point(this.grid.getHeight()-1,0),
				new Point(this.grid.getHeight()-1,this.grid.getWidth()-1)
		};
		List<Point> path = new ArrayList<Point>();
		Point dest = corners[0];
		for (int i=1;i<4;i++){
			if (values[dest.x][dest.y] > values[corners[i].x][corners[i].y])
				dest=corners[i];
		}
		if (dest!=this.positions[BluetoothRobot.bt.id].getPoint() && values[dest.x][dest.y]!=255)
			 path = this.getPathTo(dest, values);
		return path;
	}


}

