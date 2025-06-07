package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.function.Supplier;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.enemy.implementation.Ifrit.DefenseModePosition;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class IfritAttackAI extends MultiAttackAI {
	
	private Supplier<DefenseModePosition> defenseModePositionSupplier;
	
	private CharacterState attackSword;
	private CharacterState attackFireBall;
	private CharacterState attackFireSoil;
	
	public IfritAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, AttackTimer> attackTimers, //
			Supplier<DefenseModePosition> defenseModePositionSupplier) {
		super(subAI, attackStates, null, attackTimers);
		
		this.defenseModePositionSupplier = defenseModePositionSupplier;
		
		attackSword = attackStates.get("attack_sword");
		attackFireBall = attackStates.get("attack_throw_fire_ball");
		attackFireSoil = attackStates.get("attack_fire_soil");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackSword, distanceToTarget) && attackTimers.get(attackSword).timeToAttack()) {
			setAttackMove(attackSword);
		}
		else if (isInRangeForAttack(attackFireSoil, distanceToTarget) && attackTimers.get(attackFireSoil).timeToAttack()) {
			setAttackMove(attackFireSoil);
		}
		else if (isInRangeForAttack(attackFireBall, distanceToTarget) && attackTimers.get(attackFireBall).timeToAttack() && canSeeTarget()) {
			setAttackMove(attackFireBall);
		}
		
		return null;
	}
	
	private boolean canSeeTarget() {
		// we can't use ray casting here because the traversable objects will block the ray, so only the direction to the target is checked
		DefenseModePosition defenseModePosition = defenseModePositionSupplier.get();
		
		if (defenseModePosition == null) {
			return true;
		}
		
		switch (defenseModePosition) {
			case LEFT:
				return targetingPlayer.getPosition().x < character.getPosition().x;
			case RIGHT:
				return targetingPlayer.getPosition().x > character.getPosition().x;
			case UP:
				return targetingPlayer.getPosition().y > character.getPosition().y;
			case DOWN:
				return targetingPlayer.getPosition().y < character.getPosition().y;
			default:
				return true;
		}
	}
	
	@Override
	protected boolean isInRangeForAttack(CharacterState attack, float distanceToTarget) {
		if (attack == attackSword) {
			return distanceToTarget <= 1.5f;
		}
		else if (attack == attackFireBall) {
			return distanceToTarget <= 8f;
		}
		else if (attack == attackFireSoil) {
			return distanceToTarget <= 8f && distanceToTarget >= 2f;
		}
		
		return false;
	}
	
	private void setAttackMove(CharacterState attack) {
		AIAttackingMove attackMove = new AIAttackingMove(this);
		attackMove.attack = attack;
		attackMove.targetPosition = targetingPlayer.getPosition();
		setMove(MoveType.ATTACK, attackMove);
	}
	
	@Override
	protected boolean changeToAttackState(CharacterState state) {
		if (state == attackFireSoil) {
			// the fire soil attack starts at the player's position
			state.setAttackTargetPositionSupplier(() -> targetingPlayer != null ? targetingPlayer.getPosition().cpy() : targetingPlayerLastKnownPosition);
		}
		
		return super.changeToAttackState(state);
	}
}
