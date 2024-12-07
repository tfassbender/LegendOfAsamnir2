package net.jfabricationgames.gdx.character.enemy.ai;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.state.CharacterState;

/**
 * An AI for the gargoyle that attacks the player once after the player attacked the gargoyle.
 */
public class GargoyleAttackAI extends FightAI {
	
	private boolean attack;
	
	public GargoyleAttackAI(ArtificialIntelligence subAI, CharacterState attackState) {
		super(subAI, attackState, new FixedAttackTimer(0), 3f);
	}
	
	@Override
	protected boolean canSeeTarget() {
		return attack;
	}
	
	@Override
	protected boolean changeToAttackState() {
		boolean changed = super.changeToAttackState();
		if (changed) {
			attack = false;
		}
		return changed;
	}
	
	/**
	 * Attacks the player once (the next time it's possible for the state machine).
	 */
	public void attack() {
		attack = true;
	}
	
	public void abortAttack() {
		attack = false;
	}
}
