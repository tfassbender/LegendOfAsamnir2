package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.maps.MapProperties;

/**
 * Enables the projectiles to spawn other objects like enemies.
 */
public interface ProjectileSpawnFactory {
	
	public void createAndAddEnemy(String type, float x, float y, MapProperties mapProperties);
}
