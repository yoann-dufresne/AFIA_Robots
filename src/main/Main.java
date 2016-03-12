package main;

import lejos.nxt.Button;
import lejos.nxt.Motor;
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
				
		
		System.out.println("Go");
		
		Movement move = new Movement();
		LineObserver lo = new LineObserver(move);
		ld.addObserver(lo);
		
		wd.changeHeadPosition();
		wd.changeHeadPosition();

		move.forward(2*Config.TILE_SIZE, false); 
		move.turnLeft();
		move.forward(Config.TILE_SIZE, false);
		move.turnLeft();
		move.forward(2*Config.TILE_SIZE, false);
		move.turnLeft();
		move.forward(Config.TILE_SIZE, true);
		move.turnLeft();/**/
		
		System.out.println("Ended");
		
		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
		
		System.out.println("Main ended");
		System.out.println(Motor.A.isMoving());
		Button.waitForAnyPress(5000);
	}
	
}
