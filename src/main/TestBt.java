package main;

import bluetooth.BluetoothRobot;

public class TestBt {

	public static void main(String[] args) {
		BluetoothRobot br = new BluetoothRobot();
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
