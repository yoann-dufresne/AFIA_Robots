package model;

public class Tile {

	private int line;
	private int col;
	
	public WallState north;
	public WallState south;
	public WallState east;
	public WallState west;
	
	public Tile(int line, int col, WallState state) {
		this.line = line;
		this.col = col;

		this.north = state;
		this.south = state;
		this.east = state;
		this.west = state;
	}
	
	public Tile(int line, int col) {
		this(line, col, WallState.Undiscovered);
	}
	
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
	
	public WallState getState(Direction d){
		if(d == Direction.NORTH)
			return this.north;
		else if(d == Direction.WEST)
			return this.west;
		else if(d == Direction.EAST)
			return this.east;
		else 
			return this.south;

	}
}
