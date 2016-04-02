package model;

public class Tile {

	private int line;
	private int col;
	
	public boolean north;
	public boolean south;
	public boolean east;
	public boolean west;
	
	public Tile(int line, int col) {
		this.line = line;
		this.col = col;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
	
}
