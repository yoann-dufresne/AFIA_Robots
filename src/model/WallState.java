package model;

public enum WallState {
	Empty,
	Wall,
	Undiscovered;
	
	public static WallState stateFromString (String name) {
		for (WallState state : WallState.values()) {
			if (state.toString().toLowerCase().equals(name.toLowerCase()))
				return state;
		}
		return null;
	}
}
