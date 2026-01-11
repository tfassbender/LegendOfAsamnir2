package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ChaosWizardAttackAI;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class ChaosWizard extends Enemy implements EventListener, CharacterStateChangeListener {
	
	private static final String CHAOS_WIZARD_NAME_1 = "The Allmighty Chaos Wizard - Mega\n Supreme Godlike Overlord of Existence";
	private static final String CHAOS_WIZARD_NAME_2 = "The Mighty Chaos Wizard - Supreme\n Author of the Nine Realms";
	private static final String CHAOS_WIZARD_NAME_FINAL = "The Chaos Wizard - Sovereign of Reality";
	
	private static final String STATE_NAME_ATTACK_MAGIC_FIRE_BALL = "attackMagicFireBall";
	private static final String STATE_NAME_ATTACK_MAGIC_FIRE_SOIL = "attackMagicFireSoil";
	private static final String STATE_NAME_BLAST_WITH_EFFECT = "blast_with_effect";
	private static final String STATE_NAME_CAST = "cast_loop_with_effect_single";
	
	private static final float lastStageHealthFactor = 0.05f; // 5% health in the last stage (the rest is distributed evenly)
	private static final int stages = 7; // the number of stages - in each stage the boss has to be attacked once
	private int currentStage = 4; // TODO set to 1 after tests
	
	private int fireballsToShoot = 0;
	private float fireballShotTimer = 0f;
	private final float fireballShotInterval = 0.3f;
	
	private float pushNovaDelayTimer = 0f;
	private float startFirstCutsceneDelayTimer = 0f;
	private float startSecondCutsceneDelayTimer = 0f;
	
	private float immortalityTimer = 0f;
	
	private boolean restoreObelisksWhenEnteringCastStage = false;
	private float restoreObelisksDelayTimer = 0f;
	private boolean spawnFlameskullsWhenEnteringCastStage = false;
	private float spawnFlameskullsDelayTimer = 0f;
	
	private float activateLaserBlasterDelayTimer = 0f;
	private float deactivateLaserBlasterDelayTimer = 0f;
	
	private ChaosWizardAttackAI chaosWizardAttackAI;
	
	public ChaosWizard(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		// needs to be reset if the game is restarted
		typeConfig.bossName = CHAOS_WIZARD_NAME_1;
		
		stateMachine.addChangeListener(this);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		PhysicsBodyProperties properties = super.definePhysicsBodyProperties();
		properties.setDensity(Constants.DENSITY_IMMOVABLE);
		return properties;
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createFightAI(ai);
		ai.setCharacter(this);
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		// the push nova is only used after the chaos wizard was hit, so it's not part of the attack AI
		String stateNameAttackMagicFireBall = STATE_NAME_ATTACK_MAGIC_FIRE_BALL;
		String stateNameAttackMagicFireSoil = STATE_NAME_ATTACK_MAGIC_FIRE_SOIL;
		// other attacks (like the lichs and the laser blaster) are not controlled by this AI but are triggered by events
		
		CharacterState characterStateAttackMagicFireBall = stateMachine.getState(stateNameAttackMagicFireBall);
		CharacterState characterStateAttackMagicFireSoil = stateMachine.getState(stateNameAttackMagicFireSoil);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackMagicFireBall, characterStateAttackMagicFireBall);
		attackStates.put(stateNameAttackMagicFireSoil, characterStateAttackMagicFireSoil);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		float fireBallAttackDistance;
		if (currentStage >= 5) {
			fireBallAttackDistance = 0f; // use the vorpal laser blaster of pittenweem instead
		}
		else {
			fireBallAttackDistance = 100f; // no range restriction
		}
		attackDistances.put(characterStateAttackMagicFireBall, fireBallAttackDistance);
		
		float fireSoilAttackDistance = 0f;
		if (currentStage >= 3 && currentStage < 7) {
			fireSoilAttackDistance = 4f;
		}
		else if (currentStage >= 7) {
			fireSoilAttackDistance = 100f; // no range restriction in the last stage
		}
		attackDistances.put(characterStateAttackMagicFireSoil, fireSoilAttackDistance); // range is restricted in the first stages
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackMagicFireBall, new RandomIntervalAttackTimer(4f, 7f));
		attackTimers.put(characterStateAttackMagicFireSoil, new FixedAttackTimer(5f));
		
		chaosWizardAttackAI = new ChaosWizardAttackAI(ai, attackStates, attackDistances, attackTimers);
		chaosWizardAttackAI.setTargetingPlayer(Player.getInstance()); // the player might already be in range, so set the target immediately
		resetTimersForRangedAttacks();
		
		return chaosWizardAttackAI;
	}
	
	private void resetTimersForRangedAttacks() {
		chaosWizardAttackAI.resetAttackTimer(STATE_NAME_ATTACK_MAGIC_FIRE_BALL);
		chaosWizardAttackAI.resetAttackTimer(STATE_NAME_ATTACK_MAGIC_FIRE_SOIL);
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		// handle the attacks, that are not automatically executed by the state machine
		if (STATE_NAME_ATTACK_MAGIC_FIRE_BALL.equals(newState.getStateName())) {
			if (currentStage < 4) {
				fireballsToShoot = 3;
			}
			else if (currentStage < 7) {
				fireballsToShoot = 5;
			}
			else {
				fireballsToShoot = 7;
			}
		}
		else if (STATE_NAME_BLAST_WITH_EFFECT.equals(newState.getStateName())) {
			pushNovaDelayTimer = 0.2f; // delay the push nova a bit to align with the animation
		}
		else if (STATE_NAME_CAST.equals(newState.getStateName())) {
			if (restoreObelisksWhenEnteringCastStage) {
				restoreObelisksWhenEnteringCastStage = false;
				restoreObelisksDelayTimer = 0.3f; // delay the restoration a bit to align with the casting animation
			}
			if (spawnFlameskullsWhenEnteringCastStage) {
				spawnFlameskullsWhenEnteringCastStage = false;
				spawnFlameskullsDelayTimer = 1.3f; // delay the spawning a bit to align with the casting animation
			}
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (immortalityTimer > 0f) {
			immortalityTimer -= delta;
		}
		
		// handle the fireball shooting (attacks are not executed by the state machine)
		if (fireballsToShoot > 0) {
			fireballShotTimer += delta;
			if (fireballShotTimer >= fireballShotInterval) {
				fireballShotTimer = 0f;
				fireballsToShoot--;
				
				// shoot a fireball
				if (nearPlayer != null) {
					Vector2 targetDirection = nearPlayer.getPosition().sub(getPosition());
					attackHandler.startAttack("attack_magic_fire_ball", targetDirection);
					SOUND_SET.playSound("magic_fire_ball");
				}
			}
		}
		
		// handle the push nova after being hit
		if (pushNovaDelayTimer > 0f) {
			pushNovaDelayTimer -= delta;
			if (pushNovaDelayTimer <= 0f) {
				// start the push nova attack (by sending an event that is handled by config objects on the map)
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUTSCENE_CREATE_ATTACK) //
						.setStringValue("config_object__castle_of_the_chaos_wizard__spire__chaos_wizard_push_nova"));
				
				// currentStage is the stage after the hit -> trigger events depending on the new stage
				switch (currentStage) {
					case 2:
						startFirstCutsceneDelayTimer = 0.5f;
						break;
					case 3:
						changeToCastStateToRestoreObelisks();
						spawnFlameskullsWhenEnteringCastStage = true;
						break;
					case 4:
						changeToCastStateToRestoreObelisks();
						setObelisksRepellBombs(true);
						break;
					case 5:
						// no obelisks in this stage - only the vorpal laser blaster of pittenweem
						startSecondCutsceneDelayTimer = 0.5f;
						break;
				}
			}
		}
		
		// start the first cutscene after the first hit
		if (startFirstCutsceneDelayTimer > 0f) {
			startFirstCutsceneDelayTimer -= delta;
			if (startFirstCutsceneDelayTimer <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.START_CUTSCENE) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__chaos_wizard_first_damage_cutscene"));
			}
		}
		
		// start the second cutscene before the fifth phase
		if (startSecondCutsceneDelayTimer > 0f) {
			startSecondCutsceneDelayTimer -= delta;
			if (startSecondCutsceneDelayTimer <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.START_CUTSCENE) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__chaos_wizard__vorpal_laser_blaster_of_pittenweem_cutscene"));
			}
		}
		
		// restore the obelisks after some time
		if (restoreObelisksDelayTimer > 0f) {
			restoreObelisksDelayTimer -= delta;
			if (restoreObelisksDelayTimer <= 0f) {
				restoreObelisks();
			}
		}
		
		// spawn flameskulls after some time
		if (spawnFlameskullsDelayTimer > 0f) {
			spawnFlameskullsDelayTimer -= delta;
			if (spawnFlameskullsDelayTimer <= 0f) {
				spawnFlameskullsOnPlayerSide(3);
			}
		}
		
		// handle the vorpal laser blaster of pittenweem activation/deactivation delays
		if (activateLaserBlasterDelayTimer > 0f) {
			activateLaserBlasterDelayTimer -= delta;
			if (activateLaserBlasterDelayTimer <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__fire_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject(this)); // use the chaos wizard as parameter object
			}
		}
		if (deactivateLaserBlasterDelayTimer > 0f) {
			deactivateLaserBlasterDelayTimer -= delta;
			if (deactivateLaserBlasterDelayTimer <= 0f) {
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CONFIG_GENERATED_EVENT) //
						.setStringValue("loa2_l5_castle_of_the_chaos_wizard_spire__pause_vorpal_laser_blaster_of_pittenweem") //
						.setParameterObject(this)); // use the chaos wizard as parameter object
				
				// reactivate the laser blaster after some time
				activateLaserBlasterDelayTimer = 3f;
				deactivateLaserBlasterDelayTimer = 8f;
			}
		}
	}
	
	private void changeToCastStateToRestoreObelisks() {
		// change to the cast stage as visual indication that the obelisks get restored by the chaos wizard
		restoreObelisksWhenEnteringCastStage = true;
		CharacterState castState = stateMachine.getState(STATE_NAME_CAST);
		stateMachine.setOverridingFollowingState(castState, 3); // use the cast state three times after the current state finishes
	}
	
	private void setObelisksRepellBombs(boolean active) {
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__bomb_repelling_dummy__active") //
				.setBooleanValue(active));
	}
	
	private void restoreObelisks() {
		// start the obelisk restoration
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("restore_magic_obelisks"));
		
		// restore the magic shield around the chaos wizard
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
				.setIntValue(3));
		
		// turn on the magic energy lines
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_left") //
				.setBooleanValue(true)); // visible
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_bottom_right") //
				.setBooleanValue(true)); // visible
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_top_left") //
				.setBooleanValue(true)); // visible
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.DISPLAY_ANIMATION_GAME_OBJECT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__magic_energy_line_top_right") //
				.setBooleanValue(true)); // visible
	}
	
	private void spawnFlameskullsOnPlayerSide(int maxFlameskullsPerSide) {
		if (isPlayerOnLeftSide()) {
			if (countFlameskullsOnLeftSide() < maxFlameskullsPerSide) {
				String spawnType = "flameskull__castle_of_the_chaos_wizard_spire__no_charge_attack__left";
				if (currentStage == 7) {
					// allow charge attacks in the final stage of the battle
					spawnType = "flameskull__castle_of_the_chaos_wizard_spire__left";
				}
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
						.setStringValue(spawnType));
			}
		}
		else {
			// the map is divided in left and right - the sides take up the whole reachable area -> the player has to be on the right side
			if (countFlameskullsOnRightSide() < maxFlameskullsPerSide) {
				String spawnType = "flameskull__castle_of_the_chaos_wizard_spire__no_charge_attack__right";
				if (currentStage == 7) {
					// allow charge attacks in the final stage of the battle
					spawnType = "flameskull__castle_of_the_chaos_wizard_spire__right";
				}
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
						.setStringValue(spawnType));
			}
		}
	}
	
	private boolean isPlayerOnLeftSide() {
		return isPlayerInArea("config_object__chaos_wizard_spire__area_left");
	}
	
	private boolean isPlayerInArea(String areaConfigObjectUnitId) {
		Condition condition = new Condition();
		condition.parameters = new ObjectMap<>();
		condition.parameters.put("objectId", "PLAYER");
		condition.parameters.put("targetAreaObjectId", areaConfigObjectUnitId);
		
		return ConditionType.OBJECT_IN_POSITION.check(condition);
	}
	
	private int countFlameskullsOnLeftSide() {
		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_left");
	}
	
	private int countFlameskullsOnRightSide() {
		return countUnitsOnMap("loa2_l5_castle_of_the_chaos_wizard__spire__flameskull_spawned_right");
	}
	
	private int countUnitsOnMap(String unitId) {
		Array<CutsceneControlledUnit> allUnitsWithId = GameMapManager.getInstance().getMap().getAllUnitsWithId(unitId);
		return allUnitsWithId.size;
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (attackInfo.getAttackType() != AttackType.HIT || damage <= 0f || immortalityTimer > 0f) {
			// only the legendary axe "Asamnir" is able to damage the Chaos Wizard
			return;
		}
		
		// distribute the damage equally over the stages (except for the last stage, in which the battle ends if the chaos wizard takes damage)
		damage = typeConfig.health * ((1f - lastStageHealthFactor) / (stages - 1));
		currentStage++;
		
		super.takeDamage(damage, attackInfo);
		
		immortalityTimer = 3f; // short immortality after being hit to prevent multiple stage changes because of multiple hits in a short time
		pushBackPlayer();
		sendStageChangeEvent();
		createAI(); // recreate the AI to adjust attack distances and timers
	}
	
	private void sendStageChangeEvent() {
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__change_battle_stage"). //
				setIntValue(currentStage));
	}
	
	private void pushBackPlayer() {
		CharacterState pushNova = stateMachine.getState(STATE_NAME_BLAST_WITH_EFFECT);
		stateMachine.setOverridingFollowingState(pushNova, 1); // use the push nova after the damage animation finishes
		// the events that spawn the push attacks are started when the push nova state is entered
		
		fireballsToShoot = 0; // stop any fireball shooting that is in progress (because the player has no chance of avoiding them)
		attackHandler.abortAllAttacks(); // will not abort the nova attack that was not started yet
		// let all minions abort their attacks as well
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__spire__abort_all_minion_attacks"));
	}
	
	@Override
	protected void die() {
		super.die();
		
		// kill all spawned flameskulls and the lich minions when the chaos wizard is defeated
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ENEMY_DIE));
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType)) {
			if ("loa2_l5_castle_of_the_chaos_wizard__spire__change_chaos_wizard_name".equals(event.stringValue)) {
				if (event.intValue == 2) {
					typeConfig.bossName = CHAOS_WIZARD_NAME_2;
				}
				else if (event.intValue == 3) {
					typeConfig.bossName = CHAOS_WIZARD_NAME_FINAL;
				}
				
				EventHandler.getInstance().fireEvent(new EventConfig() //
						.setEventType(EventType.BOSS_ENEMY_APPEARED) //
						.setParameterObject(this) //
						.setStringValue(typeConfig.bossName));
			}
			else if ("loa2_l5_castle_of_the_chaos_wizard__spire__chaos_wizard__vorpal_laser_blaster_of_pittenweem_cutscene_end".equals(event.stringValue)) {
				// wait for a short time before activating the vorpal laser blaster of pittenweem
				activateLaserBlasterDelayTimer = 1f;
				deactivateLaserBlasterDelayTimer = 6f;
			}
		}
	}
}
