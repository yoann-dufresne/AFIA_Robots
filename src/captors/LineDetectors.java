package captors;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.Color;
import api.Observable;
import api.Observer;

public class LineDetectors extends Observable implements Runnable {
	
	private long lastDetectLeft;
	private long lastDetectRight;
	
	private int refValue;
	
	private ColorSensor leftSensor;
	private ColorSensor rightSensor;
	
	private boolean stopped;
	
	public LineDetectors() {
		this.leftSensor = new ColorSensor(SensorPort.S4, Color.RED);
		this.rightSensor = new ColorSensor(SensorPort.S3, Color.RED);
		this.lastDetectLeft = this.lastDetectRight = 0;
		this.stopped = false;
	}

	@Override
	public void run() {
		this.refValue = (this.leftSensor.getLightValue() + this.rightSensor.getLightValue()) / 2;
		
		while (true) {
			if (this.stopped)
				break;
			
			this.detectLine();
			this.detectEndLine();
		}
	}
	
	private boolean isLine (ColorSensor cs) {
		if (Math.abs(cs.getLightValue() - this.refValue) > 10)
			return true;
		return false;
	}
	
	private void detectLine () {
		while (this.lastDetectLeft == 0 || this.lastDetectRight == 0) {
			if (this.stopped)
				return;
			if (this.lastDetectLeft == 0 && this.isLine(this.leftSensor)) {
				this.lastDetectLeft = System.currentTimeMillis();
			}
			if (this.lastDetectRight == 0 && this.isLine(this.rightSensor)) {
				this.lastDetectRight = System.currentTimeMillis();
			}
		}
		this.notifyObservers();
	}
	
	private void detectEndLine () {
		while (this.lastDetectLeft != 0 || this.lastDetectRight != 0) {
			if (this.stopped)
				return;
			if (this.lastDetectLeft != 0 && !this.isLine(this.leftSensor)) {
				this.lastDetectLeft = 0;
			}
			if (this.lastDetectRight != 0 && !this.isLine(this.rightSensor)) {
				this.lastDetectRight = 0;
			}
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stop () {
		this.stopped = true;
	}
	
	@Override
	public void notifyObservers() {
		for (Observer obs : this.observers) {
			long val = new Long(this.lastDetectLeft-this.lastDetectRight);
			obs.update(this, val);
		}
	}
	
}
