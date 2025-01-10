package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ActionAI;
import net.jfabricationgames.gdx.character.enemy.ai.MinotaurAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Minotaur extends Enemy {
	
	public Minotaur(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.MELEE)) {
			super.takeDamage(damage, attackType);
		}
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFollowAI(ai);
		ai = createMinotaurAttackAI(ai);
		ai = createActionAI(ai);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToTarget(1.7f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createMinotaurAttackAI(ArtificialIntelligence ai) {
		String attackNameHit = "attack_hit";
		String attackNameKnock = "attack_knock";
		String attackNameSpin = "attack_spin";
		String attackNameStab = "attack_stab";
		
		CharacterState attackHitState = stateMachine.getState(attackNameHit);
		CharacterState attackKnockState = stateMachine.getState(attackNameKnock);
		CharacterState attackSpinState = stateMachine.getState(attackNameSpin);
		CharacterState attackStabState = stateMachine.getState(attackNameStab);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(attackNameHit, attackHitState);
		attackStates.put(attackNameKnock, attackKnockState);
		attackStates.put(attackNameSpin, attackSpinState);
		attackStates.put(attackNameStab, attackStabState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackHitState, 2.5f);
		attackDistances.put(attackKnockState, 4f);
		attackDistances.put(attackSpinState, 2.5f);
		attackDistances.put(attackStabState, 3f);
		
		float minTimeBetweenAttacks = 1f;
		float maxTimeBetweenAttacks = 2.5f;
		
		return new MinotaurAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	
	private ArtificialIntelligence createActionAI(ArtificialIntelligence ai) {
		CharacterState tauntState = stateMachine.getState("taunt");
		float timeBetweenActions = 5f;
		float minDistToEnemy = 5f;
		float maxDistToEnemy = 7f;
		
		return new ActionAI(ai, tauntState, minDistToEnemy, maxDistToEnemy, timeBetweenActions);
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (damage >= 15) {
			return "damage_high";
		}
		else {
			return "damage_low";
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
