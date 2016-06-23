package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.Config;
import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;

public class IA {

	private Position position;
	private Grid grid;
	
	public IA(Position position, Grid grid) {
		this.position = position;
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
		
		sol.aloneParkoor = this.allPossibleDestinations(position.getPoint());
		//sol.alonePath = this.traceback(sol.aloneParkoor, destination);
		
		int aloneCost = sol.aloneParkoor[destination.x][destination.y];
		this.parkoor(this.position.getPoint(), destination, sol, aloneCost);
		
		int coopCost = sol.coopParkoor == null ? 255 : Math.max(
				sol.coopParkoor[destination.x][destination.y],
				sol.aloneParkoor[sol.coopTile.x][sol.coopTile.y]
		);
		
		if (aloneCost <= coopCost) {
			sol.alonePath = this.traceback(sol.aloneParkoor, destination);
		} else {
			Tile t = this.grid.getTile(sol.coopTile.x, sol.coopTile.y);
			
			List<Direction> emptyWalls = new ArrayList<Direction>(4);
			if (t.east == WallState.Empty)
				emptyWalls.add(Direction.EAST);
			if (t.west == WallState.Empty)
				emptyWalls.add(Direction.WEST);
			if (t.north == WallState.Empty)
				emptyWalls.add(Direction.NORTH);
			if (t.south == WallState.Empty)
				emptyWalls.add(Direction.SOUTH);

			for (Direction dir : emptyWalls)
				this.grid.setWall(sol.coopTile.x, sol.coopTile.y, dir);
			sol.myCoopPath = this.traceback(sol.coopParkoor, destination);
			for (Direction dir : emptyWalls)
				this.grid.removeWall(sol.coopTile.x, sol.coopTile.y, dir);
			sol.otherCoopPath = this.traceback(sol.aloneParkoor, sol.coopTile);
		}
		
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
	public char[][] allPossibleDestinations (Point start) {
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
	
	public void parkoor (Point start, Point destination, Solution sol, int limit) {
		char[][] robotPositions = sol.aloneParkoor;
		char[][] best = null;
		Point bestTile = null;
		char minVal = 255;
		
		for (int i=0 ; i<robotPositions.length ; i++)
			for (int j=0 ; j<robotPositions[i].length ; j++) {
				if (robotPositions[i][j] == 255)
					continue;

				Tile t = this.grid.getTile(i, j);
				
				List<Direction> emptyWalls = new ArrayList<Direction>(4);
				if (t.east == WallState.Empty)
					emptyWalls.add(Direction.EAST);
				if (t.west == WallState.Empty)
					emptyWalls.add(Direction.WEST);
				if (t.north == WallState.Empty)
					emptyWalls.add(Direction.NORTH);
				if (t.south == WallState.Empty)
					emptyWalls.add(Direction.SOUTH);
	
				for (Direction dir : emptyWalls)
					this.grid.setWall(i, j, dir);
				
				char[][] tmp = this.parkoor(start, destination, limit);
				char score = (char)Math.max(
						tmp[destination.x][destination.y],
						robotPositions[i][j]
				);
				// TODO : Modifier le score en fonction du temps d'arrivée de l'autre robot
				if (score < minVal) {
					best = tmp;
					minVal = score;
					bestTile = new Point(i, j);
				}
				
				for (Direction dir : emptyWalls)
					this.grid.removeWall(i, j, dir);
			}
		
		sol.coopParkoor = best;
		sol.coopTile = bestTile;
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
	
}
