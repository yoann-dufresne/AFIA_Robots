package main;

import captors.LineDetectors;
import model.Grid;
import model.Position;

public abstract class AbstractMain {
	protected Grid grid;
	protected Position pos;
	protected LineDetectors ld;
	protected Thread ldThread;
	
	public AbstractMain(Grid g, Position pos) {
		this.grid = g;
		this.pos = pos;
		
		this.ld = new LineDetectors ();
		this.ldThread = new Thread (ld);
	}
	
	public abstract void start ();
}
