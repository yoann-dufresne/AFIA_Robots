package captors;

import java.awt.Point;
import java.util.Arrays;

import bluetooth.BluetoothRobot;
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
	private Position robotPosition;
	private Movement movement;
		
//	private int[] tmpDists;
	private int distance;
	
	public WallDetector(Position robot, Movement move) {
		this.front = new UltrasonicSensor(SensorPort.S2);
		this.front.continuous();
		this.isInFrontPosition = false;
		
		this.robotPosition = robot;
//		this.tmpDists = new int[5];
		this.movement = move;
		
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
//		this.distance =this.middleDist(this.front);
		if (this.distance< this.MIN_DIST){
			this.notifyObservers();
		}
	
//	private int middleDist (UltrasonicSensor sensor) {
//		for (int i=0 ;i<this.tmpDists.length ; i++)
//			this.tmpDists[i] = sensor.getDistance();
//		Arrays.sort(this.tmpDists);
//		
//		return this.tmpDists[3];
//	}
	
	@Override
	public void notifyObservers () {
		BluetoothRobot.bt.send("DEBUG;ultrasons " + this.robotPosition + "Wall detected");
		
		for (Observer obs : this.observers)
			obs.update(this, this.distance);
	}

	public void stop () {
		this.stopped = true;
	}

}

