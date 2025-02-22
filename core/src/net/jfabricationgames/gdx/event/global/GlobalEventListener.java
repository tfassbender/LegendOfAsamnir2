package net.jfabricationgames.gdx.event.global;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;

public class GlobalEventListener implements EventListener {
	
	private static final String GLOBAL_EVENTS_CONFIG_FILE = "config/events/globalListenedEvents.json";
	
	private static GlobalEventListener instance;
	
	public static void createGlobalEventListener() {
		if (instance != null) {
			Gdx.app.error(GlobalEventListener.class.getSimpleName(), "A GlobalEventListener was already created - skipping creation of another GlobalEventListener");
			return;
		}
		
		instance = new GlobalEventListener(EventHandler.getInstance());
	}
	
	private Map<String, GlobalEventConfig> events;//don't use an libGDX ObjectMap here, because an iterator that works nested is needed
	
	private GlobalEventListener(EventHandler eventHandler) {
		eventHandler.registerEventListener(this);
		loadGlobalEvents();
	}
	
	@SuppressWarnings("unchecked")
	private void loadGlobalEvents() {
		Json json = new Json();
		Array<String> configFiles = json.fromJson(Array.class, String.class, Gdx.files.internal(GLOBAL_EVENTS_CONFIG_FILE));
		
		events = new HashMap<String, GlobalEventConfig>();
		for (String configFile : configFiles) {
			Map<String, GlobalEventConfig> configuredEvents = json.fromJson(HashMap.class, GlobalEventConfig.class, Gdx.files.internal(configFile));
			
			checkDuplicateKeys(configuredEvents);
			events.putAll(configuredEvents);
		}
	}
	
	private void checkDuplicateKeys(Map<String, GlobalEventConfig> configuredEvents) {
		for (String eventKey : events.keySet()) {
			for (String configuredEventKey : configuredEvents.keySet()) {
				if (eventKey.equals(configuredEventKey)) {
					throw new IllegalStateException("The event key '" + configuredEventKey + "' would overwrite an already known event key. The keys have to be unique");
				}
			}
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		for (GlobalEventConfig eventConfig : events.values()) {
			if (eventMatchesConfig(event, eventConfig)) {
				eventConfig.executionType.execute(eventConfig);
			}
		}
	}
	
	private boolean eventMatchesConfig(EventConfig event, GlobalEventConfig eventConfig) {
		if (eventConfig.exactMatch) {
			return eventConfig.event.equals(event);
		}
		else {
			return event != null && event.equalsIgnoringDefaultConfigValues(eventConfig.event);
		}
	}
}
