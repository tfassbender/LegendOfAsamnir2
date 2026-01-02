package net.jfabricationgames.gdx.object.animation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class PersistentAnimationObject extends GameObject implements EventListener {
	
	private boolean visible = false;
	
	public PersistentAnimationObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
		
		animation = getAnimation(typeConfig.animation);
		animation.setRotation(Float.parseFloat(mapProperties.get("rotation", "0f", String.class)));
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		if (visible) {
			if (drawAnimation()) {
				animation.increaseStateTime(delta);
				animation.draw(batch);
			}
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (EventType.DISPLAY_ANIMATION_GAME_OBJECT.equals(event.eventType) && event.stringValue != null && event.stringValue.equals(getUnitId())) {
			if (event.booleanValue) {
				visible = true;
			}
			else {
				visible = false;
			}
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		EventHandler.getInstance().removeEventListener(this);
	}
}
