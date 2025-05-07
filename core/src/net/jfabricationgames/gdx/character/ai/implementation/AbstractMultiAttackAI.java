package net.jfabricationgames.gdx.character.ai.implementation;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.DummyAttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public abstract class AbstractMultiAttackAI extends AbstractAttackAI {
	
	protected ArrayMap<String, CharacterState> attackStates;
	protected ArrayMap<CharacterState, Float> attackDistances;
	protected ArrayMap<CharacterState, AttackTimer> attackTimers; // if used it overrules the inherited attackTimer for the specific attack
	
	private boolean moveToPlayerWhenAttacking = true;
	
	public AbstractMultiAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			AttackTimer attackTimer) {
		super(subAI, null, attackTimer);
		this.attackStates = attackStates;
		this.attackDistances = attackDistances;
	}
	
	public AbstractMultiAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		super(subAI, null, new DummyAttackTimer());
		this.attackStates = attackStates;
		this.attackDistances = attackDistances;
		this.attackTimers = attackTimers;
	}
	
	protected boolean setAndApplyAttackState(CharacterState state) {
		attackState = state;
		return super.changeToAttackState();
	}
	
	@Override
	protected boolean changeToAttackState() {
		throw new UnsupportedOperationException("changeToAttackState can't be called in this implementation. Use changeToAttackState(EnemyState) instead.");
	}
	
	@Override
	protected boolean inAttackState() {
		return attackStates.containsValue(stateMachine.getCurrentState(), false);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		updateAttackTimers(delta);
		
		if (timeToAttack()) {
			CharacterState attack = chooseAttack();
			if (attack != null) {
				if (!targetInRange(attackDistances.get(attack))) {
					Gdx.app.error(getClass().getSimpleName(), "calculateMove(): The chosen attack can't be executed, because the target is not in range.");
					return;
				}
				
				createAttackingMove(attack);
			}
		}
		else if (inAttackState()) {
			// control the movement in the attack state
			createAttackingMove(stateMachine.getCurrentState());
		}
	}
	
	protected void createAttackingMove(CharacterState attack) {
		AIAttackingMove attackMove = new AIAttackingMove(this);
		attackMove.attack = attack;
		attackMove.targetPosition = targetingPlayer.getPosition();
		setMove(MoveType.ATTACK, attackMove);
	}
	
	private void updateAttackTimers(float delta) {
		if (attackTimers != null) {
			for (CharacterState state : attackTimers.keys()) {
				// increase the timers for all attacks that are not active
				if (!Objects.equals(stateMachine.getCurrentState(), state)) {
					attackTimers.get(state).incrementTime(delta);
				}
			}
		}
		else {
			if (!inAttackState()) {
				attackTimer.incrementTime(delta);
			}
		}
	}
	
	protected boolean timeToAttack() {
		if (attackTimers != null) {
			for (AttackTimer timer : attackTimers.values()) {
				if (timer.timeToAttack()) {
					return true;
				}
			}
			
			return false;
		}
		
		return attackTimer.timeToAttack();
	}
	
	protected abstract CharacterState chooseAttack();
	
	@Override
	public void executeMove(float delta) {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			if (changeToAttackState(move.attack)) {
				move.executed();
			}
			if (inAttackState() && moveToPlayerWhenAttacking) {
				attackState.flipAnimationToDirection(directionToTarget());
				if (distanceToTarget() > minDistanceToTargetPlayer) {
					character.moveTo(move.targetPosition);
				}
			}
		}
		
		subAI.executeMove(delta);
	}
	
	protected boolean changeToAttackState(CharacterState state) {
		boolean stateApplied = setAndApplyAttackState(state);
		if (stateApplied) {
			if (attackTimers != null) {
				attackTimers.get(state).reset();
			}
			else {
				attackTimer.reset();
			}
		}
		
		return stateApplied;
	}
	
	protected boolean isInRangeForAttack(CharacterState attack, float distanceToTarget) {
		float attackDistance = attackDistances.get(attack, null);
		return distanceToTarget <= attackDistance;
	}
	
	public void setMoveToPlayerWhenAttacking(boolean moveToPlayerWhenAttacking) {
		this.moveToPlayerWhenAttacking = moveToPlayerWhenAttacking;
	}
}
