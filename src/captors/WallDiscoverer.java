package captors;

import java.awt.Point;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import model.Position;
import api.Observable;
import api.Observer;

public class WallDiscoverer extends Observable implements Runnable {
	
	public static final int MIN_DIST = 10;
	
	private boolean stopped;
	private boolean isInFrontPosition;
	private UltrasonicSensor front;
	private UltrasonicSensor back;
	private Position robotPosition;
	
	
	public WallDiscoverer (Position robot) {
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.back = new UltrasonicSensor(SensorPort.S1);
		this.front.continuous();
		this.back.continuous();
		this.isInFrontPosition = false;
		
		this.robotPosition = robot;
	}
	
	public void changeHeadPosition () {
		if (this.isInFrontPosition)
			Motor.C.rotateTo(0, false);
		else
			Motor.C.rotateTo(90, false);
		this.isInFrontPosition = !this.isInFrontPosition;
	}
	
	public boolean isInFrontPosition () {
		return this.isInFrontPosition;
	}

	@Override
	public void run() {
		this.stopped = false;
		
		while (!this.stopped) {
			double x = this.robotPosition.getX();
			double y = this.robotPosition.getY();
			
			double dx = x - Math.floor(x);
			double dy = y - Math.floor(y);
			
			// Si le robot est au milieu de la case
			if (dx > 0.3 && dx < 0.7 && dy > 0.3 && dy < 0.7) {
				this.checkForWalls();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void checkForWalls() {
		// TODO Auto-generated method stub
		
	}

	public void stop () {
		this.stopped = true;
	}

}
