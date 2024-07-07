package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.SingleFrameTextureAnimationDirector;
import net.jfabricationgames.gdx.animation.TextureAnimationDirector;
import net.jfabricationgames.gdx.attack.DummyAttackHandler;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class Dummy extends Enemy {
	
	private static final String STATE_NAME_DAMAGE = "damage";
	private static final String STATE_NAME_DAMAGE_LONG = "damage_long";
	
	private AnimationDirector<TextureRegion> idleAnimation;
	
	public Dummy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		idleAnimation = new SingleFrameTextureAnimationDirector<TextureRegion>((TextureAnimationDirector<TextureRegion>) idleState.getAnimation());
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
	}
	
	@Override
	protected void initializeAttackHandler() {
		attackHandler = new DummyAttackHandler();
	}
	
	protected String getDamageStateName(float damage) {
		if (Math.random() > 0.75f) {
			return STATE_NAME_DAMAGE_LONG;
		}
		return STATE_NAME_DAMAGE;
	}
	
	@Override
	protected void die() {
		// reset the dummy to full health (it doesn't get destroyed if it's defeated)
		health = typeConfig.health;
		
		stateMachine.setState(STATE_NAME_DAMAGE);
		
		fireEnemyDefeatedEvent();
	}
	
	@Override
	protected AnimationDirector<TextureRegion> getAnimation() {
		if (stateMachine.getCurrentState().getStateName().equals(STATE_NAME_IDLE)) {
			return idleAnimation;
		}
		
		return super.getAnimation();
	}
}
