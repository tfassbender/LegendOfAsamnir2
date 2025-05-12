package net.jfabricationgames.gdx.object.interactive;

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

public class RotatingPuzzle extends InteractiveObject implements EventListener {
	
	private static final String MAP_PROPERTY_KEY_ROTATING_PUZZLE_ID = "rotatingPuzzleId";
	private static final String MAP_PROPERTY_KEY_EVENT_PARAMETER = "eventParameter";
	
	private Array<Sprite> symbols;
	private int currentSymbolIndex = 0;
	private String rotatingPuzzleId;
	private String eventParameter;
	
	public RotatingPuzzle(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
		
		EventHandler.getInstance().registerEventListener(this);
		
		rotatingPuzzleId = properties.get(MAP_PROPERTY_KEY_ROTATING_PUZZLE_ID, String.class);
		eventParameter = properties.get(MAP_PROPERTY_KEY_EVENT_PARAMETER, String.class);
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (!drawAnimation()) {
			// draw the symbol of the current rotation state
			Sprite symbolSprite = symbols.get(currentSymbolIndex);
			symbolSprite.draw(batch);
		}
	}
	
	@Override
	public void interact() {
		super.interact();
		
		currentSymbolIndex = (currentSymbolIndex + 1) % symbols.size;
		
		// fire an event that the rotating puzzle has been rotated
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ROTATING_PUZZLE_INTERACTION).setStringValue(eventParameter));
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (EventType.ROTATING_PUZZLE_STATE_REQUEST.equals(event.eventType) && event.stringValue.equals(rotatingPuzzleId)) {
			// check whether the expected value (the intValue of the event) equals the current symbol index and set the result to the event
			event.booleanValue = event.intValue == currentSymbolIndex;
		}
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
		
		EventHandler.getInstance().removeEventListener(this);
	}
	
	public void setAdditionalTextures(Array<Sprite> additionalTextures) {
		this.symbols = additionalTextures;
	}
}
