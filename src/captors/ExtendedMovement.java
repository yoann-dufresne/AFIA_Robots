package captors;

import util.Debug;
import model.Position;
import api.Observable;
import api.Observer;

public class ExtendedMovement extends Movement implements Observer {
	
	
	public ExtendedMovement(Position position){
		super(position);
	}
	
	public void straightForward(){
		//this.waitForUnlock();
		Debug.log("DEBGUG;Straight forward started");
		this.forward(10,true);
		Debug.log("DEBGUG;Straight forward ended");
	}
	
	public void update(Observable o, Object arg){
		Debug.log("DEBGUG;Interuption");
		this.right.stop(true);
		this.left.stop(true);
		//this.right.rotateTo(this.right.getPosition(),true);
		//this.left.rotateTo(this.left.getPosition(),true);
	}
}
