package main;

import ia.AbstractExplorer;
import ia.WallValuesExplorer;
//import ia.ZigZagExplorer;
import captors.Movement;
import captors.WallDetectors;
import captors.WallObserver;
import model.Direction;
import model.Grid;
import model.GridExample;
import model.Position;
import model.WallState;

public class MainExploration {

	// details des capteurs
	// S1 == A == back
	// S2 == B == front
	
	public static void main(String[] args) {
		Position position = new Position(0.5, 0.5, Direction.EAST);
		Grid trueGrid = new Grid(4, 3, WallState.Empty);
		Grid gridToExplore = new Grid(4, 3);
		trueGrid.addWall(0, 1, Direction.EAST);
		
		//Grid trueGrid = GridExample.g;
		
		Movement move = new Movement(position);
		WallDetectors wd = new WallDetectors();
		WallObserver wo = new WallObserver(trueGrid, move, wd);
	
		
		AbstractExplorer explorer = new WallValuesExplorer(position, move, wd, wo, gridToExplore);
		explorer.explore();
	}
	
}
