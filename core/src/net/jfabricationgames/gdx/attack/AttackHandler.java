package net.jfabricationgames.gdx.attack;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.attack.implementation.ProjectileAttack;
import net.jfabricationgames.gdx.character.state.CharacterStateAttackHandler;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.projectile.Projectile;

public class AttackHandler implements CharacterStateAttackHandler {
	
	protected ArrayMap<String, AttackConfig> configs;
	
	private Body body;
	private PhysicsCollisionType collisionType;
	
	private Array<Attack> attacks = new Array<>();
	private Array<Projectile> projectiles = new Array<>();
	
	public AttackHandler(String attackConfigFile, Body body, PhysicsCollisionType collisionType) {
		this.body = body;
		this.collisionType = collisionType;
		
		loadAttackConfig(attackConfigFile);
	}
	
	protected void loadAttackConfig(String attackConfigFile) {
		configs = new ArrayMap<>();
		FileHandle attackConfigFileHandle = Gdx.files.internal(attackConfigFile);
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		Array<AttackConfig> attackConfigs = json.fromJson(Array.class, AttackConfig.class, attackConfigFileHandle);
		
		for (AttackConfig config : attackConfigs) {
			configs.put(config.id, config);
		}
	}
	
	public void handleAttacks(float delta) {
		Iterator<Attack> iter = attacks.iterator();
		while (iter.hasNext()) {
			Attack attack = iter.next();
			
			attack.increaseTimer(delta);
			
			if (attack.isExecuted()) {
				attack.remove();
				iter.remove();
			}
			
			if (attack.isToStart()) {
				attack.start();
				if (attack instanceof ProjectileAttack) {
					projectiles.add(((ProjectileAttack) attack).getProjectile());
				}
			}
		}
		
		Iterator<Projectile> projectileIterator = projectiles.iterator();
		while (projectileIterator.hasNext()) {
			Projectile projectile = projectileIterator.next();
			if (projectile.isRemoved()) {
				projectileIterator.remove();
			}
		}
	}
	
	public void renderAttacks(float delta, SpriteBatch batch) {
		for (Attack attack : attacks) {
			attack.render(delta, batch);
		}
	}
	
	public void renderAttacks(ShapeRenderer shapeRenderer) {
		for (Attack attack : attacks) {
			attack.render(shapeRenderer);
		}
	}
	
	public boolean allAttacksExecuted() {
		for (Attack attack : attacks) {
			if (!attack.isExecuted()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Attack startAttack(String attack, Vector2 direction) {
		AttackConfig attackConfig = configs.get(attack);
		if (attackConfig == null) {
			Gdx.app.error(getClass().getSimpleName(), "No attack config found for attack '" + attack + "'. Available attacks: " + configs.keys());
			return null;
		}
		
		return startAttack(attackConfig, direction, collisionType);
	}
	
	private Attack startAttack(AttackConfig config, Vector2 direction, PhysicsCollisionType collisionType) {
		Attack attack = AttackFactory.createAttack(config, direction, body, collisionType);
		attacks.add(attack);
		return attack;
	}
	
	public void abortAllAttacks() {
		for (Attack attack : attacks) {
			attack.abort();
		}
		attacks.clear();
	}
	
	public void removeAllProjectiles() {
		for (Projectile projectile : projectiles) {
			projectile.removeFromMap();
		}
		projectiles.clear();
	}
	
	/**
	 * Set the body that gets the hit fixtures, because the body might not have been created when the {@link AttackHandler} was initialised.
	 */
	public void setBody(Body body) {
		this.body = body;
	}
	
	public void handleAttackDamage(Contact contact) {
		for (Attack attack : attacks) {
			attack.dealAttackDamage(contact);
		}
	}
}
