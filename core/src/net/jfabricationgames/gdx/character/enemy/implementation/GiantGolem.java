package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.BackToStartingPointMovementAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class GiantGolem extends Enemy {
	
	public static final String STATE_NAME_IDLE = "idle";
	public static final String STATE_NAME_MOVE = "move";
	
	public GiantGolem(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		
		return new BackToStartingPointMovementAI(ai, moveState, idleState, 0f);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		// TODO
		return ai;
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		// TODO
		super.takeDamage(damage, attackType);
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
