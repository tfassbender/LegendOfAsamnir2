package net.jfabricationgames.gdx.attack.implementation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.projectile.ProjectileFactory;

public class ProjectileAttack extends Attack {
	
	private Projectile projectile;
	
	public ProjectileAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
		
		if (!config.type.isSubTypeOf(AttackType.PROJECTILE)) {
			Gdx.app.error(getClass().getSimpleName(), "A ProjectileAttack was created with a config type that is not a projectile attack: " + config.type);
		}
	}
	
	@Override
	protected void start() {
		Vector2 startingPosition = calculateStartingPosition();
		projectile = ProjectileFactory.createProjectileAndAddToMap(config, startingPosition, direction, collisionType);
		projectile.setPlayerBody(body);
		
		started = true;
	}
	
	private Vector2 calculateStartingPosition() {
		Vector2 startingPosition = body.getPosition().cpy().add(0, config.projectileStartOffsetY);
		
		if (config.startAttackAtPlayerPosition) {
			Vector2 targetPosition = targetPositionSupplier.get();
			if (targetPosition != null) {
				startingPosition = targetPosition;
			}
		}
		
		return startingPosition;
	}
	
	@Override
	protected void remove() {
		// the projectile will remove itself
	}
	
	@Override
	protected void dealAttackDamage(Contact contact) {
		// damage is calculated in the projectile
	}
	
	@Override
	protected boolean isRemoved() {
		return projectile == null || projectile.isRemoved();
	}
	
	public Projectile getProjectile() {
		return projectile;
	}
}
