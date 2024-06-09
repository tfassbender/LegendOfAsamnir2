package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractAttackAI;
import net.jfabricationgames.gdx.character.ai.move.AIAttackingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class AlarmClockAI extends AbstractAttackAI {
	
	public AlarmClockAI(ArtificialIntelligence subAI, CharacterState attackState, AttackTimer attackTimer) {
		super(subAI, attackState, attackTimer);
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		super.calculateMove(delta);
		AIAttackingMove move = new AIAttackingMove(this);
		setMove(MoveType.ATTACK, move);
	}
	
	@Override
	public void executeMove(float delta) {
		AIAttackingMove move = getMove(MoveType.ATTACK, AIAttackingMove.class);
		if (isExecutedByMe(move)) {
			if (changeToAttackState()) {
				move.executed();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	protected boolean changeToAttackState() {
		if (timeToAttack()) {
			attackState.setAttackDirection(new Vector2(1, 0)); // the direction doesn't matter because the attack only plays the sound and animation
			boolean changedState = stateMachine.setState(attackState);
			if (changedState) {
				timeSinceLastAttack = 0;
				updateTimeTillNextAttack();
			}
			
			return changedState;
		}
		return false;
	}
}
