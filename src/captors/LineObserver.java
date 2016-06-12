package captors;

import lejos.nxt.Motor;
import lejos.nxt.Sound;
import main.Config;
import model.Position;
import api.Observable;
import api.Observer;

public class LineObserver implements Observer {
	
	private Movement move;
	private Position pos;

	public LineObserver(Movement move, Position pos) {
		this.move = move;
		this.pos = pos;
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
		
		Sound.setVolume(60);
		Sound.beep();
		Sound.setVolume(0);
		
		if (Math.abs(angle) > 1.0)
			this.move.correctAngle(angle);
		
		switch (this.pos.getDirection()) {
		case NORTH:
			this.pos.setX(Math.round(this.pos.getX()) + Config.CORRECTION_RATIO);
			break;
		case SOUTH:
			this.pos.setX(Math.round(this.pos.getX()) - Config.CORRECTION_RATIO);
			break;
		case EAST:
			this.pos.setY(Math.round(this.pos.getY()) - Config.CORRECTION_RATIO);
			break;
		case WEST:
			this.pos.setY(Math.round(this.pos.getY()) + Config.CORRECTION_RATIO);
			break;
		}
		
		return;
	}

}
