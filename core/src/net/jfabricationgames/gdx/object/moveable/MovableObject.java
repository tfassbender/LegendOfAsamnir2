package net.jfabricationgames.gdx.object.moveable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.object.GameObjectMap;
import net.jfabricationgames.gdx.object.GameObjectTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class MovableObject extends GameObject implements ContactListener {
	
	/**
	 * Sort movable game objects to the end of the list, to make them drawn on top of other objects.
	 */
	public static Array<GameObject> sortMovableGameObjectsLast(Array<GameObject> objects) {
		int listSize = objects.size;
		for (int i = 0; i < listSize; i++) {
			GameObject object = objects.get(i);
			if (object instanceof MovableObject) {
				objects.removeIndex(i);
				listSize--;
				i--;
				objects.add(object);
			}
		}
		return objects;
	}
	
	public MovableObject(GameObjectTypeConfig typeConfig, Sprite sprite, MapProperties mapProperties, GameObjectMap gameMap) {
		super(typeConfig, sprite, mapProperties, gameMap);
	}
	
	@Override
	public void postAddToGameMap() {
		super.postAddToGameMap();
		
		if (typeConfig.onlyMovableByPlayer) {
			PhysicsWorld.getInstance().registerContactListener(this);
			disableMovement();
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null && !animation.isAnimationFinished()) {
			animation.increaseStateTime(delta);
			TextureRegion region = animation.getKeyFrame();
			animation.getSpriteConfig().setX((body.getPosition().x - region.getRegionWidth() * 0.5f)).setY((body.getPosition().y - region.getRegionHeight() * 0.5f));
			animation.draw(batch);
		}
		else {
			sprite.setX((body.getPosition().x - sprite.getRegionWidth() * 0.5f));
			sprite.setY((body.getPosition().y - sprite.getRegionHeight() * 0.5f));
			sprite.draw(batch);
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (typeConfig.onlyMovableByPlayer) {
			if (isPlayableCharacterContact(contact)) {
				enableMovement();
			}
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		if (typeConfig.onlyMovableByPlayer) {
			if (isPlayableCharacterContact(contact)) {
				disableMovement();
			}
		}
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	private void disableMovement() {
		body.getFixtureList().get(0).setDensity(100_000f * typeConfig.density);
		body.resetMassData();
	}
	
	private void enableMovement() {
		body.getFixtureList().get(0).setDensity(typeConfig.density);
		body.resetMassData();
	}
}
