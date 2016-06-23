package model;


public class GridExample {
	public static final Grid g = new Grid(11, 23, WallState.Empty);
	@SuppressWarnings("unused")
	private static final GridExample ex = new GridExample();
	
	private GridExample(){
		g.addWall(0, 'D'-'A', Direction.EAST);
		g.addWall(0, 'P'-'A', Direction.EAST);
		g.addWall(0, 'R'-'A', Direction.EAST);		
		g.addWall(0, 'S'-'A', Direction.EAST);		
		g.addWall(0, 'S'-'A', Direction.SOUTH);		

		g.addWall(1, 'F'-'A', Direction.EAST);
		g.addWall(1, 'F'-'A', Direction.SOUTH);
		g.addWall(1, 'K'-'A', Direction.EAST);
		g.addWall(1, 'K'-'A', Direction.SOUTH);
		g.addWall(1, 'L'-'A', Direction.SOUTH);
		g.addWall(1, 'O'-'A', Direction.SOUTH);
		
		g.addWall(2, 'B'-'A', Direction.EAST);
		g.addWall(2, 'B'-'A', Direction.SOUTH);
		g.addWall(2, 'I'-'A', Direction.SOUTH);
		g.addWall(2, 'K'-'A', Direction.EAST);
		g.addWall(2, 'S'-'A', Direction.SOUTH);
		g.addWall(2, 'N'-'A', Direction.EAST);
		g.addWall(2, 'O'-'A', Direction.EAST);
		g.addWall(2, 'O'-'A', Direction.SOUTH);
		
		g.addWall(3, 'A'-'A', Direction.EAST);
		g.addWall(3, 'H'-'A', Direction.EAST);
		g.addWall(3, 'I'-'A', Direction.EAST);
		g.addWall(3, 'I'-'A', Direction.SOUTH);
		g.addWall(3, 'S'-'A', Direction.EAST);
		
		g.addWall(4, 'A'-'A', Direction.EAST);
		g.addWall(4, 'B'-'A', Direction.SOUTH);
		g.addWall(4, 'J'-'A', Direction.SOUTH);
		g.addWall(4, 'K'-'A', Direction.EAST);
		g.addWall(4, 'K'-'A', Direction.SOUTH);

		g.addWall(5, 'D'-'A', Direction.SOUTH);
		g.addWall(5, 'K'-'A', Direction.EAST);
		g.addWall(5, 'W'-'A', Direction.SOUTH);

		g.addWall(6, 'C'-'A', Direction.EAST);
		g.addWall(6, 'C'-'A', Direction.SOUTH);
		g.addWall(6, 'D'-'A', Direction.EAST);
		g.addWall(6, 'D'-'A', Direction.SOUTH);

		g.addWall(7, 'C'-'A', Direction.EAST);
		g.addWall(7, 'H'-'A', Direction.EAST);
		g.addWall(7, 'H'-'A', Direction.SOUTH);
		g.addWall(7, 'O'-'A', Direction.EAST);
		g.addWall(7, 'U'-'A', Direction.EAST);
		g.addWall(7, 'V'-'A', Direction.SOUTH);

		g.addWall(8, 'C'-'A', Direction.EAST);
		g.addWall(8, 'H'-'A', Direction.EAST);
		g.addWall(8, 'O'-'A', Direction.EAST);
		g.addWall(8, 'P'-'A', Direction.SOUTH);
		g.addWall(8, 'Q'-'A', Direction.SOUTH);

		g.addWall(9, 'E'-'A', Direction.EAST);
		g.addWall(9, 'F'-'A', Direction.SOUTH);
		g.addWall(9, 'O'-'A', Direction.EAST);
		
		g.addWall(10, 'L'-'A', Direction.EAST);
		g.addWall(10, 'T'-'A', Direction.EAST);

	}
}