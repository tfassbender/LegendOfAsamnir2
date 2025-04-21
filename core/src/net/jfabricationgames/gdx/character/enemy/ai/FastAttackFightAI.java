package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class FastAttackFightAI extends FightAI {
	
	private float attackSpeedFactor;
	private float attackSpeedDelay;
	private float attackSpeedMaxTime = -1f; // -1 means no max time
	private float timeSinceAttackStateEntered;
	
	public FastAttackFightAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer, float attackDistance, float attackSpeedFactor) {
		super(subAI, attackState, attackTimer, attackDistance);
		this.attackSpeedFactor = attackSpeedFactor;
	}
	
	@Override
	public void calculateMove(float delta) {
		super.calculateMove(delta);
		if (inAttackState()) {
			timeSinceAttackStateEntered += delta;
		}
	}
	
	@Override
	protected boolean changeToAttackState() {
		boolean changePossible = super.changeToAttackState();
		if (changePossible) {
			timeSinceAttackStateEntered = 0;
		}
		return changePossible;
	}
	
	@Override
	protected void attackMoveTo(Vector2 targetPosition) {
		if (timeSinceAttackStateEntered >= attackSpeedDelay && (attackSpeedMaxTime < 0 || timeSinceAttackStateEntered <= attackSpeedMaxTime)) {
			character.moveTo(targetPosition, attackSpeedFactor);
		}
		else {
			super.attackMoveTo(targetPosition);
		}
	}
	
	public void setAttackSpeedDelay(float attackSpeedDelay) {
		this.attackSpeedDelay = attackSpeedDelay;
	}
	
	public void setAttackSpeedMaxTime(float attackSpeedMaxTime) {
		this.attackSpeedMaxTime = attackSpeedMaxTime;
	}
}
