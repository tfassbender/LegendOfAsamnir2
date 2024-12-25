package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.function.Consumer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.AbstractMovementAI;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.map.PositionedObject;

/**
 * A movement AI that makes the enemy avoid a specific type of player attacks (usually bombs, because others are to fast). 
 */
public class AvoidPlayerAttackAI<T extends PositionedObject> extends AbstractMovementAI {
	
	private Class<T> avoidedAttackType;
	private Array<T> avoidedAttacks = new Array<>();
	private Consumer<Array<T>> attackConsumer; // a consumer to tell the character about an attack that is avoided (to react with a counter attack, a state or ...)
	
	private float distanceToKeepFromAvoidedAttack;
	
	public AvoidPlayerAttackAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, Class<T> avoidedAttackType, float distanceToKeepFromAvoidedAttack) {
		super(subAI, movingState, idleState);
		this.avoidedAttackType = avoidedAttackType;
		this.distanceToKeepFromAvoidedAttack = distanceToKeepFromAvoidedAttack;
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!avoidedAttacks.isEmpty()) {
			Array<Vector2> positionsToAvoid = new Array<>();
			for (T avoidedAttack : avoidedAttacks) {
				float distanceToAvoidedAttack = character.getPosition().sub(avoidedAttack.getPosition()).len();
				if (distanceToAvoidedAttack < distanceToKeepFromAvoidedAttack) {
					positionsToAvoid.add(avoidedAttack.getPosition());
				}
			}
			
			if (!positionsToAvoid.isEmpty()) {
				AIPositionChangingMove move = new AIPositionChangingMove(this);
				move.movementDirection = getAverageDirectionToAvoid(positionsToAvoid);
				setMove(MoveType.MOVE, move);
			}
		}
	}
	
	/**
	 * Calculate the vector from every position to avoid to the character and sum them up to get the average direction to avoid.
	 */
	private Vector2 getAverageDirectionToAvoid(Array<Vector2> positionsToAvoid) {
		Vector2 moveDirection = new Vector2();
		for (Vector2 positionToAvoid : positionsToAvoid) {
			moveDirection.add(character.getPosition().sub(positionToAvoid));
		}
		moveDirection.scl(1f / positionsToAvoid.size);
		return moveDirection;
	}
	
	@Override
	public void executeMove(float delta) {
		AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
		if (isExecutedByMe(move)) {
			if (inMovingState() || changeToMovingState()) {
				character.moveToDirection(move.movementDirection, movementSpeedFactor);
				move.executed();
			}
		}
		
		subAI.executeMove(delta);
	}
	
	@Override
	public void beginContact(Contact contact) {
		T collidingAttack = getObjectCollidingWithEnemySensor(contact, avoidedAttackType);
		if (collidingAttack != null) {
			avoidedAttacks.add(collidingAttack);
			if (attackConsumer != null) {
				attackConsumer.accept(avoidedAttacks);
			}
		}
		
		subAI.beginContact(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		T collidingAttack = getObjectCollidingWithEnemySensor(contact, avoidedAttackType);
		if (avoidedAttacks.contains(collidingAttack, false)) {
			avoidedAttacks.removeValue(collidingAttack, false);
			if (attackConsumer != null) {
				attackConsumer.accept(avoidedAttacks);
			}
		}
		
		subAI.endContact(contact);
	}
	
	public AvoidPlayerAttackAI<T> setAttackConsumer(Consumer<Array<T>> attackConsumer) {
		this.attackConsumer = attackConsumer;
		return this;
	}
}
