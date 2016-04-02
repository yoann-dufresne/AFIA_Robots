package main;

import java.awt.Point;
import java.util.List;

import ia.IA;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import model.Direction;
import model.Grid;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDetectors;
import captors.WallObserver;

public class Main {

	public static void main(String[] args) {
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		WallDetectors wd = new WallDetectors ();
		Thread wdThread = new Thread(wd);
		wdThread.start();
		
		Position position = new Position(1.5, 0.5, Direction.EAST);
		
		System.out.println("Go");
		
		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);
		
		WallObserver wo = new WallObserver();
		wd.addObserver(wo);
		
		// actions
		Grid g = new Grid(3, 4);
		g.addWall(0, 1, Direction.SOUTH);
		g.addWall(1, 1, Direction.EAST);
		g.addWall(2, 3, Direction.NORTH);
		
		IA ia = new IA(position, g);
		//RConsole.open();
		List<Point> path = ia.goTo(1, 2);
		
		//move.followPath(path);
		// end actions
		
		
		System.out.println("Ended");
		
		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
		
		System.out.println("Main ended");
		System.out.println(Motor.A.isMoving());
		Button.waitForAnyPress(20000);
	}
	
}
