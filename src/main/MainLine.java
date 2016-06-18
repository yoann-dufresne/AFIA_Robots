package main;

import model.Direction;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;

public class MainLine {

	public static void main(String[] args) {
		Position position = new Position(0.5, 0.5, Direction.EAST);
		Movement move = new Movement(position);
		
		/*BluetoothRobot br = new BluetoothRobot();
		Thread btThread = new Thread(br);
		btThread.start();/**/
		
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();
		
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);
		
		move.forward(20);
		
		ld.stop();
	}
	
}
