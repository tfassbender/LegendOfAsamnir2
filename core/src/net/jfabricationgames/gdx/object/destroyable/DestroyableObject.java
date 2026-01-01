package net.jfabricationgames.gdx.object.destroyable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.util.MapUtil;

public class DestroyableObject extends GameObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_DESTROYED_EVENT_TEXT = "destroyedEventText";
	
	protected float health;
	protected boolean destroyed;
	
	private Vector2 originalPosition;
	
	protected String destroySound;
	
	public DestroyableObject(GameObjectTypeConfig type, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(type, sprite, properties, gameMap);
		destroyed = false;
		
		if (typeConfig.restoreEvent != null) {
			EventHandler.getInstance().registerEventListener(this);
		}
	}
	
	@Override
	protected void readTypeConfig() {
		super.readTypeConfig();
		
		destroySound = typeConfig.destroySound;
		health = typeConfig.health;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		originalPosition = new Vector2(x, y);
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		AttackType attackType = attackInfo.getAttackType();
		if (attackType.isSubTypeOf(typeConfig.requiredAttackType)) {
			health -= damage;
			
			if (health <= 0) {
				destroy();
				return;
			}
		}
		
		animation = getHitAnimation();
		playHitSound();
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		//remove this object, if it is destroyed and the destroy animation has finished
		if (destroyed && (animation == null || animation.isAnimationFinished())) {
			if (typeConfig.removeAfterBreak) {
				removeFromMap();
			}
			else {
				if (typeConfig.textureAfterBreak != null) {
					sprite = createSprite(typeConfig.textureAfterBreak);
				}
			}
		}
		else if (!destroyed && (animation == null || animation.isAnimationFinished())) {
			sprite = createSprite(typeConfig.texture);
		}
	}
	
	public void destroy() {
		destroyed = true;
		animation = getDestroyAnimation();
		playDestroySound();
		
		if (typeConfig.removeAfterBreak) {
			dropItems();
			removePhysicsBody();
		}
		else if (typeConfig.changeBodySizeAfterDestroyed) {
			removePhysicsBody();
			PhysicsWorld.getInstance().runAfterWorldStep(() -> createPhysicsBodyWithCustomSizeAndOffset( //
					originalPosition.x, //
					originalPosition.y, //
					typeConfig.physicsBodySizeFactorXAfterDestroyed, //
					typeConfig.physicsBodySizeFactorYAfterDestroyed, //
					typeConfig.physicsBodyOffsetFactorXAfterDestroyed, //
					typeConfig.physicsBodyOffsetFactorYAfterDestroyed));
		}
		
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.DESTROYABLE_OBJECT_DESTROYED) //
				.setStringValue(mapProperties.get(MAP_PROPERTY_KEY_DESTROYED_EVENT_TEXT, String.class)));
	}
	
	private void createPhysicsBodyWithCustomSizeAndOffset(float x, float y, //
			float sizeFactorX, float sizeFactorY, //
			float offsetFactorX, float offsetFactorY) {
		float width = sprite.getWidth() * Constants.WORLD_TO_SCREEN * sizeFactorX * mapConfiguredBodySizeFactor.x;
		float height = sprite.getHeight() * Constants.WORLD_TO_SCREEN * sizeFactorY * mapConfiguredBodySizeFactor.y;
		
		x += sprite.getWidth() * Constants.WORLD_TO_SCREEN * offsetFactorX;
		y += sprite.getHeight() * Constants.WORLD_TO_SCREEN * offsetFactorY;
		
		PhysicsBodyProperties properties = physicsBodyProperties.setX(x).setY(y).setWidth(width).setHeight(height);
		body = PhysicsBodyCreator.createRectangularBody(properties);
		body.setUserData(this);
		
		if (typeConfig.addSensor) {
			float sensorRadius = MapUtil.getMapPropertyConfigValueAsFloat(mapProperties, "_typeConfig_sensorRadius").orElse(typeConfig.sensorRadius);
			PhysicsBodyProperties sensorProperties = new PhysicsBodyProperties() //
					.setBody(body) //
					.setSensor(true) //
					.setRadius(sensorRadius) //
					.setCollisionType(PhysicsCollisionType.OBSTACLE_SENSOR);
			PhysicsBodyCreator.addCircularFixture(sensorProperties);
		}
	}
	
	protected AnimationDirector<TextureRegion> getDestroyAnimation() {
		if (typeConfig.animationBreak == null) {
			return null;
		}
		
		AnimationDirector<TextureRegion> animation = animationManager.getTextureAnimationDirector(typeConfig.animationBreak);
		animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
		return animation;
	}
	
	private void playDestroySound() {
		if (destroySound != null) {
			soundSet.playSound(destroySound);
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (destroyed && typeConfig.restoreEvent != null && typeConfig.restoreEvent.equalsIgnoringDefaultConfigValues(event)) {
			restore();
		}
	}
	
	private void restore() {
		destroyed = false;
		health = typeConfig.health;
		
		if (typeConfig.revertBreakAnimationOnRestore && typeConfig.animationBreak != null) {
			animation = animationManager.getTextureAnimationDirector(typeConfig.animationRestore);
			animation.setSpriteConfig(AnimationSpriteConfig.fromSprite(sprite));
		}
		
		if (typeConfig.changeBodySizeAfterDestroyed) {
			removePhysicsBody();
			PhysicsWorld.getInstance().runAfterWorldStep(() -> createPhysicsBody(originalPosition.x, originalPosition.y));
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
}
