package net.jfabricationgames.gdx.character.enemy.ai;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.BackToStartingPointMovementAI;
import net.jfabricationgames.gdx.character.state.CharacterState;

/**
 * Moves the gargoyle back to the starting point if the player is out of sight.
 */
public class GargoyleMovementAI extends BackToStartingPointMovementAI {
	
	public GargoyleMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState idleState) {
		super(subAI, movingState, idleState, 1f);
	}
	
	@Override
	public void calculateMove(float delta) {
		if (playerCharacter == null) {
			// move back to the starting point if the player is not in sight
			super.calculateMove(delta);
		}
		else {
			// do nothing except for invoking the AI chain
			subAI.calculateMove(delta);
		}
	}
}
