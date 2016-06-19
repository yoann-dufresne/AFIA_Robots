package captors;


import java.awt.Point;

import bluetooth.BluetoothRobot;
import main.Config;
import model.Direction;
import model.Grid;
import model.Position;
import api.Observable;
import api.Observer;

public class WallDiscovererObserver implements Observer {

	private static final int TILE_SIZE_CM = new Double(100*Config.TILE_SIZE).intValue();
	public static final int MAX_DIST_CM = new Double (4 * Config.TILE_SIZE * 100).intValue();


	private Grid grid;
	private Position position;
	private BluetoothRobot bt;


	public WallDiscovererObserver(Grid grid, Position pos, BluetoothRobot bt){
		this.grid = grid;
		this.position = pos;
		this.bt = bt;
	}


	@Override
	public void update(Observable o, Object arg) {
		int[] dists = (int[])arg;
		Point tile = this.position.getPoint();
		String msg = "DISCOVERED";

		for (int i=0 ; i<4 ; i++) {
			Direction dir = Direction.values()[i];
			int x = tile.x;
			int y = tile.y;

			int dist = dists[i];
			if (dist == -1)
				continue;
			else if (dist > MAX_DIST_CM)
				dist = MAX_DIST_CM;
			
			while (dist > TILE_SIZE_CM) {
				if (x < 0 || x >= this.grid.getHeight() || y < 0 || y >= this.grid.getWidth())
					break;

				this.grid.setDiscovered(x, y, dir);
				dist -= TILE_SIZE_CM;

				switch (dir) {
				case NORTH:
					msg += ';' + x + ';' + y + ";NORTH;EMPTY"; 
					x--;
					msg += ';' + x + ';' + y + ";SOUTH;EMPTY";
					break;
				case EAST:
					msg += ';' + x + ';' + y + ";EAST;EMPTY";
					y++;
					msg += ';' + x + ';' + y + ";WEST;EMPTY";
					break;
				case SOUTH:
					msg += ';' + x + ';' + y + ";SOUTH;EMPTY";
					x++;
					msg += ';' + x + ';' + y + ";NORTH;EMPTY";
					break;
				case WEST:
					msg += ';' + x + ';' + y + ";WEST;EMPTY";
					y--;
					msg += ';' + x + ';' + y + ";EAST;EMPTY";
					break;
				}
			}

			if (x >= 0 && x < this.grid.getHeight() && y >= 0 && y < this.grid.getWidth())
				if (dists[i] < MAX_DIST_CM) {
					switch (dir) {
					case NORTH:
						msg += ';' + x + ';' + y + ";NORTH;WALL";
						if (x > 0)
							msg += ';' + x-1 + ';' + y + ";SOUTH;WALL";
						break;
					case SOUTH:
						msg += ';' + x + ';' + y + ";SOUTH;WALL";
						if (x < this.grid.getHeight()-1)
							msg += ';' + x+1 + ';' + y + ";NORTH;WALL";
						break;
					case EAST:
						msg += ';' + x + ';' + y + ";EAST;WALL";
						if (y < this.grid.getWidth()-1)
							msg += ';' + x + ';' + y+1 + ";WEST;WALL";
						break;
					case WEST:
						msg += ';' + x + ';' + y + ";WEST;WALL";
						if (y > 0)
							msg += ';' + x + ';' + y-1 + ";EAST;WALL";
						break;
					}
					
					this.grid.addWall(x, y, dir);
				}
		}
		
		this.bt.send(msg);
	}

}
