package captors;


import java.awt.Point;

import main.Config;
import model.Direction;
import model.Grid;
import model.Position;
import api.Observable;
import api.Observer;

public class WallDiscovererObserver implements Observer {
	
	private static final int TILE_SIZE_CM = new Double(100*Config.TILE_SIZE).intValue();
	
	
	private Grid grid;
	private Position position;


	public WallDiscovererObserver(Grid grid, Position pos){
		this.grid = grid;
		this.position = pos;
	}


	@Override
	public void update(Observable o, Object arg) {
		int[] dists = (int[])arg;
		Point tile = this.position.getPoint();
		
		for (int i=0 ; i<4 ; i++) {
			Direction dir = Direction.values()[i];
			int x = tile.x;
			int y = tile.y;
			
			int dist = dists[i];
			while (dist > TILE_SIZE_CM && dist != 255) {
				this.grid.setDiscovered(x, y, dir);
				dist -= TILE_SIZE_CM;
				
				switch (dir) {
				case NORTH:
					x--;
					break;
				case EAST:
					y++;
					break;
				case SOUTH:
					x++;
					break;
				case WEST:
					y--;
					break;
				}
			}
			
			if (dist != 255)
				this.grid.addWall(x, y, dir);
		}
	}

}
