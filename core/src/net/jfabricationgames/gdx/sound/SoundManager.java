package net.jfabricationgames.gdx.sound;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.sound.config.SoundManagerConfig;
import net.jfabricationgames.gdx.sound.config.SoundSetConfig;

/**
 * Manages {@link SoundSet}s for all types
 */
public class SoundManager implements Disposable, EventListener {
	
	private static final String GLOBAL_SETTINGS_KEY_SOUND_EFFECT_VOLUME = "sound_effect_volume_in_percent";
	
	private static SoundManager instance;
	
	public static synchronized SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}
	
	private ArrayMap<String, SoundSet> soundSets;
	
	private float soundVolume = 1f;
	
	private SoundManager() {
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void loadConfig(String soundConfigPath) {
		soundSets = new ArrayMap<>();
		FileHandle configFile = Gdx.files.internal(soundConfigPath);
		Json json = new Json();
		SoundManagerConfig config = json.fromJson(SoundManagerConfig.class, HashMap.class, configFile);
		for (Entry<String, SoundSetConfig> configEntry : config.getSoundSets().entrySet()) {
			soundSets.put(configEntry.getKey(), loadSoundSetConfig(configEntry.getValue(), configEntry.getKey()));
		}
	}
	
	private SoundSet loadSoundSetConfig(SoundSetConfig config, String name) {
		return new SoundSet(name, config.getSounds());
	}
	
	public SoundSet loadSoundSet(String name) {
		return getSoundSetChecked(name).load();
	}
	
	public void disposeSoundSet(String name) {
		getSoundSetChecked(name).dispose();
	}
	
	private SoundSet getSoundSetChecked(String name) {
		SoundSet soundSet = soundSets.get(name);
		if (soundSet == null) {
			throw new IllegalArgumentException("A sound set named '" + name + "' doesn't exist.");
		}
		return soundSet;
	}
	
	public float getSoundVolume() {
		return soundVolume;
	}
	
	public void setSoundVolume(float volume) {
		soundVolume = volume;
		
		GlobalValuesDataHandler.getInstance().put(GLOBAL_SETTINGS_KEY_SOUND_EFFECT_VOLUME, Integer.toString((int) (volume * 100)));
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.GAME_LOADED) {
			loadSoundConfig();
		}
	}
	
	private void loadSoundConfig() {
		GlobalValuesDataHandler dataHandler = GlobalValuesDataHandler.getInstance();
		
		float volumeInPercent = dataHandler.getAsInteger(GLOBAL_SETTINGS_KEY_SOUND_EFFECT_VOLUME, 100);
		setSoundVolume(volumeInPercent / 100f);
	}
	
	@Override
	public void dispose() {
		soundSets.values().forEach(SoundSet::dispose);
	}
}
