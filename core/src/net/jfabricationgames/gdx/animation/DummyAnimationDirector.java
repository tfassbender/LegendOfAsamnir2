package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DummyAnimationDirector<T extends TextureRegion> extends TextureAnimationDirector<T> {
	
	public DummyAnimationDirector() {
		super(null, new AnimationConfig());
	}
	
	@Override
	protected void initializeSpriteConfigWithoutPosition() {}
	
	@Override
	public T getKeyFrame() {
		return null;
	}
	
	@Override
	public boolean isAnimationFinished() {
		return true;
	}
}
