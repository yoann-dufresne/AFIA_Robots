package main;

import java.awt.Point;
import java.util.List;

import api.Map;
import ia.IA;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import model.Direction;
import model.Grid;
import model.GridGenerator;
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
		//Grid g = GridGenerator.generate(23, 11, 0.2);
		Grid g = new Grid(3, 4);
		g.addWall(0, 1, Direction.SOUTH);
		g.addWall(1, 1, Direction.EAST);
		g.addWall(2, 3, Direction.NORTH);/**/
		
		IA ia = new IA(position, g);
		List<Point> path = ia.goTo(1, 2);
		System.gc();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("path size : " + path.size());
		move.followPath(path);
		//System.out.println(Runtime.getRuntime().freeMemory());
		
		/*Map<Point, Integer> tests = new Map<Point, Integer>();
		tests.put(position.getPoint(), 0);
		System.out.println(ia.parkoor(tests, position.getPoint(), new Point(22, 10)).size());
		System.out.println(Runtime.getRuntime().freeMemory());
		tests = new Map<Point, Integer>();
		tests.put(position.getPoint(), 0);
		System.gc();
		System.out.println(ia.allPossibleDestinations(tests, position.getPoint()).size());
		System.out.println(Runtime.getRuntime().freeMemory());/**/
		
		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
		
		System.out.println("Main ended");
		System.out.println(Motor.A.isMoving());
		Button.waitForAnyPress(20000);
	}
	
}
