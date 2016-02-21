package api;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {

	protected List<Observer> observers;
	
	public Observable() {
		this.observers = new ArrayList<Observer>();
	}
	
	public void addObserver(Observer o) {
		this.observers.add(o);
	}
	
	public void deleteObserver(Observer o)  {
		this.observers.remove(o);
	}
	
	public abstract void notifyObservers();
	
}
