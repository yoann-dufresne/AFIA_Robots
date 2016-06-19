package main;

import bluetooth.BluetoothRobot;
import ia.WallValuesExplorer;
import lejos.nxt.Button;
import model.Direction;
import model.Grid;
import model.Position;
import captors.LineDetectors;
import captors.LineObserver;
import captors.Movement;
import captors.WallDiscoverer;
import captors.WallDiscovererObserver;

public class MainExplorer extends AbstractMain {
	
	public MainExplorer() {
	}
	
	public void start () {
		this.started = false;
		Position position = new Position(0.5, 0.5, Direction.EAST);
		Grid g = new Grid(5, 23);
		Movement move = new Movement(position);
		
		BluetoothRobot br = new BluetoothRobot(position, g, this);
		Thread btThread = new Thread(br);
		btThread.start();/**/
		
		while (!this.started) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		LineDetectors ld = new LineDetectors ();
		Thread ldThread = new Thread (ld);
		ldThread.start ();/**/
		
		WallDiscoverer wd = new WallDiscoverer(position, move);
		wd.changeHeadPosition(); // Met vers l'avant
		Thread wdThread = new Thread(wd);
		wdThread.start();/**/
		
		LineObserver lo = new LineObserver(move, position);
		ld.addObserver(lo);

		WallDiscovererObserver wo = new WallDiscovererObserver(g, position);
		wd.addObserver(wo);/**/
		
		WallValuesExplorer wve = new WallValuesExplorer(position, move, g, "laby.txt");
		wve.explore();/**/
		
		ld.stop();
		wd.stop();
		
		try {
			btThread.join();
			ldThread.join();
			wdThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (wd.isInFrontPosition())
			wd.changeHeadPosition();/**/
		
		Button.waitForAnyPress();
	}

	public static void main(String[] args) {
		MainExplorer main = new MainExplorer();
		main.start();
	}

}
