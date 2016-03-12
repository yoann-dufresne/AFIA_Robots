package captors;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import main.Config;

public class Movement {
	
	private NXTRegulatedMotor left;
	private NXTRegulatedMotor right;
	
	private boolean interrupted;

	public Movement() {
		this.right = Motor.A;
		this.left = Motor.B;
	}
	
	// ----------- Basic movements -----------
	
	public void forward (double distance, boolean stop) {
		this.interrupted = false;
		
		// Circonf / 360 == Distance / xxx 
		int angle = new Double(360.0 * distance / Config.WHEEL_CIRCUMFERENCE).intValue();
		
		this.right.rotateTo(this.right.getPosition() + angle, true);
		this.left.rotateTo(this.left.getPosition() + angle, true);
		
		while (this.interrupted || this.right.isMoving()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}/**/
		
		if (stop) {
			this.right.stop();
			this.left.stop();
		}
	}
	
	public void rotate (double degree) {
		double wheel = Config.WHEELS_DISTANCE * degree / (2 * Config.WHEEL_RADIUS);
		this.right.rotateTo(new Double(this.right.getPosition() - wheel).intValue(), true);
		this.left.rotateTo(new Double(this.left.getPosition() + wheel).intValue());
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
	
	
	// ------------ Angle corrections ------------
	
	public void correctAngle (double angle) {
		int leftAngle = this.left.getLimitAngle() - this.left.getPosition();
		int rightAngle = this.right.getLimitAngle() - this.right.getPosition();
		
		this.interrupted = true;
		
		this.rotate(angle);
		this.left.rotateTo(this.left.getPosition() + leftAngle, true);
		this.right.rotateTo(this.right.getPosition() + rightAngle, true);
		
		this.interrupted = false;
	}
	
}
