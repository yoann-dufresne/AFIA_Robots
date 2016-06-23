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

	private char[] wallsProbas;

	private Grid grid;
	private Position position;


	public WallDiscovererObserver(Grid grid, Position pos){
		this.grid = grid;
		this.position = pos;
		
		this.wallsProbas = new char[grid.getWidth()*grid.getHeight()*2];
		for (int i=0 ; i<this.wallsProbas.length ; i++) {
			if (i % 2 == 0 && i > 2 * grid.getWidth() * (grid.getHeight() - 1))
				this.wallsProbas[i] = 0;
			else
				this.wallsProbas[i] = 100;
		}
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
					probaIdx = this.getProbaIdx(x, y, dir);
					proba = (int)this.wallsProbas[probaIdx];
				} catch (IllegalArgumentException e) {}
				
				boolean discovered = false;
				if (probaIdx != -1 && absDist <= proba) {
					this.grid.setDiscovered(x, y, dir);
					this.wallsProbas[probaIdx] = (char)absDist;
					discovered = true;
				}
				
				dist -= TILE_SIZE_CM;

				// TODO : Refactorer le vomi
				switch (dir) {
				case NORTH:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty");
					x--;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					break;
				case EAST:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					y++;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					break;
				case SOUTH:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					x++;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty");
					break;
				case WEST:
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					y--;
					if (discovered)
						BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					break;
				}
			}

			if (x >= 0 && x < this.grid.getHeight() && y >= 0 && y < this.grid.getWidth())
				if (dists[i] < MAX_DIST_CM) {
					char proba = 100;
					int probaIdx = -1;
					try {
						probaIdx = this.getProbaIdx(x, y, dir);
						proba = this.wallsProbas[probaIdx];
					} catch (IllegalArgumentException e) {
						continue;
					}
					
					if (absDist < proba) {
						this.wallsProbas[probaIdx] = (char)absDist;
						this.grid.setWall(x, y, dir);
						
						switch (dir) {
						case NORTH:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Wall");
							if (x > 0)
								BluetoothRobot.bt.send("DISCOVERED;" + (x-1) + ";" + y + ";SOUTH;Wall");
							break;
						case SOUTH:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Wall");
							if (x < this.grid.getHeight()-1)
								BluetoothRobot.bt.send("DISCOVERED;" + (x+1) + ";" + y + ";NORTH;Wall");
							break;
						case EAST:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Wall");
							if (y < this.grid.getWidth()-1)
								BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + (y+1) + ";WEST;Wall");
							break;
						case WEST:
							BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Wall");
							if (y > 0)
								BluetoothRobot.bt.send("DISCOVERED;" + x + ";" + (y-1) + ";EAST;Wall");
							break;
						}
					}
				}
		}
	}
	
	// --------------------------------------------------------------
	// ----------------------- Proba idxs ---------------------------
	// --------------------------------------------------------------
	
	public int getProbaIdx (int row, int col, Direction dir) throws IllegalArgumentException {
		if (dir == Direction.NORTH) {
			dir = Direction.SOUTH;
			row = row-1;
		} else if (dir == Direction.WEST) {
			dir = Direction.EAST;
			col = col-1;
		}

		if (row < 0 || col < 0 ||
				(row == this.grid.getHeight()-1 && dir == Direction.SOUTH) ||
				(col == this.grid.getWidth()-1 && dir == Direction.EAST))
			throw new IllegalArgumentException("Impossible to change the probability of external walls");
		
		return 2* (row * this.grid.getWidth() + col) + (dir == Direction.SOUTH ? 0 : 1);
	}

}
