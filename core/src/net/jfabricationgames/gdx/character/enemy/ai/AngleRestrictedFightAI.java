package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

/**
 * A fight AI that only attacks of the target is in a specific angle to the enemy.
 */
public class AngleRestrictedFightAI extends FightAI {
	
	private Array<Float> angles; // a list of angles in which the enemy is allowed to attack
	private float maxAngleDelta; // the maximum angle difference between the searched angles and the angle to the attacked target
	
	public AngleRestrictedFightAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer, float attackDistance, Array<Float> angles, float maxAngleDelta) {
		super(subAI, attackState, attackTimer, attackDistance);
		
		this.angles = angles;
		this.maxAngleDelta = maxAngleDelta;
		
		verifyAngles();
	}
	
	private void verifyAngles() {
		for (float angle : angles) {
			if (angle < 0 || angle >= 360) {
				throw new IllegalArgumentException("The angle " + angle + " is not in the range of 0 to 360 degrees.");
			}
		}
		
		if (maxAngleDelta < 0) {
			throw new IllegalArgumentException("The maximum angle delta must be greater than or equal to 0.");
		}
	}
	
	@Override
	protected boolean canSeeTarget() {
		float angleToTarget = directionToTarget().angleDeg();
		
		for (float angle : angles) {
			if (Math.abs(angle - angleToTarget) < maxAngleDelta) {
				return true;
			}
			
			// check if the angle is near 0 or 360 degrees
			if ((angle + maxAngleDelta > 360 || angle - maxAngleDelta < 0) && Math.abs((angle + angleToTarget + 360) % 360 - 360) < maxAngleDelta) {
				return true;
			}
		}
		
		return false;
	}
}
