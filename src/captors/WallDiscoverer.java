package captors;

import java.awt.Point;
import java.util.Arrays;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import model.Direction;
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
	private Point previous;
	private Movement synchro;
	
	private int[] tmpDists;
	private int[] previousDistances;
	
	public WallDiscoverer (Position robot, Movement move) {
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.back = new UltrasonicSensor(SensorPort.S1);
		this.front.continuous();
		this.back.continuous();
		this.isInFrontPosition = false;
		
		this.robotPosition = robot;
		this.previousDistances = new int[4];
		this.previous = new Point(-1, -1);
		
		this.tmpDists = new int[3];
		this.synchro = move;
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
			double x = this.robotPosition.getX();
			double y = this.robotPosition.getY();
			Point pos = this.robotPosition.getPoint();
			
			if (!this.previous.equals(pos)) {
				double dx = x - Math.floor(x);
				double dy = y - Math.floor(y);
				
				// Si le robot est au milieu de la case
				if (dx > 0.3 && dx < 0.7 && dy > 0.3 && dy < 0.7) {
					this.previous = pos;
					synchronized (this.synchro) {
						this.checkForWalls();
					}
					this.notifyObservers();
				}
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void checkForWalls() {
		Direction dir = this.robotPosition.getDirection();
		
		this.previousDistances[dir.ordinal()] = this.middleDist(this.front);
		this.previousDistances[(dir.ordinal() + 2) % 4] = -1;
		
		this.changeHeadPosition();
		
		this.previousDistances[(dir.ordinal() + 3) % 4] = this.middleDist(this.front);
		this.previousDistances[(dir.ordinal() + 1) % 4] = this.middleDist(this.back);
		
		this.changeHeadPosition();
	}
	
	private int middleDist (UltrasonicSensor sensor) {
		for (int i=0 ;i<this.tmpDists.length ; i++)
			this.tmpDists[i] = sensor.getDistance();
		Arrays.sort(this.tmpDists);
		
		return this.tmpDists[1];
	}
	
	@Override
	public void notifyObservers () {
		for (Observer obs : this.observers)
			obs.update(this, this.previousDistances);
	}

	public void stop () {
		this.stopped = true;
	}

}
