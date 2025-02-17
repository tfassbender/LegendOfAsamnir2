package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.physics.box2d.Contact;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.map.PositionedObject;

/**
 * An AI that changes to a block state when a specific type of attack is detected.
 * The block state should be kept for a given time (to be configured in the block state).
 * All movements are blocked by this AI while blocking.
 */
public class BlockProjectilesAI<T extends PositionedObject> extends AbstractArtificialIntelligence {
	
	private CharacterState blockState;
	
	private Class<T> avoidedAttackType;
	private boolean projectileContactRegistered;
	
	public BlockProjectilesAI(ArtificialIntelligence subAI, CharacterState blockState, Class<T> avoidedAttackType) {
		super(subAI);
		this.blockState = blockState;
		this.avoidedAttackType = avoidedAttackType;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (projectileContactRegistered) {
			AIMove move = new AIActionMove(this);
			setMove(MoveType.MOVE, move);
			projectileContactRegistered = false;
		}
	}
	
	@Override
	public void executeMove(float delta) {
		AIActionMove move = getMove(MoveType.MOVE, AIActionMove.class);
		if (isExecutedByMe(move)) {
			if (inBlockingState() || changeToBlockingState()) {
				move.executed();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	private boolean inBlockingState() {
		return stateMachine.getCurrentState() == blockState;
	}
	private boolean changeToBlockingState() {
		return stateMachine.setState(blockState);
	}
	
	@Override
	public void beginContact(Contact contact) {
		T collidingAttack = getObjectCollidingWithEnemySensor(contact, avoidedAttackType);
		if (collidingAttack != null) {
			projectileContactRegistered = true;
		}
		
		subAI.beginContact(contact);
	}
}
