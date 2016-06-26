package captors;

import java.awt.Point;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import model.Direction;
import model.Position;
import api.Observable;
import api.Observer;

public class WallDetector extends Observable implements Runnable {
	
	public static final int MIN_DIST = 20;
	
	private boolean stopped;
	private boolean isInFrontPosition;
	private UltrasonicSensor front;
	
	private int distance;

	private Position pos;
	
	public WallDetector(Position pos) {
		this.pos = pos;
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.front.continuous();
		this.isInFrontPosition = true;
		this.changeHeadPosition();
		this.distance= 255;
		this.stopped = false;
	}
	
	public void changeHeadPosition () {
		if (this.isInFrontPosition)
			Motor.C.rotateTo(0, false);
		else
			Motor.C.rotateTo(95, false);
		this.isInFrontPosition = !this.isInFrontPosition;
	}
	
	public boolean isInFrontPosition () {
		return this.isInFrontPosition;
	}

	@Override
	public void run() {
		this.stopped = false;
		
		while (!this.stopped) {
			this.checkForWall();
		}
	}
	
	private void checkForWall() {
		if (!this.isInFrontPosition())
			this.changeHeadPosition();
		this.distance= this.front.getDistance();
		if (this.distance < WallDetector.MIN_DIST){
			Point current = this.pos.getPoint();
			Direction dir = this.pos.getDirection();
			this.notifyObservers();
			
			while (!this.stopped && this.pos.getPoint().equals(current) && this.pos.getDirection().equals(dir)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void notifyObservers () {
		for (Observer obs : this.observers)
			obs.update(this, this.distance);
	}

	public void stop () {
		this.stopped = true;
	}

}

