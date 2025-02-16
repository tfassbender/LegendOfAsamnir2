package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A director for an animation that consists of a single texture that grows over time.
 */
public class GrowingAnimationDirector<T extends TextureRegion> extends AnimationDirector<T> {
	
	private T texture;
	
	private AnimationConfig animationConfig;
	private AnimationSpriteConfig initialSpriteSize;
	
	public GrowingAnimationDirector(T texture, AnimationConfig animationConfig) {
		super(animationConfig);
		this.texture = texture;
		this.animationConfig = animationConfig;
		
		initializeSpriteConfigWithoutPosition();
		initialSpriteSize = getSpriteConfigCopy();
		updateTextureScale();
	}
	
	@Override
	protected void initializeSpriteConfigWithoutPosition() {
		T keyFrame = getKeyFrame();
		spriteConfig = new AnimationSpriteConfig().setWidth(keyFrame.getRegionWidth()).setHeight(keyFrame.getRegionHeight());
	}
	
	@Override
	public T getKeyFrame() {
		return texture;
	}
	
	@Override
	public void endAnimation() {
		stateTime = animationConfig.duration;
	}
	
	@Override
	public boolean isAnimationFinished() {
		return stateTime >= animationConfig.duration + animationConfig.stayAfterMaxScaleDuration;
	}
	
	@Override
	public boolean isAnimationLooped() {
		return false;
	}
	
	@Override
	public float getAnimationDuration() {
		return animationConfig.duration;
	}
	
	@Override
	public void flip(boolean x, boolean y) {
		texture.flip(x, y);
	}
	
	@Override
	public void increaseStateTime(float delta) {
		super.increaseStateTime(delta);
		updateTextureScale();
	}
	
	@Override
	public void setStateTime(float stateTime) {
		super.setStateTime(stateTime);
		updateTextureScale();
	}
	
	@Override
	public void resetStateTime() {
		super.resetStateTime();
		updateTextureScale();
	}
	
	@Override
	public void scaleSprite(Sprite sprite) {
		sprite.setScale(getScale());
	}
	
	public float getWidth() {
		return spriteConfig.width;
	}
	
	public float getHeight() {
		return spriteConfig.height;
	}
	
	public float getScale() {
		if (stateTime < animationConfig.duration) {
			return animationConfig.startScale + (animationConfig.maxScale - animationConfig.startScale) * (stateTime / animationConfig.duration);
		}
		else {
			return animationConfig.maxScale;
		}
	}
	
	private void updateTextureScale() {
		float scale = getScale();
		spriteConfig.setWidth(initialSpriteSize.width * scale);
		spriteConfig.setHeight(initialSpriteSize.height * scale);
	}
}
