package net.jfabricationgames.gdx.attack;

import com.badlogic.gdx.math.Vector2;

/**
 * Indicates that the implementing class' objects can be hit and can take damage.
 */
public interface Hittable {
	
	public void takeDamage(float damage, AttackType melee);
	
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected);
	
	public default Vector2 getPushDirection(Vector2 position, Vector2 hitCenter) {
		return position.cpy().sub(hitCenter).nor();
	}
	
	public default void freeze() {
		//default implementation does nothing
	}
}
