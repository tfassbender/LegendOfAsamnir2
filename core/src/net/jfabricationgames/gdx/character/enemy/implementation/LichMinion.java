package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

/**
 * A Lich that is used as a minion in the battle with the Chaos Wizard. Has no AI, but is controlled by the Chaos Wizard's AI.
 */
public class LichMinion extends Lich implements CharacterStateChangeListener {
	
	private static final String STATE_NAME_ATTACK_MAGIC_FIRE_BALL = "attack_magic_fire_ball";
	
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
		ai = createFightAI();
	}
	
	private FightAI createFightAI() {
		CharacterState attackState = stateMachine.getState(STATE_NAME_ATTACK_MAGIC_FIRE_BALL);
		float attackDistance = 10f;
		
		FightAI fightAI = new FightAI(ai, attackState, new RandomIntervalAttackTimer(4f, 6f), attackDistance);
		fightAI.setMoveToPlayerWhileAttacking(false);
		
		return fightAI;
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
}
