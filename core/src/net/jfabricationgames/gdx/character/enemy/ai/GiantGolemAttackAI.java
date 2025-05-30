package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class GiantGolemAttackAI extends MultiAttackAI {
	
	private static final float STOMP_ANGLE_DELTA = 30f;
	
	private CharacterState attackFist;
	private CharacterState attackStomp;
	
	public GiantGolemAttackAI(ArtificialIntelligence subAI, ArrayMap<String, CharacterState> attackStates, ArrayMap<CharacterState, Float> attackDistances, AttackTimer attackTimer) {
		super(subAI, attackStates, attackDistances, attackTimer);
		
		attackFist = attackStates.get("attack_fist");
		attackStomp = attackStates.get("attack_stomp");
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		float angleToTarget = directionToTarget().angleDeg();
		
		if (isInRangeForAttack(attackStomp, distanceToTarget) && isInStompTargetAngle(angleToTarget)) {
			setAttackMove(attackStomp);
		}
		else if (isInRangeForAttack(attackFist, distanceToTarget)) {
			setAttackMove(attackFist);
		}
		
		return null;
	}
	
	private void setAttackMove(CharacterState attack) {
		AIAttackingMove attackMove = new AIAttackingMove(this);
		attackMove.attack = attack;
		attackMove.targetPosition = targetingPlayer.getPosition();
		setMove(MoveType.ATTACK, attackMove);
	}
	
	private boolean isInStompTargetAngle(float angle) {
		return (angle > 180 - STOMP_ANGLE_DELTA && angle < 180 + STOMP_ANGLE_DELTA) || (angle > 360 - STOMP_ANGLE_DELTA || angle < STOMP_ANGLE_DELTA);
	}
}
