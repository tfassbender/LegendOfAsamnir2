package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.function.Supplier;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.implementation.Ifrit.DefenseModePosition;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class IfritAttackAI extends AbstractMultiAttackAI {
	
	private Supplier<DefenseModePosition> defenseModePositionSupplier;
	
	private CharacterState attackSword;
	private CharacterState attackFireBall;
	private CharacterState attackFireSoil;
	
	private AttackTimer attackTimerSword;
	private AttackTimer attackTimerFireBall;
	private AttackTimer attackTimerFireSoil;
	
	protected float timeSinceLastAttackSword = 0;
	protected float timeSinceLastAttackFireBall = 0;
	protected float timeSinceLastAttackFireSoil = 0;
	
	private Vector2 targetingPlayerLastKnownPosition;
	
	public IfritAttackAI(ArtificialIntelligence subAI, ArrayMap<String, CharacterState> attackStates, Supplier<DefenseModePosition> defenseModePositionSupplier) {
		super(subAI, attackStates, null, new FixedAttackTimer(0f));
		
		this.defenseModePositionSupplier = defenseModePositionSupplier;
		
		attackSword = attackStates.get("attack_sword");
		attackFireBall = attackStates.get("attack_throw_fire_ball");
		attackFireSoil = attackStates.get("attack_fire_soil");
		
		attackTimerSword = new FixedAttackTimer(3f);
		attackTimerFireBall = new FixedAttackTimer(1.5f);
		attackTimerFireSoil = new FixedAttackTimer(5f);
		
		setMoveToPlayerWhenAttacking(false);
	}
	
	@Override
	public void calculateMove(float delta) {
		if (!inAttackState()) {
			timeSinceLastAttackSword += delta;
			timeSinceLastAttackFireBall += delta;
			timeSinceLastAttackFireSoil += delta;
		}
		
		super.calculateMove(delta);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackSword, distanceToTarget) //
				&& timeSinceLastAttackSword >= attackTimerSword.getTimeTillNextAttack()) {
			setAttackMove(attackSword);
		}
		else if (isInRangeForAttack(attackFireSoil, distanceToTarget) //
				&& timeSinceLastAttackFireSoil >= attackTimerFireSoil.getTimeTillNextAttack()) {
			setAttackMove(attackFireSoil);
		}
		else if (isInRangeForAttack(attackFireBall, distanceToTarget) //
				&& timeSinceLastAttackFireBall >= attackTimerFireBall.getTimeTillNextAttack() //
				&& canSeeTarget()) {
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
	
	@Override
	protected boolean changeToAttackState(CharacterState state) {
		if (state == attackFireSoil) {
			// the fire soil attack starts at the player's position
			state.setAttackTargetPositionSupplier(() -> targetingPlayer != null ? targetingPlayer.getPosition().cpy() : targetingPlayerLastKnownPosition);
		}
		
		boolean changedState = super.changeToAttackState(state);
		if (changedState) {
			if (state == attackSword) {
				timeSinceLastAttackSword = 0;
			}
			else if (state == attackFireBall) {
				timeSinceLastAttackFireBall = 0;
			}
			else if (state == attackFireSoil) {
				timeSinceLastAttackFireSoil = 0;
			}
		}
		
		return changedState;
	}
	
	@Override
	protected void setTargetingPlayer(PlayableCharacter collidingPlayer) {
		super.setTargetingPlayer(collidingPlayer);
		targetingPlayerLastKnownPosition = null;
	}
	
	@Override
	protected void resetTargetingPlayer() {
		targetingPlayerLastKnownPosition = targetingPlayer.getPosition().cpy();
		super.resetTargetingPlayer();
	}
}
