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
	
	private static final float FADE_OUT_DURATION = 2f;
	private static final float FADE_IN_DURATION = 2f;
	private float fadeOutTimer = 0f;
	private float fadeInTimer = 0f;
	private boolean fadingOut = false;
	private boolean fadingIn = false;
	
	private float musicVolume = 1f; // TODO should be changeable in the game settings
	
	private ArrayMap<String, BackgroundMusicConfig> configs;
	private PlayingMusic playingMusic;
	
	private BackgroundMusicManager() {
		configs = new ArrayMap<String, BackgroundMusicConfig>();
	}
	
	/**
	 * Called from the game loop to update the delta time (for fade in/out effects).
	 */
	public void update(float delta) {
		if (fadingIn && isPlaying()) {
			fadeInTimer += delta;
			if (fadeInTimer >= FADE_IN_DURATION) {
				fadingIn = false;
				fadeInTimer = 0f;
			}
			else {
				playingMusic.music.setVolume((fadeInTimer / FADE_IN_DURATION) * playingMusic.configVolume * musicVolume);
			}
		}
		else if (fadingOut && isPlaying()) {
			fadeOutTimer += delta;
			if (fadeOutTimer >= FADE_OUT_DURATION) {
				fadingOut = false;
				fadeOutTimer = 0f;
				stop(true);
			}
			else {
				playingMusic.music.setVolume((1 - fadeOutTimer / FADE_OUT_DURATION) * playingMusic.configVolume * musicVolume);
			}
		}
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
		play(name, false);
	}
	
	public void play(String name, boolean fadeIn) {
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
		
		playingMusic = new PlayingMusic(config);
		playingMusic.music.play();
		if (fadeIn) {
			fadingIn = true;
			fadeInTimer = 0f;
			playingMusic.music.setVolume(0);
		}
	}
	
	private boolean isPlaying() {
		return playingMusic != null && playingMusic.music.isPlaying();
	}
	
	private boolean isPlaying(String name) {
		return playingMusic != null && Objects.equals(name, playingMusic.name) && playingMusic.music.isPlaying();
	}
	
	public void stop() {
		stop(false);
	}
	
	public void stop(boolean startNextInQueue) {
		if (playingMusic != null) {
			playingMusic.music.stop();
			playingMusic.music.dispose();
			
			if (startNextInQueue && playingMusic.nextInQueue != null) {
				playingMusic.nextInQueueStarter.onCompletion(playingMusic.music);
			}
			else {
				// dispose the whole queue to free the memory
				while (playingMusic.nextInQueue != null) {
					playingMusic.music.dispose();
					playingMusic = playingMusic.nextInQueue;
				}
			}
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAY_BACKGROUND_MUSIC) {
			playDelayed(event.stringValue, event.floatValue, event.booleanValue);
		}
		else if (event.eventType == EventType.PLAY_MAP_BACKGROUND_MUSIC) {
			String mapBackgroundMusic = GameMapManager.getInstance().getMap().getBackgroundMusicName();
			playDelayed(mapBackgroundMusic, event.floatValue, event.booleanValue);
		}
		else if (event.eventType == EventType.STOP_BACKGROUND_MUSIC) {
			if (event.booleanValue) { // the boolean value is used as "fade out" parameter
				fadingOut = true;
				fadeOutTimer = 0f;
				// after fading out the music is stopped in the update method
			}
			else {
				stop();
			}
		}
		else if (event.eventType == EventType.ADD_BACKGROUND_MUSIC_TO_QUEUE) {
			addMusicToQueue(event.stringValue, event.floatValue, event.booleanValue);
		}
		else if (event.eventType == EventType.ADD_MAP_BACKGROUND_MUSIC_TO_QUEUE) {
			String mapBackgroundMusic = GameMapManager.getInstance().getMap().getBackgroundMusicName();
			addMusicToQueue(mapBackgroundMusic, event.floatValue, event.booleanValue);
		}
		else if (event.eventType == EventType.CLEAR_BACKGROUND_MUSIC_QUEUE) {
			disposeQueue();
		}
	}
	
	private void playDelayed(String musicName, float delayInSeconds, boolean fadeIn) {
		// the call to the play method must not be delayed for the addMusicToQueue method to work properly
		play(musicName);
		pause();
		delay(this::resume, delayInSeconds);
		
		fadingIn = fadeIn;
		fadeInTimer = 0f;
		
		// prevent stopping the just started music because of the fade out effect of the previous music
		fadingOut = false;
		fadeOutTimer = 0f;
	}
	
	private void addMusicToQueue(String name, float delayInSeconds, boolean fadeIn) {
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
			last.nextInQueue = new PlayingMusic(config);
			last.addOnCompleteListenerForQueue(delayInSeconds, fadeIn);
		}
	}
	
	private void disposeQueue() {
		if (playingMusic == null) {
			return;
		}
		
		PlayingMusic queued = playingMusic;
		while (queued.nextInQueue != null) {
			PlayingMusic next = queued.nextInQueue;
			
			queued.music.setOnCompletionListener(null);
			queued.nextInQueueStarter = null;
			queued.nextInQueue = null;
			
			if (next != null) {
				next.music.dispose();
			}
			queued = next;
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
	
	private class PlayingMusic {
		
		public String name;
		public float configVolume;
		public Music music;
		
		public PlayingMusic nextInQueue;
		public Music.OnCompletionListener nextInQueueStarter;
		
		public PlayingMusic(BackgroundMusicConfig config) {
			this.name = config.name;
			this.configVolume = config.volume;
			this.music = Gdx.audio.newMusic(Gdx.files.internal(config.file));
			
			music.setVolume(config.volume);
			music.setLooping(config.loop);
		}
		
		public PlayingMusic getLastInQueue() {
			PlayingMusic last = this;
			while (last.nextInQueue != null) {
				last = last.nextInQueue;
			}
			return last;
		}
		
		public void addOnCompleteListenerForQueue(float delayInSeconds, boolean fadeIn) {
			if (nextInQueue != null) {
				nextInQueueStarter = new Music.OnCompletionListener() {
					
					@Override
					public void onCompletion(Music music) {
						playingMusic = nextInQueue;
						
						// the call to the play method must not be delayed for the addMusicToQueue method to work properly
						nextInQueue.music.play();
						nextInQueue.music.pause();
						
						delay(BackgroundMusicManager.this::resume, delayInSeconds);
						
						fadingIn = fadeIn;
						fadeInTimer = 0f;
						
						// prevent stopping the just started music because of the fade out effect of the previous music
						fadingOut = false;
						fadeOutTimer = 0f;
					}
				};
				
				music.setOnCompletionListener(nextInQueueStarter);
			}
		}
	}
}
