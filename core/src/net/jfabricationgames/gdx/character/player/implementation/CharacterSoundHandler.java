package net.jfabricationgames.gdx.character.player.implementation;

import net.jfabricationgames.gdx.sound.SoundHandler;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

class CharacterSoundHandler {
	
	private static final String SOUND_SET_KEY = "dwarf";
	
	private SoundSet soundSet;
	
	public CharacterSoundHandler() {
		soundSet = SoundManager.getInstance().loadSoundSet(SOUND_SET_KEY);
	}
	
	public SoundHandler playSound(CharacterAction action) {
		if (action.getSound() != null) {
			return playSound(action.getSound());
		}
		return SoundHandler.noSound();
	}
	
	public SoundHandler playSound(String sound) {
		return soundSet.playSound(sound);
	}
	
	public void dispose() {
		soundSet.dispose();
	}
}
