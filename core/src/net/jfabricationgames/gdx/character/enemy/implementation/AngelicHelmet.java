package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;

public class AngelicHelmet extends Enemy implements CharacterStateChangeListener {
	
	private static final float PROJECTILE_SPREAD_ANGLE = 15f; // degrees
	
	public AngelicHelmet(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		stateMachine.addChangeListener(this);
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		if ("attack".equals(newState.getStateName())) {
			int projectiles = timeAlive > 10f ? 5 : 3; // use more projectiles after 10 seconds
			for (int i = 0; i < projectiles; i++) {
				Vector2 targetDirection = getTargetDirection(i, projectiles);
				if (targetDirection != null) {
					attackHandler.startAttack("attack", targetDirection);
				}
			}
		}
	}
	
	private Vector2 getTargetDirection(int shotIndex, int totalShots) {
		if (nearPlayer == null) {
			return null;
		}
		
		// let the middle shot target the player directly and spread the other shots around that
		Vector2 directionToTarget = nearPlayer.getPosition().sub(getPosition());
		float angleOffset = PROJECTILE_SPREAD_ANGLE * ((float) shotIndex - ((float) (totalShots - 1) / 2f));
		
		return directionToTarget.rotateDeg(angleOffset);
	}
}
