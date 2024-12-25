package net.jfabricationgames.gdx.character.enemy.ai;

import net.jfabricationgames.gdx.character.ai.AbstractArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.move.AIActionMove;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.condition.ConditionHandler;

/**
 * An AI that makes the character stay inactive until a condition is reached.
 * It does so by blocking all other AIs that are configured below this one, so it will not block AIs that are higher in the execution chain. 
 */
public class InactiveTillConditionReachedAI extends AbstractArtificialIntelligence {
	
	private String condition;
	
	private ConditionHandler conditionHandler;
	
	public InactiveTillConditionReachedAI(ArtificialIntelligence subAI, String condition) {
		super(subAI);
		this.condition = condition;
		conditionHandler = ConditionHandler.getInstance();
	}
	
	@Override
	public void calculateMove(float delta) {
		subAI.calculateMove(delta);
		
		if (!conditionHandler.isConditionMet(condition)) {
			// block other AIs by adding moves of all types that are only executed by this AI
			
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			setMove(MoveType.MOVE, move);
			
			AIActionMove attack = new AIActionMove(this);
			setMove(MoveType.ATTACK, attack);
		}
	}
	
	@Override
	public void executeMove(float delta) {
		// no need to execute anything because the moves are only used to block other AIs
		
		subAI.executeMove(delta);
	}
}
