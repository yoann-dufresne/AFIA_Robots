package main;
import lejos.nxt.Button;
import model.Direction;
import model.Grid;
import model.Position;
import bluetooth.BluetoothRobot;

public class TestDeLEspace extends AbstractMain {

	public TestDeLEspace() {
		super(
				new Grid(11, 23),
				new Position(0.5, 0.5, Direction.EAST)
		);
	}
	
	public void start () {
		BluetoothRobot br = new BluetoothRobot(this.pos, this.grid);
		Thread btThread = new Thread(br);
		btThread.start();
		
		while (!br.started()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		br.main.start();
		
		try {
			br.stop();
			btThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Button.waitForAnyPress();
	}
	
	public static void main(String[] args) {
		TestDeLEspace main = new TestDeLEspace();
		main.start();
	}
}

