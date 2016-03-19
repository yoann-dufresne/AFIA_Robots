package captors;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import main.Config;
import model.Direction;
import model.Position;

public class Movement {
	
	private NXTRegulatedMotor left;
	private NXTRegulatedMotor right;
	
	private int prevAngle;
	
	private boolean interrupted;
	private Position position;

	public Movement(Position position) {
		this.right = Motor.A;
		this.left = Motor.B;
		this.position = position;
	}
	
	// ----------- Basic movements -----------
	
	public void forward (double distance, boolean stop) {
		this.interrupted = false;
		
		// Circonf / 360 == Distance / xxx 
		int angle = new Double(360.0 * distance / Config.WHEEL_CIRCUMFERENCE).intValue();
		
		this.prevAngle = this.left.getPosition();
		
		this.right.rotateTo(this.right.getPosition() + angle, true);
		this.left.rotateTo(this.left.getPosition() + angle, true);
		
		while (this.interrupted || this.right.isMoving()) {
			try {
				Thread.sleep(10);
				this.updateModel ();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}/**/
		
		if (stop) {
			this.right.stop();
			this.left.stop();
		}
	}
	
	public void updateModel() {
		int newAngle = this.left.getPosition();
		
		// int angle = new Double(360.0 * distance / Config.WHEEL_CIRCUMFERENCE).intValue();
		double dist = new Double(newAngle - this.prevAngle) * Config.WHEEL_CIRCUMFERENCE / 360.0;
		double proportion = dist / Config.TILE_SIZE;
		
		switch (this.position.getDirection()) {
		case NORTH:
			this.position.updateX(-proportion);
			break;
		case SOUTH:
			this.position.updateX(proportion);
			break;
		case EAST:
			this.position.updateY(proportion);
			break;
		case WEST:
			this.position.updateY(-proportion);
			break;

		default:
			break;
		}
		
		this.prevAngle = newAngle;
	}

	public void rotate (double degree) {
		double wheel = Config.WHEELS_DISTANCE * degree / (2 * Config.WHEEL_RADIUS);
		this.right.rotateTo(new Double(this.right.getPosition() - wheel).intValue(), true);
		this.left.rotateTo(new Double(this.left.getPosition() + wheel).intValue());
	}
	
	public void turnRight () {
		this.rotate(90);
		this.position.turnRight();
	}
	
	public void turnLeft () {
		this.rotate(-90);
		this.position.turnRight();
	}
	
	public void uTurn () {
		this.rotate(180);
		this.position.turnRight();
		this.position.turnRight();
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
