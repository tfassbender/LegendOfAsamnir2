package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.utils.Array;

public interface CutsceneUnitProvider {
	
	public CutsceneControlledUnit getUnitById(String controlledUnitId);
	public Array<CutsceneControlledUnit> getAllUnitsWithId(String controlledUnitId);
}
