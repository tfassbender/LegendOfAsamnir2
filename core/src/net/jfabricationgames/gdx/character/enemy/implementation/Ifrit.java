package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.IfritAttackAI;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;
import net.jfabricationgames.gdx.util.AngleUtil;
import net.jfabricationgames.gdx.util.AngleUtil.AngleDirection;

public class Ifrit extends Enemy {
	
	private boolean defenseMode;
	private boolean meleeDamageOnly = false;
	private DefenseModePosition defenseModePosition;
	
	/**
	 * Change to defense mode every time the health passes a multiple of this value.
	 */
	private float healthLossForDefenseModeInPercent = 20f;
	
	public Ifrit(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health -= 0.1f; // reduce the health by a small value to prevent the defense mode from being activated at the first damage
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
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		String attackNameSword = "attack_sword";
		String attackNameFireBall = "attack_throw_fire_ball";
		String attackNameFireSoil = "attack_fire_soil";
		
		CharacterState attackSword = stateMachine.getState(attackNameSword);
		CharacterState attackFireBall = stateMachine.getState(attackNameFireBall);
		CharacterState attackFireSoil = stateMachine.getState(attackNameFireSoil);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(attackNameSword, attackSword);
		attackStates.put(attackNameFireBall, attackFireBall);
		attackStates.put(attackNameFireSoil, attackFireSoil);
		
		return new IfritAttackAI(ai, attackStates, this::getDefenseModePosition);
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		AttackType attackType = attackInfo.getAttackType();
		if (defenseMode && !attackType.isSubTypeOf(AttackType.ARROW)) { // only take damage from arrows in defense mode
			return;
		}
		if (meleeDamageOnly && !attackType.isSubTypeOf(AttackType.MELEE)) {
			return;
		}
		
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (defenseMode) {
			endDefenseMode();
			
			if (healthSegmentAfterDamage == 1) {
				// only take damage from melee attacks after this stage
				meleeDamageOnly = true;
			}
		}
		else if (healthSegmentAfterDamage < healthSegmentBeforeDamage) {
			if (healthSegmentAfterDamage == 0) {
				startFinalPartOfBattle();
			}
			else {
				changeToDefenseMode();
			}
		}
	}
	
	private void changeToDefenseMode() {
		defenseMode = true;
		
		// abort attacks because the player can't block or dodge them when the cutscene starts
		stateMachine.forceStateChange(STATE_NAME_IDLE);
		
		startDirectionalCutscene("loa2_l3_muspelheim_lava_dungeon__surtur_fire_wall_%s_cutscene");
		
		// destroy fire totems on the outer ring
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__outer_ring"));
	}
	
	private void endDefenseMode() {
		defenseMode = false;
		
		// destroy the fire totems
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__inner_platform__horizontal"));
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__inner_platform__vertical"));
		
		// spawn fire totems on the outer ring
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CUSTCENE_SPAWN_UNIT) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__outer_ring"));
		
		putOutInnerFireWalls();
		
		// ignite a fire wall in the direction of the player (to block ranged attacks)
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SOLID_OBJECT) //
				.setIntValue(defenseModePosition.outerFireWallId));
	}
	
	private void startFinalPartOfBattle() {
		// abort attacks because the player can't block or dodge them when the cutscene starts
		stateMachine.forceStateChange(STATE_NAME_IDLE);
		
		startDirectionalCutscene("loa2_l3_muspelheim_lava_dungeon__surtur_final_arena_%s_cutscene");
		
		// destroy fire totems on the outer ring
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__outer_ring"));
	}
	
	private void startDirectionalCutscene(String cutsceneFormatString) {
		// start a cutscene to move surtur to the defense position and ignite the fire wall
		AngleDirection directionToPlayer = nearPlayer != null ? AngleUtil.getDirection(getPosition(), nearPlayer.getPosition()) : AngleDirection.DOWN;
		defenseModePosition = DefenseModePosition.byAngleDirection(directionToPlayer);
		String cutsceneName = String.format(cutsceneFormatString, defenseModePosition.cutsceneDirectionName);
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE) //
				.setStringValue(cutsceneName));
	}
	
	private DefenseModePosition getDefenseModePosition() {
		if (!defenseMode) {
			return null;
		}
		
		return defenseModePosition;
	}
	
	private void putOutInnerFireWalls() {
		for (int i = 1; i <= 4; i++) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(i));
		}
	}
	
	private void putOutOuterFireWalls() {
		for (int i = 5; i <= 8; i++) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR) //
					.setIntValue(i));
		}
	}
	
	private void destroyAllFireTotems() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__inner_platform__horizontal"));
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__inner_platform__vertical"));
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DIE) //
				.setStringValue("loa2_l3_muspelheim_lava_dungeon__boss__fire_totem__outer_ring"));
	}
	
	private void unlockBossGate() {
		// this will trigger a config object that unlocks the gate after a few seconds
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.CONFIG_GAME_OBJECT_ACTION) //
				.setStringValue("loa2_l3_muspelheim__config_object__open_key_wall_after_surtur_defeated"));
	}
	
	@Override
	protected void die() {
		super.die();
		
		putOutInnerFireWalls();
		putOutOuterFireWalls();
		destroyAllFireTotems();
		unlockBossGate();
		playMapBackgroundMusicAfterBossDefeated();
		
		GameStateManager.fireQuickSaveEvent();
	}
	
	public enum DefenseModePosition {
		
		UP(AngleDirection.DOWN, 7), //
		LEFT(AngleDirection.RIGHT, 5), //
		DOWN(AngleDirection.UP, 8), //
		RIGHT(AngleDirection.LEFT, 6); //
		
		public final String cutsceneDirectionName;
		public final int outerFireWallId;
		private final AngleDirection angleDirection;
		
		private DefenseModePosition(AngleDirection angleDirection, int outerFireWallId) {
			cutsceneDirectionName = name().toLowerCase();
			this.angleDirection = angleDirection;
			this.outerFireWallId = outerFireWallId;
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
