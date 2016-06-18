package main;

import model.Direction;
import model.Grid;
import model.Position;
import bluetooth.BluetoothRobot;

public class TestBt {

	public static void main(String[] args) {
		Position pos = new Position(0.5, 0.5, Direction.EAST);
		Grid grid = new Grid(5, 11);
		
		BluetoothRobot br = new BluetoothRobot(pos, grid);
		Thread btThread = new Thread(br);
		btThread.start();/**/
		
		try {
			Thread.sleep(60000);
			br.stop();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
