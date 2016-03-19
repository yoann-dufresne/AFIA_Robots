package model;

public enum Direction {
	NORTH, EAST, SOUTH, WEST;
	
	public Direction turnLeft () {
		return Direction.values()[(this.ordinal()-1)%Direction.values().length];
	}
	
	public Direction turnRight () {
		return Direction.values()[(this.ordinal()+1)%Direction.values().length];
	}
}
