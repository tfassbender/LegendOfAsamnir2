package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class VengefulSpirit extends Enemy {
	
	public VengefulSpirit(EnemyTypeConfig typeConfig, MapProperties properties) {
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
		
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(3f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSpectralSword = stateMachine.getState("attack");
		CharacterState attackArcaneShower = stateMachine.getState("attack_arcane_shower");
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put("attack", attackSpectralSword);
		attackStates.put("attack_arcane_shower", attackArcaneShower);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackSpectralSword, 5f);
		attackDistances.put(attackArcaneShower, 8f);
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(attackSpectralSword, new RandomIntervalAttackTimer(2f, 3f));
		attackTimers.put(attackArcaneShower, new FixedAttackTimer(5f));
		
		MultiAttackAI attackAi = new MultiAttackAI(ai, attackStates, attackDistances, attackTimers);
		attackAi.setMoveToPlayerWhenAttacking(false);
		
		return attackAi;
	}
}
