package captors;

import model.Position;
import api.Observable;
import api.Observer;

public class ExtendedMovement extends Movement implements Observer {
	
	
	public ExtendedMovement(Position position){
		super(position);
	}
	
	public void straightForward(){
		//this.waitForUnlock();
		this.forward(10,true);
	}
	
	public void update(Observable o, Object arg){
		this.right.rotateTo(this.right.getPosition(),true);
		this.left.rotateTo(this.left.getPosition(),true);
	}
}
