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
		
		List<Point> path = new ArrayList<Point>();
		if (values.containsKey(dest)) {
			path.add(dest);
			this.traceback(values, path);
		}
		
		return path;
	}

	public Map<Point, Integer> parkoor (Map<Point, Integer> values, Point start, Point destination) {
		List<Point> positions = new ArrayList<Point>();
		positions.add(start);
		
		while (positions.size() != 0 && !positions.get(0).equals(destination)) {
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
		while (positions.size() > idx && values.get(p) < values.get(positions.get(idx)))
			idx++;
		positions.add(idx, p);
	}
	
	private void traceback(Map<Point, Integer> values, List<Point> path) {
		Point lastPoint = path.get(0);
		if (values.get(lastPoint) == 0)
			return;
		
		int lastVal = values.get(lastPoint);
		List<Point> possibleProvenance = this.grid.getAllPaths(lastPoint);
		for (Point tile : possibleProvenance) {
			if (!values.containsKey(tile) || path.contains(tile))
				continue;
			
			int val = values.get(tile);
			int dist = new Double(lastPoint.distance(tile)).intValue();
			if (val > lastVal - dist)
				continue;
			
			path.add(0, tile);
			break;
		}
		
		this.traceback(values, path);
	}
	
}
