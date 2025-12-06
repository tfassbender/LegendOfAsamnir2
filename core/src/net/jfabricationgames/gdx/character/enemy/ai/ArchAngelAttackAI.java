package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class ArchAngelAttackAI extends MultiAttackAI {
	
	private CharacterState attackHit;
	private CharacterState attackCast;
	
	public ArchAngelAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackHit = attackStates.get("attack");
		attackCast = attackStates.get("cast");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackHit, distanceToTarget) && attackTimers.get(attackHit).timeToAttack()) {
			return attackHit;
		}
		if (isInRangeForAttack(attackCast, distanceToTarget) && attackTimers.get(attackCast).timeToAttack()) {
			return attackCast;
		}
		
		return null;
	}
}
