package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.attack.hit.AttackType;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.AvoidPlayerAttackAI;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.enemy.ai.InactiveTillConditionReachedAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.projectile.Bomb;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Bugbear extends Enemy {
	
	private static final String STATE_NAME_BLOCK = "block";
	private static final String STATE_NAME_BLOCK_2 = "block2"; // the same block animation as STATE_NAME_BLOCK but this one will change to STATE_NAME_BLOCK_HOLD afterwards
	private static final String STATE_NAME_BLOCK_HOLD = "block_hold"; // a state to hold the block for a few seconds so the player can attack the bugbear with a bomb
	
	private boolean finalCutsceneStarted;
	private boolean useForceField; // a force field to repel bombs that the player throws
	
	public Bugbear(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		CharacterState idleState = stateMachine.getState("idle");
		CharacterState movingState = stateMachine.getState("move");
		CharacterState attackState = stateMachine.getState("attack");
		
		float minTimeBetweenAttacks = 3.5f;
		float maxTimeBetweenAttacks = 4.5f;
		float minDistanceToPlayer = 1.5f;
		float attackDistance = 3f;
		float avoidingDistance = 12f; // avoiding distance should be higher or equal to the sensor radius to prevent flickering because of changes of direction
		
		ai = new BaseAI();
		ai = new FollowAI(ai, movingState, idleState).setMinDistanceToTarget(minDistanceToPlayer);
		ai = new FightAI(ai, attackState, new RandomIntervalAttackTimer(minTimeBetweenAttacks, maxTimeBetweenAttacks), attackDistance);
		ai = new AvoidPlayerAttackAI<Bomb>(ai, movingState, idleState, Bomb.class, avoidingDistance).setAttackConsumer(this::onAvoidAttack);
		ai = new InactiveTillConditionReachedAI(ai, "loa2_l2_niflheim__central_arena_entered");
	}
	
	private void onAvoidAttack(Array<Bomb> avoidedAttacks) {
		useForceField = !avoidedAttacks.isEmpty();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (useForceField && !isInBlockingState() // when blocking the force field should not be activated (it's the only way to attack the bugbear)
				&& attackHandler.allAttacksExecuted()) { // only start one force field at the time to improve the animation
			attackHandler.startAttack("attack_force_field", new Vector2(1, 0)); // direction does not matter
		}
		
		if (!finalCutsceneStarted && getPercentualHealth() > 0 && getPercentualHealth() < 0.15f) {
			finalCutsceneStarted = true;
			
			// TODO start the final cutscene
		}
	}
	
	private boolean isInBlockingState() {
		return stateMachine.isInState(STATE_NAME_BLOCK) || stateMachine.isInState(STATE_NAME_BLOCK_2) || stateMachine.isInState(STATE_NAME_BLOCK_HOLD);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.BOMB) && damage > 0) { // damage > 0 prevents being hit by the bomb and not the explosion
			if (!finalCutsceneStarted && getPercentualHealth() > 0.15f && getPercentualHealthAfterDamage(damage) <= 0.15f) {
				// take damage till the health is at 10% and then start the defeat cutscene
				float damageTillTenPercentHealth = health - typeConfig.health * 0.10f;
				super.takeDamage(damageTillTenPercentHealth, attackType);
				
				startDefeatCutscene(); // bugbear dies in the cutscene
			}
			else {
				super.takeDamage(damage, attackType);
			}
		}
		else if (attackType.isSubTypeOf(AttackType.ARROW)) {
			// change to a blocking state hat stays active for a few seconds, so the player can attack the bugbear with a bomb
			stateMachine.setState(STATE_NAME_BLOCK_2);
		}
		else {
			// all attacks but bombs can be blocked
			stateMachine.setState(STATE_NAME_BLOCK);
		}
	}
	
	private float getPercentualHealthAfterDamage(float damage) {
		return (health - damage) / typeConfig.health;
	}
	
	private void startDefeatCutscene() {
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.START_CUTSCENE) //
				.setStringValue("loa2_l2_niflheim_bandit_camp__bugbear_defeat_cutscene"));
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
