package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.constants.Constants;

/**
 * An animation consisting of a set of {@link TextureRegion}s that are displayed in a sequence.
 */
public class TextureAnimationDirector<T extends TextureRegion> extends AnimationDirector<T> {
	
	protected Animation<T> animation;
	
	public TextureAnimationDirector(Animation<T> animation, AnimationConfig animationConfig) {
		super(animationConfig);
		this.animation = animation;
		try {
			initializeSpriteConfigWithoutPosition();
		}
		catch (IndexOutOfBoundsException e) {
			throw new IllegalStateException("The animation '" + animationConfig.name + "' has no key frames. Please check the texture atlas and the 'texture_settings.json' file.");
		}
	}
	
	@Override
	protected void initializeSpriteConfigWithoutPosition() {
		T keyFrame = getKeyFrame();
		spriteConfig = new AnimationSpriteConfig().setWidth(keyFrame.getRegionWidth()).setHeight(keyFrame.getRegionHeight());
	}
	
	/**
	 * Get the frame at the current time.
	 */
	@Override
	public T getKeyFrame() {
		return animation.getKeyFrame(stateTime);
	}
	
	/**
	 * Set the animation state time to the end of the animation.
	 */
	@Override
	public void endAnimation() {
		stateTime = animation.getAnimationDuration();
	}
	
	@Override
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(stateTime);
	}
	
	@Override
	public boolean isAnimationLooped() {
		return animation.getPlayMode() == PlayMode.LOOP;
	}
	
	@Override
	public float getAnimationDuration() {
		return animation.getAnimationDuration();
	}
	
	/**
	 * Flip all key frames of the animation.
	 */
	@Override
	public void flip(boolean x, boolean y) {
		for (TextureRegion region : animation.getKeyFrames()) {
			region.flip(x, y);
		}
	}
	
	@Override
	public float getXOffset() {
		return Constants.WORLD_TO_SCREEN * (float) (Math.random() * animationConfig.shakingRandomMovementRange * 2 - animationConfig.shakingRandomMovementRange);
	}
	
	@Override
	public float getYOffset() {
		return Constants.WORLD_TO_SCREEN * (float) (Math.random() * animationConfig.shakingRandomMovementRange * 2 - animationConfig.shakingRandomMovementRange);
	}
	
	public void drawInMenu(SpriteBatch batch) {
		if (spriteConfig == null) {
			throw new IllegalStateException("No AnimationSpriteConfig. Please add an AnimationSpriteConfig in order to use the draw method");
		}
		
		T keyFrame = getKeyFrame();
		batch.draw(keyFrame, spriteConfig.x, spriteConfig.y, spriteConfig.width, spriteConfig.height);
	}
	
	public void setPlayMode(PlayMode playMode) {
		animation.setPlayMode(playMode);
	}
	
	/**
	 * Get the {@link Animation} that this object holds.
	 */
	public Animation<T> getAnimation() {
		return animation;
	}
}
