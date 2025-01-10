package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.GargoyleAttackAI;
import net.jfabricationgames.gdx.character.enemy.ai.GargoyleMovementAI;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class Gargoyle extends Enemy {
	
	private static final String MAP_PROPERTIES_KEY_MOVE_BACK_TO_STARTING_POINT = "moveBackToStartingPoint";
	
	private boolean moveBackToStartingPoint;
	
	private GargoyleAttackAI attackAI;
	
	public Gargoyle(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		moveBackToStartingPoint = Boolean.parseBoolean(properties.get(MAP_PROPERTIES_KEY_MOVE_BACK_TO_STARTING_POINT, "false", String.class));
		
		// the createAI method is called in the super constructor - so the AI has to be (re-)created here
		createAI();
		ai.setCharacter(this);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		
		if (moveBackToStartingPoint) {
			ai = new GargoyleMovementAI(ai, movingState, idleState);
		}
		
		CharacterState attackState = stateMachine.getState("attack");
		attackAI = new GargoyleAttackAI(ai, attackState);
		ai = attackAI;
	}
	
	@Override
	protected String getDieStateName() {
		return "death";
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		// gargoyles cannot take damage - but they show the damage animation
		stateMachine.setState(getDamageStateName(damage));
		
		if (attackType == AttackType.MELEE) {
			attackAI.attack(); // attack back after taking damage
		}
	}
}
