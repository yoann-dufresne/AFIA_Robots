package ia;

import graphicalInterface.Window;
import mock.Movement;
import mock.WallDetectors;
import mock.WallObserver;
import model.Direction;
import model.Grid;
import model.GridView;
import model.Position;

public class ZigZagExplorer extends AbstractExplorer {

	public ZigZagExplorer(Position position, Movement move, WallDetectors wd, WallObserver wo, Grid grid, Window w) {		
		super(position, move, wd, wo, grid, w);
	}

	public void explore(){
		
		GridView[][] gridviews = generateGridViews();
		this.exploreGridView(gridviews[0][0]);
		
// ----------------------------------------------------
	}

	private void exploreGridView(GridView gv) {
		boolean exploration_finished=false;
		while(!exploration_finished){

			wd.changeHeadPosition();
			//int min_wallDistance = this.explore_tile(false, 9999); // magic value, such large
			wd.changeHeadPosition();
			
			int dist_wall = forward(gv.getWidth(), gv);			
			if(this.position.getDirection() == Direction.EAST || this.position.getDirection() == Direction.SOUTH)
				this.move.turnRight();
			else			
				this.move.turnLeft();
			
			dist_wall = forward(dist_wall, gv);
			if(this.position.getDirection() == Direction.EAST || this.position.getDirection() == Direction.SOUTH)
				this.move.turnRight();
			else			
				this.move.turnLeft();

			exploration_finished = true; 
		}
	}

	private int forward( int nb_case, GridView gv){
		boolean useA;
		if (this.position.getDirection() == Direction.EAST)
			useA = true;
		else if (this.position.getDirection() == Direction.SOUTH)
			useA = true;
		else
			useA = false;

		int min_wallDistance = this.explore_tile(useA, 9999); // magic value, such large

		for (int i=0; i< nb_case; i++) {
			this.move.forward(1);

			min_wallDistance = this.explore_tile(useA, min_wallDistance);
		}

		int maxWallDistance = 0;
		if (this.position.getDirection() == Direction.EAST || this.position.getDirection() == Direction.WEST)
			maxWallDistance = gv.getHeight();
		else 
			maxWallDistance = gv.getWidth();
		
		return Math.min(min_wallDistance + 2, maxWallDistance);
	}

	private GridView[][] generateGridViews () {
		GridView[][] gridViews = new GridView[2][4];
		gridViews[0][0] = new GridView(this.grid, 0, 5, 0, 5);
		gridViews[1][0] = new GridView(this.grid, 6, 10, 0, 5);

		gridViews[0][1] = new GridView(this.grid, 0, 5, 6, 11);
		gridViews[1][1] = new GridView(this.grid, 6, 10, 6, 11);

		gridViews[0][2] = new GridView(this.grid, 0, 5, 12, 17);
		gridViews[1][2] = new GridView(this.grid, 6, 10, 12, 17);

		gridViews[0][3] = new GridView(this.grid, 0, 5, 18, 22);
		gridViews[1][3] = new GridView(this.grid, 6, 10, 18, 22);

		return gridViews;
	}
	
}
