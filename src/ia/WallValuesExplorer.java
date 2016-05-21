package ia;

import java.awt.Point;
//import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import captors.Movement;
import captors.WallDetectors;
import captors.WallObserver;
import model.Direction;
import model.Grid;
import model.Position;
import model.Tile;
import model.WallState;

public class WallValuesExplorer extends AbstractExplorer {

	protected int tileValues[][];
	
	public WallValuesExplorer(Position position, Movement move, WallDetectors wd, WallObserver wo, Grid grid) {		
		super(position, move, wd, wo, grid);
		this.tileValues = new int[XMax][YMax];
	}

	@Override
	public void explore () {
	//Tant que tout n'est pas découvert
		//Recherche de la plus grande valeur dans tileValues
		//Bouger jusqu'à la case associée à la valeur
		//  --> Si on rencontre un mur, on stoppe et on update les valeurs
		this.exploreTurningHead();
		this.computeScores(this.position.getPoint());
		this.printTiles();
		this.goToBest();
		this.computeScores(this.position.getPoint());
		this.printTiles();
	}
	
	public void goToBest(){
		Point highPoint = this.findHighestScore();
		this.position.turnRight();
		for(int i=0; i < highPoint.x; i++){
			this.move.forward(1);
			this.exploreTurningHead();
		}
		this.position.turnLeft();
		for(int i=0; i < highPoint.y; i++){
			this.move.forward(1);
			this.exploreTurningHead();
		}
	}
	
	public void goToBestTest(){
		this.position.turnRight();
		for(int i=0; i < 6; i++){
			this.move.forward(1);
			this.exploreTurningHead();
			this.computeScores(this.position.getPoint());
			this.printTiles();
		}
		
		this.position.turnLeft();
		for(int i=0; i < 12; i++){
			this.move.forward(1);
			this.exploreTurningHead();
			this.computeScores(this.position.getPoint());
			this.printTiles();
		}

		this.position.turnLeft();
		for(int i=0; i < 6; i++){
			this.move.forward(1);
			this.exploreTurningHead();
			this.computeScores(this.position.getPoint());
			this.printTiles();
		}

	}
	
	public Point findHighestScore(){
		Point highPoint = new Point();
		int highScore;
		highScore=0;
		for (int y=0; y<YMax; y++){
			for (int x=0; x<XMax; x++){
				if (this.tileValues[x][y] > highScore){
					highPoint= new Point(x,y);
					highScore= this.tileValues[x][y];
				}
			}
		}
		return highPoint;
	}
	
	public void computeScores(Point p){
		for(int y = 0; y < YMax; y++){
			for(int x = 0; x < XMax; x++){
				int score = this.tileScore(x, y);
				if (score != 0)
					this.tileValues[x][y] = (int) Math.max(1,score - p.distance(x,y));
				else 
					this.tileValues[x][y]= 0;
//				this.tileValues[x][y]= score;
			}
		}
	}

	
	public void printTiles(){
		for(int x = 0; x < XMax; x++){
			for(int y = 0; y < YMax; y++){
				if (x == this.position.getPoint().x && y == this.position.getPoint().y)
					System.out.print(" x ");
				else
					System.out.print(String.format("%03d", tileValues[x][y]));
				System.out.print(" ");
			}
			System.out.println(" ");
		}
		System.out.println("\n");
	}
	
	/** for a given point get the number of undiscovered wall in all directions
	@return a mark corresponding to the max number of wall that can be discovered from this tile
	*/
	public int tileScore(int x, int y) {
		int mark = 0;
		Point point = new Point(x, y);
		for (Direction d : Direction.values()) {
			mark += getValuesByDirection(point, d); 
		}
		return mark;

	}
	
	
	/**
	get the number of tiles without wall on the given direction from the given point
	*/
	public int getValuesByDirection(Point originPoint, Direction d) {
		int mark = 0;
		
		WallState state;
		Tile tile;
		
		tile = this.grid.getTile(originPoint);
		Point point = new Point(originPoint);
		if (tile == null)
			return mark;
		
		state = tile.getState(d);
		
		int offset = 0;
		while (offset < 6 && state != WallState.Wall){
			this.grid.translatePoint(point,d);
			
			if(state == WallState.Undiscovered){
				mark++; 
			}
			
			offset++;
			tile = this.grid.getTile(point);
			if (tile == null)
				return mark;
			
			state = tile.getState(d);
		}
		
		return mark;
	}
}
