package model;

public enum Direction {
	NORTH, EAST, SOUTH, WEST;
	
	public Direction turnLeft () {
		return Direction.values()[(this.ordinal()-1)%Direction.values().length];
	}
	
	public Direction turnRight () {
		return Direction.values()[(this.ordinal()+1)%Direction.values().length];
	}

	public int turnTo(Direction wantedDir) {
		int diff = (this.ordinal() - wantedDir.ordinal() + Direction.values().length) % Direction.values().length;
		int diff2 = (this.ordinal() + wantedDir.ordinal()) % Direction.values().length;
		if (Math.abs(diff) < Math.abs(diff2))
			return diff;
		else 
			return diff2;
	}
}
