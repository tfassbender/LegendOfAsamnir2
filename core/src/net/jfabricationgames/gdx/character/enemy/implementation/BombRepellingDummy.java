package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.AvoidPlayerAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.projectile.Bomb;

public class BombRepellingDummy extends Enemy implements EventListener {
	
	private boolean useForceField; // a force field to repel bombs that the player throws
	private boolean active = false;
	
	public BombRepellingDummy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		CharacterState idleState = stateMachine.getState("idle");
		CharacterState movingState = stateMachine.getState("move");
		float avoidingDistance = 3f;
		
		ai = new BaseAI();
		ai = new AvoidPlayerAttackAI<Bomb>(ai, movingState, idleState, Bomb.class, avoidingDistance).setAttackConsumer(this::onAvoidAttack);
	}
	
	private void onAvoidAttack(Array<Bomb> avoidedAttacks) {
		useForceField = !avoidedAttacks.isEmpty();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (active && useForceField && attackHandler.allAttacksExecuted()) { // only start one force field at the time to improve the animation
			attackHandler.startAttack("attack_force_field", new Vector2(1, 0)); // direction does not matter
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		// the dummy has no texture, so only draw the attacks
		attackHandler.renderAttacks(delta, batch);
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		// the dummy doesn't take damage (it's not really an enemy)
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) // 
				&& "loa2_l5_castle_of_the_chaos_wizard__bomb_repelling_dummy__active".equals(event.stringValue)) {
			active = event.booleanValue;
		}
	}
}
