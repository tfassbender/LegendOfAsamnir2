package net.jfabricationgames.gdx.music;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

public class BackgroundMusicManager {
	
	private static BackgroundMusicManager instance;
	
	public static synchronized BackgroundMusicManager getInstance() {
		if (instance == null) {
			instance = new BackgroundMusicManager();
		}
		return instance;
	}
	
	private ArrayMap<String, BackgroundMusicConfig> configs;
	private PlayingMusic playingMusic;
	
	private BackgroundMusicManager() {
		configs = new ArrayMap<String, BackgroundMusicConfig>();
	}
	
	public void loadConfig(String backgroundMusicConfigPath) {
		FileHandle configFile = Gdx.files.internal(backgroundMusicConfigPath);
		Json json = new Json();
		
		@SuppressWarnings("unchecked")
		Array<BackgroundMusicConfig> configList = json.fromJson(Array.class, BackgroundMusicConfig.class, configFile);
		for (BackgroundMusicConfig musicConfig : configList) {
			configs.put(musicConfig.name, musicConfig);
		}
	}
	
	public void play(String name) {
		BackgroundMusicConfig config = configs.get(name);
		if (config == null) {
			Gdx.app.error(getClass().getSimpleName(), "The background music with the name '" + name + "' doesn't exist.");
			return;
		}
		if (isPlaying(name)) {
			Gdx.app.log(getClass().getSimpleName(), "The background music with the name '" + name + "' is already playing and won't be restarted.");
			return;
		}
		
		stop(); // stop and dispose the current music if there is one playing
		
		playingMusic = new PlayingMusic(name, Gdx.audio.newMusic(Gdx.files.internal(config.file)));
		playingMusic.music.setVolume(config.volume);
		playingMusic.music.setLooping(config.loop);
		playingMusic.music.play();
	}
	
	private boolean isPlaying(String name) {
		return playingMusic != null && Objects.equals(name, playingMusic.name);
	}
	
	public void stop() {
		if (playingMusic != null) {
			playingMusic.music.stop();
			playingMusic.music.dispose();
			playingMusic = null;
		}
	}
	
	private class PlayingMusic {
		
		private String name;
		private Music music;
		
		public PlayingMusic(String name, Music music) {
			this.name = name;
			this.music = music;
		}
	}
}
