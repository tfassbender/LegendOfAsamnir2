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
import net.jfabricationgames.gdx.character.enemy.EnemyFactory;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ArchAngelAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;

public class ArchAngel extends Enemy implements CharacterStateChangeListener, EventListener {
	
	private static final Vector2 FIXED_ATTACK_DIRECTION = new Vector2(0, 1);
	
	private static final String ENEMY_TYPE_NAME_ANGELIC_HELMET = "angelic_helmet";
	private static final String SOUND_EFFECT_NAME_SPAWN = "spawn";
	private static final String ATTACK_NAME_FORCE_FIELD = "attack_force_field";
	private static final String CONFIG_EVENT_PARAMETER_START_DEFENSE_MODE = "loa2_l5_castle_of_the_chaos_wizard__throne_room__archangel_start_defense_mode";
	private static final String CONFIG_EVENT_PARAMETER_END_DEFENSE_MODE = "loa2_l5_castle_of_the_chaos_wizard__throne_room__archangel_end_defense_mode";
	
	private static final String STATE_NAME_DEFENSE_MODE = "defense_mode";
	private static final String STATE_NAME_DEFENSE_MODE_CAST = "defense_mode_cast";
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
	private ArchAngelAttackAI archAngelAttackAI;
	
	private float spawnAngelicHelmetsDelay = 0f;
	private float spawnAngelicHelmetInDefenseModeDelay = 0f;
	
