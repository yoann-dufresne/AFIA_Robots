package main;
import model.Direction;
import model.Grid;
import model.GridExample;
import model.Position;
import bluetooth.BluetoothRobot;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;

/**
 * Receive data from another NXT, a PC, a phone,
 * or another bluetooth device.
 *
 * Waits for a connection, receives an int and returns
 * its negative as a reply, 100 times, and then closes
 * the connection, and waits for a new one.
 *
 * @author Lawrie Griffiths
 *
 */
public class Main {

	public static void main(String[] args) {
		Position position = new Position(0.5, 0.5, Direction.EAST);
		Grid g = GridExample.g;

		BluetoothRobot br = new BluetoothRobot(position, g);
		Thread btThread = new Thread(br);
		btThread.start();

		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();

		System.out.println("Go");

		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);

//		IA ia = new IA(position, g);
//		Point dest = new Point(9, 22);
//		Solution sol = ia.goTo(dest.x, dest.y);
//		List<Point> path1 = null;
//		List<Point> path2 = null;
//		int cost = 2323;
//		if (sol.alonePath != null){
//			path1 = sol.alonePath;
//			cost = sol.aloneParkoor[dest.x][dest.y];
//		}
//		else{
//			cost = Math.max(
//					sol.coopParkoor[dest.x][dest.y],
//					sol.aloneParkoor[sol.coopTile.x][sol.coopTile.y]
//			);
//			path1 = sol.otherCoopPath;
//			path2 = sol.myCoopPath;
//		}
		System.out.println("avant gc : " + Runtime.getRuntime().freeMemory());
		System.gc();
		System.out.println("apres gc : " + Runtime.getRuntime().freeMemory());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("tout fini");
	}
}

