package main;

import model.Direction;
import model.Grid;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDiscoverer;
import captors.WallObserver;
import bluetooth.BluetoothRobot;

public class MainExplorer {

	public static void main(String[] args) {
		/*BluetoothRobot br = new BluetoothRobot();
		Thread btThread = new Thread(br);
		btThread.start();/**/
		
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		/*WallDetectors wd = new WallDetectors ();
		Thread wdThread = new Thread(wd);
		wdThread.start();/**/
		
		Position position = new Position(0.5, 0.5, Direction.EAST);
		
		Movement move = new Movement(position);
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);

		/*WallObserver wo = new WallObserver();
		wd.addObserver(wo);/**/
		
		Grid g = new Grid(3, 4);
	}

}
