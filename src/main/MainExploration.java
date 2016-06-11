package main;

import model.Direction;
import model.Grid;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDiscoverer;
import captors.WallDiscovererObserver;

public class MainExploration {

	public static void main(String[] args) {
		Position position = new Position(0.5, 0.5, Direction.EAST);
		Grid g = new Grid(3, 4);
		
		/*BluetoothRobot br = new BluetoothRobot();
		Thread btThread = new Thread(br);
		btThread.start();/**/
		
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		WallDiscoverer wd = new WallDiscoverer(position);
		Thread wdThread = new Thread(wd);
		wdThread.start();/**/
		
		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);

		WallDiscovererObserver wo = new WallDiscovererObserver(g, position);
		wd.addObserver(wo);/**/
	}

}
