package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.character.state.CharacterStateChangeListener;

public class Flameskull extends Enemy implements CharacterStateChangeListener {
	
	private static final Vector2 DIRECTION_UP = new Vector2(0, 1);
	private static final float PROJECTILE_SPREAD_ANGLE = 15f; // degrees
	
	public Flameskull(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		stateMachine.addChangeListener(this);
	}
	
	@Override
	public void onCharacterStateChange(CharacterState oldState, CharacterState newState) {
		if ("attack_fireball".equals(newState.getStateName())) {
			int projectiles = 3;
			for (int i = 0; i < projectiles; i++) {
				Vector2 targetDirection = getTargetDirection(i, projectiles);
				if (targetDirection != null) {
					attackHandler.startAttack("attack", targetDirection);
				}
			}
		}
		else if ("attack_burst".equals(newState.getStateName())) {
			int projectiles = 10;
			for (int i = 0; i < projectiles; i++) {
				Vector2 targetDirection = getRotationDirection(i, projectiles);
				attackHandler.startAttack("attack", targetDirection);
			}
		}
		else if ("attack_charge".equals(oldState.getStateName())) {
			// die after the charge attack
			takeDamage(typeConfig.health, AttackInfo.dummy());
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
	
	private Vector2 getRotationDirection(int shotIndex, int totalShots) {
		// spread the shots evenly in a circle
		float angle = 360f / (float) totalShots * (float) shotIndex;
		return DIRECTION_UP.cpy().rotateDeg(angle);
	}
}
