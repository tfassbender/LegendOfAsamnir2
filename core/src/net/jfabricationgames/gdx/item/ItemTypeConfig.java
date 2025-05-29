package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.utils.ObjectMap;

public class ItemTypeConfig {
	
	public ItemType type = ItemType.ITEM;
	
	public String texture;
	public String animation;
	public float physicsObjectRadius = 0.1f;
	public float textureScale = 1f;
	public String pickUpSound;
	public int costs;
	public String globalValue; // a global value that will be set to "true" when the item is picked up
	public ObjectMap<String, String> defaultMapProperties;
}
