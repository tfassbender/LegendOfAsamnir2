package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.GiantGolemAttackAI;
import net.jfabricationgames.gdx.character.enemy.ai.GiantGolemMovementAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.state.GameStateManager;

public class GiantGolem extends Enemy {
	
	public static final String STATE_NAME_IDLE = "idle";
	public static final String STATE_NAME_MOVE = "move";
	public static final String STATE_NAME_VULNERABLE = "vulnerable";
	public static final String STATE_NAME_DIE = "die";
	
	private GiantGolemMovementAI giantGolemMovementAI;
	
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
		
		// follow the player if the health is high
		FollowAI followAI = new FollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		giantGolemMovementAI = new GiantGolemMovementAI(followAI, moveState, idleState, this::getPercentualHealth);
		
		return giantGolemMovementAI;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		// calculate the starting position for the giant golem (a physics body is needed for that)
		giantGolemMovementAI.setCharacter(this); // needs to be set to calculate the starting position
		giantGolemMovementAI.calculateStartingPosition();
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String attackNameFist = "attack_fist";
		String attackNameStomp = "attack_stomp";
		
		CharacterState attackFistState = stateMachine.getState(attackNameFist);
		CharacterState attackStompState = stateMachine.getState(attackNameStomp);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(attackNameFist, attackFistState);
		attackStates.put(attackNameStomp, attackStompState);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackFistState, 2.5f);
		attackDistances.put(attackStompState, 3.2f);
		
		float minTimeBetweenAttacks = 2.5f;
		float maxTimeBetweenAttacks = 3.5f;
		
		return new GiantGolemAttackAI(ai, attackStates, attackDistances, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks));
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIST) && !stateMachine.isInState(STATE_NAME_VULNERABLE)) {
			// after being attacked by a dwarven guardian construct, the giant golem is vulnerable for a short time
			stateMachine.setState(STATE_NAME_VULNERABLE);
		}
		else if (attackType.isSubTypeOf(AttackType.BOMB) && stateMachine.isInState(STATE_NAME_VULNERABLE)) {
			// in the vulnerable state the giant golem can take damage from bombs
			super.takeDamage(damage, attackType);
			
			if (damage > 0 && !stateMachine.isInState(STATE_NAME_DIE)) { // only if the bomb explodes - not only touches the giant golem
				// after being hit, the vulnerable state ends (to not take damage from multiple bombs at once)
				stateMachine.forceStateChange(STATE_NAME_IDLE);
			}
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
