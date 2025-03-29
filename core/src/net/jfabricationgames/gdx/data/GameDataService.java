package net.jfabricationgames.gdx.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class GameDataService implements EventListener {
	
	public static final String GAME_DATA_SAVE_DIRECTORY = ".legend_of_asamnir_2/saves/";
	public static final String GAME_DATA_SAVE_FILENAME_QUICKSAVE = "quicksave.json";
	public static final String GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER = "<index>";
	public static final String GAME_DATA_SAVE_FILENAME = "save_" + GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER + ".json";
	
	public static void initializeEventListener() {
		GameDataService service = new GameDataService();
		EventHandler.getInstance().registerEventListener(service);
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.QUICKSAVE) {
			storeGameDataToQuickSaveSlot();
		}
	}
	
	public void storeGameDataToQuickSaveSlot() {
		store(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	public void storeGameDataToSaveSlot(int slot) {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.SAVE_GAME));
		store(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	private void store(String fileName) {
		Gdx.app.log(getClass().getSimpleName(), "saving game to file: " + fileName);
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		
		executeAnnotatedMethodsBeforePersisting();
		GameDataContainer gameData = GameDataHandler.getInstance().getGameData();
		Json json = new Json(OutputType.json);
		json.setUsePrototypes(false);
		String serializedGameData = json.prettyPrint(gameData);
		
		fileHandle.writeString(serializedGameData, false);
	}
	
	private void executeAnnotatedMethodsBeforePersisting() {
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.BEFORE_PERSIST_STATE));
	}
	
	public void loadGameDataFromQuicksaveSlot() throws IllegalStateException {
		loadGameData(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	public void loadGameDataFromSaveSlot(int slot) throws IllegalStateException {
		loadGameData(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	private void loadGameData(String fileName) throws IllegalStateException {
		Gdx.app.log(getClass().getSimpleName(), "loading game from file: " + fileName);
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		
		if (!fileHandle.exists()) {
			throw new IllegalStateException("The save file '" + fileName + "' does not exist.");
		}
		
		Json json = new Json();
		GameDataContainer gameData = json.fromJson(GameDataContainer.class, fileHandle);
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.UPDATE_MAP_AFTER_LOADING_GAME_STATE));
		
		GameDataHandler.getInstance().updateData(gameData);
		
		if (gameData.characterDataContainer.respawnWithStartingStats //
				&& Player.isInitialized()) { // don't initialize the player if the game was not started, because the assets are not loaded
			Player.getInstance().resetAfterGameOver();
		}
	}
	
	public boolean isGameDataSlotExisting(int slot) {
		return isGameDataSlotExisting(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	public boolean isQuickSaveGameDataSlotExisting() {
		return isGameDataSlotExisting(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	private boolean isGameDataSlotExisting(String fileName) {
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		return fileHandle.exists();
	}
	
	public String getSaveDateAsString(int slot) {
		return formattDate(getSaveDate(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot))));
	}
	
	public String getQuickSaveDateAsString() {
		return formattDate(getSaveDate(GAME_DATA_SAVE_FILENAME_QUICKSAVE));
	}
	
	private String formattDate(LocalDateTime date) {
		if (date == null) {
			return "---";
		}
		
		return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
	}
	
	public LocalDateTime getSaveDate(int slot) {
		return getSaveDate(GAME_DATA_SAVE_FILENAME.replace(GAME_DATA_SAVE_FILENAME_INDEX_PLACEHOLDER, Integer.toString(slot)));
	}
	
	public LocalDateTime getQuickSaveDate() {
		return getSaveDate(GAME_DATA_SAVE_FILENAME_QUICKSAVE);
	}
	
	private LocalDateTime getSaveDate(String fileName) {
		FileHandle fileHandle = Gdx.files.external(GAME_DATA_SAVE_DIRECTORY + fileName);
		if (!fileHandle.exists()) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(fileHandle.lastModified()), TimeZone.getDefault().toZoneId());
	}
}
