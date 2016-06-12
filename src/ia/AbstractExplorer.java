package ia;


import model.Grid;
import model.Position;
import captors.Movement;

/**
 *	Class controlling the exploration phase
 */
public abstract class AbstractExplorer{

	protected Position position;
	protected Movement movement;

	protected Grid grid;

	protected int XMax;
	protected int YMax;


	public AbstractExplorer(Position position, Movement move, Grid grid){
		this.position = position;
		this.movement = move;

		this.grid = grid;

		this.XMax = grid.getHeight();
		this.YMax = grid.getWidth();
	}

	public abstract void explore();

}
