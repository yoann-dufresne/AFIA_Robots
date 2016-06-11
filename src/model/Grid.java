package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class Grid {

	private Tile[][] tiles;
	private int height;
	private int width;
	
	public Grid(int height, int width, WallState state) { // height = Xmax, width = Ymax
		this.tiles = new Tile[height][width];
		this.height = height;
		this.width = width;
		
		for (int line=0 ; line<height ; line++)
			for (int col=0 ; col<width ; col++)
				this.tiles[line][col] = new Tile(line, col, state);
	
		makeBorders();
	}
	
	public Grid(int height, int width){
		this(height, width, WallState.Undiscovered);
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	
	public Tile getTile(Point p){
		return this.getTile(p.x, p.y);
	}
	
	public Tile getTile(int x, int y) {
		if(x >= 0 && x < height && y >= 0 && y < width)
			return tiles[x][y];
		else 
			return null;
	}
	

	public void addWall(int x, int y, Direction direction) {
		this.setTile(x, y, direction, WallState.Wall);
	}
	
	public void removeWall(int x, int y, Direction direction) {
		this.setTile(x, y, direction, WallState.Empty);
	}

	
	public void makeBorders(){
		for(int row=0; row < this.height; row++){
			this.addWall(row, 0, Direction.WEST);
			this.addWall(row,  this.width-1, Direction.EAST);
		}
		
		for(int col=0; col < this.width; col++){
			this.addWall(0, col, Direction.NORTH);
			this.addWall(this.height-1, col , Direction.SOUTH);
		}
	}
	
	/**
	 * Get the farthest point before a wall following the delta direction (dx or dy)
	 * @param tile The begin tile
	 * @param dir Direction to follow
	 * @return The farthest tile before a wall.
	 */
	private Point getFarthest (Point tile, Direction dir) {
		switch (dir) {
		case NORTH:
			if (this.tiles[tile.x][tile.y].north == WallState.Wall)
				return tile;
			else return this.getFarthest(new Point(tile.x-1, tile.y), dir);
			
		case SOUTH:
			if (this.tiles[tile.x][tile.y].south == WallState.Wall)
				return tile;
			else return this.getFarthest(new Point(tile.x+1, tile.y), dir);
			
		case WEST: 
			if (this.tiles[tile.x][tile.y].west == WallState.Wall)
				return tile;
			else return this.getFarthest(new Point(tile.x, tile.y-1), dir);
			
		case EAST:
			if (this.tiles[tile.x][tile.y].east == WallState.Wall)
				return tile;
			else return this.getFarthest(new Point(tile.x, tile.y+1), dir);
			
		default:
			return null;
		}
	}
	
	public Point getFarthestWest (Point tile) {
		return this.getFarthest(tile, Direction.WEST);
	}
	
	public Point getFarthestEast (Point tile) {
		return this.getFarthest(tile, Direction.EAST);
	}
	
	public Point getFarthestNorth (Point tile) {
		return this.getFarthest(tile, Direction.NORTH);
	}
	
	public Point getFarthestSouth (Point tile) {
		return this.getFarthest(tile, Direction.SOUTH);
	}
	
	public Point[] getAllFarthests (Point tile) {
		Point[] points = new Point[4];
		points[Direction.EAST.ordinal()] = this.getFarthestEast(tile);
		points[Direction.WEST.ordinal()] = this.getFarthestWest(tile);
		points[Direction.NORTH.ordinal()] = this.getFarthestNorth(tile);
		points[Direction.SOUTH.ordinal()] = this.getFarthestSouth(tile);
		
		return points;
	}
	
	
	/**
	 * Get all the points before a wall following the delta direction (dx or dy)
	 * @param tile The begin tile
	 * @param dir Direction to follow
	 * @return The farthest tile before a wall.
	 */
	private List<Point> getAllOnPath (Point tile, Direction dir) {
		List<Point> tiles = null;
		
		switch (dir) {
		case NORTH:
			if (this.tiles[tile.x][tile.y].north == WallState.Wall)
				tiles = new ArrayList<Point>();
			else
				tiles = this.getAllOnPath(new Point(tile.x-1, tile.y), dir);
			tiles.add(tile);
			break;
			
		case SOUTH:
			if (this.tiles[tile.x][tile.y].south == WallState.Wall)
				tiles = new ArrayList<Point>();
			else
				tiles = this.getAllOnPath(new Point(tile.x+1, tile.y), dir);
			tiles.add(tile);
			break;
			
		case WEST:
			if (this.tiles[tile.x][tile.y].west == WallState.Wall)
				tiles = new ArrayList<Point>();
			else
				tiles = this.getAllOnPath(new Point(tile.x, tile.y-1), dir);
			tiles.add(tile);
			break;
			
		case EAST:
			if (this.tiles[tile.x][tile.y].east == WallState.Wall)
				tiles = new ArrayList<Point>();
			else
				tiles = this.getAllOnPath(new Point(tile.x, tile.y+1), dir);
			tiles.add(tile);
			break;
		}
		
		return tiles;
	}
	
	public List<Point> getAllOnWestPath (Point tile) {
		return this.getAllOnPath(tile, Direction.WEST);
	}
	
	public List<Point> getAllOnEastPath (Point tile) {
		return this.getAllOnPath(tile, Direction.EAST);
	}
	
	public List<Point> getAllOnNorthPath (Point tile) {
		return this.getAllOnPath(tile, Direction.NORTH);
	}
	
	public List<Point> getAllOnSouthPath (Point tile) {
		return this.getAllOnPath(tile, Direction.SOUTH);
	}
	
	/**
	 * Get all the points on east, west, north and south roads starting from tile.
	 * @param tile Starting point
	 * @return All the points
	 */
	public List<Point> getAllPaths (Point tile) {
		List<Point> tiles = new ArrayList<Point>();
		tiles.addAll(this.getAllOnEastPath(tile));
		tiles.addAll(this.getAllOnWestPath(tile));
		tiles.addAll(this.getAllOnNorthPath(tile));
		tiles.addAll(this.getAllOnSouthPath(tile));
		
		return tiles;
	}
	
	public void setTile(int x, int y, Direction direction, WallState state) {	
		switch (direction) {
		case NORTH:
			this.getTile(x, y).north = state;
			if(x-1 >= 0)
				this.getTile(x-1, y).south = state;
			break;
		case SOUTH:
			this.getTile(x, y).south = state;
			if(x+1 < this.height)
				this.getTile(x+1, y).north = state;
			break;
		case EAST:
			this.getTile(x, y).east = state;
			if(y+1 < this.width)
				this.getTile(x, y+1).west = state;
			break;
		case WEST:
			this.getTile(x, y).west = state;
			if(y-1 >= 0)
				this.getTile(x, y-1).east = state;
			break;
		}
	}
	
	public void setDiscovered(int x, int y, Direction direction) {
		if(x >= 0 && y >= 0 && 
		   x < this.height && y < this.width)
			setTile(x, y, direction, WallState.Empty);
	}
	
	//replace the point coordinate by the next one on the given direction
	public void translatePoint(Point p, Direction d){
		switch(d){
		case NORTH:
			if (p.x>0)
				p.translate(-1,0);
			break;				
		case EAST:
			if (p.y<width)
				p.translate(0,1);
			break;				
		case SOUTH:
			if (p.x<height)
				p.translate(1,0);
			break;
		case WEST:
			if (p.y>0)
				p.translate(0,-1);
			break;
		}
	}

}
