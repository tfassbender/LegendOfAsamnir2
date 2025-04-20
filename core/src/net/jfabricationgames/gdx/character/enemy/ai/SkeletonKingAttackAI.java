package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class SkeletonKingAttackAI extends AbstractMultiAttackAI {
	
	private CharacterState attackSwing;
	private CharacterState attackLeap;
	private CharacterState stateCommand;
	
	public SkeletonKingAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackSwing = attackStates.get("attack_swing");
		attackLeap = attackStates.get("attack_leap");
		stateCommand = attackStates.get("command");
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackSwing, distanceToTarget) && attackTimers.get(attackSwing).timeToAttack()) {
			setAttackMove(attackSwing);
		}
		else if (isInRangeForAttack(attackLeap, distanceToTarget) && attackTimers.get(attackLeap).timeToAttack()) {
			setAttackMove(attackLeap);
		}
		else if (isInRangeForAttack(stateCommand, distanceToTarget) && attackTimers.get(stateCommand).timeToAttack()) {
			setAttackMove(stateCommand);
		}
		
		return null;
	}
	
	@Override
	protected boolean timeToAttack() {
		return true; // every attack uses it's own attack timer, so we choose the timeToAttack in the chooseAttack method
	}
	
	private void setAttackMove(CharacterState attack) {
		AIAttackingMove attackMove = new AIAttackingMove(this);
		attackMove.attack = attack;
		attackMove.targetPosition = targetingPlayer.getPosition();
		setMove(MoveType.ATTACK, attackMove);
	}
}
