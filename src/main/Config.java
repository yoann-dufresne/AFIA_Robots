package main;

public class Config {
	
	/* Units :
	 * ¥ angles : degree
	 * ¥ distances : meters
	 */

	/* Maze config */
	public static final double TILE_SIZE = 0.4;
	
	
	/* Robot config */
	public static final double WHEEL_RADIUS = 0.021;
	public static final double WHEELS_DISTANCE = 0.175 - 0.022;
	public static final double WHEEL_CIRCUMFERENCE = 2 * Math.PI * WHEEL_RADIUS;
	public static final double INTER_WHEELS_CIRCUMFERENCE = WHEELS_DISTANCE * Math.PI;
	public static final double LIGHTS_DISTANCE = 0.0825;
}