	public ArchAngel(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health -= 0.1f; // reduce the health by a small value to prevent the defense mode from being activated at the first damage
		
		health = typeConfig.health * 0.1f; // TODO remove after tests - start the archangel with 10% health for testing
		
		stateMachine.addChangeListener(this);
		EventHandler.getInstance().registerEventListener(this);
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
		String stateNameCaseSpawnAngelicHelmet = "cast_spawn_angelic_helmet";
		
		CharacterState characterStateAttackHit = stateMachine.getState(stateNameAttackHit);
		CharacterState characterStateCastAttack = stateMachine.getState(stateNameCastAttack);
		CharacterState characterStateCaseSpawnAngelicHelmet = stateMachine.getState(stateNameCaseSpawnAngelicHelmet);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackHit, characterStateAttackHit);
		attackStates.put(stateNameCastAttack, characterStateCastAttack);
		attackStates.put(stateNameCaseSpawnAngelicHelmet, characterStateCaseSpawnAngelicHelmet);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackHit, 2f);
		attackDistances.put(characterStateCastAttack, 8f);
		attackDistances.put(characterStateCaseSpawnAngelicHelmet, 100f); // no range restriction
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackHit, new FixedAttackTimer(1.5f));
		attackTimers.put(characterStateCastAttack, new RandomIntervalAttackTimer(5f, 7f));
		attackTimers.put(characterStateCaseSpawnAngelicHelmet, new RandomIntervalAttackTimer(15f, 20f));
		
		archAngelAttackAI = new ArchAngelAttackAI(ai, attackStates, attackDistances, attackTimers);
		resetTimersForRangedAttacks();
		
		return archAngelAttackAI;
	}
	
	private void resetTimersForRangedAttacks() {
		archAngelAttackAI.resetAttackTimer("cast_attack");
		archAngelAttackAI.resetAttackTimer("cast_spawn_angelic_helmet");
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (defenseMode) {
			defenseModeForceFieldTimer -= delta;
			defenseModeCastTimer -= delta;
			
			if (defenseModeForceFieldTimer <= 0f) {
				defenseModeForceFieldTimer = 1f; // start a new force field every second
				attackHandler.startAttack(ATTACK_NAME_FORCE_FIELD, FIXED_ATTACK_DIRECTION); // direction does not matter
			}
			
			if (defenseModeCastTimer <= 0f) {
				defenseModeCastTimer = 5f;
				stateMachine.setState(STATE_NAME_DEFENSE_MODE_CAST);
				spawnAngelicHelmetInDefenseModeDelay = 0.5f; // spawn the helmet after a short delay to give the cast animation time to play
				SOUND_SET.playSound(SOUND_EFFECT_NAME_SPAWN);
			}
		}
		
		if (spawnAngelicHelmetsDelay > 0f) {
			spawnAngelicHelmetsDelay -= delta;
			if (spawnAngelicHelmetsDelay <= 0f) {
				spawnAngelicHelmets();
			}
		}
		if (spawnAngelicHelmetInDefenseModeDelay > 0f) {
			spawnAngelicHelmetInDefenseModeDelay -= delta;
			if (spawnAngelicHelmetInDefenseModeDelay <= 0f) {
				spawnAngelicHelmetInPlayerDirection();
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (defenseMode) {
			// no damage in defense mode - defense mode ends by an event
			return;
		}
		
		if (damage >= health) {
			// prevent death because the archangel will die in a cutscene
			damage = health - 1f;
		}
		
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (health <= typeConfig.health * 0.01f) {
			// start the final cutscene when the health falls below 1%
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.CONFIG_GENERATED_EVENT) //
					.setStringValue("loa2_l5_castle_of_the_chaos_wizard__throne_room__archangel_start_final_position_cutscene"));
			
			// prevent further actions (like a second cutscene from being started because of the defense mode)
			return;
		}
		
		if (healthSegmentAfterDamage < healthSegmentBeforeDamage) {
			// start a cutscene to move to a defense position and change the magic pipes (the defense mode will be started by an event from the cutscene)
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.CONFIG_GENERATED_EVENT) //
					.setStringValue("loa2_l5_castle_of_the_chaos_wizard__throne_room__archangel_start_defense_mode_cutscene"));
		}
	}
	
	@Override
	protected void die() {
		super.die();
		
		// kill all spawned angelic helmets when the archangel is defeated
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE));
		
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
			attackState.setAttackTargetPositionSupplier(archAngelAttackAI::getTargetPlayerPosition);
			stateMachine.setState(attackState);
		}
		else if (STATE_NAME_CAST_SPAWN_ANGELIC_HELMET.equals(newState.getStateName())) {
			spawnAngelicHelmetsDelay = 0.5f; // spawn the helmets after a short delay to give the cast animation time to play
			SOUND_SET.playSound(SOUND_EFFECT_NAME_SPAWN);
		}
	}
	
	private void spawnAngelicHelmets() {
		int numberOfHelmets = 3;
		for (int i = 0; i < numberOfHelmets; i++) {
			// calculate the positions around the archangel (equally distributed in a circle)
			float angle = i * (360f / numberOfHelmets);
			float radius = 2f;
			float xOffset = radius * (float) Math.cos(Math.toRadians(angle));
			float yOffset = radius * (float) Math.sin(Math.toRadians(angle));
			Vector2 spawnPosition = getPosition().add(xOffset, yOffset);
			
			createAndAddEnemyAfterWorldStep(ENEMY_TYPE_NAME_ANGELIC_HELMET, //
					spawnPosition.x * Constants.SCREEN_TO_WORLD, //
					spawnPosition.y * Constants.SCREEN_TO_WORLD, //
					new MapProperties());
		}
	}
	
	private void spawnAngelicHelmetInPlayerDirection() {
		Vector2 directionToPlayer = nearPlayer != null ? nearPlayer.getPosition().cpy().sub(getPosition()).nor() : new Vector2(-1, 0);
		float radius = 2f;
		Vector2 spawnPosition = getPosition().add(directionToPlayer.scl(radius));
		
		createAndAddEnemyAfterWorldStep(ENEMY_TYPE_NAME_ANGELIC_HELMET, //
				spawnPosition.x * Constants.SCREEN_TO_WORLD, //
				spawnPosition.y * Constants.SCREEN_TO_WORLD, //
				new MapProperties());
	}
	
	private void createAndAddEnemyAfterWorldStep(String type, float x, float y, MapProperties mapProperties) {
		PhysicsWorld.getInstance().runAfterWorldStep(() -> {
			EnemyFactory.asInstance().createAndAddEnemy(type, x, y, mapProperties);
		});
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType)) {
			if (CONFIG_EVENT_PARAMETER_START_DEFENSE_MODE.equals(event.stringValue)) {
				startDefenseMode();
			}
			else if (CONFIG_EVENT_PARAMETER_END_DEFENSE_MODE.equals(event.stringValue)) {
				stopDefenseMode();
			}
		}
	}
	
	private void startDefenseMode() {
		if (!defenseMode) {
			defenseMode = true;
			defenseModeCastTimer = 5f;
			stateMachine.forceStateChange(STATE_NAME_DEFENSE_MODE);
		}
	}
	
	private void stopDefenseMode() {
		if (defenseMode) {
			defenseMode = false;
			stateMachine.forceStateChange(STATE_NAME_IDLE);
			resetTimersForRangedAttacks();
			
			// spawn some angelic helmets when the defense mode ends
			spawnAngelicHelmetsDelay = 0.5f; // spawn the helmets after a short delay to give the cast animation time to play
			SOUND_SET.playSound(SOUND_EFFECT_NAME_SPAWN);
		}
	}
}
