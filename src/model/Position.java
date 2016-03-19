package model;

import api.Observable;
import api.Observer;

public class Position extends Observable {
	
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
		this.posX += dx;
		
		double floatPart = this.posX - Math.round(this.posX);
		if(abs(floatPart - 0.5) < 0.1){
			this.notifyObservers();
		}
	}
	
	public void updateY(double dy) {
		this.posY += dy;
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

	@Override
	public void notifyObservers() {
		for (Observer obs : this.observers)
			obs.update(this, null);
	}
}
