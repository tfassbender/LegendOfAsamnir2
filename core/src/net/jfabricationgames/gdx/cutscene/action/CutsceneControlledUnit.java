package net.jfabricationgames.gdx.cutscene.action;

import com.badlogic.gdx.math.Vector2;

public interface CutsceneControlledUnit {
	
	public static final String MAP_PROPERTIES_KEY_UNIT_ID = "unitId";
	
	public String getUnitId();
	
	public Vector2 getPosition();
	
	public default Vector2 getBodySize() {
		return Vector2.Zero;
	}
	
	public void removeFromMap();
}
