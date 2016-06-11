package main;

public class Config {
	
	/* Units :
	 * angles : degree
	 * distances : meters
	 */

	/* Maze config */
	public static final double TILE_SIZE = 0.4;
	
	
	/* Robot config */
	public static final double WHEEL_RADIUS = 0.021;
	public static final double WHEELS_DISTANCE = 0.175 - 0.022;
	public static final double WHEEL_CIRCUMFERENCE = 2 * Math.PI * WHEEL_RADIUS;
	public static final double INTER_WHEELS_CIRCUMFERENCE = WHEELS_DISTANCE * Math.PI;
	
	public static final double LIGHTS_DISTANCE = 0.0825;
	public static final double LIGHT_CENTER_DISTANCE = 0.04;
	
	public static final double CORRECTION_RATIO = LIGHT_CENTER_DISTANCE / TILE_SIZE;
	
	public static final int GRID_WIDTH = 23;
	public static final int GRID_HEIGHT = 11;
	
	public static final int MAX_DIST_US = 255;
}
