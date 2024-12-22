package net.jfabricationgames.gdx.attack.implementation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.attack.Attack;
import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.projectile.ProjectileFactory;

public class ProjectileAttack extends Attack {
	
	private Projectile projectile;
	
	public ProjectileAttack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		super(config, direction, body, collisionType);
	}
	
	@Override
	protected void start() {
		Vector2 startingPosition = body.getPosition().cpy().add(0, config.projectileStartOffsetY);
		projectile = ProjectileFactory.createProjectileAndAddToMap(config.projectileType, startingPosition, direction, collisionType);
		projectile.setDamage(config.damage);
		projectile.setPushForce(config.pushForce);
		projectile.setPushForceWhenBlocked(config.pushForceWhenBlocked);
		projectile.setPushForceAffectedByBlock(config.pushForceAffectedByBlock);
		projectile.setExplosionDamage(config.explosionDamage);
		projectile.setExplosionPushForce(config.explosionPushForce);
		projectile.setExplosionPushForceAffectedByBlock(config.explosionPushForceAffectedByBlock);
		projectile.setPlayerBody(body);
		
		started = true;
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
}
