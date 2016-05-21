package ia;

import graphicalInterface.Window;

import java.awt.Point;

import mock.Movement;
import mock.WallDetectors;
import mock.WallObserver;
import model.Direction;
import model.Grid;
import model.Position;

/**
 *	Class controlling the exploration phase
 */
public abstract class AbstractExplorer{

	protected WallDetectors wd;
	protected WallObserver wo;

	protected Position position;
	protected Movement move;

	protected Grid grid;

	protected int XMax;
	protected int YMax;


	public AbstractExplorer(Position position, Movement move, WallDetectors wd, WallObserver wo, Grid grid){
		this.position = position;
		this.move = move;

		this.grid = grid;

		this.wd = wd;
		this.wo = wo;

		XMax = grid.getHeight();
		YMax = grid.getWidth();
	}

	public abstract void explore();


	protected int explore_tile(boolean useA, int min_wallDistance) {
		int nb_case_detected_right, nb_case_detected_left;
		nb_case_detected_right = this.wo.getNbCase_A();
		nb_case_detected_left = this.wo.getNbCase_B();
		addDetectedWall(
				this.wo.getOrientation(false),
				nb_case_detected_right);

		addDetectedWall(
				this.wo.getOrientation(true), 
				nb_case_detected_left);

		if(useA){ // A = right
			min_wallDistance = Math.min(min_wallDistance, nb_case_detected_right);

		}else { // B = left
			min_wallDistance = Math.min(min_wallDistance, nb_case_detected_left);
		}
		return min_wallDistance;
	}

	protected void exploreTurningHead(){
		//		this.explore_tile(false, 100);
		//		this.wd.changeHeadPosition();
		//		this.explore_tile(true, 100);
		Direction dir = this.wo.getOrientation(false);
		addDetectedWall(
				this.wo.getOrientation(false),
				this.wo.getNbCase_A());
		dir = this.wo.getOrientation(true);
		addDetectedWall(
				this.wo.getOrientation(true),
				this.wo.getNbCase_B());
		this.wd.changeHeadPosition();
		addDetectedWall(
				this.wo.getOrientation(false),
				this.wo.getNbCase_A());
		addDetectedWall(
				this.wo.getOrientation(true),
				this.wo.getNbCase_B());
	}

	protected void addDetectedWall(Direction direction, int nb_case){
		Point point = this.position.getPoint();
		for(int i=Math.min(6,nb_case); i>0; --i){
			this.grid.setDiscovered(point.x, point.y, direction);
			this.grid.translatePoint(point, direction);
		}
		if(nb_case < 6)
			this.grid.addWall(point.x, point.y, direction);
	}
}

