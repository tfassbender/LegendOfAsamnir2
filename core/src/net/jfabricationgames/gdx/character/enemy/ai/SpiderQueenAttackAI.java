package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class SpiderQueenAttackAI extends AbstractMultiAttackAI {
	
	private CharacterState attackMelee;
	private CharacterState attackWeb;
	private CharacterState attackCocoon;
	private CharacterState attackCocoonProjectile;
	
	public SpiderQueenAttackAI(ArtificialIntelligence subAI, //
			ArrayMap<String, CharacterState> attackStates, //
			ArrayMap<CharacterState, Float> attackDistances, //
			ArrayMap<CharacterState, AttackTimer> attackTimers) {
		
		super(subAI, attackStates, attackDistances, attackTimers);
		
		attackMelee = attackStates.get("attack_melee");
		attackWeb = attackStates.get("attack_web");
		attackCocoon = attackStates.get("attack_cocoon");
		attackCocoonProjectile = attackStates.get("attack_cocoon_projectile");
		
		setMoveToPlayerWhenAttacking(false);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		
		if (isInRangeForAttack(attackMelee, distanceToTarget) && attackTimers.get(attackMelee).timeToAttack()) {
			return attackMelee;
		}
		if (isInRangeForAttack(attackWeb, distanceToTarget) && attackTimers.get(attackWeb).timeToAttack()) {
			return attackWeb;
		}
		if (isInRangeForAttack(attackCocoonProjectile, distanceToTarget) && attackTimers.get(attackCocoonProjectile).timeToAttack()) {
			return attackCocoonProjectile;
		}
		if (isInRangeForAttack(attackCocoon, distanceToTarget) && attackTimers.get(attackCocoon).timeToAttack()) {
			return attackCocoon;
		}
		
		return null;
	}
	
	@Override
	protected void createAttackingMove(CharacterState attack) {
		if (attackCocoon.equals(attack)) {
			// TODO target the opposite direction of the player
		}
		
		super.createAttackingMove(attack);
	}
}
