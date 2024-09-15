package net.jfabricationgames.gdx.skill;

public enum DifficultyLevel {
	
	EASY(0), // I'm a warrior!
	NORMAL(1), // I'm a berserker!
	HARD(2); // The allfather knows my name!
	
	public final int index; // the index in the config file
	
	private DifficultyLevel(int index) {
		this.index = index;
	}
}
