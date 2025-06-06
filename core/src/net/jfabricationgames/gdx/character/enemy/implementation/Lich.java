package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.character.state.CharacterState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
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
		ai = createCultistHorrorMovementAI(ai);
		ai = createCultistAbominationAttackAI(ai);
	}
	
	private ArtificialIntelligence createCultistHorrorMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState("cultist_horror_idle");
		CharacterState moveState = stateMachine.getState("cultist_horror_move");
		
		FollowAI followAI = new FollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(1.8f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createCultistAbominationAttackAI(ArtificialIntelligence ai) {
		CharacterState attackState = stateMachine.getState("cultist_horror_attack_knifes");
		
		FightAI fightAI = new FightAI(ai, attackState, new RandomIntervalAttackTimer(3f, 4f), 2f);
		
		return fightAI;
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
		if (attackInfo.getAttackType().isSubTypeOf(AttackType.MAGIC) && !firstForm) {
			// take a small amount of damage from magic attacks (the wand has 0 damage by default)
			damage = Math.max(1f, damage);
		}
		
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
			changeToSecondForm();
		}
		else {
			super.handleEvent(event);
		}
	}
	
	private void changeToSecondForm() {
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
		
		// remove the solid fixture from the body (by changing it to a sensor) and add a new fixture with updated size
		body.getFixtureList().get(0).setSensor(true);
		physicsBodyProperties.setWidth(2f);
		physicsBodyProperties.setHeight(2.2f);
		PhysicsBodyCreator.addFixture(physicsBodyProperties);
		
		// create a new AI for the second form
		ai = new BaseAI();
		ai = createLichMovementAI(ai);
		ai = createLichAttackAI(ai);
		ai.setCharacter(this);
	}
	
	private ArtificialIntelligence createLichMovementAI(ArtificialIntelligence ai) {
		CharacterState idleState = stateMachine.getState("idle");
		CharacterState moveState = stateMachine.getState("move");
		
		FollowAI followAI = new FollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(1.8f);
		followAI.followTarget(Player.getInstance()); // the player is already inside the sensor range, so set the target to follow immediately
		
		return followAI;
	}
	
	private ArtificialIntelligence createLichAttackAI(ArtificialIntelligence ai) {
		CharacterState attackState = stateMachine.getState("attack_charge");
		// TODO rage attack state
		
		FightAI fightAI = new FightAI(ai, attackState, new RandomIntervalAttackTimer(3f, 4f), 2f);
		fightAI.setMoveWhileAttacking(false);
		fightAI.setTargetingPlayer(Player.getInstance()); // the player is already inside the sensor range, so set the target immediately
		
		return fightAI;
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
