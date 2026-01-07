package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;

public class LichMinionMagicBlastProjectile extends HomingInertialMassProjectile {
	
	private Vector2 initialDirection;
	private float keepInitialDirectionTime = 1f; // time in seconds
	
	protected LichMinionMagicBlastProjectile(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
	}
	
	@Override
	protected void move(float delta) {
		if (body != null) { // check whether the projectile still exists on the map
			if (keepInitialDirectionTime > 0f && initialDirection != null) {
				// keep moving in the initial direction for some time
				Vector2 targetPos = body.getPosition().cpy().add(initialDirection);
				moveTo(targetPos);
				keepInitialDirectionTime -= delta;
				if (keepInitialDirectionTime < 0f) {
					keepInitialDirectionTime = 0f;
				}
			}
			else {
				// make the projectile follow the player
				moveTo(player.getPosition());
			}
		}
	}
	
	public void setInitialDirection(Vector2 direction) {
		initialDirection = direction.nor().scl(100f);
	}
}
