package captors;

import java.awt.Point;
import java.util.List;

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
	public Position position;
	private boolean pathStopped;

	public Movement(Position position) {
		this.right = Motor.A;
		this.left = Motor.B;
		this.position = position;
	}
	
	public void followPath(List<Point> path) {
		this.pathStopped = false;
		System.out.println("Go !");
		
		for(Point p: path){
			if (this.pathStopped) {
				System.out.println("Stopped !");
				break;
			}
			
			System.out.println(p.x + " " + p.y);
			System.out.println(this.position.getPoint().x + " " + this.position.getPoint().y);
			if(p.equals(this.position.getPoint())){
				System.out.println("Same !");
				continue;
			}
			
			System.out.println("Move !");
			this.moveTo(p);
		}
	}
	
	// ----------- Basic movements -----------
	
	private void moveTo(Point p) {
		Direction wantedDir = null;
		int diff = 0;
		
		
		if(p.x == this.position.getPoint().x){
			diff = p.y - this.position.getPoint().y;
			if (diff > 0)
				wantedDir = Direction.EAST;
			else 
				wantedDir = Direction.WEST;
		}
		else {
			// same Y
			diff =  p.x - this.position.getPoint().x;
			if (diff > 0)
				wantedDir = Direction.SOUTH;
			else
				wantedDir = Direction.NORTH;
		}
		
		this.turn(wantedDir);
		this.forward(Math.abs(diff));
	}

	public void turn(Direction wantedDir) {
		if(wantedDir == this.position.getDirection())
			return;

		int turnValue = this.position.getDirection().turnTo(wantedDir);
		
		if(turnValue < 0) {
			this.turnLeft(-turnValue);
		} else 
			this.turnRight(turnValue);
	}

	private void turnLeft(int repeat) {
		for(int i= 0; i < repeat; i++)
			this.turnLeft();
	}

	private void turnRight(int repeat) {
		for(int i= 0; i < repeat; i++)
			this.turnRight();
	}
	
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
	
	public void forward (double nbTiles){
		double xInit = this.position.getX();
		double yInit = this.position.getY();
		
		double distance = nbTiles * Config.TILE_SIZE;
		
		this.interrupted = false;
		
		// Circonf / 360 == Distance / xxx 
		int angle = new Double(360.0 * (distance+Config.TILE_SIZE) / Config.WHEEL_CIRCUMFERENCE).intValue();
		
		this.prevAngle = this.left.getPosition();
		
		this.right.rotateTo(this.right.getPosition() + angle, true);
		this.left.rotateTo(this.left.getPosition() + angle, true);

		double currentPosition = -1, stopPosition= -1;

		while (this.interrupted || this.right.isMoving() ) {
			try {
				Thread.sleep(10);
				this.updateModel ();
				
				switch (this.position.getDirection()) {
				case NORTH:
					currentPosition = this.position.getX();
					stopPosition = Math.floor(xInit) - nbTiles + 0.5;
					break;
				case SOUTH:
					currentPosition = this.position.getX();
					stopPosition = Math.floor(xInit) + nbTiles + 0.5;
					break;
				case EAST:
					currentPosition = this.position.getY();
					stopPosition = Math.floor(yInit) + nbTiles + 0.5;
					break;
				case WEST:
					currentPosition = this.position.getY();
					stopPosition = Math.floor(yInit) - nbTiles + 0.5;
					break;
				}
				
				double delta = 0.01;
				if (Math.abs(currentPosition - stopPosition) <= delta){
					this.right.stop(true);
					this.left.stop(false);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopOnThisTile() {
		double x = this.position.getX();
		double y = this.position.getY();
		double dx, dy;
		
		// Stop current movement
		this.pathStopped = true;
		this.left.rotateTo(this.left.getPosition());
		this.right.rotateTo(this.right.getPosition());
		
		// Move to the center of the tile
		Direction dir = this.position.getDirection();
		switch (dir) {
		case NORTH:
			dx = x - Math.floor(x) - 0.5;
			this.forward(dx);
			break;
		case SOUTH:
			dx = Math.floor(x) + 0.5 - x;
			this.forward(dx);
			break;
		case EAST:
			dy = Math.floor(y) + 0.5 - y;
			this.forward(dy);
			break;
		case WEST:
			dy = y - Math.floor(y) - 0.5;
			this.forward(dy);
			break;
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
		this.position.turnLeft();
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
