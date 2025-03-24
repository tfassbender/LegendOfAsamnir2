package net.jfabricationgames.gdx.map.implementation;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.player.Player;
import net.jfabricationgames.gdx.condition.ConditionHandler;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.handler.MapDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.properties.MapObjectStateProperties;
import net.jfabricationgames.gdx.data.state.BeforePersistState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.map.ground.GameMapGroundType;
import net.jfabricationgames.gdx.map.ground.MapObjectType;
import net.jfabricationgames.gdx.music.BackgroundMusicManager;
import net.jfabricationgames.gdx.object.GameObject;
import net.jfabricationgames.gdx.physics.AfterWorldStep;
import net.jfabricationgames.gdx.physics.BeforeWorldStep;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.rune.RuneType;
import net.jfabricationgames.gdx.util.AnnotationUtil;

public class GameMapImplementation implements GameMap {
	
	private static final String MAP_PROPERTY_KEY_DUNGEON_LEVEL = "dungeon_level";
	private static final String MAP_PROPERTY_KEY_BUILDING_LEVEL = "building_level";
	
	private static final String MAP_PROPERTY_BACKGROUND_MUSIC_NAME = "background_music";
	private static final String DEFAULT_BACKGROUND_MUSIC_NAME = "regular_area";
	
	@Override
	public GameMapGroundType getGroundTypeByName(String name) {
		return TiledMapPhysicsLoader.groundTypes.get(name);
	}
	
	private GameMapRenderer renderer;
	private GameMapProcessor processor;
	
	private String currentMapIdentifier;
	
	private Array<Function<Array<GameObject>, Array<GameObject>>> postAddObjectProcessingFunctions = new Array<>();
	
	private float continuousMapDamage;
	private float continuousMapDamageInterval;
	private float continuousMapDamageTimeDelta;
	private String continuousMapDamagePreventionCondition;
	
	protected TiledMap map;
	protected ObjectMap<Integer, Vector2> playerStartingPositions = new ObjectMap<>();
	
	//the lists are initialized in the factories
	protected Array<Item> items;
	protected Array<Item> itemsAboveGameObjects;
	protected Array<GameObject> objects;
	protected Array<Enemy> enemies;
	protected Array<NonPlayableCharacter> nonPlayableCharacters;
	protected Array<Animal> animals;
	protected Array<Projectile> projectiles;
	
	protected PlayableCharacter player;
	
