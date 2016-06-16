package main;

import bluetooth.BluetoothRobot;

public class TestBt {

	public static void main(String[] args) {
		BluetoothRobot br = new BluetoothRobot();
		Thread btThread = new Thread(br);
		btThread.start();/**/
	}
	
}
