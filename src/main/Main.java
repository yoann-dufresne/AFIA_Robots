package main;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import captors.LineDetectors;
import captors.Movement;
import captors.WallDetectors;

public class Main {

	public static void main(String[] args) {
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		System.out.println("Go");
		
		WallDetectors wd = new WallDetectors ();
		wd.changePosition ();
		Thread wdThread = new Thread(wd);
		wdThread.start();
		
		Movement move = new Movement(ld, wd);
		//move.goToNextWall();
		move.goToNextWall();
		move.turnRight();
		move.goToNextWall();
		move.uTurn();
		move.goToNextWall();
		move.turnLeft();
		move.goToNextLine(true);/**/
		move.rotate(360);
		
		System.out.println("Ended");
		
		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
		Button.waitForAnyEvent(10000);
	}
	
}
