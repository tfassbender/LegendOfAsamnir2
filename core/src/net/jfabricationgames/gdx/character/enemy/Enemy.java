package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.attack.AttackHandler;
import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.CharacterTypeConfig;
import net.jfabricationgames.gdx.character.ai.ArtificialIntelligence;
import net.jfabricationgames.gdx.character.enemy.statsbar.EnemyHealthBarRenderer;
import net.jfabricationgames.gdx.character.enemy.statsbar.EnemyStatsBarRenderer;
import net.jfabricationgames.gdx.character.enemy.statsbar.EnemyTimerBarRenderer;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.character.state.CharacterStateMachine;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.ItemDropUtil;
import net.jfabricationgames.gdx.physics.CollisionUtil;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsUtil;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.util.MapUtil;

public class Enemy extends AbstractCharacter implements Hittable, StatefulMapObject, EventListener {
	
	private static final String MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT = "enemyDefeatedEventText";
	
	private static final SoundSet SOUND_SET = SoundManager.getInstance().loadSoundSet("enemy");
	
	protected PhysicsBodyProperties physicsBodyProperties;
	
	protected EnemyHealthBarRenderer healthBarRenderer;
	protected EnemyStatsBarRenderer statsBarRenderer;
	
	protected EnemyTypeConfig typeConfig;
	protected AttackHandler attackHandler;
	
	protected float health;
	protected float timeAlive = 0.01f; // prevent division by zero
	
	protected ObjectMap<String, Float> dropTypes;
	@MapObjectState
	protected boolean droppedItems;
	@MapObjectState
	protected boolean defeated;
	
	private EnemyCharacterMap gameMap;
	private Runnable onRemoveFromMap;
	
	protected PlayableCharacter nearPlayer; // will be set if the player is in reach of the enemy's sensor (set to null if not)
	
