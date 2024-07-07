package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * An "animation" that only shows the first frame of the given animation.
 */
public class SingleFrameTextureAnimationDirector<T extends TextureRegion> extends TextureAnimationDirector<T> {
	
	public SingleFrameTextureAnimationDirector(TextureAnimationDirector<T> animation) {
		super(animation.getAnimation(), animation.animationConfig);
	}
	
	/**
	 * Get the starting frame of the animation.
	 */
	@Override
	public T getKeyFrame() {
		return animation.getKeyFrame(0);
	}
}
