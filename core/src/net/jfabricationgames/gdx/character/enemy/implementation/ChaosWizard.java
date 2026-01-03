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
	
	private static final float lastStageHealthFactor = 0.05f; // 5% health in the last stage (the rest is distributed evenly)
	private static final int stages = 7; // the number of stages - in each stage the boss has to be attacked once
	private int currentStage = 1;
	
	private int fireballsToShoot = 0;
	private float fireballShotTimer = 0f;
	private final float fireballShotInterval = 0.5f;
	
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
		attackDistances.put(characterStateAttackMagicFireSoil, 4f); // range is restricted in the first stages
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackMagicFireBall, new RandomIntervalAttackTimer(4f, 7f));
		attackTimers.put(characterStateAttackMagicFireSoil, new FixedAttackTimer(5f));
		
		chaosWizardAttackAI = new ChaosWizardAttackAI(ai, attackStates, attackDistances, attackTimers);
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
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (fireballsToShoot > 0) {
			fireballShotTimer += delta;
			if (fireballShotTimer >= fireballShotInterval) {
				fireballShotTimer = 0f;
				fireballsToShoot--;
				
				// shoot a fireball
				if (nearPlayer != null) {
					Vector2 targetDirection = nearPlayer.getPosition().sub(getPosition());
					attackHandler.startAttack("attack_magic_fire_ball", targetDirection);
				}
				
				if (fireballsToShoot == 0) {
					// change the state back to idle after the last fireball was shot
					stateMachine.setState("idle");
				}
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (attackInfo.getAttackType() != AttackType.HIT || damage <= 0f) {
			// only the legendary axe "Asamnir" is able to damage the Chaos Wizard
			return;
		}
		
		// distribute the damage equally over the stages (except for the last stage, in which the battle ends if the chaos wizard takes damage)
		damage = typeConfig.health * ((1f - lastStageHealthFactor) / (stages - 1));
		currentStage++;
		
		if (currentStage == 7) {
			// final stage reached -> make the fire soil attack usable from any distance
			chaosWizardAttackAI.removeRangeRestrictionFromFireSoilAttack();
		}
		
		super.takeDamage(damage, attackInfo);
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
