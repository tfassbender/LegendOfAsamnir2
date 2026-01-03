package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.ElderDragonAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class ElderDragon extends Enemy implements CharacterStateChangeListener {
	
	private static final Vector2 defenseModeAttackDirection = new Vector2(0, 1);
	
	/**
	 *  count the times the fireball attack was used in a row (to abort it manually)
	 */
	private int fireballStateRepetitions = 0;
	
	/**
	 * Change to defense mode every time the health passes a multiple of this value.
	 */
	private float healthLossForDefenseModeInPercent = 25f;
	private boolean defenseMode = false;
	private int lowestHealthSegmentReached = 3; // the defense mode only triggers once per health segment
	
	public ElderDragon(EnemyTypeConfig typeConfig, MapProperties properties) {
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
		followAI.setMinDistanceToTarget(3f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String stateNameAttackHit = "attack_hit";
		String stateNameAttackSpit = "attack_spit_start";
		
		CharacterState characterStateAttackHit = stateMachine.getState(stateNameAttackHit);
		CharacterState characterStateAttackSpit = stateMachine.getState(stateNameAttackSpit);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackHit, characterStateAttackHit);
		attackStates.put(stateNameAttackSpit, characterStateAttackSpit);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackHit, 3f);
		attackDistances.put(characterStateAttackSpit, 100f); // no range restriction
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackHit, new FixedAttackTimer(1.5f));
		attackTimers.put(characterStateAttackSpit, new FixedAttackTimer(20f));
		
		ElderDragonAttackAI elderDragonAttackAI = new ElderDragonAttackAI(ai, attackStates, attackDistances, attackTimers);
		
		return elderDragonAttackAI;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// normal: 5 fireballs - below 25% health: 10 fireballs
		int maxFireballRepetitions = (health < typeConfig.health * 0.25f) ? 9 : 4; // one additional fireball will be fired before the state is changed
		if (fireballStateRepetitions > maxFireballRepetitions) {
			stateMachine.setState("attack_spit_end");
		}
		
		if (defenseMode) {
			// slowly regain health in defense mode (5% of max health per second)
			health = Math.min(health + typeConfig.health * 0.05f * delta, typeConfig.health);
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (defenseMode) {
			if (attackInfo.getAttackType().isSubTypeOf(AttackType.MAGIC)) {
				stopDefenseMode();
			}
		}
		else if (healthSegmentAfterDamage < healthSegmentBeforeDamage && healthSegmentAfterDamage < lowestHealthSegmentReached) {
			lowestHealthSegmentReached = healthSegmentAfterDamage;
			startDefenseMode();
		}
	}
	
	private void startDefenseMode() {
		defenseMode = true;
		CharacterState attackFireWallStart = stateMachine.getState("attack_fire_wall_start");
		attackFireWallStart.setAttackDirection(defenseModeAttackDirection);
		stateMachine.forceStateChange(attackFireWallStart);
	}
	
	private void stopDefenseMode() {
		defenseMode = false;
		stateMachine.forceStateChange(STATE_NAME_IDLE);
		attackHandler.removeAllProjectiles(); // remove the fire wall
	}
	
	@Override
	protected void die() {
		super.die();
		
		// no quicksave here, because the next fight will be started right away
		
		playMapBackgroundMusicAfterBossDefeated();
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__elder_dragon_defeated"));
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		if ("attack_spit_loop".equals(newState.getStateName())) {
			fireballStateRepetitions++;
		}
		else {
			fireballStateRepetitions = 0;
		}
	}
}
