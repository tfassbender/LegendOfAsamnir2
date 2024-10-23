package net.jfabricationgames.gdx.character.enemy.ai;

import java.util.function.Supplier;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.PreDefinedMovementAI;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;

/**
 * A movement AI for the giant golem. 
 * If the golem's health is high enough it will follow the player to attack him.
 * Otherwise the giant golem moves to its starting point, where it can heal itself.
 */
public class GiantGolemMovementAI extends PreDefinedMovementAI {
	
	private Supplier<Float> percentualHealthSupplier;
	
	public GiantGolemMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState, Supplier<Float> percentualHealthSupplier) {
		super(subAI, movingState, idleState, false, 0f, new Vector2(0, 0));
		this.percentualHealthSupplier = percentualHealthSupplier;
	}
	
	public void calculateStartingPosition() {
		absolutePositions = calculateAbsolutePositions(character.getPosition(), relativePositions);
	}
	
	@Override
	public void calculateMove(float delta) {
		if (percentualHealthSupplier.get() <= 0.39f) { // health is reduced in 20% steps - so 0.39f is everything below 40%
			// move to the starting point to heal
			super.calculateMove(delta);
			
			AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
			if (!isExecutedByMe(move)) {
				// delete the move of the sub AI to prevent a quivering of the movement and the animation
				subAI.clearMoves(); // NOTE: clearMoves clears all moves - this only works here because the underlying AI is the lowest one (except for the BaseAI)
			}
		}
		else {
			// follow the player (sub AI is a FollowAI)
			subAI.calculateMove(delta);
			
			// check the move that the follow AI created
			AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
			if (move != null) {
				Vector2 startingPosition = absolutePositions.get(0);
				Vector2 currentPosition = character.getPosition();
				Vector2 targetPosition = move.movementTarget;
				
				// don't move to far up (near the starting position) because there are dwarven guardian constructs - only move there when healing
				if (targetPosition != null && currentPosition.y > startingPosition.y - 2.5f && targetPosition.y > currentPosition.y) {
					// remove the calculated move
					subAI.clearMoves(); // NOTE: clearMoves clears all moves - this only works here because the underlying AI is the lowest one (except for the BaseAI)
				}
			}
		}
	}
}
