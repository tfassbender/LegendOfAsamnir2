package net.jfabricationgames.gdx.object.traversable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

/**
 * An object that changes it's body to a sensor and back by (configurable) events, so that the player may walk through it.
 */
public class TraverseableObject extends GameObject implements EventListener {
	
	private AnimationDirector<TextureRegion> animationTraverseable;
	private AnimationDirector<TextureRegion> animationSolid;
	
	private EventConfig changeBodyToSensorEvent;
	private EventConfig changeBodyToSolidObjectEvent;
	
	public TraverseableObject(GameObjectTypeConfig type, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(type, sprite, properties, gameMap);
		
		EventHandler.getInstance().registerEventListener(this);
		
		initializeEvents();
	}
	
	private void initializeEvents() {
		changeBodyToSensorEvent = typeConfig.changeBodyToSensorEvent.copy();
		changeBodyToSolidObjectEvent = typeConfig.changeBodyToSolidObjectEvent.copy();
		
		if (typeConfig.intValueByMapProperty != null) {
			// change the intValue of the event objects to the value that is configured in the map properties (to not need a new event config for each object)
			int intValue = Integer.parseInt(mapProperties.get(typeConfig.intValueByMapProperty, "0", String.class));
			changeBodyToSensorEvent.intValue = intValue;
			changeBodyToSolidObjectEvent.intValue = intValue;
		}
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		float width = mapProperties.get("width", Float.class) * Constants.WORLD_TO_SCREEN;
		float height = mapProperties.get("height", Float.class) * Constants.WORLD_TO_SCREEN;
		
		x = mapProperties.get("x", Float.class) * Constants.WORLD_TO_SCREEN + width * 0.5f;
		y = mapProperties.get("y", Float.class) * Constants.WORLD_TO_SCREEN + height * 0.5f;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height)
				//change the collision type to MAP_UNREACHABLE_AREA to not interact with projectiles but with the player and enemies
				.setCollisionType(PhysicsCollisionType.MAP_UNREACHABLE_AREA);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
		
		centerSpriteAndAnimationOnObject(x, y);
		
		if (!typeConfig.initiallySolid) {
			changeBodyToSensor();
			animation = animationTraverseable;
		}
	}
	
	private void centerSpriteAndAnimationOnObject(float x, float y) {
		sprite.setPosition(x - sprite.getWidth() * 0.5f, y - sprite.getHeight() * 0.5f);
		
		if (typeConfig.animationTraverseable != null) {
			animationTraverseable = animationManager.getTextureAnimationDirectorCopy(typeConfig.animationTraverseable);
			AnimationSpriteConfig spriteConfig = AnimationSpriteConfig.fromSprite(sprite);
			spriteConfig.setX(spriteConfig.x + typeConfig.animationTraverseableOffsetX);
			spriteConfig.setY(spriteConfig.y + typeConfig.animationTraverseableOffsetY);
			animationTraverseable.setSpriteConfig(spriteConfig);
		}
		if (typeConfig.animationSolid != null) {
			animationSolid = animationManager.getTextureAnimationDirectorCopy(typeConfig.animationSolid);
			AnimationSpriteConfig spriteConfig = AnimationSpriteConfig.fromSprite(sprite);
			spriteConfig.setX(spriteConfig.x + typeConfig.animationSolidOffsetX);
			spriteConfig.setY(spriteConfig.y + typeConfig.animationSolidOffsetY);
			animationSolid.setSpriteConfig(spriteConfig);
			
			animation = animationSolid;
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event == null) {
			return;
		}
		
		if (event.equals(changeBodyToSensorEvent)) {
			changeBodyToSensor();
			animation = animationTraverseable;
		}
		else if (event.equals(changeBodyToSolidObjectEvent)) {
			changeBodyToNonSensor();
			animation = animationSolid;
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		EventHandler.getInstance().removeEventListener(this);
	}
}
