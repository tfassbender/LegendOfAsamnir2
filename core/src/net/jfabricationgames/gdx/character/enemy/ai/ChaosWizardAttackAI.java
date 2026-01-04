package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class ChaosWizardAttackAI extends MultiAttackAI {
	
	private CharacterState attackMagicFireBall;
	private CharacterState attackMagicFireSoil;
	
	public ChaosWizardAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackMagicFireBall = attackStates.get("attackMagicFireBall");
		attackMagicFireSoil = attackStates.get("attackMagicFireSoil");
		
		setMoveToPlayerWhileAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackMagicFireSoil, distanceToTarget) && attackTimers.get(attackMagicFireSoil).timeToAttack()) {
			return attackMagicFireSoil;
		}
		if (isInRangeForAttack(attackMagicFireBall, distanceToTarget) && attackTimers.get(attackMagicFireBall).timeToAttack()) {
			return attackMagicFireBall;
		}
		
		return null;
	}
}
