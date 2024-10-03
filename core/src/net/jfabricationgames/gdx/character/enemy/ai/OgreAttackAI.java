package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMultiAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class OgreAttackAI extends AbstractMultiAttackAI {
	
	private CharacterState charge;
	private CharacterState attack1;
	private CharacterState attack2;
	
	private boolean charged = false;
	private boolean attackAfterCharge = false;
	
	public OgreAttackAI(ArtificialIntelligence subAI, ArrayMap<String, CharacterState> attackStates, ArrayMap<CharacterState, Float> attackDistances, AttackTimer attackTimer) {
		super(subAI, attackStates, attackDistances, attackTimer);
		
		charge = attackStates.get("charge");
		attack1 = attackStates.get("attack_1");
		attack2 = attackStates.get("attack_2");
	}
	
	@Override
	public void calculateMove(float delta) {
		if (charged && character.getStateMachine().getCurrentState() != charge) {
			// the charge state is over - execute the attack next
			charged = false;
			attackAfterCharge = true;
			
			float angleToTarget = directionToTarget().angleDeg();
			if (angleToTarget >= 225 && angleToTarget <= 315) {
				setAttackMove(attack2);
			}
			else {
				setAttackMove(attack1);
			}
		}
		else {
			super.calculateMove(delta);
		}
	}
	
	@Override
	protected boolean timeToAttack() {
		if (attackAfterCharge) {
			// overwrite the timer because the charge state is handled as an attack state
			attackAfterCharge = false;
			return true;
		}
		
		return super.timeToAttack();
	}
	
	private void setAttackMove(CharacterState attack) {
		AIAttackingMove attackMove = new AIAttackingMove(this);
		attackMove.attack = attack;
		attackMove.targetPosition = targetingPlayer.getPosition();
		setMove(MoveType.ATTACK, attackMove);
	}
	
	@Override
	protected CharacterState chooseAttack() {
		float distanceToTarget = distanceToTarget();
		if (isInRangeForAttack(charge, distanceToTarget)) { // all attacks have the same range
			charged = true;
			return charge; // always charge first - the attack is chosen afterwards
		}
		
		return null;
	}
}
