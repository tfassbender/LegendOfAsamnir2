package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.ArrayMap;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.implementation.MultiAttackAI;
import net.jfabricationgames.gdx.character.ai.implementation.RayCastFollowAI;
import net.jfabricationgames.gdx.character.ai.util.timer.AttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.ai.util.timer.RandomIntervalAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.state.CharacterState;

public class VengefulSpirit extends Enemy {
	
	private boolean defenseMode;
	
	/**
	 * Change to defense mode every time the health passes a multiple of this value.
	 */
	private float healthLossForDefenseModeInPercent = 34f;
	
	private AnimationDirector<TextureRegion> spectralShieldAnimation;
	private SpectralShieldState spectralShieldState = SpectralShieldState.DISABLED;
	
	public VengefulSpirit(EnemyTypeConfig typeConfig, MapProperties properties) {
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
		
		RayCastFollowAI followAI = new RayCastFollowAI(ai, moveState, idleState);
		followAI.setMinDistanceToTarget(3f);
		
		return followAI;
	}
	
	private ArtificialIntelligence createFightAI(ArtificialIntelligence ai) {
		CharacterState attackSpectralSword = stateMachine.getState("attack");
		CharacterState attackArcaneShower = stateMachine.getState("attack_arcane_shower");
		
		ArrayMap<String, CharacterState> attackStates = new ArrayMap<>();
		attackStates.put("attack", attackSpectralSword);
		attackStates.put("attack_arcane_shower", attackArcaneShower);
		
		ArrayMap<CharacterState, Float> attackDistances = new ArrayMap<>();
		attackDistances.put(attackSpectralSword, 5f);
		attackDistances.put(attackArcaneShower, 8f);
		
		ArrayMap<CharacterState, AttackTimer> attackTimers = new ArrayMap<>();
		attackTimers.put(attackSpectralSword, new RandomIntervalAttackTimer(2f, 3f));
		attackTimers.put(attackArcaneShower, new FixedAttackTimer(5f));
		
		MultiAttackAI attackAi = new MultiAttackAI(ai, attackStates, attackDistances, attackTimers);
		attackAi.setMoveToPlayerWhileAttacking(false);
		
		return attackAi;
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		if (spectralShieldAnimation != null) {
			spectralShieldAnimation.increaseStateTime(delta);
			TextureRegion region = spectralShieldAnimation.getKeyFrame();
			spectralShieldAnimation.getSpriteConfig() //
					.setX((body.getPosition().x - region.getRegionWidth() * 0.5f + imageOffsetX)) //
					.setY((body.getPosition().y - region.getRegionHeight() * 0.5f + imageOffsetY));
			spectralShieldAnimation.draw(batch);
			
			if (spectralShieldAnimation.isAnimationFinished()) {
				switch (spectralShieldState) {
					case APPEAR:
					case DAMAGE:
						spectralShieldState = SpectralShieldState.IDLE;
						spectralShieldAnimation = spectralShieldState.animation;
						spectralShieldAnimation.resetStateTime();
						break;
					case BREAK:
						spectralShieldState = SpectralShieldState.DISABLED;
						spectralShieldAnimation = null;
						break;
					default:
						// do nothing
						break;
				}
			}
		}
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		AttackType attackType = attackInfo.getAttackType();
		if (defenseMode && !attackType.isSubTypeOf(AttackType.MAGIC)) { // only take damage from magic in defense mode
			spectralShieldAnimation = SpectralShieldState.DAMAGE.animation;
			return;
		}
		
		boolean fullHealthBeforeDamage = health == typeConfig.health; // prevent changing to defense mode on first hit
		int healthSegmentBeforeDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		super.takeDamage(damage, attackInfo);
		int healthSegmentAfterDamage = (int) (health / typeConfig.health * (100f / healthLossForDefenseModeInPercent));
		
		if (defenseMode) {
			// end the defense mode when attacked by a magic attack
			spectralShieldState = SpectralShieldState.BREAK;
			spectralShieldAnimation = SpectralShieldState.BREAK.animation;
			defenseMode = false;
		}
		else if (healthSegmentAfterDamage < healthSegmentBeforeDamage && !fullHealthBeforeDamage) {
			defenseMode = true;
			spectralShieldState = SpectralShieldState.APPEAR;
			spectralShieldAnimation = SpectralShieldState.APPEAR.animation;
		}
	}
	
	private enum SpectralShieldState {
		
		DISABLED(null), // defense mode not active
		APPEAR("vengeful_spirit_spectral_shield_appear"), //
		IDLE("vengeful_spirit_spectral_shield_idle"), //
		DAMAGE("vengeful_spirit_spectral_shield_damage"), //
		BREAK("vengeful_spirit_spectral_shield_break");
		
		private final AnimationDirector<TextureRegion> animation;
		
		private SpectralShieldState(String animationName) {
			if (animationName != null) {
				animation = AnimationManager.getInstance().getTextureAnimationDirectorCopy(animationName);
			}
			else {
				animation = null;
			}
		}
	}
}
