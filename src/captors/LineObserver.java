package captors;

import lejos.nxt.Motor;
import main.Config;
import api.Observable;
import api.Observer;

public class LineObserver implements Observer {
	
	private Movement move;

	public LineObserver(Movement move) {
		this.move = move;
	}
	
	private double getAngle(long timeshift) {
		double v = new Double(Motor.A.getSpeed()) * Config.WHEEL_CIRCUMFERENCE / 360.0; // meters/seconds
		double deltaD = v * new Double(timeshift) / 1000.0; // meters
		double angle = Math.atan(deltaD / Config.LIGHTS_DISTANCE) * 360.0 / (2*Math.PI);
		
		return angle;
	}

	@Override
	public void update(Observable o, Object arg) {
		long timeshift = (Long)arg;
		double angle = this.getAngle(timeshift);
		if (angle > 1.0)
			this.move.correctAngle(angle);
		return;
	}

}
