package net.jfabricationgames.gdx.object.interactive;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;

public class RotatingPuzzle extends InteractiveObject {
	
	private Array<Sprite> symbols;
	private int currentSymbolIndex = 0;
	
	public RotatingPuzzle(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties properties, GameObjectMap gameMap) {
		super(typeConfig, sprite, properties, gameMap);
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
	}
	
	public void setAdditionalTextures(Array<Sprite> additionalTextures) {
		this.symbols = additionalTextures;
	}
}