	protected GameMapImplementation(OrthographicCamera camera) {
		renderer = new GameMapRenderer(this, camera);
		processor = new GameMapProcessor(this);
		
		itemsAboveGameObjects = new Array<>();
		projectiles = new Array<>();
		
		// create the player before other map objects, because it contains event listeners that listen for events that are fired when these objects are created
		player = Player.getInstance();
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@BeforePersistState
	public void updateMapData() {
		MapDataHandler.getInstance().setMapIdentifier(currentMapIdentifier);
	}
	
	@Override
	public void updateAfterLoadingGameState() {
		updateMap();
		updatePlayerPosition();
		updateCameraPosition();
	}
	
	private void updateMap() {
		String mapIdentifier = MapDataHandler.getInstance().getMapIdentifier();
		int startingPointId = 0;
		
		GameMapManager gameMapManager = GameMapManager.getInstance();
		if (mapIdentifier == null) {
			Gdx.app.error(getClass().getSimpleName(), "The map identifier was not set in the map data handler. The starting map config will be used." + //
					" This may happen during debbugging, but should not happen in a release.");
			mapIdentifier = gameMapManager.getInitialMapIdentifier();
			startingPointId = gameMapManager.getInitialStartingPointId();
		}
		
		gameMapManager.showMap(mapIdentifier, startingPointId);
	}
	
	private void updatePlayerPosition() {
		CharacterPropertiesDataHandler characterDataHandler = CharacterPropertiesDataHandler.getInstance();
		Vector2 playerPosition = characterDataHandler.getPlayerPosition();
		if (playerPosition != null) { // can be null if no respawn checkpoint was reached yet
			player.setPosition(playerPosition.x, playerPosition.y);
		}
	}
	
	private void updateCameraPosition() {
		player.centerCameraOnPlayer();
	}
	
	@Override
	public void beforeLoadMap(String mapIdentifier) {
		currentMapIdentifier = mapIdentifier;
		MapObjectDataHandler.getInstance().setMapIdentifier(mapIdentifier);
		
		removeCurrentMapIfPresent();
		player.reAddToWorld();
	}
	
	@Override
	public void afterLoadMap(String mapIdentifier, int playerStartingPointId) {
		renderer.changeMap(map);
		TiledMapPhysicsLoader.createPhysics(map);
		Vector2 startingPosition = playerStartingPositions.get(playerStartingPointId);
		if (startingPosition == null) {
			String message = "The starting point with the id '" + playerStartingPointId + "' was not found on map '" + mapIdentifier + "'";
			Gdx.app.error(getClass().getSimpleName(), message);
			throw new IllegalStateException(message);
		}
		player.setPosition(startingPosition.x, startingPosition.y);
		player.afterLoadMap();
		
		updateMapObjectStates();
		resetLanternUsed();
		updateContinuousMapDamageProperties();
		updateRemovedObjects();
		playBackgroundMusic();
	}
	
	private void removeCurrentMapIfPresent() {
		if (isMapInitialized()) {
			removeGameObjects();
			removeBodiesFromWorld();
			clearObjectLists();
		}
	}
	
	private boolean isMapInitialized() {
		return items != null // 
				&& itemsAboveGameObjects != null // 
				&& objects != null //
				&& enemies != null //
				&& nonPlayableCharacters != null //
				&& animals != null && projectiles != null; //
	}
	
	/**
	 * Remove the game objects first, to avoid using their bodies again (what would lead to segmentation faults in the native box2d methods)
	 */
	private void removeGameObjects() {
		//create copies of the lists, because the iterator doesn't work correctly: it's concurrently modified, but doesn't throw an exception
		Array<Item> itemsCopy = new Array<>(items);
		Array<Item> itemsAboveGameObjectsCopy = new Array<>(itemsAboveGameObjects);
		Array<GameObject> objectsCopy = new Array<>(objects);
		Array<Enemy> enemiesCopy = new Array<>(enemies);
		Array<NonPlayableCharacter> nonPlayableCharactersCopy = new Array<>(nonPlayableCharacters);
		Array<Animal> animalsCopy = new Array<>(animals);
		Array<Projectile> projectilesCopy = new Array<>(projectiles);
		
		for (Item item : itemsCopy) {
			item.removeFromMap();
		}
		for (Item item : itemsAboveGameObjectsCopy) {
			item.removeFromMap();
		}
		for (GameObject object : objectsCopy) {
			object.removeFromMap();
		}
		for (Enemy enemy : enemiesCopy) {
			enemy.removeFromMap();
		}
		for (NonPlayableCharacter npc : nonPlayableCharactersCopy) {
			npc.removeFromMap();
		}
		for (Animal animal : animalsCopy) {
			animal.removeFromMap();
		}
		for (Projectile projectile : projectilesCopy) {
			projectile.removeFromMap();
		}
		player.removeFromMap();
	}
	
	private void removeBodiesFromWorld() {
		Gdx.app.debug(getClass().getSimpleName(), "removeBodiesFromWorld - world locked: " + PhysicsWorld.getInstance().isInWorldStepExecution());
		PhysicsWorld.getInstance().removeBodiesFromWorld();
	}
	
	private void clearObjectLists() {
		items.clear();
		itemsAboveGameObjects.clear();
		objects.clear();
		enemies.clear();
		nonPlayableCharacters.clear();
		animals.clear();
		projectiles.clear();
	}
	
	private void resetLanternUsed() {
		GlobalValuesDataHandler.getInstance().put(Constants.GLOBAL_VALUE_KEY_LANTERN_USED, false);
	}
	
	private void updateContinuousMapDamageProperties() {
		continuousMapDamage = Float.parseFloat(map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_CONTINUOUSE_DAMAGE.getKey(), "0f", String.class));
		continuousMapDamageInterval = Float.parseFloat(map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_CONTINUOUSE_DAMAGE_INTERVAL.getKey(), "0f", String.class));
		continuousMapDamagePreventionCondition = map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_CONTINUOUSE_DAMAGE_PREVENTION_CONDITION.getKey(), "", String.class);
		continuousMapDamageTimeDelta = 0f;
	}
	
	private void updateRemovedObjects() {
		if (RuneType.ALGIZ.isCollected()) {
			// execute the picked up method again, to remove all objects that block invisible ways
			processRunePickUp(RuneType.ALGIZ);
		}
	}
	
	private void playBackgroundMusic() {
		BackgroundMusicManager.getInstance().play(getBackgroundMusicName());
	}
	
	@Override
	public String getBackgroundMusicName() {
		return map.getProperties().get(MAP_PROPERTY_BACKGROUND_MUSIC_NAME, DEFAULT_BACKGROUND_MUSIC_NAME, String.class);
	}
	
	@Override
	public void processRunePickUp(RuneType rune) {
		switch (rune) {
			case ALGIZ:
				removePhysicsObjectsWithType(MapObjectType.INVISIBLE_PATH_BLOCKER);
				break;
			case KENAZ:
				GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_KENAZ_FORGED, true);
				break;
			default:
				//other runes only set global values
				break;
		}
	}
	
	@Override
	public void executeBeforeWorldStep() {
		executeAnnotatedMethodsOnAllObjects(BeforeWorldStep.class);
	}
	
	@Override
	public void executeAfterWorldStep() {
		executeAnnotatedMethodsOnAllObjects(AfterWorldStep.class);
	}
	
	private void executeAnnotatedMethodsOnAllObjects(Class<? extends Annotation> annotation) {
		AnnotationUtil.executeAnnotatedMethods(annotation, player);
		
		executeAnnotatedMethods(annotation, items);
		executeAnnotatedMethods(annotation, itemsAboveGameObjects);
		executeAnnotatedMethods(annotation, objects);
		executeAnnotatedMethods(annotation, enemies);
		executeAnnotatedMethods(annotation, nonPlayableCharacters);
		executeAnnotatedMethods(annotation, animals);
		executeAnnotatedMethods(annotation, projectiles);
	}
	
	private void executeAnnotatedMethods(Class<? extends Annotation> annotation, Array<?> mapObjects) {
		for (Object mapObject : mapObjects) {
			AnnotationUtil.executeAnnotatedMethods(annotation, mapObject);
		}
	}
	
	@Override
	public void processPlayer(float delta) {
		handleMapContinuousDamage(delta);
		player.process(delta);
	}
	
	private void handleMapContinuousDamage(float delta) {
		if (continuousMapDamage > 0 && !preventContinuousMapDamageConditionMet()) {
			continuousMapDamageTimeDelta += delta;
			if (continuousMapDamageTimeDelta >= continuousMapDamageInterval) {
				continuousMapDamageTimeDelta -= continuousMapDamageInterval;
				player.takeDamage(continuousMapDamage, new AttackInfo(AttackType.CONTINUOUS_MAP_DAMAGE));
			}
		}
	}
	
	private boolean preventContinuousMapDamageConditionMet() {
		return ConditionHandler.getInstance().isConditionMet(continuousMapDamagePreventionCondition);
	}
	
	@Override
	public void processAndRender(float delta) {
		renderer.updateCamera();
		renderer.renderBackground(delta);
		renderer.renderEffectLayers();
		processAndRenderGameObject(delta);
		renderer.renderAbovePlayer();
		renderer.renderShadows();
		renderer.renderDarknessArroundPlayer();
		
		// process the cutscene last, otherwise the color transition won't work correctly 
		// (because it can't be rendered within the GameMapRenderer batch)
		processor.processCutscene(delta);
	}
	
	private void processAndRenderGameObject(float delta) {
		renderer.beginShapeRenderer();
		renderer.renderObjectShapes();
		renderer.renderProjectileShapes(delta);
		renderer.endShapeRenderer();
		
		renderer.beginBatch();
		renderer.renderItems(delta);
		renderer.renderObjects(delta);
		renderer.renderItemsAboveGameObjects(delta);
		
		processor.processEnemies(delta);
		renderer.renderEnemies(delta);
		
		processor.processNpcs(delta);
		renderer.renderNpcs(delta);
		
		processor.processAnimals(delta);
		renderer.renderAnimals(delta);
		
		processor.processProjectiles(delta);
		renderer.renderProjectiles(delta);
		
		renderer.renderPlayer(delta);
		renderer.endBatch();
		
		renderer.beginShapeRenderer();
		renderer.renderEnemyHealthBars();
		renderer.endShapeRenderer();
	}
	
	@Override
	public boolean isDungeonMap() {
		return Boolean.parseBoolean(map.getProperties().get(MAP_PROPERTY_KEY_DUNGEON_LEVEL, "false", String.class));
	}
	
	@Override
	public boolean isBuildingMap() {
		return Boolean.parseBoolean(map.getProperties().get(MAP_PROPERTY_KEY_BUILDING_LEVEL, "false", String.class));
	}
	
	@Override
	public void addItem(Item item) {
		logAddObject(item, items.size);
		items.add(item);
	}
	
	@Override
	public void addItemAboveGameObjects(Item item) {
		logAddObject(item, itemsAboveGameObjects.size, "above game objects");
		itemsAboveGameObjects.add(item);
	}
	
	@Override
	public void removeItem(Item item, Body body) {
		logRemoveObject(item, items.size, "maybe above game objects");
		items.removeValue(item, false);
		itemsAboveGameObjects.removeValue(item, false);
		removePhysicsBody(body);
	}
	
	private void removePhysicsBody(Body body) {
		PhysicsWorld.getInstance().removeBodyWhenPossible(body);
	}
	
	@Override
	public void addObject(GameObject object) {
		logAddObject(object, objects.size);
		objects.add(object);
		object.postAddToGameMap();
		
		for (Function<Array<GameObject>, Array<GameObject>> postAddObjectProcessingFunction : postAddObjectProcessingFunctions) {
			objects = postAddObjectProcessingFunction.apply(objects);
		}
	}
	
	/**
	 * Add a function that will be executed after an object was added.
	 */
	@Override
	public void addPostAddObjectProcessing(Function<Array<GameObject>, Array<GameObject>> postAddObjectProcessingFunction) {
		postAddObjectProcessingFunctions.add(postAddObjectProcessingFunction);
	}
	
	public void removePostAddObjectProcessing(Function<Array<GameObject>, Array<GameObject>> postAddObjectProcessingFunction) {
		postAddObjectProcessingFunctions.removeValue(postAddObjectProcessingFunction, false);
	}
	
	@Override
	public void removeObject(GameObject gameObject, Body body) {
		logRemoveObject(gameObject, objects.size);
		objects.removeValue(gameObject, false);
		removePhysicsBody(body);
	}
	
	@Override
	public void addEnemy(Enemy enemy) {
		logAddObject(enemy, enemies.size);
		enemies.add(enemy);
	}
	
	@Override
	public void removeEnemy(Enemy enemy, Body body) {
		logRemoveObject(enemy, enemies.size);
		enemies.removeValue(enemy, false);
		removePhysicsBody(body);
	}
	
	public void addNpc(NonPlayableCharacter npc) {
		logAddObject(npc, nonPlayableCharacters.size);
		nonPlayableCharacters.add(npc);
	}
	
	@Override
	public void removeNpc(NonPlayableCharacter npc, Body body) {
		logRemoveObject(npc, nonPlayableCharacters.size);
		nonPlayableCharacters.removeValue(npc, false);
		removePhysicsBody(body);
	}
	
	public void addAnimal(Animal animal) {
		logAddObject(animal, animals.size);
		animals.add(animal);
	}
	
	@Override
	public void removeAnimal(Animal animal, Body body) {
		logRemoveObject(animal, animals.size);
		animals.removeValue(animal, false);
		removePhysicsBody(body);
	}
	
	@Override
	public void addProjectile(Projectile projectile) {
		logAddObject(projectile, projectiles.size);
		projectiles.add(projectile);
	}
	
	@Override
	public void removeProjectile(Projectile projectile, Body body) {
		logRemoveObject(projectile, projectiles.size);
		projectiles.removeValue(projectile, false);
		removePhysicsBody(body);
	}
	
	@Override
	public void removePhysicsObjectsWithType(MapObjectType type) {
		PhysicsWorld world = PhysicsWorld.getInstance();
		Array<Body> bodies = world.findBodies(body -> body.getUserData() == type); // check with != because type is an enum-constant
		for (Body body : bodies) {
			world.removeBodyWhenPossible(body);
		}
	}
	
	@Override
	public MapProperties getGlobalMapProperties() {
		return map.getProperties();
	}
	
	@Override
	public float getMapWidth() {
		return map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_WIDTH_IN_TILE_DIMENSIONS.getKey(), Integer.class) //
				* map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_TILE_WIDTH_IN_PIXELS.getKey(), Integer.class);
	}
	@Override
	public float getMapHeight() {
		return map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_HEIGHT_IN_TILE_DIMENSIONS.getKey(), Integer.class) //
				* map.getProperties().get(GameMap.GlobalMapPropertyKeys.MAP_TILE_HEIGHT_IN_PIXELS.getKey(), Integer.class);
	}
	
	@Override
	public String getCurrentMapIdentifier() {
		return currentMapIdentifier;
	}
	
	@Override
	public CutsceneControlledUnit getUnitById(String unitId) {
		if (CutsceneHandler.CONTROLLED_UNIT_ID_PLAYER.equals(unitId)) {
			return player;
		}
		
		Array<Enemy> copyOfEnemies = new Array<>(enemies); // a copy is needed to avoid a nested iteration (when an enemy tries to find a unit by id)
		for (Enemy enemy : copyOfEnemies) {
			if (unitId.equals(enemy.getUnitId())) {
				return enemy;
			}
		}
		
		for (NonPlayableCharacter npc : nonPlayableCharacters) {
			if (unitId.equals(npc.getUnitId())) {
				return npc;
			}
		}
		
		for (Animal animal : animals) {
			if (unitId.equals(animal.getUnitId())) {
				return animal;
			}
		}
		
		for (GameObject object : objects) {
			if (unitId.equals(object.getUnitId())) {
				return object;
			}
		}
		
		for (Item item : items) {
			if (unitId.equals(item.getUnitId())) {
				return item;
			}
		}
		
		return null;
	}
	
	@Override
	public Projectile getProjectileByUnitId(String unitId) {
		for (Projectile projectile : projectiles) {
			if (unitId.equals(projectile.getUnitId())) {
				return projectile;
			}
		}
		
		return null;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.UPDATE_MAP_OBJECT_STATES) {
			updateMapObjectStates();
		}
		else if (event.eventType == EventType.BEFORE_PERSIST_STATE) {
			AnnotationUtil.executeAnnotatedMethods(BeforePersistState.class, this);
		}
		else if (event.eventType == EventType.UPDATE_MAP_AFTER_LOADING_GAME_STATE) {
			updateAfterLoadingGameState();
		}
		else if (event.eventType == EventType.CUTSCENE_REMOVE_UNIT && event.stringValue != null) {
			CutsceneControlledUnit unitToRemove = getUnitById(event.stringValue);
			if (unitToRemove != null) {
				unitToRemove.removeFromMap();
			}
		}
	}
	
	private void updateMapObjectStates() {
		MapObjectDataHandler dataHandler = MapObjectDataHandler.getInstance();
		
		applyStatesToMapObjects(dataHandler, objects);
		applyStatesToMapObjects(dataHandler, items);
		applyStatesToMapObjects(dataHandler, enemies);
		
		addNotConfiguredObjects(dataHandler);
	}
	
	private void applyStatesToMapObjects(MapObjectDataHandler dataHandler, Array<? extends StatefulMapObject> mapObjects) {
		//create a copy of the list before executing to avoid a concurrent modification (which would not throw an exception)
		Array<StatefulMapObject> immutableObjectList = new Array<>(mapObjects);
		for (StatefulMapObject object : immutableObjectList) {
			applyStateIfPresent(dataHandler, object);
		}
	}
	
	private void applyStateIfPresent(MapObjectDataHandler dataHandler, StatefulMapObject object) {
		ObjectMap<String, String> state = dataHandler.getStateById(object.getMapObjectId());
		if (state != null) {
			object.applyState(state);
		}
	}
	
	private void addNotConfiguredObjects(MapObjectDataHandler dataHandler) {
		ObjectMap<String, MapObjectStateProperties> currentMapStates = dataHandler.getCurrentMapStates();
		
		if (currentMapStates != null) {
			for (String mapStateKey : currentMapStates.keys()) {
				if (isUnconfiguredObjectKey(mapStateKey)) {
					addUnconfiguredObjectToMap(currentMapStates.get(mapStateKey));
				}
			}
		}
	}
	
	private boolean isUnconfiguredObjectKey(String mapStateKey) {
		return mapStateKey.startsWith(MapObjectDataHandler.OBJECT_NOT_CONFIGURED_IN_MAP_PREFIX);
	}
	
	private void addUnconfiguredObjectToMap(MapObjectStateProperties mapObjectStateProperties) {
		String objectType = mapObjectStateProperties.state.get(MapObjectDataHandler.TYPE_DESCRIPTION_MAP_KEY);
		switch (objectType) {
			case "Item":
				ItemFactory.addItemFromSavedState(mapObjectStateProperties.state);
				break;
			default:
				throw new IllegalStateException("Unexpected type: " + objectType);
		}
	}
	
	private void logAddObject(Object object, int count) {
		logAddObject(object, count, "");
	}
	
	private void logAddObject(Object object, int count, String objectDescription) {
		if (objectDescription != null && !objectDescription.isEmpty()) {
			objectDescription = " (" + objectDescription + ") ";
		}
		else {
			objectDescription = "";
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Adding " + object.getClass().getSimpleName() + objectDescription + " (count: " + count + "): " + object);
	}
	
	private void logRemoveObject(Object object, int count) {
		logRemoveObject(object, count, "");
	}
	
	private void logRemoveObject(Object object, int count, String objectDescription) {
		if (objectDescription != null && !objectDescription.isEmpty()) {
			objectDescription = " (" + objectDescription + ") ";
		}
		else {
			objectDescription = "";
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Removing " + object.getClass().getSimpleName() + objectDescription + " (count: " + count + "): " + object);
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		map.dispose();
	}
}
