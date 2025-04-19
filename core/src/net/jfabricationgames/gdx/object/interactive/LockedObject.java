package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class LockedObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_LOCKED = "locked";
	private static final String MAP_PROPERTY_KEY_UNLOCKED_BY_EVENT = "unlockedByEvent";
	private static final String MAP_PROPERTY_KEY_LOCK_ID = "lockId";
	private static final String MAP_PROPERTY_KEY_UNLOCK_CONDITION = "unlockCondition";
	private static final String MAP_PROPERTY_KEY_GATE_ID = "gateId";
	private static final String MAP_PROPERTY_KEY_UNLOCKED_INITIALLY = "unlockedInitially";
	private static final String GLOBAL_VALUE_KEY_GATE_OPENED_PREFIX = "gate_opened__";
	
	private static final String LOCK_EVENT_TEXT_SIMPLE_KEY = "unlocked_by_simple_key";
	private static final String LOCK_EVENT_TEXT_SPECIAL_KEY = "unlocked_by_special_key";
	private static final String LOCK_EVENT_TEXT_UNLOCKED_BY_EVENT_OR_CONDITION = "unlocked_by_event";
	
	private ObjectMap<String, String> keyProperties;
	
	private boolean playerContact = false;
	private boolean reverseInteractionAfterPlayerContactEnds = false;
	private boolean skipInteractionSound = false;
	
	public LockedObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		
		keyProperties = KeyItemProperties.getKeyProperties(properties);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void postAddToGameMap() {
		super.postAddToGameMap();
		
		if (Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_UNLOCKED_INITIALLY, "false", String.class))) {
			unlockWithoutAnimationOrSound();
		}
	}
	
	private void unlockWithoutAnimationOrSound() {
		skipInteractionSound = true;
		executeInteraction();
		animation.setStateTime(animation.getAnimationDuration());
		skipInteractionSound = false;
	}
	
	@Override
	protected void playInteractionSound() {
		if (!skipInteractionSound) {
			super.playInteractionSound();
		}
	}
	
	@Override
	public boolean interactionCanBeExecuted() {
		if (!super.interactionCanBeExecuted()) {
			return false;
		}
		
		if (!typeConfig.defaultLocked && !lockedByMapProperty()) {
			return true;
		}
		if (canBeUnlocked()) {
			return true;
		}
		else {
			fireLockEvent();
		}
		
		return false;
	}
	
	private boolean lockedByMapProperty() {
		return Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_LOCKED, "false", String.class)) //
				|| isUnlockedByCondition();
	}
	
	private boolean canBeUnlocked() {
		if (isUnlockedByEvent()) {
			return false;
		}
		
		if (isUnlockedByCondition()) {
			return isUnlockConditionMet();
		}
		
		CharacterItemDataHandler itemContainer = CharacterItemDataHandler.getInstance();
		if (itemContainer.containsKey(keyProperties)) {
			itemContainer.takeKey(keyProperties);
			return true;
		}
		
		return false;
	}
	
	private boolean isUnlockedByEvent() {
		return Boolean.parseBoolean(mapProperties.get(MAP_PROPERTY_KEY_UNLOCKED_BY_EVENT, "false", String.class));
	}
	
	private boolean isUnlockedByCondition() {
		return mapProperties.get(MAP_PROPERTY_KEY_UNLOCK_CONDITION, String.class) != null;
	}
	
	private boolean isUnlockConditionMet() {
		String conditionId = mapProperties.get(MAP_PROPERTY_KEY_UNLOCK_CONDITION, String.class);
		return ConditionHandler.getInstance().isConditionMet(conditionId);
	}
	
	/**
	 * Fire an event, that can be used to show a configurable on screen text.
	 */
	private void fireLockEvent() {
		EventConfig eventConfig = new EventConfig().setEventType(EventType.INTERACTED_WITH_LOCKED_OBJECT);
		if (isUnlockedByEvent() || isUnlockedByCondition()) {
			eventConfig.setStringValue(LOCK_EVENT_TEXT_UNLOCKED_BY_EVENT_OR_CONDITION);
		}
		else {
			if (KeyItemProperties.isSpecialKey(keyProperties)) {
				eventConfig.setStringValue(LOCK_EVENT_TEXT_SPECIAL_KEY);
			}
			else {
				eventConfig.setStringValue(LOCK_EVENT_TEXT_SIMPLE_KEY);
			}
		}
		
		EventHandler.getInstance().fireEvent(eventConfig);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.OPEN_LOCK) {
			if (isUnlockedByEvent() && event.stringValue.equals(mapProperties.get(MAP_PROPERTY_KEY_LOCK_ID))) {
				if (!actionExecuted) {
					executeInteraction();
				}
			}
		}
		else if (event.eventType == EventType.CLOSE_LOCK) {
			if (isUnlockedByEvent() && event.stringValue.equals(mapProperties.get(MAP_PROPERTY_KEY_LOCK_ID))) {
				if (actionExecuted) {
					if (!playerContact) {
						reverseInteraction();
					}
					else {
						//if the player is still in contact with the object, the interaction has to be reversed after the contact ends
						reverseInteractionAfterPlayerContactEnds = true;
					}
				}
			}
		}
	}
	
	@Override
	protected void executeInteraction() {
		float animationTime = 0;
		boolean setAnimationTime = false;
		if (animation != null) {
			setAnimationTime = true;
			animationTime = animation.getStateTime();
		}
		
		super.executeInteraction();
		
		setGateGlobalValue();
		
		if (setAnimationTime) {
			animation.setStateTime(animation.getAnimationDuration() - animationTime);
		}
	}
	
	/**
	 * Sets a global value that tells whether the gate is opened (if the gate configures the map property "gateId").
	 */
	private void setGateGlobalValue() {
		if (mapProperties.containsKey(MAP_PROPERTY_KEY_GATE_ID)) {
			String gateId = mapProperties.get(MAP_PROPERTY_KEY_GATE_ID, String.class);
			GlobalValuesDataHandler.getInstance().put(GLOBAL_VALUE_KEY_GATE_OPENED_PREFIX + gateId, true);
		}
	}
	
	private void reverseInteraction() {
		if (actionExecuted && typeConfig.changeBodyToSensorAfterAction) {
			changeBodyToNonSensor();
			changedBodyToSensor = false;
		}
		
		actionExecuted = false;
		
		if (typeConfig.animationActionReversed != null) {
			float animationTime = 0f;
			if (animation != null) {
				animationTime = animation.getStateTime();
				animation = getReversedActionAnimation();
				animation.setStateTime(animation.getAnimationDuration() - animationTime);
			}
			else {
				animation = getReversedActionAnimation();
			}
		}
		
		if (typeConfig.textureAfterAction != null) {
			sprite = createSprite(typeConfig.texture);
		}
		
		playInteractionSound();
		
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private AnimationDirector<TextureRegion> getReversedActionAnimation() {
		return getAnimation(typeConfig.animationActionReversed);
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		EventHandler.getInstance().removeEventListener(this);
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		if (isPlayableCharacterContact(contact)) {
			playerContact = true;
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		super.endContact(contact);
		if (isPlayableCharacterContact(contact)) {
			playerContact = false;
			if (reverseInteractionAfterPlayerContactEnds) {
				reverseInteraction();
				reverseInteractionAfterPlayerContactEnds = false;
			}
		}
	}
}
