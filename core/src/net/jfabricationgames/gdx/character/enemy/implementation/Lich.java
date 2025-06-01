package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class Lich extends Enemy {
	
	private boolean firstForm = true; // form 1: cultist abomination - form 2: lich
	
	public Lich(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health = 10f; // TODO remove this after tests
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		// TODO attack AI
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(3f);
		
		return followAI;
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		super.takeDamage(damage, attackInfo);
		
		final float firstFormCriticalHealthPercentage = 0.05f; // change to second form if only 5% health left in first form
		if (firstForm && getPercentualHealth() <= firstFormCriticalHealthPercentage) {
			health = typeConfig.health * firstFormCriticalHealthPercentage; // prevent the victory sound that is played in the BossStatusBar when the boss health reaches 0
			
			// switch to the second form instead of removing the enemy
			stateMachine.setState("cultist_horror_death");
			firstForm = false;
		}
	}
	
	@Override
	protected void die() {
		if (!firstForm) {
			super.die();
		}
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (firstForm) {
			return "cultist_horror_damage";
		}
		else {
			return "damage";
		}
	}
}
