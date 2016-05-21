package main;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import java.awt.Point;
import java.io.PrintStream;
import java.util.List;

import bluetooth.BluetoothRobot;
import api.Map;
import ia.IA;
import ia.Solution;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import model.Direction;
import model.Grid;
import model.GridExample;
import model.GridGenerator;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDetectors;
import captors.WallObserver;
import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 *
 * Compile this program with javac (not nxjc), and run it
 * with java.
 *
 * You need pccomm.jar and bluecove.jar on the CLASSPATH.
 * On Linux, you will also need bluecove-gpl.jar on the CLASSPATH.
 *
 * Run the program by:
 *
 *   java BTSend
 *
 * Your NXT should be running a sample such as BTReceive or
 * SignalTest. Run the NXT program first until it is
 * waiting for a connection, and then run the PC program.
 *
 * @author Lawrie Griffiths
 *
 */
public class Main
{

	public static void main(String[] args) {
		System.setOut(new PrintStream(RConsole.getPrintStream()));
		System.out.println("caca prout");


		BluetoothRobot br = new BluetoothRobot();
		Thread btThread = new Thread(br);
		btThread.start();

		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();

		WallDetectors wd = new WallDetectors ();
		Thread wdThread = new Thread(wd);
		wdThread.start();

		Position position = new Position(0.5, 0.5, Direction.EAST);

		System.out.println("Go");

		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);

		WallObserver wo = new WallObserver();
		wd.addObserver(wo);

		// actions
//		Grid g = GridGenerator.generate(23, 11, 0.2);
		//Grid g = new Grid(3, 4);
//		g.addWall(0, 1, Direction.SOUTH);
//		g.addWall(1, 1, Direction.EAST);
//		g.addWall(2, 3, Direction.NORTH);/**/
		Grid g = GridExample.g;

		IA ia = new IA(position, g);
		Point dest = new Point(9, 22);
		Solution sol = ia.goTo(dest.x, dest.y);
		List<Point> path1 = null;
		List<Point> path2 = null;
		int cost = 2323;
		if (sol.alonePath != null){
			path1 = sol.alonePath;
			cost = sol.aloneParkoor[dest.x][dest.y];
		}
		else{
			cost = Math.max(
					sol.coopParkoor[dest.x][dest.y],
					sol.aloneParkoor[sol.coopTile.x][sol.coopTile.y]
			);
			path1 = sol.otherCoopPath;
			path2 = sol.myCoopPath;
		}
		System.out.println("avant gc : " + Runtime.getRuntime().freeMemory());
		System.gc();
		System.out.println("apres gc : " + Runtime.getRuntime().freeMemory());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("cost : " + cost);
//		move.followPath(path);
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

//		Motor.C.rotateTo(0);
		ld.stop();
		wd.stop();
//
		System.out.println("Main ended");
//		System.out.println(Motor.A.isMoving());
		Button.waitForAnyPress(20000);
	}
}
