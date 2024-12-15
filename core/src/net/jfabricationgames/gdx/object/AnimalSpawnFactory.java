package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.maps.MapProperties;

public interface AnimalSpawnFactory {
	
	public void createAndAddAnimal(String type, float x, float y, MapProperties mapProperties, Runnable onRemoveFromMap);
}
