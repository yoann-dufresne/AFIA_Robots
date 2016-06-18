package main;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import bluetooth.BluetoothRobot;

class MainBTLogging {
   
   public static void main(String[] args) throws InterruptedException, IOException {
	   ColorSensor colSensor = new ColorSensor(SensorPort.S4, Color.RED);

	   BluetoothRobot br = new BluetoothRobot();
	   Thread btThread = new Thread(br);
	   btThread.start();/**/

	   Thread.sleep(5000);
	   while (!Button.ESCAPE.isDown()) {
          br.send("" + colSensor.getLightValue());
          Thread.sleep(1000);
	   }
      
   }
}