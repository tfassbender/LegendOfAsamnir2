package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class MultiStateSwitchObject extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_ROTATING_PUZZLE_ID = "stateSwitchId";
	private static final String MAP_PROPERTY_KEY_INITIAL_STATE = "initialState";
	private static final String MAP_PROPERTY_KEY_ALLOWED_DIRECTIONS = "allowedDirections";
	
	private Array<Sprite> directionTextures;
	private Direction currentDirection;
	
	private String stateSwitchId;
	private Array<Direction> allowedDirections;
	
	public MultiStateSwitchObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		
		EventHandler.getInstance().registerEventListener(this);
		
		stateSwitchId = properties.get(MAP_PROPERTY_KEY_ROTATING_PUZZLE_ID, String.class);
		
		processAllowedDirectionsFromMapProperties(properties);
		chooseInitialStateFromMapProperties(properties);
	}
	
	private void processAllowedDirectionsFromMapProperties(MapProperties properties) {
		String allowedDirectionsString = properties.get(MAP_PROPERTY_KEY_ALLOWED_DIRECTIONS, String.class);
		allowedDirections = new Array<>();
		if (allowedDirectionsString != null) {
			// expected format: "[UP,RIGHT,DOWN,LEFT]" - order does not matter
			String[] directionNames = allowedDirectionsString.replace("[", "").replace("]", "").split(",");
			for (String directionName : directionNames) {
				Direction direction = Direction.fromName(directionName.trim());
				if (direction != null) {
					allowedDirections.add(direction);
				}
				else {
					Gdx.app.error(getClass().getSimpleName(), "The allowed direction '" + directionName + "' is not valid for MultiStateSwitchObject. Map-ID: " + getMapObjectId());
				}
			}
		}
		else {
			Gdx.app.error(getClass().getSimpleName(), "No allowed directions defined for MultiStateSwitchObject. Map-ID: " + getMapObjectId());
			// default to all directions
			allowedDirections.addAll(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT);
		}
	}
	
	private void chooseInitialStateFromMapProperties(MapProperties properties) {
		String initialStateString = properties.get(MAP_PROPERTY_KEY_INITIAL_STATE, String.class);
		currentDirection = Direction.fromName(initialStateString);
		if (currentDirection == null) {
			Gdx.app.error(getClass().getSimpleName(), "The initial state '" + initialStateString + "' is not valid for MultiStateSwitchObject. Map-ID: " + getMapObjectId());
			// default to UP
			currentDirection = Direction.UP;
		}
		else if (!allowedDirections.contains(currentDirection, false)) {
			Gdx.app.error(getClass().getSimpleName(), "The initial state '" + initialStateString + "' is not in the allowed directions for MultiStateSwitchObject. Map-ID: " + getMapObjectId());
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		// draw the texture of the current direction
		Sprite sprite = directionTextures.get(currentDirection.index);
		sprite.draw(batch);
		
		if (showInteractionIcon()) {
			interactionAnimation.increaseStateTime(delta);
			interactionAnimation.draw(batch);
		}
	}
	
	@Override
	public void interact() {
		super.interact();
		
		currentDirection = nextDirection();
		
		// fire an event that the switch state has been changed
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.MULTI_STATE_SWITCH_ACTION) //
				.setStringValue(stateSwitchId) //
				.setIntValue(currentDirection.index));
	}
	
	/**
	 * Choose the next direction (in the directions enum) that is allowed - the order in the allowedDirections array is ignored.
	 * 
	 * @return the next allowed direction
	 */
	private Direction nextDirection() {
		Direction nextDirection = currentDirection;
		do {
			int nextIndex = (nextDirection.index + 1) % Direction.values().length;
			nextDirection = Direction.fromIndex(nextIndex);
		} while (!allowedDirections.contains(nextDirection, false));
		
		return nextDirection;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (EventType.MULTI_STATE_SWITCH_STATE_REQUEST.equals(event.eventType) && event.stringValue.equals(stateSwitchId)) {
			// check whether the expected value (the intValue of the event) equals the current direction code and set the result to the event
			event.booleanValue = event.intValue == currentDirection.index;
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		EventHandler.getInstance().removeEventListener(this);
	}
	
	public void setAdditionalTextures(Array<Sprite> additionalTextures) {
		this.directionTextures = additionalTextures;
	}
	
	private enum Direction {
		
		UP(0), //
		RIGHT(1), //
		DOWN(2), // 
		LEFT(3);
		
		private final int index;
		
		private Direction(int index) {
			this.index = index;
		}
		
		private static Direction fromIndex(int index) {
			for (Direction direction : values()) {
				if (direction.index == index) {
					return direction;
				}
			}
			return null;
		}
		
		private static Direction fromName(String name) {
			for (Direction direction : values()) {
				if (direction.name().equalsIgnoreCase(name)) {
					return direction;
				}
			}
			return null;
		}
	}
}
