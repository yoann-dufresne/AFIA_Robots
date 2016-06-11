package ia;


import model.Grid;
import model.Position;
import captors.Movement;
import captors.WallDiscoverer;
import captors.WallDiscovererObserver;

/**
 *	Class controlling the exploration phase
 */
public abstract class AbstractExplorer{

	protected WallDiscoverer wd;
	protected WallDiscovererObserver wo;

	protected Position position;
	protected Movement move;

	protected Grid grid;

	protected int XMax;
	protected int YMax;


	public AbstractExplorer(Position position, Movement move, Grid grid){
		this.position = position;
		this.move = move;

		this.grid = grid;

		/*this.wd = wd;
		this.wo = wo;/**/

		XMax = grid.getHeight();
		YMax = grid.getWidth();
	}

	public abstract void explore();


	/*protected int explore_tile(boolean useA, int min_wallDistance) {
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
	}*/

}
