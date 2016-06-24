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
	public static final int MAX_DIST_CM = 4 * TILE_SIZE_CM;

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

		BluetoothRobot.bt.send("DEBUG;Updates");
		for (int i=0 ; i<4 ; i++) {
			Direction dir = Direction.values()[i];
			int x = tile.x;
			int y = tile.y;

			int dist = dists[i];
			if (dist > MAX_DIST_CM)
				dist = MAX_DIST_CM;
			
			int absDist = 0;
			while (dist > TILE_SIZE_CM) {
				BluetoothRobot.bt.send("DEBUG;" + x + " " + y + " " + dir + " " + dist);
				if (x < 0 || x >= this.grid.getHeight() || y < 0 || y >= this.grid.getWidth())
					break;
				absDist++;

				int proba = 100;
				int probaIdx = -1;
				try {
					proba = this.grid.getProba(x, y, dir);
				} catch (IllegalArgumentException e) {}
				
				boolean discovered = false;
				if (probaIdx != -1 && absDist <= proba) {
					this.grid.setEmpty(x, y, dir);
					this.grid.setProba(x, y, dir, absDist);
					discovered = true;
				}
				
				dist -= TILE_SIZE_CM;

				// TODO : Refactorer le vomi
				switch (dir) {
				case NORTH:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty" + absDist);
					x--;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty" + absDist);
					break;
				case EAST:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty" + absDist);
					y++;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty" + absDist);
					break;
				case SOUTH:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty" + absDist);
					x++;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty" + absDist);
					break;
				case WEST:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty" + absDist);
					y--;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty" + absDist);
					break;
				}
			}

			if (x >= 0 && x < this.grid.getHeight() && y >= 0 && y < this.grid.getWidth())
				if (dists[i] < MAX_DIST_CM) {
					int proba = 100;
					try {
						proba = this.grid.getProba(x, y, dir);
					} catch (IllegalArgumentException e) {
						continue;
					}
					
					if (absDist < proba) {
						this.grid.setProba(x, y, dir, absDist);
						this.grid.setWall(x, y, dir);
						
						switch (dir) {
						case NORTH:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Wall" + absDist);
							if (x > 0)
								BluetoothRobot.bt.send("DISCOVERED;" + (x-1) + ";" + y + ";SOUTH;Wall" + absDist);
							break;
						case SOUTH:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Wall" + absDist);
							if (x < this.grid.getHeight()-1)
								BluetoothRobot.bt.send("DISCOVERED;" + (x+1) + ";" + y + ";NORTH;Wall" + absDist);
							break;
						case EAST:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Wall" + absDist);
							if (y < this.grid.getWidth()-1)
								BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + (y+1) + ";WEST;Wall" + absDist);
							break;
						case WEST:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Wall" + absDist);
							if (y > 0)
								BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + (y-1) + ";EAST;Wall" + absDist);
							break;
						}
					}
				}
		}
	}

}