	public Enemy(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(properties);
		this.typeConfig = typeConfig;
		
		healthBarRenderer = new EnemyHealthBarRenderer();
		if (typeConfig.usesTimerBar) {
			statsBarRenderer = new EnemyTimerBarRenderer();
		}
		
		readTypeConfig();
		readMapProperties();
		createPhysicsBodyProperties();
		initializeAttackHandler();
		initializeStates();
		initializeMovingState();
		initializeIdleState();
		
		createAI();
		ai.setCharacter(this);
		
		setImageOffset(typeConfig.imageOffsetX, typeConfig.imageOffsetY);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	protected void readTypeConfig() {
		health = typeConfig.health;
		movingSpeed = typeConfig.movingSpeed;
	}
	
	private void readMapProperties() {
		dropTypes = ItemDropUtil.processMapProperties(properties, typeConfig.drops);
	}
	
	protected void createPhysicsBodyProperties() {
		physicsBodyProperties = new PhysicsBodyProperties() //
				.setType(BodyType.DynamicBody) //
				.setSensor(false) //
				.setCollisionType(PhysicsCollisionType.ENEMY) //
				.setDensity(10f) //
				.setLinearDamping(10f);
		if (!typeConfig.movable) {
			// increase the density to make the enemy immovable
			physicsBodyProperties.setDensity(100_000f);
		}
	}
	
	protected void initializeAttackHandler() {
		//the body is not yet created -> set a null body here and update it when it is created (see createPhysicsBody(...))
		attackHandler = new AttackHandler(typeConfig.attackConfig, null, PhysicsCollisionType.ENEMY_ATTACK);
	}
	
	private void initializeStates() {
		stateMachine = new CharacterStateMachine(typeConfig.stateConfig, typeConfig.initialState, attackHandler, properties);
	}
	
	/**
	 * Create the {@link ArtificialIntelligence} that controls this enemy. The default implementation creates an AI from the configuration file, that
	 * is referenced in the field typeConfig.aiConfig.
	 */
	protected void createAI() {
		createAiFromConfiguration(typeConfig.aiConfig);
	}
	
	public void setGameMap(EnemyCharacterMap gameMap) {
		this.gameMap = gameMap;
	}
	
	@Override
	public void createPhysicsBody(float x, float y) {
		super.createPhysicsBody(x, y);
		
		//add the body to the attack handler, because it needed to be initialised before the body was created
		attackHandler.setBody(body);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		return physicsBodyProperties.setRadius(typeConfig.bodyRadius).setWidth(typeConfig.bodyWidth).setHeight(typeConfig.bodyHeight).setPhysicsBodyShape(typeConfig.bodyShape);
	}
	
	@Override
	protected void addAdditionalPhysicsParts() {
		if (typeConfig.addSensor) {
			PhysicsUtil.addEnemySensor(body, typeConfig.sensorRadius);
		}
	}
	
	@Override
	protected CharacterTypeConfig getTypeConfig() {
		return typeConfig;
	}
	
	@Override
	public String getMapObjectId() {
		return StatefulMapObject.getMapObjectId(properties);
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {
		if (Boolean.parseBoolean(state.get("droppedItems")) && dropsSpecialItems()) {
			//don't drop special items twice, because special items will be saved and re-added to the map if they are not picked up
			droppedItems = true;
		}
		if (Boolean.parseBoolean(state.get("defeated"))) {
			defeated = true;
			removeFromMap();
		}
	}
	
	@Override
	public void act(float delta) {
		stateMachine.updateState(delta);
		attackHandler.handleAttacks(delta);
		timeAlive += delta;
		
		if (!isAlive()) {
			if (!droppedItems) {
				dropItems();
			}
			if (stateMachine.isInEndState()) {
				removeFromMap();
			}
		}
		else {
			if (!cutsceneHandler.isCutsceneActive()) {
				ai.clearMoves();
				ai.calculateMove(delta);
				ai.executeMove(delta);
			}
			
			pushAwayPlayer();
			
			if (typeConfig.timerInSeconds > 0 && timeAlive >= typeConfig.timerInSeconds) {
				spawnNextEnemy();
			}
		}
	}
	
	private void spawnNextEnemy() {
		if (typeConfig.spawnEnemyTypeAfterTimerExceeded != null) {
			// position needs to be collected before removing from the map (which will set the body to null)
			float x = body.getPosition().x * Constants.SCREEN_TO_WORLD;
			float y = body.getPosition().y * Constants.SCREEN_TO_WORLD;
			
			PhysicsWorld.getInstance().runAfterWorldStep(() -> {
				EnemyFactory.asInstance().createAndAddEnemy(typeConfig.spawnEnemyTypeAfterTimerExceeded, x, y, new MapProperties());
			});
			
			droppedItems = true; // don't drop items
			health = 0;
			die();
		}
	}
	
	@Override
	public void draw(float delta, SpriteBatch batch) {
		super.draw(delta, batch);
		
		attackHandler.renderAttacks(delta, batch);
	}
	
	private boolean isAlive() {
		return health > 0;
	}
	
	public void drawStatsBar(ShapeRenderer shapeRenderer) {
		if (drawStatsBar()) {
			AnimationSpriteConfig spriteConfig = getAnimation().getSpriteConfig();
			float width = spriteConfig.width * typeConfig.healthBarWidthFactor;
			float x = body.getPosition().x - spriteConfig.width * 0.5f * Constants.WORLD_TO_SCREEN // 
					+ (spriteConfig.width - width) / 2f * Constants.WORLD_TO_SCREEN // center the status bar
					+ typeConfig.healthBarOffsetX;
			float y = body.getPosition().y + spriteConfig.height * 0.5f * Constants.WORLD_TO_SCREEN + typeConfig.healthBarOffsetY;
			drawStatsBar(shapeRenderer, x, y, width);
		}
	}
	
	protected void drawStatsBar(ShapeRenderer shapeRenderer, float x, float y, float width) {
		healthBarRenderer.drawStatsBar(shapeRenderer, getPercentualHealth(), x, y, width);
		if (statsBarRenderer != null) {
			statsBarRenderer.drawStatsBar(shapeRenderer, getPercentualTimer(), x, y - 0.05f, width);
		}
	}
	
	protected boolean drawStatsBar() {
		return typeConfig.usesHealthBar && isAlive() && (getPercentualHealth() < 1 || typeConfig.healthBarAlwaysVisible);
	}
	
	public float getPercentualHealth() {
		return health / typeConfig.health;
	}
	
	private float getPercentualTimer() {
		// 1 if timeAlive is zero, 0 if time alive is >= typeConfig.timer
		return Math.max(1f - timeAlive / typeConfig.timerInSeconds, 0);
	}
	
	public void moveToDirection(float x, float y) {
		moveToDirection(new Vector2(x, y));
	}
	
	public void move(Vector2 delta) {
		intendedMovement = delta;
		
		float force = 10f * body.getMass();
		if (typeConfig.applyGroundPhysics) {
			force *= groundProperties.movementSpeedFactor;
		}
		
		body.applyForceToCenter(delta.x * force, delta.y * force, true);
	}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		if (!typeConfig.takesDamage) {
			stateMachine.setState(getDamageStateName(damage));
			return;
		}
		
		if (!typeConfig.takesDamageFromProjectiles && attackInfo.getAttackType().isSubTypeOf(AttackType.PROJECTILE) //
				|| !typeConfig.takesDamageInBlockingState && isInBlockingState()) {
			if (typeConfig.soundWhenAttackBlocked != null) {
				SOUND_SET.playSound(typeConfig.soundWhenAttackBlocked);
			}
			
			return;
		}
		
		if (isAlive()) {
			float modifiedDamage = modifyDamage(damage, attackInfo.getAttackType());
			
			health -= modifiedDamage;
			
			if (!isAlive()) {
				die();
			}
			else {
				stateMachine.setState(getDamageStateName(modifiedDamage));
			}
		}
	}
	
	private boolean isInBlockingState() {
		return typeConfig.blockingStateName != null && stateMachine.isInState(typeConfig.blockingStateName);
	}
	
	private float modifyDamage(float damage, AttackType attackType) {
		// search for the damage modifier of this attack type or the first super type that is found
		Float modifier = null;
		while (modifier == null && attackType != null) {
			modifier = typeConfig.damageModifiersByAttackType.get(attackType.name());
			attackType = attackType.getSuperType();
		}
		
		return damage * (modifier == null ? 1f : modifier.floatValue());
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected) {
		if (hasBody() && isAlive()) {
			Vector2 pushDirection = getPushDirection(getPosition(), hitCenter);
			//enemies define the force to get pushed themselves (the player's attack is multiplied to this self defined force as a factor)
			force *= typeConfig.pushForceDamage * 10f * body.getMass();
			body.applyForceToCenter(pushDirection.x * force, pushDirection.y * force, true);
		}
	}
	
	private boolean hasBody() {
		return body != null;
	}
	
	/**
	 * Returns the name of the state that shows the enemy taking damage. Override this method if the state is not named "damage".
	 */
	protected String getDamageStateName(float damage) {
		return "damage";
	}
	
	protected void die() {
		stateMachine.setState(getDieStateName());
		
		defeated = true;
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
		
		fireEnemyDefeatedEvent();
		
		changeBodyToSensor();
	}
	
	protected void fireEnemyDefeatedEvent() {
		if (properties.containsKey(MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT)) {
			String enemyDefeatedEventText = properties.get(MAP_PROPERTIES_KEY_ENEMY_DEFEATED_EVENT_TEXT, "", String.class);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ENEMY_DEFEATED).setStringValue(enemyDefeatedEventText));
		}
	}
	
	/**
	 * Returns the name of the state that shows the enemy dying. Override this method if the state is not named "die".
	 */
	protected String getDieStateName() {
		return "die";
	}
	
	protected void dropItems() {
		float x = (body.getPosition().x + typeConfig.dropPositionOffsetX) * Constants.SCREEN_TO_WORLD;
		float y = (body.getPosition().y + typeConfig.dropPositionOffsetY) * Constants.SCREEN_TO_WORLD;
		
		if (dropsSpecialItems()) {
			String specialDropType = properties.get(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE, String.class);
			String specialDropMapProperties = properties.get(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_MAP_PROPERTIES, String.class);
			MapProperties mapProperties = MapUtil.createMapPropertiesFromString(specialDropMapProperties);
			ItemDropUtil.dropItem(specialDropType, mapProperties, x, y, typeConfig.renderDropsAboveObject);
		}
		else {
			ItemDropUtil.dropItems(dropTypes, x, y, typeConfig.renderDropsAboveObject);
		}
		
		droppedItems = true;
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
	}
	
	private boolean dropsSpecialItems() {
		return properties.containsKey(Constants.MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE);
	}
	
	@Override
	public void removeFromMap() {
		ai.characterRemovedFromMap();
		gameMap.removeEnemy(this, body);
		PhysicsWorld.getInstance().removeContactListener(this);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
		
		EventHandler.getInstance().removeEventListener(this);
		
		if (onRemoveFromMap != null) {
			onRemoveFromMap.run();
		}
	}
	
	public void setOnRemoveFromMap(Runnable onRemoveFromMap) {
		this.onRemoveFromMap = onRemoveFromMap;
	}
	
	public void playMapBackgroundMusicAfterBossDefeated() {
		// stop the boss music
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.STOP_BACKGROUND_MUSIC) //
				.setBooleanValue(true)); // fade out
		
		// clear the queue (though it should be empty) because the "fade out" parameter of the 
		// last event will otherwise start the next music in the queue
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CLEAR_BACKGROUND_MUSIC_QUEUE));
		
		// add to queue is needed because of the fade out of the previous music
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.ADD_MAP_BACKGROUND_MUSIC_TO_QUEUE) //
				.setFloatValue(3f) // delay in seconds
				.setBooleanValue(true)); // fade in
	}
	
	@Override
	public void beginContact(Contact contact) {
		super.beginContact(contact);
		attackHandler.handleAttackDamage(contact);
		findNearPlayer(contact);
	}
	
	@Override
	public void endContact(Contact contact) {
		super.endContact(contact);
		looseNearPlayer(contact);
	}
	
	private void findNearPlayer(Contact contact) {
		PlayableCharacter player = CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.ENEMY_SENSOR, contact, PlayableCharacter.class);
		if (player != null) {
			nearPlayer = player;
		}
	}
	
	private void looseNearPlayer(Contact contact) {
		PlayableCharacter player = CollisionUtil.getObjectCollidingWith(this, PhysicsCollisionType.ENEMY_SENSOR, contact, PlayableCharacter.class);
		// the collision type is checked too, because the player attack also has the player as user data
		PhysicsCollisionType collisionType = CollisionUtil.getCollisionTypeOfObjectCollidingWith(this, PhysicsCollisionType.ENEMY_SENSOR, contact);
		
		if (player == nearPlayer && collisionType == PhysicsCollisionType.PLAYER) {
			nearPlayer = null;
		}
	}
	
	protected void pushAwayPlayer() {
		if (nearPlayer != null && typeConfig.useSensorAsForceField) {
			float force = 8f; // just enough to push the player away if he's running into the enemy
			float forceWhenBlocked = typeConfig.ignoreForceFieldWhenBlocking ? 0 : force;
			nearPlayer.pushByHit(getPosition(), force, forceWhenBlocked, false);
		}
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.BOSS_ENEMY_SHOW && typeConfig.isBoss) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.BOSS_ENEMY_APPEARED) //
					.setParameterObject(this) //
					.setStringValue(typeConfig.bossName));
			
			if (typeConfig.playBossAppearedSound) {
				SOUND_SET.playSound("boss_appeared");
			}
		}
		
		if (event.eventType == EventType.ENEMY_TAKE_DAMAGE && event.stringValue.equals(getUnitId())) {
			takeDamage(event.floatValue, AttackInfo.dummy());
		}
		
		if (isAlive() && event.equalsIgnoringDefaultConfigValues(typeConfig.deathEvent) //
				&& (getUnitId() == null || getUnitId().equals(event.stringValue))) { // if unitId is set it has to match the event's stringValue
			health = 0;
			die();
		}
	}
}
