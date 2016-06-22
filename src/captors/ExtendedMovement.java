package captors;

import main.Config;
import api.Observable;

public class ExtendedMovement extends Movement implements Observer{
	
	
	public ExtendedMovement(Position position){
		super(position);
	}
	
	public void straightForward(){
		//TODO go forward to next wall
		this.forward(1.5,false); //vérifier l'utilisation du booleen
	}
	
	public void update(Observable o, Object arg){
		this.right.rotateTo(this.right.getPosition(),true);
		this.left.rotateTo(this.left.getPosition(),true);
	}
}
