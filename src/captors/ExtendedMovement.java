package captors;

import bluetooth.BluetoothRobot;
import model.Position;
import api.Observable;
import api.Observer;

public class ExtendedMovement extends Movement implements Observer {
	
	
	public ExtendedMovement(Position position){
		super(position);
	}
	
	public void straightForward(){
		//this.waitForUnlock();
		BluetoothRobot.bt.send("DEBUG;Straight forward started");
		this.forward(10,true);
		BluetoothRobot.bt.send("DEBUG;Straight forward ended");
	}
	
	public void update(Observable o, Object arg){
		BluetoothRobot.bt.send("DEBUG;Interuption");
		this.right.stop(true);
		this.left.stop(true);
		//this.right.rotateTo(this.right.getPosition(),true);
		//this.left.rotateTo(this.left.getPosition(),true);
	}
}
