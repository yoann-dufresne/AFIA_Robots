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
					this.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty"); 
					x--;
					this.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					break;
				case EAST:
					this.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					y++;
					this.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					break;
				case SOUTH:
					this.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					x++;
					this.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty");
					break;
				case WEST:
					this.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					y--;
					this.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					break;
				}
			}

			if (this.bt != null && x >= 0 && x < this.grid.getHeight() && y >= 0 && y < this.grid.getWidth())
				if (dists[i] < MAX_DIST_CM) {
					switch (dir) {
					case NORTH:
						this.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Wall");
						if (x > 0)
							this.bt.send("DISCOVERED;" + (x-1) + ";" + y + ";SOUTH;Wall");
						break;
					case SOUTH:
						this.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Wall");
						if (x < this.grid.getHeight()-1)
							this.bt.send("DISCOVERED;" + (x+1) + ";" + y + ";NORTH;Wall");
						break;
					case EAST:
						this.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Wall");
						if (y < this.grid.getWidth()-1)
							this.bt.send("DISCOVERED;" + x + ";" + (y+1) + ";WEST;Wall");
						break;
					case WEST:
						this.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Wall");
						if (y > 0)
							this.bt.send("DISCOVERED;" + x + ";" + (y-1) + ";EAST;Wall");
						break;
					}
					
					this.grid.addWall(x, y, dir);
				}
		}
	}

}
