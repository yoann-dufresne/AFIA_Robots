package model;

public class Position {
	
	private double posX;
	private double posY;
	private Direction direction;
	
	public Position(double x, double y, Direction direction) {
		this.posX = x;
		this.posY = y;
		this.direction = direction;
	}

	public double getX() {
		return posX;
	}
	
	public double getY() {
		return posY;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void updateX(double dx) {
		posX += dx;
	}
	
	public void updateY(double dy) {
		posY += dy;
	}
	
	public void turnRight () {
		this.direction = this.direction.turnRight();
	}
	
	public void turnLeft () {
		this.direction = this.direction.turnLeft();
	}

	public void setX(double x) {
		this.posX = x;
	}

	public void setY(double y) {
		this.posY = y;
	}
}
