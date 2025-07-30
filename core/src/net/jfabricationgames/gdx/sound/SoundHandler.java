package net.jfabricationgames.gdx.sound;

import com.badlogic.gdx.audio.Sound;

public class SoundHandler {
	
	private static final SoundHandler NO_SOUND = new SoundHandler(null) {
		
		@Override
		public void stop() {
			// do nothing
		}
		
		@Override
		public boolean isSoundStoped() {
			return true;
		}
	};
	
	public static SoundHandler noSound() {
		return NO_SOUND;
	}
	
	private Sound sound;
	private boolean soundSoped = false;
	
	public SoundHandler(Sound sound) {
		this.sound = sound;
	}
	
	public void stop() {
		sound.stop();
		soundSoped = true;
	}
	
	public boolean isSoundStoped() {
		return soundSoped;
	}
}
