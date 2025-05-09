package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class CocoonProjectile extends Projectile {
	
	private boolean spawnCocoon;
	
	public CocoonProjectile(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap, boolean spawnCocoon) {
		super(typeConfig, attackConfig, animation, gameMap);
		this.spawnCocoon = spawnCocoon;
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.2f);
	}
	
	@Override
	public void removeFromMap() {
		if (spawnCocoon) {
			// position needs to be collected before removing from the map (which will set the body to null)
			float x = body.getPosition().x * Constants.SCREEN_TO_WORLD;
			float y = body.getPosition().y * Constants.SCREEN_TO_WORLD;
			
			PhysicsWorld.getInstance().runAfterWorldStep(() -> {
				spawnFactory.createAndAddEnemy(typeConfig.spawnEnemyTypeWhenRemoved, x, y, new MapProperties());
			});
		}
		
		super.removeFromMap();
	}
}
