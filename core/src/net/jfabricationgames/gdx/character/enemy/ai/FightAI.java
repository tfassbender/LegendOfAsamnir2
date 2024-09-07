package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class FightAI extends AbstractAttackAI {
	
	private float attackDistance;
	
	public FightAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer, float attackDistance) {
		super(subAI, attackState, attackTimer);
		this.attackDistance = attackDistance;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		super.calculateMove(delta);
		if (targetInRange(attackDistance) && canSeeTarget()) {
			System.out.println("target in range 1: " + targetingPlayer.getPosition().cpy().sub(character.getPosition()).len());
			AIAttackingMove move = new AIAttackingMove(this);
			move.targetPosition = targetingPlayer.getPosition();
			setMove(MoveType.ATTACK, move);
		}
	}
	
	protected boolean canSeeTarget() {
		return true;
	}
	
	@Override
	public void executeMove(float delta) {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			System.out.println("target in range 2: " + targetingPlayer.getPosition().cpy().sub(character.getPosition()).len());
			if (changeToAttackState()) {
				move.executed();
			}
			if (inAttackState()) {
				attackState.flipAnimationToDirection(directionToTarget());
				if (distanceToTarget() > minDistanceToTargetPlayer) {
					attackMoveTo(move.targetPosition);
				}
			}
		}
		
		subAI.executeMove(delta);
	}
	
	protected void attackMoveTo(Vector2 targetPosition) {
		character.moveTo(targetPosition);
	}
}
