package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ActionAI;
import net.jfabricationgames.gdx.character.enemy.ai.OgreAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Ogre extends Enemy {
	
	private static final String STATE_NAME_DEATH = "death";
	
	public Ogre(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFollowAI(ai);
		ai = createOgreAttackAI(ai);
		ai = createActionAI(ai);
	}
	
	private ArtificialIntelligence createFollowAI(ArtificialIntelligence ai) {
		CharacterState movingState = stateMachine.getState("move");
		CharacterState idleState = stateMachine.getState("idle");
		
		FollowAI followAI = new FollowAI(ai, movingState, idleState);
		followAI.setMinDistanceToTarget(1.7f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createOgreAttackAI(ArtificialIntelligence ai) {
		String chargeName = "charge";
		String attackName1 = "attack_1";
		String attackName2 = "attack_2";
		
		CharacterState chargeState = stateMachine.getState(chargeName);
		CharacterState attackState1 = stateMachine.getState(attackName1);
		CharacterState attackState2 = stateMachine.getState(attackName2);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(chargeName, chargeState);
		attackStates.put(attackName1, attackState1);
		attackStates.put(attackName2, attackState2);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(chargeState, 2.5f);
		attackDistances.put(attackState1, 2.5f);
		attackDistances.put(attackState2, 2.5f);
		
		float minTimeBetweenAttacks = 2f;
		float maxTimeBetweenAttacks = 4f;
		
		return new OgreAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	
	private ArtificialIntelligence createActionAI(ArtificialIntelligence ai) {
		CharacterState tauntState = stateMachine.getState("idle_2");
		float timeBetweenActions = 5f;
		float minDistToEnemy = 5f;
		float maxDistToEnemy = 20f;
		
		return new ActionAI(ai, tauntState, minDistToEnemy, maxDistToEnemy, timeBetweenActions);
	}
	
	@Override
	protected String getDieStateName() {
		return STATE_NAME_DEATH;
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
