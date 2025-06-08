package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;

public class HomingProjectile extends Projectile {
	
	private PlayableCharacter player;
	
	protected HomingProjectile(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, AnimationDirector<TextureRegion> animation, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, animation, gameMap);
		
		player = Player.getInstance();
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (body != null) { // check whether the projectile still exists on the map
			// make the projectile follow the player
			moveTo(player.getPosition());
		}
	}
	
	private void moveTo(Vector2 pos) {
		Vector2 direction = pos.cpy().sub(getPosition());
		direction.nor().scl(typeConfig.speed);
		
		move(direction);
		rotateProjectile(direction);
	}
	
	private void move(Vector2 delta) {
		float force = 10f * body.getMass();
		body.applyForceToCenter(delta.x * force, delta.y * force, true);
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.4f);
	}
}
