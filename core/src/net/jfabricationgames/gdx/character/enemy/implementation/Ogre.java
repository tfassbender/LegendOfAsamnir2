package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ActionAI;
import net.jfabricationgames.gdx.character.enemy.ai.OgreAttackAI;
import net.jfabricationgames.gdx.character.player.Player;
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
		// the charge states are followed by the attack states
		String stateNameCharge = "charge";
		String stateNameCharge2 = "charge_2";
		String stateNameAttack1 = "attack_1";
		String stateNameAttack2 = "attack_2";
		
		CharacterState chargeState = stateMachine.getState(stateNameCharge);
		CharacterState chargeState2 = stateMachine.getState(stateNameCharge2);
		
		CharacterState attackState1 = stateMachine.getState(stateNameAttack1);
		CharacterState attackState2 = stateMachine.getState(stateNameAttack2);
		attackState1.setTargetDirectionSupplier(this::directionToTarget);
		attackState2.setTargetDirectionSupplier(this::directionToTarget);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameCharge, chargeState);
		attackStates.put(stateNameCharge2, chargeState2);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(chargeState, 2.5f);
		attackDistances.put(chargeState2, 2.5f);
		
		float minTimeBetweenAttacks = 2f;
		float maxTimeBetweenAttacks = 3f;
		
		OgreAttackAI attackAI = new OgreAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
		attackAI.setMoveToPlayerWhileAttacking(false);
		
		return attackAI;
	}
	
	protected Vector2 directionToTarget() {
		return Player.getInstance().getPosition().cpy().sub(getPosition());
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
		playMapBackgroundMusicAfterBossDefeated();
	}
}
