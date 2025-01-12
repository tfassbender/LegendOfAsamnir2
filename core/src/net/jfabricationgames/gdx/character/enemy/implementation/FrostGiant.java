package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.enemy.ai.FrostGiantMovementAI;
import net.jfabricationgames.gdx.character.enemy.ai.RayCastFightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.state.GameStateManager;

public class FrostGiant extends Enemy {
	
	private FrostGiantMovementAI frostGiantMovementAI;
	
	private boolean minYSet;
	
	public FrostGiant(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		ai = createFightAI(ai);
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		CharacterState moveDisarmedState = stateMachine.getState("move_disarmed");
		
		// follow the player if the health is high
		FollowAI followAI = new FollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		frostGiantMovementAI = new FrostGiantMovementAI(followAI, moveState, moveDisarmedState, idleState);
		
		return frostGiantMovementAI;
	}
	
	private boolean isDisarmed() {
		Projectile axe = GameMapManager.getInstance().getMap().getProjectileByUnitId("frost_giant_axe");
		return axe != null; // if the axe is placed on the map the frost giant doesn't hold it anymore, so he is disarmed
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackHit = stateMachine.getState("attack_hit");
		CharacterState attackState = stateMachine.getState("attack_throw");
		
		/* 
		 * The frost giant cannot attack (hit) while disarmed, because the interrupting states won't allow that.
		 * Therefore a simple FightAI (without any further conditions) is sufficient here.
		 * The stomp attack is used as counter attack after damage and is therefore not handled here.
		 */
		ai = new FightAI(ai, attackHit, new FixedAttackTimer(2f), 2f);
		return new RayCastFightAI(ai, attackState, new FixedAttackTimer(10f), 5f);
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!minYSet) {
			minYSet = true;
			// set the minimum y position here, because when creating the AI the map is not loaded yet
			CutsceneControlledUnit minY = GameMapManager.getInstance().getMap().getUnitById("loa2_l2_niflheim_ice_fields__boss_movement_position_min_y");
			frostGiantMovementAI.setMinY(minY.getPosition().y);
		}
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (isDisarmed()) {
			return "damage_disarmed";
		}
		else {
			return "damage";
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
}
