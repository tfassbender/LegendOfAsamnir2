package net.jfabricationgames.gdx.sound;

public class SoundPlayConfig {
	
	public static final float DEFAULT_SETTING = -1f;
	
	public float delay;
	public float volume;
	
	public SoundPlayConfig() {
		delay = DEFAULT_SETTING;
		volume = DEFAULT_SETTING;
	}
	
	public SoundPlayConfig setDelay(float delay) {
		this.delay = delay;
		return this;
	}
	
	public SoundPlayConfig setVolume(float volume) {
		this.volume = volume;
		return this;
	}
}