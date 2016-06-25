package ia;

import java.awt.Point;
import java.util.List;

public class Solution {

	// Remplis dans tous les cas
	public char[][] aloneParkoor0;
	public char[][] aloneParkoor1;
	public int aloneScore;
	
	// Calculé si mieux que parcours deux robots
	public List<Point> alonePath;
	public int aloneRobotID;
	
	// Remplis si meilleurs que alone parkoor
	public char[][] myCoopParkoor;
	public char[][] otherCoopParkoor;
	public Point coopTile;
	public int mainRobot;
	public int secondRobot;
	public int coopScore;
	
	// Calculés si mieux que parcours 1 robot
	public List<Point> myCoopPath;
	public List<Point> otherCoopPath;
	
}
