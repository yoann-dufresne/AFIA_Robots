package main;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import model.Direction;
import model.Grid;
import model.Position;
import bluetooth.BluetoothRobot;

class MainBTLogging {

	public static void main(String[] args) throws InterruptedException, IOException {
		ColorSensor colSensor = new ColorSensor(SensorPort.S4, Color.RED);
		Position pos = new Position(0.5, 0.5, Direction.EAST);
		Grid grid = new Grid(5, 11);

		BluetoothRobot br = new BluetoothRobot(pos, grid);
		Thread btThread = new Thread(br);
		btThread.start();/**/

		Thread.sleep(5000);
		while (!Button.ESCAPE.isDown()) {
			br.send("" + colSensor.getLightValue());
			Thread.sleep(1000);
		}

	}
}