package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class ElderDragonAttackAI extends MultiAttackAI {
	
	private CharacterState attackHit;
	private CharacterState attackSpit;
	
	public ElderDragonAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackHit = attackStates.get("attack_hit");
		attackSpit = attackStates.get("attack_spit_start");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackHit, distanceToTarget) && attackTimers.get(attackHit).timeToAttack()) {
			return attackHit;
		}
		if (isInRangeForAttack(attackSpit, distanceToTarget) && attackTimers.get(attackSpit).timeToAttack()) {
			return attackSpit;
		}
		
		return null;
	}
}
