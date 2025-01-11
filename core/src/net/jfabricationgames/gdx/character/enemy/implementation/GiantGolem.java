package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.GiantGolemAttackAI;
import net.jfabricationgames.gdx.character.enemy.ai.GiantGolemMovementAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.state.GameStateManager;

public class GiantGolem extends Enemy {
	
	public static final String STATE_NAME_IDLE = "idle";
	public static final String STATE_NAME_MOVE = "move";
	public static final String STATE_NAME_VULNERABLE = "vulnerable";
	public static final String STATE_NAME_DIE = "die";
	
	private GiantGolemMovementAI giantGolemMovementAI;
	
	private Vector2 startingPosition;
	private boolean forceFieldDeactivated;
	private boolean minYSet;
	
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
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		// calculate the starting position for the giant golem (a physics body is needed for that)
		giantGolemMovementAI.setCharacter(this); // needs to be set to calculate the starting position
		giantGolemMovementAI.calculateStartingPosition();
		
		startingPosition = new Vector2(x, y);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIST) && !stateMachine.isInState(STATE_NAME_VULNERABLE)) {
			// after being attacked by a dwarven guardian construct, the giant golem is vulnerable for a short time
			stateMachine.setState(STATE_NAME_VULNERABLE);
			// reactivate the force field after being attacked
			forceFieldDeactivated = false;
		}
		else if (attackType.isSubTypeOf(AttackType.DWARVEN_GUARDIAN_CONSTRUCT_FIRE) && stateMachine.isInState(STATE_NAME_VULNERABLE)) {
			// after being attacked by a fire attack of a dwarven guardian construct, the giant golem's force field is deactivated
			forceFieldDeactivated = true;
		}
		else if (damage > 0 && // only if the bomb explodes - not only touches the giant golem
				attackType.isSubTypeOf(AttackType.BOMB) && stateMachine.isInState(STATE_NAME_VULNERABLE)) {
			// in the vulnerable state the giant golem can take damage from bombs
			super.takeDamage(20, attackType); // always take 20 (percent) damage from bombs - no matter the upgrade level of the bombs
			
			if (!stateMachine.isInState(STATE_NAME_DIE)) {
				// after being hit, the vulnerable state ends (to not take damage from multiple bombs at once)
				stateMachine.forceStateChange(STATE_NAME_IDLE);
			}
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!minYSet) {
			minYSet = true;
			// set the minimum y position here, because when creating the AI the map is not loaded yet
			CutsceneControlledUnit minY = GameMapManager.getInstance().getMap().getUnitById("loa2_l1_dwarven_caves_castle__boss_movement_position_min_y");
			giantGolemMovementAI.setMinY(minY.getPosition().y);
		}
		
		if (getPercentualHealth() > 0 && getPercentualHealth() < 0.5f && getPosition().y > startingPosition.y - 2.5f) {
			// low health and near starting position -> heal
			health += delta * 0.75f; // heal 0.75% per second
			
			if (!forceFieldDeactivated) {
				// start force field attacks while healing to prevent the player from using bombs to easily
				if (getPosition().y > startingPosition.y - 1.5f && // don't start to early - wait till the golem almost reached the starting position 
						attackHandler.allAttacksExecuted()) { // only start one force field at the time to improve the animation
					attackHandler.startAttack("attack_force_field", new Vector2(1, 0)); // direction does not matter
				}
			}
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
