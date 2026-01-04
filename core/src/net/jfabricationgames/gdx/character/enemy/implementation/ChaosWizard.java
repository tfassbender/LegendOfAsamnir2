package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

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
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
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
	private int currentStage = 1;
	
	private int fireballsToShoot = 0;
	private float fireballShotTimer = 0f;
	private final float fireballShotInterval = 0.3f;
	
	private float pushNovaDelayTimer = 0f;
	private float startFirstCutsceneDelayTimer = 0f;
	
	private float immortalityTimer = 0f;
	
	private boolean restoreObelisksWhenEnteringCastStage = false;
	private float restoreObelisksDelayTimer = 0f;
	
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
		attackDistances.put(characterStateAttackMagicFireBall, 100f); // no range restriction
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
				
				switch (currentStage) {
					case 2:
						startFirstCutsceneDelayTimer = 0.5f;
						break;
					case 3:
						changeToCastStateToRestoreObelisks();
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
		
		// restore the obelisks after some time
		if (restoreObelisksDelayTimer > 0f) {
			restoreObelisksDelayTimer -= delta;
			if (restoreObelisksDelayTimer <= 0f) {
				restoreObelisks();
			}
		}
	}
	
	private void changeToCastStateToRestoreObelisks() {
		// change to the cast stage as visual indication that the obelisks get restored by the chaos wizard
		restoreObelisksWhenEnteringCastStage = true;
		CharacterState castState = stateMachine.getState(STATE_NAME_CAST);
		stateMachine.setOverridingFollowingState(castState, 3); // use the cast state three times after the current state finishes
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
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) && //
				"loa2_l5_castle_of_the_chaos_wizard__spire__change_chaos_wizard_name".equals(event.stringValue)) {
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
	}
}
