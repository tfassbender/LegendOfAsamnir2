package net.jfabricationgames.gdx.map;

class GameMapConfig {
	
	public String name;
	public String map;
	public boolean initial = false;
	public boolean homeMap = false; // the map where the game can be continued after a game over
	public int homeMapStartingPointId = 0; // the starting point on the home map where the player should be placed after a game over
	public String mapEnteringConfig;
	
	//debug config
	public int initialStartingPointId = 0;
	public String debugStartConfig;
}
