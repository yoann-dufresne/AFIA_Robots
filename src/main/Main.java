package main;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import model.Direction;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDetectors;

public class Main {

	public static void main(String[] args) {
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		WallDetectors wd = new WallDetectors ();
		Thread wdThread = new Thread(wd);
		wdThread.start();
		
		Position position = new Position(1.5, 1.5, Direction.EAST);
		
		System.out.println("Go");
		
		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);
		
		wd.changeHeadPosition();
		wd.changeHeadPosition();

		move.forward(2*Config.TILE_SIZE, false); 
		move.turnRight();
		move.forward(Config.TILE_SIZE, false);
		move.turnRight();
		move.forward(2*Config.TILE_SIZE, false);
		move.turnRight();
		move.forward(Config.TILE_SIZE, true);
		move.turnRight();/**/
		
		System.out.println("Ended");
		
		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
		
		System.out.println("Main ended");
		System.out.println(Motor.A.isMoving());
		Button.waitForAnyPress(20000);
	}
	
}
