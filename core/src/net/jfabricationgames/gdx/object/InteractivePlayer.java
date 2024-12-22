package net.jfabricationgames.gdx.object;

import net.jfabricationgames.gdx.object.moveable.DraggableObject;

public interface InteractivePlayer {
	
	public boolean isSpecialActionFeatherSelected();
	
	public void setDraggableObject(DraggableObject draggableObject);
}
