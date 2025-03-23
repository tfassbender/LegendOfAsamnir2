package net.jfabricationgames.gdx.attack;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.jfabricationgames.gdx.character.state.CharacterStateAttack;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public abstract class Attack implements CharacterStateAttack {
	
	protected float timer;
	protected boolean started;
	protected boolean aborted;
	
	protected AttackConfig config;
	protected PhysicsBodyProperties hitFixtureProperties;
	
	protected PhysicsCollisionType collisionType;
	
	protected Fixture hitFixture;
	
	protected Body body;
	protected Vector2 direction;
	protected Supplier<Vector2> targetPositionSupplier;
	
	protected Attack(AttackConfig config, Vector2 direction, Body body, PhysicsCollisionType collisionType) {
		this.config = config;
		this.collisionType = collisionType;
		this.direction = direction;
		this.body = body;
		
		timer = 0;
		started = false;
		aborted = false;
	}
	
	public void abort() {
		aborted = true;
	}
	
	public boolean isExecuted() {
		if ((config.type == AttackType.BOOMERANG || config.type == AttackType.HOOKSHOT) && !isRemoved()) {
			//there must be only one boomerang or hookshot at a time
			return false;
		}
		return aborted || (started && timer >= config.delay + config.duration);
	}
	
	protected boolean isToStart() {
		return !aborted && !started && timer >= config.delay;
	}
	
	protected void increaseTimer(float delta) {
		timer += delta;
	}
	
	protected void render(float delta, SpriteBatch batch) {}
	
	protected abstract void start();
	
	protected abstract void remove();
	
	protected boolean isRemoved() {
		return body == null;
	}
	
	protected abstract void dealAttackDamage(Contact contact);
	
	@Override
	public void setTargetPositionSupplier(Supplier<Vector2> targetPositionSupplier) {
		this.targetPositionSupplier = targetPositionSupplier;
	}
}
