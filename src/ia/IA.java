package ia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import api.Map;
import model.Grid;
import model.Position;

public class IA {

	private Position position;
	private Grid grid;
	
	public IA(Position position, Grid grid) {
		this.position = position;
		this.grid = grid;
	}
	
	public List<Point> goTo (int row, int col) {
		Point pos = new Point(
				new Double(Math.floor(this.position.getX())).intValue(),
				new Double(Math.floor(this.position.getY())).intValue()
		);
		Map<Point, Integer> values = new Map<Point, Integer>();
		values.put(pos, 0);
		
		Point dest = new Point(row, col);
		values = this.parkoor(values, pos, dest);
		
		List<Point> path = new ArrayList<Point>(1);
		if (values.containsKey(dest)) {
			path = this.traceback(values, dest);
		}
		
		return path;
	}
	
	public Map<Point, Integer> allPossibleDestinations (Map<Point, Integer> values, Point start) {
		List<Point> positions = new ArrayList<Point>();
		positions.add(start);
		
		while (positions.size() != 0) {
			Point first = positions.remove(0);
			int prevVal = values.get(first);
			
			Point[] tiles = this.grid.getAllFarthests(first);
			for (Point tile : tiles) {
				int dist = new Double(first.distance(tile)).intValue();
				if (!values.containsKey(tile) || values.get(tile) > prevVal + dist) {
					values.put(tile, prevVal + dist);
					this.insertInList(tile, positions, values);
				}
			}
		}
		
		return values;
	}

	public Map<Point, Integer> parkoor (Map<Point, Integer> values, Point start, Point destination) {
		List<Point> positions = new ArrayList<Point>();
		positions.add(start);
		
		while (positions.size() != 0 && !positions.get(0).equals(destination)) {
			if (positions.size() > 1) {
				//System.out.println(values.get(positions.get(0)) + " " + values.get(positions.get(1)));
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Point first = positions.remove(0);
			int prevVal = values.get(first);
			
			Point[] tiles = this.grid.getAllFarthests(first);
			for (Point tile : tiles) {
				int dist = new Double(first.distance(tile)).intValue();
				if (!values.containsKey(tile) || values.get(tile) > prevVal + dist) {
					values.put(tile, prevVal + dist);
					this.insertInList(tile, positions, values);
				}
			}
		}
		
		return values;
	}

	private void insertInList(Point p, List<Point> positions, Map<Point, Integer> values) {
		int idx = 0;
		while (positions.size() > idx && values.get(p) > values.get(positions.get(idx)))
			idx++;
		positions.add(idx, p);
	}
	
	private List<Point> traceback(Map<Point, Integer> values, Point first) {
		Point currentPoint = first;
		List<Point> path = new ArrayList<Point>();
		path.add(first);
		
		while (values.get(currentPoint) != 0) {
			int currentVal = values.get(currentPoint);
			
			List<Point> possibleProvenance = this.grid.getAllPaths(currentPoint);
			for (Point tile : possibleProvenance) {
				if (!values.containsKey(tile) || path.contains(tile))
					continue;
				
				int val = values.get(tile);
				int dist = new Double(currentPoint.distance(tile)).intValue();
				if (val > currentVal - dist)
					continue;
				
				path.add(0, tile);
				currentPoint = tile;
				break;
			}
		}
		
		return path;
	}
	
}
