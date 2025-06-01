package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.state.GameStateManager;

public class Lich extends Enemy {
	
	private static final float FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE = 0.05f; // change to second form if only 5% health left in first form
	
	private boolean firstForm = true; // form 1: cultist abomination - form 2: lich
	private boolean increaseHealthTillFull = false; // increase the health when the second form is summoned (so the health bar fills slowly)
	
	public Lich(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		health = 10f; // TODO remove this after tests
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = createMovementAI(ai);
		// TODO attack AI
	}
	
	private ArtificialIntelligence createMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState(STATE_NAME_IDLE);
		CharacterState moveState = stateMachine.getState(STATE_NAME_MOVE);
		
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(2f);
		
		return followAI;
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (increaseHealthTillFull) {
			if (health >= typeConfig.health) {
				increaseHealthTillFull = false;
				health = typeConfig.health;
			}
			else {
				// fill the health bar in 3 seconds (using delta time)
				health += typeConfig.health / (3f / delta);
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		super.takeDamage(damage, attackInfo);
		
		if (firstForm && getPercentualHealth() <= FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE) {
			health = typeConfig.health * FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE; // prevent the victory sound that is played in the BossStatusBar when the boss health reaches 0
			
			// start the cutscene to summon the second form (the lich) instead of removing the enemy
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE) //
					.setStringValue("loa2_l4_helheim_cultist_dungeon__lich_change_to_second_form_cutscene"));
			firstForm = false;
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.CONFIG_GENERATED_EVENT && "loa2_l4_helheim_cultist_dungeon__lich_form_2_summoned".equals(event.stringValue)) {
			// start the second phase of the boss fight
			typeConfig.health = 150f; // increase the maximum health
			increaseHealthTillFull = true; // increase the health to full in the next few seconds
			// set the health to the same percentage value as in the first form, to prevent a flickering effect in the health bar
			health = FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE * typeConfig.health;
			
			// update the boss name
			typeConfig.bossName = "Lich - Herald of the Void";
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.BOSS_ENEMY_APPEARED) //
					.setParameterObject(this) //
					.setStringValue(typeConfig.bossName));
		}
		else {
			super.handleEvent(event);
		}
	}
	
	@Override
	protected void die() {
		if (!firstForm) {
			super.die();
			
			// start the final cutscene after the boss is defeated
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE) //
					.setStringValue("loa2_l4_helheim_cultist_dungeon__lich_defeated_cutscene"));
			playMapBackgroundMusicAfterBossDefeated();
			
			GameStateManager.fireQuickSaveEvent();
		}
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		if (firstForm) {
			return "cultist_horror_damage";
		}
		else {
			return "damage";
		}
	}
}
