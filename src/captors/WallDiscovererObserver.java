package captors;


import java.awt.Point;

import util.Debug;
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
	private BluetoothRobot bt;


	public WallDiscovererObserver(Grid grid, Position pos, BluetoothRobot bt){
		this.grid = grid;
		this.position = pos;
		this.bt = bt;
		
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
		BluetoothRobot.bt.send("DEBUG; Update");
		int[] dists = (int[])arg;
		Point tile = this.position.getPoint();

		for (int i=0 ; i<4 ; i++) {
			Direction dir = Direction.values()[i];
			int x = tile.x;
			int y = tile.y;

			int dist = dists[i];
			if (dist > MAX_DIST_CM)
				dist = MAX_DIST_CM;
			
			int absDist = 0;
			while (dist > TILE_SIZE_CM) {
				this.bt.send("DEBUG;remaining dist " + dist);
				this.bt.send("DEBUG;" + x + " " + y + " " + dir);
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
				if (absDist <= proba) {
					this.grid.setDiscovered(x, y, dir);
					this.wallsProbas[probaIdx] = (char)absDist;
					discovered = true;
				}
				
				this.bt.send("DEBUG;discovered " + discovered);
				this.bt.send("DEBUG;idx " + probaIdx);
				this.bt.send("DEBUG;probas " + absDist + " <= " + proba);
				
				dist -= TILE_SIZE_CM;

				// TODO : Refactorer le vomi
				switch (dir) {
				case NORTH:
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty");
					x--;
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					break;
				case EAST:
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					y++;
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					break;
				case SOUTH:
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";SOUTH;Empty");
					x++;
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";NORTH;Empty");
					break;
				case WEST:
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";WEST;Empty");
					y--;
					if (discovered)
						this.bt.send("DISCOVERED;" + x + ";" + y + ";EAST;Empty");
					break;
				}
			}

			if (this.bt != null && x >= 0 && x < this.grid.getHeight() && y >= 0 && y < this.grid.getWidth())
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
						this.grid.addWall(x, y, dir);
						
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
					}
				}
		}
		BluetoothRobot.bt.send("DEBUG;/Update");
	}
	
	// --------------------------------------------------------------
	// ----------------------- Proba idxs ---------------------------
	// --------------------------------------------------------------
	
	public int getProbaIdx (int row, int col, Direction dir) throws IllegalArgumentException {
		if ((row == 0 && dir == Direction.NORTH) ||
				(row == this.grid.getHeight()-1 && dir == Direction.SOUTH) ||
				(col == 0 && dir == Direction.WEST) ||
				(col == this.grid.getWidth()-1 && dir == Direction.EAST))
			throw new IllegalArgumentException("Impossible to change the probability of external walls");
		
		if (dir == Direction.NORTH) {
			dir = Direction.SOUTH;
			row = row-1;
		} else if (dir == Direction.WEST) {
			dir = Direction.EAST;
			col = col+1;
		}
		
		return 2* (row * this.grid.getWidth() + col) + (dir == Direction.SOUTH ? 0 : 1);
	}

}
