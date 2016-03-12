package captors;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import api.Observable;
import api.Observer;

public class WallDetectors extends Observable implements Runnable {
	
	public static final int MIN_DIST = 10;
	
	private boolean stopped;
	private boolean isInFrontPosition;
	private UltrasonicSensor front;

	private boolean testSides;
	
	public WallDetectors () {
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.front.continuous();
		this.isInFrontPosition = false;
		this.testSides = true;
	}
	
	public void changeHeadPosition () {
		if (this.isInFrontPosition)
			Motor.C.rotateTo(0, false);
		else
			Motor.C.rotateTo(90, false);
		this.isInFrontPosition = !this.isInFrontPosition;
	}

	@Override
	public void notifyObservers () {
		for (Observer obs : this.observers)
			obs.update(this, new Integer(this.front.getDistance()));
	}
	
	public boolean isInFrontPosition () {
		return this.isInFrontPosition;
	}

	@Override
	public void run() {
		this.stopped = false;
		
		while (!this.stopped) {
			if (this.testSides) {
				
			} else {
				int dist = this.front.getDistance();
				if (dist <= MIN_DIST)
					this.notifyObservers();
			}
		}
	}
	
	public void stop () {
		this.stopped = true;
	}

}
