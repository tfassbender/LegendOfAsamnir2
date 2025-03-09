package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;
import net.jfabricationgames.gdx.util.AngleUtil;
import net.jfabricationgames.gdx.util.AngleUtil.AngleDirection;

public class Ifrit extends Enemy {
	
	private boolean defenseMode;
	private DefenseModePosition defenseModePosition;
	
	/**
	 * Change to defense mode every time the health passes a multiple of this value.
	 */
	private float healthLossForDefenseModeInPercent = 20f;
	
	public Ifrit(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health -= 0.1f; // reduce the health by a small value to make prevent the defense mode from being activated at the first damage
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
		
		// follow the player if the health is high
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		// TODO
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSword = stateMachine.getState("attack_sword");
		CharacterState attackFireBall = stateMachine.getState("attack_throw_fire_ball");
		CharacterState attackFireSoil = stateMachine.getState("attack_fire_soil");
		
		// TODO
		
		FightAI fightAi = new FightAI(ai, attackSword, new FixedAttackTimer(2f), 2f);
		fightAi.setMoveWhileAttacking(false);
		
		return fightAi;
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (defenseMode && attackType != AttackType.ARROW) { // only take damage from arrows in defense mode
			return;
		}
		
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackType);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (healthSegmentAfterDamage < healthSegmentBeforeDamage) {
			changeToDefenseMode();
		}
	}
	
	private void changeToDefenseMode() {
		defenseMode = true;
		
		// abort attacks because the player can't block or dodge them when the cutscene starts
		stateMachine.forceStateChange(STATE_NAME_IDLE);
		
		// start a cutscene to move surtur to the defense position and ignite the fire wall
		AngleDirection directionToPlayer = nearPlayer != null ? AngleUtil.getDirection(getPosition(), nearPlayer.getPosition()) : AngleDirection.DOWN;
		defenseModePosition = DefenseModePosition.byAngleDirection(directionToPlayer);
		String cutsceneName = String.format("loa2_l3_muspelheim_lava_dungeon__surtur_fire_wall_%s_cutscene", defenseModePosition.cutsceneDirectionName);
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE).setStringValue(cutsceneName));
	}
	
	@Override
	protected void die() {
		super.die();
		GameStateManager.fireQuickSaveEvent();
	}
	
	private enum DefenseModePosition {
		
		UP(AngleDirection.DOWN), //
		LEFT(AngleDirection.RIGHT), //
		DOWN(AngleDirection.UP), //
		RIGHT(AngleDirection.LEFT); //
		
		public final String cutsceneDirectionName;
		private final AngleDirection angleDirection;
		
		private DefenseModePosition(AngleDirection angleDirection) {
			cutsceneDirectionName = name().toLowerCase();
			this.angleDirection = angleDirection;
		}
		
		public static DefenseModePosition byAngleDirection(AngleDirection angleDirection) {
			for (DefenseModePosition position : values()) {
				if (position.angleDirection == angleDirection) {
					return position;
				}
			}
			
			return null;
		}
	}
}
