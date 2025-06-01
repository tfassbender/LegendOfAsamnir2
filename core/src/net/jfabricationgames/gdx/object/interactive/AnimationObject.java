package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

/**
 * A game object that just plays an animation.
 */
public class AnimationObject extends InteractiveObject {
	
	public AnimationObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		// execute the interaction directly, to play the animation
		executeInteraction();
	}
	
	@Override
	protected void dropItems() {
		// there is no body yet (because this is called from the executeInteraction method from the constructor)
		// therefore this method needs to be empty
	}
	
	@Override
	protected void changeBodyToSensor() {
		removeFromMap(); // remove the object after the animation is played, so spawn points can add the object again if needed
	}
}
