package main;

import ia.WallValuesExplorer;
import model.Grid;
import model.Position;
import captors.LineObserver;
import captors.Movement;
import captors.WallDiscoverer;
import captors.WallDiscovererObserver;

public class MainExplorer extends AbstractMain {
	
	public MainExplorer(Grid g, Position pos) {
		super(g, pos);
	}
	
	public void start () {
		Movement move = new Movement(this.pos);
		
		WallDiscoverer wd = new WallDiscoverer(this.pos, move);
		wd.changeHeadPosition(); // Met vers l'avant
		Thread wdThread = new Thread(wd);
		wdThread.start();/**/
		
		LineObserver lo = new LineObserver(move, this.pos);
		this.ld.addObserver(lo);
		this.ldThread.start();

		WallDiscovererObserver wo = new WallDiscovererObserver(this.grid, this.pos);
		wd.addObserver(wo);/**/
		
		WallValuesExplorer wve = new WallValuesExplorer(this.pos, move, this.grid);
		wve.explore();/**/
		
		this.ld.stop();
		wd.stop();
		
		try {
			this.ldThread.join();
			wdThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (wd.isInFrontPosition())
			wd.changeHeadPosition();/**/
	}

}
