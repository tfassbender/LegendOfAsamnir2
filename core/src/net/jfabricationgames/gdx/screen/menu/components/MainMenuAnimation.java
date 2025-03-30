package net.jfabricationgames.gdx.screen.menu.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;

public class MainMenuAnimation {
	
	private AnimationDirector<TextureRegion> dwarfAnimation;
	private AnimationDirector<TextureRegion> dwarfIdleAnimation;
	private AnimationDirector<TextureRegion> dwarfRunningAnimation;
	private AnimationDirector<TextureRegion> dwarfAttackAnimation;
	private AnimationDirector<TextureRegion> chaosWizardAnimation;
	private AnimationDirector<TextureRegion> chaosWizardIdleAnimation;
	private AnimationDirector<TextureRegion> chaosWizardCastAnimation;
	private AnimationDirector<TextureRegion> chaosWizardCastLoopAnimation;
	private AnimationDirector<TextureRegion> chaosWizardReapearAnimation;
	
	private final float dwarfStartingPosition = 100f;
	private final float dwarfPositionChangeToAttackAnimation = 950f;
	private final float dwarfPositionMaxBeforeContinueRunning = 1050f;
	private final float dwarfMovement = 200f;
	private final float chaosWizardPosition = dwarfPositionMaxBeforeContinueRunning - 30f;
	
	private float dwarfPosition = dwarfStartingPosition;
	private float dwarfPositionMax = dwarfPositionMaxBeforeContinueRunning;
	
	private float summedDelta = 0f;
	private float flipDwarfFirstTime = 6.5f;
	private float flipDwarfSecondTime = 7.5f;
	private float flipDwarfThirdTime = 8f;
	private float continueRunning = 9.5f;
	private float chaosWizardReapear = 12f;
	private float restartTime = 15f;
	
	private boolean changedToAttackAnimation = false;
	private boolean changedToIdleAnimation = false;
	private boolean changedToCastAnimation = false;
	private boolean changedToCastLoopAnimation = false;
	private boolean chaosWizardDissapear = false;
	private boolean chaosWizardBackToIdleAnimation = false;
	private boolean dwarfFacingRight = true;
	
	public MainMenuAnimation() {
		AnimationManager.getInstance().loadAnimations("config/animation/main_menu.json");
		dwarfIdleAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_idle_right");
		dwarfRunningAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_run_right");
		dwarfAttackAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_attack_jump_right");
		chaosWizardIdleAnimation = AnimationManager.getInstance().getTextureAnimationDirector("elite_mage_idle");
		chaosWizardCastAnimation = AnimationManager.getInstance().getTextureAnimationDirector("elite_mage_cast_with_effect");
		chaosWizardCastLoopAnimation = AnimationManager.getInstance().getTextureAnimationDirector("elite_mage_cast_loop_with_effect");
		chaosWizardReapearAnimation = AnimationManager.getInstance().getTextureAnimationDirector("elite_mage_blast_with_effect");
		chaosWizardIdleAnimation.flip(true, false);
		chaosWizardCastAnimation.flip(true, false);
		chaosWizardCastLoopAnimation.flip(true, false);
		chaosWizardReapearAnimation.flip(true, false);
		
		dwarfAnimation = dwarfRunningAnimation;
		chaosWizardAnimation = chaosWizardIdleAnimation;
	}
	
	public void drawAnimation(SpriteBatch batch, float delta) {
		summedDelta += delta;
		
		changeAnimations(delta);
		
		dwarfAnimation.increaseStateTime(delta);
		TextureRegion dwarf = dwarfAnimation.getKeyFrame();
		chaosWizardAnimation.increaseStateTime(delta);
		TextureRegion chaosWizard = chaosWizardAnimation.getKeyFrame();
		
		batch.draw(dwarf, dwarfPosition - (dwarfFacingRight ? 0 : 20), 10, 100, 100);
		
		if (!chaosWizardDissapear) {
			batch.draw(chaosWizard, chaosWizardPosition, 17, 180, 100);
		}
	}
	
	private void changeAnimations(float delta) {
		if (dwarfPosition < dwarfPositionMax) {
			dwarfPosition += delta * dwarfMovement;
		}
		
		if (dwarfPosition > dwarfPositionChangeToAttackAnimation && !changedToAttackAnimation) {
			dwarfAnimation = dwarfAttackAnimation;
			changedToAttackAnimation = true;
			chaosWizardDissapear = true;
		}
		
		if (changedToAttackAnimation && !changedToIdleAnimation && dwarfAnimation.isAnimationFinished()) {
			dwarfAnimation = dwarfIdleAnimation;
			changedToIdleAnimation = true;
		}
		
		if (summedDelta > flipDwarfFirstTime && summedDelta < flipDwarfSecondTime && dwarfFacingRight) {
			dwarfAnimation.flip(true, false);
			dwarfFacingRight = false;
		}
		else if (summedDelta > flipDwarfSecondTime && summedDelta < flipDwarfThirdTime && !dwarfFacingRight) {
			dwarfAnimation.flip(true, false);
			dwarfFacingRight = true;
		}
		else if (summedDelta > flipDwarfThirdTime && summedDelta < continueRunning && dwarfFacingRight) {
			dwarfAnimation.flip(true, false);
			dwarfFacingRight = false;
		}
		else if (summedDelta > continueRunning && !dwarfFacingRight) {
			dwarfAnimation.flip(true, false);
			dwarfFacingRight = true;
			dwarfPositionMax = dwarfPositionMaxBeforeContinueRunning + 300f;
			dwarfAnimation = dwarfRunningAnimation;
			dwarfRunningAnimation.resetStateTime();
		}
		else if (summedDelta > chaosWizardReapear && chaosWizardDissapear) {
			chaosWizardAnimation = chaosWizardReapearAnimation;
			chaosWizardDissapear = false;
			chaosWizardReapearAnimation.setStateTime(0.2f); // start at the third frame of the animation
		}
		else if (summedDelta > chaosWizardReapear && chaosWizardAnimation.isAnimationFinished() && !chaosWizardBackToIdleAnimation) {
			chaosWizardAnimation = chaosWizardIdleAnimation;
			chaosWizardIdleAnimation.resetStateTime();
			chaosWizardBackToIdleAnimation = true;
		}
		else if (summedDelta > restartTime) {
			dwarfPosition = dwarfStartingPosition;
			dwarfPositionMax = dwarfPositionMaxBeforeContinueRunning;
			summedDelta = 0f;
			changedToAttackAnimation = false;
			changedToIdleAnimation = false;
			changedToCastAnimation = false;
			changedToCastLoopAnimation = false;
			chaosWizardDissapear = false;
			chaosWizardBackToIdleAnimation = false;
			chaosWizardAnimation = chaosWizardIdleAnimation;
			
			dwarfIdleAnimation.resetStateTime();
			dwarfRunningAnimation.resetStateTime();
			dwarfAttackAnimation.resetStateTime();
			chaosWizardIdleAnimation.resetStateTime();
			chaosWizardCastAnimation.resetStateTime();
			chaosWizardCastLoopAnimation.resetStateTime();
		}
		
		if (dwarfPosition > 750 && !changedToCastAnimation) {
			chaosWizardAnimation = chaosWizardCastLoopAnimation;
			changedToCastAnimation = true;
		}
		if (changedToCastAnimation && !changedToCastLoopAnimation && chaosWizardAnimation.isAnimationFinished()) {
			chaosWizardAnimation = chaosWizardCastLoopAnimation;
			changedToCastLoopAnimation = true;
		}
	}
}
