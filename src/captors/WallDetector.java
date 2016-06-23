package captors;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import api.Observable;
import api.Observer;

public class WallDetector extends Observable implements Runnable {
	
	public static final int MIN_DIST = 20;
	
	private boolean stopped;
	private boolean isInFrontPosition;
	private UltrasonicSensor front;
	
	private int distance;
	
	public WallDetector() {
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.front.continuous();
		this.isInFrontPosition = true;
		this.changeHeadPosition();
		this.distance= 255;
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
			
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private void checkForWall() {
		if (!this.isInFrontPosition())
			this.changeHeadPosition();
		this.distance= this.front.getDistance();
		if (this.distance< WallDetector.MIN_DIST){
			this.notifyObservers();
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

