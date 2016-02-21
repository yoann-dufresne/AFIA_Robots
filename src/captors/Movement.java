package captors;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import api.Observable;
import api.Observer;

public class Movement implements Observer {
	
	public static final double R_WHEEL = 0.042;
	public static final double CIRCUMFERENCE_WHEEL = 2 * Math.PI * R_WHEEL;
	public static final double D_SENSORS = 0.0825;
	public static final double D_WHEELS = 0.175 - 0.022;
	public static final double CIRCUMFERENCE_INTER_WHEEL = D_WHEELS * Math.PI;
	
	private boolean line;
	private boolean wall;
	private double angle;
	
	private NXTRegulatedMotor left;
	private NXTRegulatedMotor right;

	public Movement(LineDetectors ld, WallDetectors wd) {
		this.line = false;
		this.wall = false;
		ld.addObserver(this);
		wd.addObserver(this);
		
		this.right = Motor.A;
		this.left = Motor.B;
	}
	
	public void goToNextLine (boolean stop) {
		this.line = this.wall = false;

		Motor.A.rotateTo(Motor.A.getPosition() + 3600, true);
		Motor.B.rotateTo(Motor.B.getPosition() + 3600, true);
		
		while (!this.line && !this.wall)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		if (stop) {
			Motor.A.stop(true);
			Motor.B.stop(true);
		}
	}
	
	public void goToNextWall () {
		this.wall = false;
		
		Motor.A.rotateTo(Motor.A.getPosition() + 36000, true);
		Motor.B.rotateTo(Motor.B.getPosition() + 36000, true);
		
		while (!this.wall) {
			if (this.line && Math.abs(this.angle) > 5) {
				this.correctAngle ();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Motor.A.stop(true);
		Motor.B.stop(true);
	}
	
	public void rotate (double degree) {
		double wheel = D_WHEELS * degree / R_WHEEL;
		Motor.A.rotateTo(new Double(Motor.A.getPosition() - wheel).intValue(), true);
		Motor.B.rotateTo(new Double(Motor.B.getPosition() + wheel).intValue());
	}
	
	public void turnRight () {
		this.rotate(90);
	}
	
	public void turnLeft () {
		this.rotate(-90);
	}
	
	public void uTurn () {
		this.rotate(180);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof LineDetectors) {
			long shift = (Long)arg;
			this.angle = this.getAngle (shift);
			//System.out.println(this.angle);
			this.line = true;
		} else if (o instanceof WallDetectors) {
			this.wall = true;
			int dist = (Integer)arg;
			//System.out.println(dist);
		}
	}

	private double getAngle(long timeshift) {
		double v = new Double(Motor.A.getSpeed()) * CIRCUMFERENCE_WHEEL / 360.0; // meters/seconds
		double deltaD = v * new Double(timeshift) / 1000.0; // meters
		double angle = Math.atan(deltaD / D_SENSORS) * 360.0 / (2*Math.PI);
		
		return angle;
	}
	
	private void correctAngle () {
		int leftAngle = this.left.getLimitAngle() - this.left.getPosition();
		int rightAngle = this.right.getLimitAngle() - this.right.getPosition();
		System.out.println("Angle : " + this.angle);
		this.rotate(this.angle / 2);
		this.left.rotateTo(this.left.getPosition() + leftAngle, true);
		this.right.rotateTo(this.right.getPosition() + rightAngle, true);
		this.line = false;
	}
	
}
