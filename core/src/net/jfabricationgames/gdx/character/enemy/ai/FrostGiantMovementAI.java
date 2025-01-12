package net.jfabricationgames.gdx.character.enemy.ai;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.move.AIPositionChangingMove;
import net.jfabricationgames.gdx.character.ai.move.MoveType;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.projectile.Projectile;

/**
 * Makes the frost giant follow the player or move to his axe (if he is disarmed).
 * Also prevents moving out of the boss room.
 */
public class FrostGiantMovementAI extends FollowAI {
	
	private float minY; // the minimum y position where the golem is allowed to move to (to not move out of the boss room)
	private CharacterState movingDisarmedState;
	
	public FrostGiantMovementAI(ArtificialIntelligence subAI, CharacterState movingState, CharacterState movingDisarmedState, CharacterState idleState) {
		super(subAI, movingState, idleState);
		
		this.movingDisarmedState = movingDisarmedState;
	}
	
	@Override
	public void calculateMove(float delta) {
		if (isDisarmed()) {
			if (nearAxe()) {
				getAxeOnMap().removeFromMap(); // pick up the axe
				
				// the state change needs to be enforced, because the change from disarmed to armed is not possible in the interrupting states (intentionally)
				stateMachine.forceStateChange("idle");
			}
			else {
				moveToAxe(delta);
			}
		}
		else {
			followPlayer(delta);
		}
	}
	
	private boolean nearAxe() {
		Projectile axe = getAxeOnMap();
		if (axe != null && axe.isAttackOver()) { // only pick up the axe after it landed (otherwise it's picked up directly after adding it to the map)
			Vector2 axePosition = axe.getPosition();
			Vector2 characterPosition = character.getPosition();
			return characterPosition.dst(axePosition) < 1f;
		}
		
		return false;
	}
	
	private void moveToAxe(float delta) {
		if (getAxePosition() != null) {
			AIPositionChangingMove move = new AIPositionChangingMove(this);
			move.movementTarget = getAxePosition();
			setMove(MoveType.MOVE, move);
		}
	}
	
	private void followPlayer(float delta) {
		super.calculateMove(delta);
		
		if (character.getPosition().y <= minY) {
			AIPositionChangingMove move = getMove(MoveType.MOVE, AIPositionChangingMove.class);
			if (move != null && move.movementTarget != null && move.movementTarget.y < character.getPosition().y) {
				// don't move to far down to not leave the boss room
				setMove(MoveType.MOVE, null);
			}
		}
	}
	
	private boolean isDisarmed() {
		return getAxeOnMap() != null; // if the axe is placed on the map the frost giant doesn't hold it anymore, so he is disarmed
	}
	
	private Vector2 getAxePosition() {
		Projectile axe = getAxeOnMap();
		if (axe != null) {
			return axe.getPosition();
		}
		
		return null;
	}
	
	private Projectile getAxeOnMap() {
		return GameMapManager.getInstance().getMap().getProjectileByUnitId("frost_giant_axe");
	}
	
	@Override
	protected boolean inMovingState() {
		return stateMachine.getCurrentState() == movingState || stateMachine.getCurrentState() == movingDisarmedState;
	}
	
	@Override
	protected boolean changeToMovingState() {
		if (isDisarmed()) {
			return stateMachine.setState(movingDisarmedState);
		}
		else {
			return stateMachine.setState(movingState);
		}
	}
	
	public void setMinY(float minY) {
		this.minY = minY;
	}
}
