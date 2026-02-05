package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.FollowAI;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.MultipleAttackTimer;
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
	
	protected boolean firstForm = true; // form 1: cultist abomination - form 2: lich
	private boolean increaseHealthTillFull = false; // increase the health when the second form is summoned (so the health bar fills slowly)
	private float healthLossForInverseControlSpellInPercent = 30f; // fire a spell that inverts the controls when the health is reduced by multiples this percentage 
	
	private AnimationDirector<TextureRegion> spellAnimation;
	
	public Lich(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		// reset the boss state (in case the player was killed by the lich)
		typeConfig.bossName = "Cultist Abomination - The devouring Husk";
		typeConfig.health = 75f;
		health = typeConfig.health;
		firstForm = true;
		
		spellAnimation = AnimationManager.getInstance().getTextureAnimationDirectorCopy("lich_soul_storm_effect");
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
		
		FightAI fightAI = new FightAI(ai, attackState, new RandomIntervalAttackTimer(2f, 3f), 2f);
		fightAI.setMoveToPlayerWhileAttacking(false);
		
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
		
		// draw the spell animation in the state "attack_rage_spell_invert_controls"
		if ("attack_rage_spell_invert_controls".equals(stateMachine.getCurrentState().getStateName())) {
			spellAnimation.increaseStateTime(delta);
			TextureRegion region = spellAnimation.getKeyFrame();
			spellAnimation.getSpriteConfig() //
					.setX((body.getPosition().x - region.getRegionWidth() * 0.5f + imageOffsetX)) //
					.setY((body.getPosition().y - region.getRegionHeight() * 0.5f + imageOffsetY));
			spellAnimation.draw(batch);
			// spell animation is a loop, so it doesn't need to be restarted
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (attackInfo.getAttackType().isSubTypeOf(AttackType.MAGIC) && !firstForm) {
			// take a small amount of damage from magic attacks (the wand has 0 damage by default)
			damage = Math.max(1f, damage);
		}
		
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForInverseControlSpellInPercent));
		float healthInPercentageBeforeDamage = (health / typeConfig.health) * 100f;
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForInverseControlSpellInPercent));
		float healthInPercentageAfterDamage = (health / typeConfig.health) * 100f;
		
		if (firstForm && getPercentualHealth() <= FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE) {
			health = typeConfig.health * FIRST_FORM_CRITICAL_HEALTH_PERCENTAGE; // prevent the victory sound that is played in the BossStatusBar when the boss health reaches 0
			
			// start the cutscene to summon the second form (the lich) instead of removing the enemy
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE) //
					.setStringValue("loa2_l4_helheim_cultist_dungeon__lich_change_to_second_form_cutscene"));
			
			// abort attacks because the player can't block or dodge them when the cutscene starts
			stateMachine.forceStateChange("cultist_horror_idle");
			
			firstForm = false;
		}
		
		if (!firstForm && healthSegmentAfterDamage < healthSegmentBeforeDamage //
				&& healthSegmentBeforeDamage < 3) { // don't fire the spell on the first health segment change
			// change the state to fire a spell that inverts the controls of the player
			CharacterState attackState = stateMachine.getState("attack_rage_nova");
			attackState.setAttackDirection(Vector2.Zero); // a direction must be set, but since it's a nova, the value doesn't matter
			stateMachine.forceStateChange(attackState); // the spell state follows automatically
		}
		
		if (!firstForm && healthInPercentageBeforeDamage > 20f && healthInPercentageAfterDamage <= 20f) {
			// change the AI to the final stage with two magic blast attacks at the same time
			ai = new BaseAI();
			ai = createLichMovementAI(ai);
			ai = createLichAttackAI(ai, true);
			ai.setCharacter(this);
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
		typeConfig.health = 200f; // increase the maximum health
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
		ai = createLichAttackAI(ai, false);
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
	
	private ArtificialIntelligence createLichAttackAI(ArtificialIntelligence ai, boolean finalStage) {
		String stateNameAttackMelee = "attack_charge";
		String stateNameAttackArcaneShower = "attack_arcane_shower";
		String stateNameAttackMagicBlast = "attack_charge_magic_blast";
		
		CharacterState characterStateAttackMelee = stateMachine.getState(stateNameAttackMelee);
		CharacterState characterStateAttackArcaneShower = stateMachine.getState(stateNameAttackArcaneShower);
		CharacterState characterStateAttackMagicBlast = stateMachine.getState(stateNameAttackMagicBlast);
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put(stateNameAttackMelee, characterStateAttackMelee);
		attackStates.put(stateNameAttackArcaneShower, characterStateAttackArcaneShower);
		attackStates.put(stateNameAttackMagicBlast, characterStateAttackMagicBlast);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(characterStateAttackMelee, 2.5f);
		attackDistances.put(characterStateAttackArcaneShower, 100f); // no distance - can strike from anywhere
		attackDistances.put(characterStateAttackMagicBlast, 15f);
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(characterStateAttackMelee, new FixedAttackTimer(2f)); // melee attack must be fast to not let the player stand down and attack
		attackTimers.put(characterStateAttackArcaneShower, new RandomIntervalAttackTimer(5f, 7f));
		if (finalStage) {
			// use 2 magic blasts in the final stage (health below 20%)
			AttackTimer multiAttackTimer = new MultipleAttackTimer(new FixedAttackTimer(8f), new RandomIntervalAttackTimer(6f, 8f));
			attackTimers.put(characterStateAttackMagicBlast, multiAttackTimer); // magic blast projectiles are active for 5 seconds
		}
		else {
			attackTimers.put(characterStateAttackMagicBlast, new FixedAttackTimer(10f)); // magic blast projectiles are active for 5 seconds
		}
		
		MultiAttackAI attackAI = new MultiAttackAI(ai, attackStates, attackDistances, attackTimers);
		attackAI.setMoveToPlayerWhileAttacking(false);
		attackAI.setTargetingPlayer(Player.getInstance()); // the player is already inside the sensor range, so set the target immediately
		
		return attackAI;
	}
	
	@Override
	protected void die() {
		if (!firstForm) {
			super.die();
			
			afterLichDefeated();
		}
	}
	
	protected void afterLichDefeated() {
		// start the final cutscene after the boss is defeated
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.START_CUTSCENE) //
				.setStringValue("loa2_l4_helheim_cultist_dungeon__lich_defeated_cutscene"));
		playMapBackgroundMusicAfterBossDefeated();
		
		GameStateManager.fireQuickSaveEvent();
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
