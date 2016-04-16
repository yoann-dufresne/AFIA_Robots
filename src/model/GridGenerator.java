package model;

import java.util.Random;

public class GridGenerator {

	public static Grid generate (int width, int height, double proba) {
		Grid grid = new Grid(height, width);
		
		Random rand = new Random();
		
		for (int line=0 ; line<height ; line++)
			for (int col=0 ; col<width ; col++) {
				double coin = rand.nextDouble();
				if (coin <= proba)
					grid.addWall(line, col, Direction.EAST);
				coin = rand.nextDouble();
				if (coin <= proba)
					grid.addWall(line, col, Direction.SOUTH);
			}
		
		return grid;
	}
	
}
