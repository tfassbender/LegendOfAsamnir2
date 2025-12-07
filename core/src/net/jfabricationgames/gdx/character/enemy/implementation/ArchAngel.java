package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ArchAngelAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class ArchAngel extends Enemy implements CharacterStateChangeListener {
	
	private static final Vector2 FIXED_ATTACK_DIRECTION = new Vector2(0, 1);
	
	private static final String ATTACK_NAME_FORCE_FIELD = "attack_force_field";
	private static final String STATE_NAME_DEFENSE_MODE = "defense_mode";
	private static final String STATE_NAME_DEFENSE_MODE_CAST = "defense_mode_cast";
	private static final String STATE_NAME_CAST_SPAWN_ANGEL = "cast_spawn_angel";
	private static final String STATE_NAME_CAST_SPAWN_ANGELIC_HELMET = "cast_spawn_angelic_helmet";
	private static final String STATE_NAME_CAST_ATTACK = "cast_attack";
	private static final String STATE_NAME_ATTACK_LIGNTNING_SOIL = "attack_lightning_soil";
	private static final String STATE_NAME_ATTACK_FIRE_SOIL = "attack_fire_soil";
	private static final String STATE_NAME_ATTACK_WIND_SOIL = "attack_wind_soil";
	private static final String STATE_NAME_ATTACK_EARTH_SOIL = "attack_earth_soil";
	
	/**
	* Change to defense mode every time the health passes a multiple of this value.
	*/
	private float healthLossForDefenseModeInPercent = 20f;
	private float defenseModeForceFieldTimer = 0f;
	private float defenseModeCastTimer = 0f;
	private boolean defenseMode = false;
	private CharacterState nextState = null; // used to change the state in the next act() call - see onCharacterStateChange
	private ArchAngelAttackAI archAngelAttackAI;
	
	public ArchAngel(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health -= 0.1f; // reduce the health by a small value to prevent the defense mode from being activated at the first damage
		
		stateMachine.addChangeListener(this);
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
		followAI.setMinDistanceToTarget(4f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String stateNameAttackHit = "attack";
		String stateNameCastAttack = "cast_attack";
		//cast_attack, cast_spawn_angel, cast_spawn_angelic_helmet,
		String stateNameCaseSpawnAngel = "cast_spawn_angel";
		String stateNameCaseSpawnAngelicHelmet = "cast_spawn_angelic_helmet";
		
		CharacterState characterStateAttackHit = stateMachine.getState(stateNameAttackHit);
		CharacterState characterStateCastAttack = stateMachine.getState(stateNameCastAttack);
		CharacterState characterStateCaseSpawnAngel = stateMachine.getState(stateNameCaseSpawnAngel);
		CharacterState characterStateCaseSpawnAngelicHelmet = stateMachine.getState(stateNameCaseSpawnAngelicHelmet);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackHit, characterStateAttackHit);
		attackStates.put(stateNameCastAttack, characterStateCastAttack);
		attackStates.put(stateNameCaseSpawnAngel, characterStateCaseSpawnAngel);
		attackStates.put(stateNameCaseSpawnAngelicHelmet, characterStateCaseSpawnAngelicHelmet);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackHit, 2f);
		attackDistances.put(characterStateCastAttack, 8f);
		attackDistances.put(characterStateCaseSpawnAngel, 100f); // no range restriction
		attackDistances.put(characterStateCaseSpawnAngelicHelmet, 100f); // no range restriction
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackHit, new FixedAttackTimer(1.5f));
		attackTimers.put(characterStateCastAttack, new RandomIntervalAttackTimer(5f, 7f));
		attackTimers.put(characterStateCaseSpawnAngel, new RandomIntervalAttackTimer(25f, 35f));
		attackTimers.put(characterStateCaseSpawnAngelicHelmet, new RandomIntervalAttackTimer(15f, 20f));
		
		archAngelAttackAI = new ArchAngelAttackAI(ai, attackStates, attackDistances, attackTimers);
		
		return archAngelAttackAI;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (defenseMode) {
			defenseModeForceFieldTimer -= delta;
			defenseModeCastTimer -= delta;
			
			if (defenseModeForceFieldTimer <= 0f) {
				defenseModeForceFieldTimer = 1.5f; // start a new force field every 1.5 seconds
				attackHandler.startAttack(ATTACK_NAME_FORCE_FIELD, FIXED_ATTACK_DIRECTION); // direction does not matter
			}
			
			if (defenseModeCastTimer <= 0f) {
				defenseModeCastTimer = 5f;
				stateMachine.setState(STATE_NAME_DEFENSE_MODE_CAST);
				// TODO spawn a new minion 
			}
		}
		else {
			if (nextState != null) {
				// change the state that was set in onCharacterStateChange()
				nextState.setAttackTargetPositionSupplier(archAngelAttackAI::getTargetPlayerPosition);
				stateMachine.setState(nextState);
				nextState = null;
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (defenseMode) {
			// no damage in defense mode - defense mode ends by an event
			return;
		}
		
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (healthSegmentAfterDamage < healthSegmentBeforeDamage) {
			// TODO start a cutscene to move to a defense position and change the magic pipes before starting the defense mode
			startDefenseMode();
		}
	}
	
	private void startDefenseMode() {
		defenseMode = true;
		defenseModeCastTimer = 5f;
		stateMachine.forceStateChange(STATE_NAME_DEFENSE_MODE);
	}
	
	// TODO will be ended by an event - implement event handling
	private void stopDefenseMode() {
		defenseMode = false;
		stateMachine.forceStateChange(STATE_NAME_IDLE);
	}
	
	@Override
	protected void die() {
		super.die();
		
		playMapBackgroundMusicAfterBossDefeated();
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__arch_angel_defeated"));
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		if (STATE_NAME_CAST_ATTACK.equals(newState.getStateName())) {
			// use a random "soil" attack (fire, lightning, wind, earth) by changing the state manually
			CharacterState attackState;
			
			float random = (float) Math.random();
			if (random < 0.25f) {
				attackState = stateMachine.getState(STATE_NAME_ATTACK_FIRE_SOIL);
			}
			else if (random < 0.5f) {
				attackState = stateMachine.getState(STATE_NAME_ATTACK_WIND_SOIL);
			}
			else if (random < 0.75f) {
				attackState = stateMachine.getState(STATE_NAME_ATTACK_EARTH_SOIL);
			}
			else {
				attackState = stateMachine.getState(STATE_NAME_ATTACK_LIGNTNING_SOIL);
			}
			
			attackState.setAttackDirection(FIXED_ATTACK_DIRECTION); // the attack starts at the player's position, so the direction does not matter
			nextState = attackState;
			/*
			 * The state can not be changed in this method, because that would need a nested iterator for the state change listeners Array, 
			 * which the libGDX Array implementation does not allow. Therefore we just set the state to a variable and change it in the next act() call.
			 */
		}
		else if (STATE_NAME_CAST_SPAWN_ANGEL.equals(newState.getStateName())) {
			// TODO spawn an angel minion
		}
		else if (STATE_NAME_CAST_SPAWN_ANGELIC_HELMET.equals(newState.getStateName())) {
			// TODO spawn angelic helmet minions
		}
	}
}
