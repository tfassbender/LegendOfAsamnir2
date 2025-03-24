package net.jfabricationgames.gdx.music;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.map.GameMapManager;

public class BackgroundMusicManager implements EventListener {
	
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
		
		EventHandler.getInstance().registerEventListener(this);
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
		return playingMusic != null && Objects.equals(name, playingMusic.name) && playingMusic.music.isPlaying();
	}
	
	public void stop() {
		if (playingMusic != null) {
			playingMusic.music.stop();
			playingMusic.music.dispose();
			playingMusic = null;
		}
	}
	
	private void addMusicToQueue(String name) {
		BackgroundMusicConfig config = configs.get(name);
		if (config == null) {
			Gdx.app.error(getClass().getSimpleName(), "The background music with the name '" + name + "' doesn't exist.");
			return;
		}
		
		if (playingMusic == null) {
			play(name);
		}
		else {
			PlayingMusic last = playingMusic.getLastInQueue();
			last.nextInQueue = new PlayingMusic(name, Gdx.audio.newMusic(Gdx.files.internal(config.file)));
			last.nextInQueue.music.setVolume(config.volume);
			last.nextInQueue.music.setLooping(config.loop);
			last.addOnCompleteListenerForQueue();
		}
	}
	
	private class PlayingMusic {
		
		public String name;
		public Music music;
		public PlayingMusic nextInQueue;
		
		public PlayingMusic(String name, Music music) {
			this.name = name;
			this.music = music;
		}
		
		public PlayingMusic getLastInQueue() {
			PlayingMusic last = this;
			while (last.nextInQueue != null) {
				last = last.nextInQueue;
			}
			return last;
		}
		
		public void addOnCompleteListenerForQueue() {
			if (nextInQueue != null) {
				music.setOnCompletionListener(new Music.OnCompletionListener() {
					
					@Override
					public void onCompletion(Music music) {
						nextInQueue.music.play();
						playingMusic = nextInQueue;
					}
				});
			}
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAY_BACKGROUND_MUSIC) {
			play(event.stringValue);
			pause();
			delay(this::resume, event.floatValue);
		}
		else if (event.eventType == EventType.PLAY_MAP_BACKGROUND_MUSIC) {
			String mapBackgroundMusic = GameMapManager.getInstance().getMap().getBackgroundMusicName();
			play(mapBackgroundMusic);
			pause();
			delay(this::resume, event.floatValue);
		}
		else if (event.eventType == EventType.STOP_BACKGROUND_MUSIC) {
			stop();
		}
		else if (event.eventType == EventType.ADD_BACKGROUND_MUSIC_TO_QUEUE) {
			addMusicToQueue(event.stringValue);
		}
	}
	
	private void pause() {
		if (playingMusic != null) {
			playingMusic.music.pause();
		}
	}
	
	private void resume() {
		if (playingMusic != null) {
			playingMusic.music.play();
		}
	}
	
	private void delay(Runnable runnable, float delayInSeconds) {
		if (delayInSeconds <= 0) {
			runnable.run();
		}
		else {
			new Thread(() -> {
				try {
					Thread.sleep((long) (delayInSeconds * 1000));
					runnable.run();
				}
				catch (InterruptedException e) {
					Gdx.app.error(getClass().getSimpleName(), "Error while delaying the execution of a runnable.", e);
				}
			}).start();
		}
	}
}
