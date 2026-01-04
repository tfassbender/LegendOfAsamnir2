package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

/**
 * A Lich that is used as a minion in the battle with the Chaos Wizard. Has no AI, but is controlled by the Chaos Wizard's AI.
 */
public class LichMinion extends Lich implements CharacterStateChangeListener, EventListener {
	
	private static final String STATE_NAME_ATTACK_MAGIC_FIRE_BALL = "attack_magic_fire_ball";
	
	private int battleStage = 1;
	
	private int fireballsToShoot = 0;
	private float fireballShotTimer = 0f;
	private final float fireballShotInterval = 0.3f;
	
	public LichMinion(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
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
		ai = createLichAttackAI(ai);
		ai.setCharacter(this);
	}
	
	private ArtificialIntelligence createLichAttackAI(ArtificialIntelligence ai) {
		String stateNameAttackArcaneShower = "attack_arcane_shower";
		
		CharacterState characterStateAttackMagicFireBall = stateMachine.getState(STATE_NAME_ATTACK_MAGIC_FIRE_BALL);
		CharacterState characterStateAttackArcaneShower = stateMachine.getState(stateNameAttackArcaneShower);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(STATE_NAME_ATTACK_MAGIC_FIRE_BALL, characterStateAttackMagicFireBall);
		attackStates.put(stateNameAttackArcaneShower, characterStateAttackArcaneShower);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackMagicFireBall, 10f);
		float arcaneShowerDistance = battleStage < 2 ? 0f : 10f; // don't use the arcane shower in the first battle stage (range restricted to 0)
		attackDistances.put(characterStateAttackArcaneShower, arcaneShowerDistance);
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackMagicFireBall, new RandomIntervalAttackTimer(4f, 6f));
		attackTimers.put(characterStateAttackArcaneShower, new RandomIntervalAttackTimer(8f, 10f));
		
		MultiAttackAI attackAI = new MultiAttackAI(ai, attackStates, attackDistances, attackTimers);
		attackAI.setMoveToPlayerWhileAttacking(false);
		attackAI.setTargetingPlayer(Player.getInstance()); // the player might already be inside the sensor range, so set the target immediately
		attackAI.resetAttackTimer(stateNameAttackArcaneShower);
		attackAI.resetAttackTimer(STATE_NAME_ATTACK_MAGIC_FIRE_BALL);
		
		return attackAI;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
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
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		// the Lich Minion cannot take damage
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		// handle the attacks, that are not automatically executed by the state machine
		if (STATE_NAME_ATTACK_MAGIC_FIRE_BALL.equals(newState.getStateName())) {
			fireballsToShoot = 3;
			fireballShotTimer = 0.1f; // fire the first fire ball faster to align with the attack animation 
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		super.handleEvent(event);
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) //
				&& "loa2_l5_castle_of_the_chaos_wizard__spire__change_battle_stage".equals(event.stringValue)) {
			battleStage = event.intValue;
			createAI(); // recreate the AI to adjust the attacks that are used
		}
	}
}
