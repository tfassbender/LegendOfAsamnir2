package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.maps.MapProperties;

public interface NpcSpawnFactory {
	
	public void createAndAddNpc(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap);
}
