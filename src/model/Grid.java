package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bluetooth.BluetoothRobot;


public class Grid {

	private Tile[][] tiles;
	private int height;
	private int width;
	private char[] probas;
	
	public Grid(int height, int width, WallState state) { // height = Xmax, width = Ymax
		this.tiles = new Tile[height][width];
		this.height = height;
		this.width = width;
		
		for (int line=0 ; line<height ; line++)
			for (int col=0 ; col<width ; col++)
				this.tiles[line][col] = new Tile(line, col, state);
	
		makeBorders();
		
		this.probas = new char[width*height*2];
		for (int i=0 ; i<this.probas.length ; i++) {
			if (i % 2 == 0 && i > 2 * width * (height - 1))
				this.probas[i] = 0;
			else
				this.probas[i] = 100;
		}
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
		try {
			return this.getTile(p.x, p.y);
		} catch (NullPointerException e) {
			BluetoothRobot.bt.send("DEBUG;Null pointer exception " + p.x + " " + p.y);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		return this.getTile(p.x, p.y);
	}
	
	public Tile getTile(int x, int y) {
		if(x >= 0 && x < height && y >= 0 && y < width)
			return tiles[x][y];
		else 
			return null;
	}
	
	public void setState(int x, int y, Direction direction, WallState state) {	
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
	
	public void setEmpty (int x, int y, Direction direction) {
		if(x >= 0 && y >= 0 && 
		   x < this.height && y < this.width)
			this.setState(x, y, direction, WallState.Empty);
	}

	public void setWall(int x, int y, Direction direction) {
		this.setState(x, y, direction, WallState.Wall);
	}

	
	public void makeBorders(){
		for(int row=0; row < this.height; row++){
			this.setWall(row, 0, Direction.WEST);
			this.setWall(row,  this.width-1, Direction.EAST);
		}
		
		for(int col=0; col < this.width; col++){
			this.setWall(0, col, Direction.NORTH);
			this.setWall(this.height-1, col , Direction.SOUTH);
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

	public Tile[] getNeighbors(Point current) {
		return this.getNeighbors(this.getTile(current));
	}
	
	public Tile[] getNeighbors(Tile tile) {
		Tile[] nei = new Tile[4];
		
		if (tile.getLine() > 0 && tile.north != WallState.Wall)
			nei[Direction.NORTH.ordinal()] = this.getTile(tile.getLine()-1, tile.getCol());
		
		if (tile.getCol() > 0 && tile.west != WallState.Wall)
			nei[Direction.WEST.ordinal()] = this.getTile(tile.getLine(), tile.getCol()-1);
		
		if (tile.getLine() < this.height-1 && tile.south != WallState.Wall)
			nei[Direction.SOUTH.ordinal()] = this.getTile(tile.getLine()+1, tile.getCol());
		
		if (tile.getCol() < this.width-1 && tile.east != WallState.Wall)
			nei[Direction.EAST.ordinal()] = this.getTile(tile.getLine(), tile.getCol()+1);
		
		return nei;
	}
	
	public int getProba (int row, int col, Direction dir) throws IllegalArgumentException {
		int idx = this.getProbaIdx(row, col, dir);
		return this.probas[idx];
	}
	
	public void setProba (int row, int col, Direction dir, int value) {
		try {
			int idx = this.getProbaIdx(row, col, dir);
			this.probas[idx] = (char)value;
		} catch (IllegalArgumentException e) {}
	}

	public int getProbaIdx (int row, int col, Direction dir) throws IllegalArgumentException {
		if (dir == Direction.NORTH) {
			dir = Direction.SOUTH;
			row = row-1;
		} else if (dir == Direction.WEST) {
			dir = Direction.EAST;
			col = col-1;
		}

		if (row < 0 || col < 0 ||
				(row == this.getHeight()-1 && dir == Direction.SOUTH) ||
				(col == this.getWidth()-1 && dir == Direction.EAST))
			throw new IllegalArgumentException("Impossible to change the probability of external walls");
		
		return 2* (row * this.getWidth() + col) + (dir == Direction.SOUTH ? 0 : 1);
	}
}
