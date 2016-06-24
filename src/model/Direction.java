package model;

import java.awt.Point;

public enum Direction {
	NORTH, EAST, SOUTH, WEST;
	
	public Direction turnLeft () {
		return Direction.values()[(Direction.values().length + this.ordinal()-1)%Direction.values().length];
	}
	
	public Direction turnRight () {
		return Direction.values()[(this.ordinal()+1)%Direction.values().length];
	}

	public int turnTo(Direction wantedDir) {
		if (Math.abs(this.ordinal() - wantedDir.ordinal()) == 2)
			return 2;
		
		if (this.ordinal() - wantedDir.ordinal() == 0)
			return 0;
		 
		if (this == NORTH) {
			if (wantedDir == WEST)
				return -1;
			else
				return 1;
		} else if (this == EAST) {
			if (wantedDir == NORTH)
				return -1;
			else
				return 1;
		} else if (this == SOUTH) {
			if (wantedDir == EAST)
				return -1;
			else
				return 1;
		} else {
			if (wantedDir == SOUTH)
				return -1;
			else
				return 1;
		}
	}
	
	public static Direction directionFromString (String name) {
		for (Direction dir : Direction.values()) {
			if (dir.toString().toLowerCase().equals(name.toLowerCase()))
				return dir;
		}
		return null;
	}
	
	public static Direction getDirectionBetween (Point from, Point to) {
		if (from.x < to.x)
			return SOUTH;
		else if (from.x > to.x)
			return NORTH;
		else if (from.y > to.y)
			return WEST;
		else
			return EAST;
	}
}
