package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.attack.AttackInfo;
import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.camera.CameraMovementHandler;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.data.handler.CharacterItemDataHandler;
import net.jfabricationgames.gdx.data.handler.CharacterPropertiesDataHandler;
import net.jfabricationgames.gdx.data.handler.FastTravelDataHandler;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.handler.type.DataItemAmmoType;
import net.jfabricationgames.gdx.data.properties.FastTravelPointProperties;
import net.jfabricationgames.gdx.data.state.BeforePersistState;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.item.SpecialAction;
import net.jfabricationgames.gdx.object.event.EventObject;
import net.jfabricationgames.gdx.object.moveable.DraggableObject;
import net.jfabricationgames.gdx.physics.BeforeWorldStep;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.projectile.Hookshot;
import net.jfabricationgames.gdx.projectile.MagicWave;
import net.jfabricationgames.gdx.projectile.Projectile;
import net.jfabricationgames.gdx.projectile.ProjectileReflector;
import net.jfabricationgames.gdx.rune.RuneType;
import net.jfabricationgames.gdx.skill.Difficulty;
import net.jfabricationgames.gdx.skill.PlayerAttackHandler;
import net.jfabricationgames.gdx.skill.WeaponSkill;
import net.jfabricationgames.gdx.skill.WeaponSkillType;
import net.jfabricationgames.gdx.sound.SoundHandler;
import net.jfabricationgames.gdx.state.GameStateManager;
import net.jfabricationgames.gdx.util.AnnotationUtil;
import net.jfabricationgames.gdx.util.GameUtil;

public class Dwarf implements PlayableCharacter, Disposable, ContactListener, EventListener, ProjectileReflector {
	
	public static final float FREEZING_TIME_IN_SECONDS = 5f;
	public static final float HEAT_DAMAGE_TIME_IN_SECONDS = 5f;
	
	private static final float MOVING_SPEED_CUTSCENE = 3.5f;
	
	private static final float TIME_TILL_GAME_OVER_MENU = 3f;
	private static final float LOW_MANA_LEVEL = 15f;
	
	private static final float MIN_ENDURANCE_TO_START_BLOCK = 15f;
	private static final float MIN_ENDURANCE_TO_START_SPRINT = 15f;
	private static final float ENDURANCE_LOSS_FOR_BLOCKING_DAMAGE = 10f;
	
	private static final String ATTACK_CONFIG_FILE_NAME = "config/dwarf/attacks.json";
	
	private static final String SOUND_AMMO_EMPTY = "ammo_empty";
	private static final String SOUND_REFLECT_PROJECTILE = "reflect_projectile";
	private static final String SOUND_SELL_OR_BUY_ITEM = "sell_buy_item";
	private static final String SOUND_ROPE = "rope";
	private static final String SOUND_ICE_PICK = "ice_pick";
	private static final String ATTACK_NAME_WAIT = "wait";
	private static final String ATTACK_NAME_REFLECT_MAGIC_WAVE = "reflected_magic_wave";
	private static final String RUNE_KENAZ_ANIMATION_NAME = "rune_kenaz";
	
	private static final String GLOBAL_VALUE_KEY_ASAMNIR_STOLEN = "loa2_main__asamnir_stolen";
	private static final String GLOBAL_VALUE_KEY_SPARE_WEAPON_GAINED = "loa2_main__spare_weapon_gained";
	
	protected PlayerAttackHandler attackHandler;
	
	protected WeaponSkill weaponSkill;
	protected Difficulty difficulty;
	
	protected CharacterAction action;
	protected SpecialAction activeSpecialAction;
	private SoundHandler actionSound;
	
	protected CharacterInputProcessor inputProcessor;
	
	protected CharacterPropertiesDataHandler propertiesDataHandler;
	protected CharacterItemDataHandler itemDataHandler;
	protected FastTravelDataHandler fastTravelDataHandler;
	
	protected CharacterBodyHandler bodyHandler;
	protected CharacterRenderer renderer;
	protected CharacterSoundHandler soundHandler;
	
	private DraggableObject draggableObject;
	
	private float freezingTimer; // if > 0 the player is frozen and movement is slowed down
	private float heatDamageTimer; // if > 0 the player is damaged by heat (from the map) so the character turns red
	private float invertedControlsTimer; // if > 0 the player has inverted controls (from a spell attack)
	private boolean hookshotActive = false; // the player can't move while the hookshot is active
	private Hookshot activeHookshot;
	
	public Dwarf() {
		propertiesDataHandler = CharacterPropertiesDataHandler.getInstance();
		itemDataHandler = CharacterItemDataHandler.getInstance();
		fastTravelDataHandler = FastTravelDataHandler.getInstance();
		
		action = CharacterAction.NONE;
		activeSpecialAction = SpecialAction.BOW;
		renderer = new CharacterRenderer(this);
		bodyHandler = new CharacterBodyHandler(this);
		soundHandler = new CharacterSoundHandler();
		
		PhysicsWorld.getInstance().registerContactListener(this);
		
		weaponSkill = WeaponSkill.loadWeaponSkillFromConfig();
		difficulty = Difficulty.loadDifficultyConfig();
		
		attackHandler = new PlayerAttackHandler(ATTACK_CONFIG_FILE_NAME, bodyHandler.body, PhysicsCollisionType.PLAYER_ATTACK, weaponSkill, difficulty);
		inputProcessor = new CharacterInputProcessor(this);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void reAddToWorld() {
		bodyHandler.createPhysicsBody();
		attackHandler = new PlayerAttackHandler(ATTACK_CONFIG_FILE_NAME, bodyHandler.body, PhysicsCollisionType.PLAYER_ATTACK, weaponSkill, difficulty);
	}
	
	@Override
	public void afterLoadMap() {
		// fire events if stats are below half when re-added to the world, because they can't go below half anymore in this state
		checkManaNotFull(true);
		checkHealthNotFull(false);
		checkHealthBelowHalf(false);
		checkArmorNotFull(false);
		checkArmorBelowHalf(false);
		checkManaBelowHalf(false);
		checkAmmoBelowHalf(ItemAmmoType.ARROW, false);
		checkAmmoBelowHalf(ItemAmmoType.BOMB, false);
	}
	
	@Override
	public void beforeChangeToGameScreen() {
		// the game difficulty could have been changed
		attackHandler.reloadAttackConfigAfterSkillChangeOrGameDifficultyChange();
	}
	
	protected boolean changeAction(CharacterAction action) {
		if (isAlive() || action == CharacterAction.DIE) {
			if (!propertiesDataHandler.hasEnoughEndurance(action) && action != CharacterAction.ATTACK_SPIN) {
				// spin attacks can be executed without endurance, because holding them charged costs endurance
				return false;
			}
			if ((action == CharacterAction.BLOCK || action == CharacterAction.SHIELD_HIT) && !propertiesDataHandler.hasBlock()) {
				return false;
			}
			
			this.action = action;
			renderer.changeAnimation();
			
			propertiesDataHandler.reduceEnduranceForAction(action);
			
			if (actionSound != null && !actionSound.isSoundStoped()) {
				actionSound.stop(); // stop the previous action sound if it is still playing
			}
			actionSound = soundHandler.playSound(action);
			
			if (action == CharacterAction.BLOCK || action == CharacterAction.BLOCK_MOVE) {
				// abort attacks that are still in progress
				attackHandler.abortAllAttacks();
			}
			
			if (action.isAttack()) {
				attackHandler.startAttack(action.getAttack(), inputProcessor.getMovingDirection().getNormalizedDirectionVector());
			}
			
			return true;
		}
		return false;
	}
	
	protected boolean jump() {
		if (propertiesDataHandler.hasEnoughEndurance(CharacterAction.JUMP.getEnduranceCosts()) //
				&& hasWeapon()) { // the jump animation has a weapon, so it should not be executed without a weapon
			return changeAction(CharacterAction.JUMP);
		}
		
		return false;
	}
	
	protected boolean executeSpecialAction() {
		if (activeSpecialAction != null) {
			switch (activeSpecialAction) {
				case BOW:
				case BOMB:
					ItemAmmoType ammoType;
					if (activeSpecialAction == SpecialAction.BOW) {
						ammoType = ItemAmmoType.getByName("ARROW");
					}
					else {
						ammoType = ItemAmmoType.getByName(activeSpecialAction.name());
					}
					if (attackHandler.allAttacksExecuted()) {
						if (itemDataHandler.hasAmmo(ammoType.toDataType())) {
							if (propertiesDataHandler.hasEnoughEndurance(activeSpecialAction.enduranceCost)) {
								boolean ammoBelowHalfBeforeShot = itemDataHandler.getAmmo(DataItemAmmoType.getByItemAmmoType(ammoType)) < 0.5f * itemDataHandler.getMaxAmmo(DataItemAmmoType.getByItemAmmoType(ammoType));
								itemDataHandler.decreaseAmmo(ammoType.toDataType());
								checkAmmoBelowHalf(ammoType, ammoBelowHalfBeforeShot);
								propertiesDataHandler.reduceEndurance(activeSpecialAction.enduranceCost);
								attackHandler.startAttack(ammoType.name().toLowerCase(), inputProcessor.getMovingDirection().getNormalizedDirectionVector());
								
								if (!itemDataHandler.hasAmmo(ammoType.toDataType())) {
									fireOutOfAmmoEvent(ammoType);
								}
							}
						}
						else {
							soundHandler.playSound(SOUND_AMMO_EMPTY);
						}
						
						return true;
					}
					break;
				case BOOMERANG:
				case WAND:
				case HOOKSHOT:
					if (hookshotActive && activeHookshot != null && !activeHookshot.attachedToTarget() && activeSpecialAction == SpecialAction.HOOKSHOT) {
						// let the active hookshot return to the player if the hookshot action is executed again
						activeHookshot.removeAfterMovedBackToPlayer(); // remove the hookshot directly
						return true;
					}
					
					if (hasEnoughMana(activeSpecialAction) //
							&& propertiesDataHandler.hasEnoughEndurance(activeSpecialAction.enduranceCost) // 
							&& attackHandler.allAttacksExecuted()) {
						useMana(activeSpecialAction);
						propertiesDataHandler.reduceEndurance(activeSpecialAction.enduranceCost);
						attackHandler.startAttack(activeSpecialAction.name().toLowerCase(), inputProcessor.getMovingDirection().getNormalizedDirectionVector());
						
						if (activeSpecialAction == SpecialAction.HOOKSHOT) {
							hookshotActive = true; // will be deactivated by an event, that is fired from the hookshot
							inputProcessor.setActionCooldownInPercent(70f); // the action cooldown is shorter for the hookshot, so it can be aborted faster
						}
						
						return true;
					}
					break;
				case LANTERN:
					if (hasEnoughMana(activeSpecialAction) && //
							propertiesDataHandler.hasEnoughEndurance(activeSpecialAction.enduranceCost) // 
							&& attackHandler.allAttacksExecuted()) {
						useMana(activeSpecialAction);
						propertiesDataHandler.reduceEndurance(activeSpecialAction.enduranceCost);
						renderer.startDarknessFade();
						
						//handle the lantern as attack to have a duration (so it's not executed in every game step)
						delayAttacks();
					}
					break;
				case ROPE:
					if (draggableObject != null) {
						soundHandler.playSound(SOUND_ROPE);
						draggableObject.toggleDrag(bodyHandler.body);
						inputProcessor.setActionCooldownInPercent(50f); // the rope needs a faster cooldown to be used to change directions on ice
						return true;
					}
					break;
				case ICE_PICK:
					soundHandler.playSound(SOUND_ICE_PICK);
					bodyHandler.stopMovement();
					return true;
				case FEATHER:
					//do nothing here - the action will be executed in InteractiveAction.SHOW_OR_CHANGE_TEXT
					break;
				case COMPASS:
					//do nothing here - the compass is shown when it's selected as active action, but cannot be activated
					break;
				default:
					throw new IllegalStateException("Unexpected SpecialAction: " + activeSpecialAction);
			}
		}
		
		return false;
	}
	
	private void checkAmmoBelowHalf(ItemAmmoType ammoType, boolean ammoBelowHalfBeforeShot) {
		if (!ammoBelowHalfBeforeShot && itemDataHandler.getAmmo(DataItemAmmoType.getByItemAmmoType(ammoType)) < 0.5f * itemDataHandler.getMaxAmmo(DataItemAmmoType.getByItemAmmoType(ammoType))) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue(ammoType.name()) //
					.setFloatValue(0.5f));
		}
	}
	
	private boolean hasEnoughMana(SpecialAction action) {
		return propertiesDataHandler.hasEnoughMana(getManaCosts(action));
	}
	
	private void useMana(SpecialAction action) {
		useMana(getManaCosts(action));
	}
	
	private void useMana(float mana) {
		boolean manaAboveCriticalLevelBeforeUse = propertiesDataHandler.hasEnoughMana(LOW_MANA_LEVEL);
		boolean manaFullBeforeUse = propertiesDataHandler.getManaPlusIncreasePercentual() >= 1f - 1e-3f; // prevent rounding errors
		boolean manaBelowHalfBeforeUse = propertiesDataHandler.getManaPlusIncreasePercentual() < 0.5f - 1e-3f; // prevent rounding errors
		propertiesDataHandler.reduceMana(mana);
		if (manaAboveCriticalLevelBeforeUse && !propertiesDataHandler.hasEnoughMana(LOW_MANA_LEVEL)) {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.OUT_OF_AMMO).setStringValue("MANA"));
		}
		checkManaNotFull(manaFullBeforeUse);
		checkManaBelowHalf(manaBelowHalfBeforeUse);
	}
	
	private void checkManaNotFull(boolean manaFullBeforeUse) {
		if (manaFullBeforeUse && propertiesDataHandler.getManaPlusIncreasePercentual() < 1f) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("MANA") //
					.setFloatValue(1f));
		}
	}
	
	private void checkManaBelowHalf(boolean manaBelowHalfBeforeUse) {
		if (!manaBelowHalfBeforeUse && propertiesDataHandler.getManaPlusIncreasePercentual() < 0.5f) {
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("MANA") //
					.setFloatValue(0.5f));
		}
	}
	
	private float getManaCosts(SpecialAction action) {
		float manaCost = action.manaCost;
		switch (action) {
			case WAND:
				manaCost *= weaponSkill.getSkillLevelConfig(WeaponSkillType.WAND).manaCostInPercent / 100f;
				break;
			case BOOMERANG:
				manaCost *= weaponSkill.getSkillLevelConfig(WeaponSkillType.BOOMERANG).manaCostInPercent / 100f;
				break;
			default:
				break;
		}
		return manaCost;
	}
	
	private void fireOutOfAmmoEvent(ItemAmmoType ammoType) {
		EventConfig eventConfig = new EventConfig().setEventType(EventType.OUT_OF_AMMO).setStringValue(ammoType.name());
		EventHandler.getInstance().fireEvent(eventConfig);
	}
	
	private void delayAttacks() {
		attackHandler.startAttack(ATTACK_NAME_WAIT, inputProcessor.getMovingDirection().getNormalizedDirectionVector());
	}
	
	protected boolean isAnimationFinished() {
		return renderer.animation.isAnimationFinished();
	}
	
	protected boolean isBlocking() {
		return action == CharacterAction.BLOCK || action == CharacterAction.BLOCK_MOVE || action == CharacterAction.SHIELD_HIT;
	}
	
	@BeforeWorldStep
	public void resetGroundProperties() {
		bodyHandler.resetGroundProperties();
	}
	
	@BeforePersistState
	public void updatePositionToDataContainer() {
		propertiesDataHandler.setPlayerPosition(getPosition());
	}
	
	@Override
	public void centerCameraOnPlayer() {
		CameraMovementHandler.getInstance().centerCameraOnPlayer();
	}
	
	@Override
	public void resetAfterGameOver() {
		propertiesDataHandler.resetAllToNull();
		propertiesDataHandler.increaseHealthFull();
		propertiesDataHandler.increaseArmorByHalf();
		propertiesDataHandler.increaseManaFull();
		// endurance fills up automatically
	}
	
	@Override
	public Vector2 getPosition() {
		return bodyHandler.body.getPosition().cpy();
	}
	
	@Override
	public void setPosition(float x, float y) {
		bodyHandler.body.setTransform(x, y, 0);
		propertiesDataHandler.setRespawnPoint(new Vector2(x, y));
	}
	
	@Override
	public int getAmmo(String ammoType) {
		return itemDataHandler.getAmmo(DataItemAmmoType.getByNameIgnoreCase(ammoType));
	}
	
	@Override
	public int getTokens(String tokenName) {
		return itemDataHandler.getTokens(tokenName);
	}
	
	@Override
	public void process(float delta) {
		updateAction(delta);
		propertiesDataHandler.updateStats(delta, action);
		attackHandler.handleAttacks(delta);
		
		inputProcessor.handleInputs(delta);
		inputProcessor.move(delta);
		
		renderer.processDarknessFadingAnimation(delta);
		
		freezingTimer = Math.max(0, freezingTimer - delta);
		heatDamageTimer = Math.max(0, heatDamageTimer - delta);
		invertedControlsTimer = Math.max(0, invertedControlsTimer - delta);
	}
	
	private void updateAction(float delta) {
		renderer.animation.increaseStateTime(delta);
		if (action != CharacterAction.BLOCK) { // ending the block is done in the CharacterInputProcessor
			if (renderer.animation.isAnimationFinished()) {
				if (action == CharacterAction.SHIELD_HIT && hasEnoughEnduranceToBlock()) {
					changeAction(CharacterAction.BLOCK);
				}
				else if (action == CharacterAction.BLOCK_MOVE) {
					changeAction(CharacterAction.BLOCK);
				}
				else {
					changeAction(CharacterAction.NONE);
				}
			}
		}
	}
	
	public boolean hasEnoughEnduranceToBlock() {
		return propertiesDataHandler.hasEnoughEndurance(MIN_ENDURANCE_TO_START_BLOCK);
	}
	
	public boolean hasEnoughEnduranceToSprint() {
		return propertiesDataHandler.hasEnoughEndurance(MIN_ENDURANCE_TO_START_SPRINT);
	}
	
	@Override
	public void render(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		renderer.drawDwarf(batch);
		renderer.drawAimMarker(batch);
		if (activeSpecialAction == SpecialAction.COMPASS) {
			renderer.drawCompassMarker(shapeRenderer);
		}
		attackHandler.renderAttacks(delta, batch);
	}
	
	@Override
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		renderer.renderDarkness(batch, shapeRenderer);
	}
	
	public boolean canMove() {
		return !hookshotActive;
	}
	
	@Override
	public void moveTo(Vector2 position, float speedFactor) {
		Vector2 direction = position.cpy().sub(getPosition());
		direction.nor().scl(MOVING_SPEED_CUTSCENE * speedFactor);
		
		move(direction.x, direction.y);
		inputProcessor.setMovingDirection(direction);
	}
	
	protected void move(float deltaX, float deltaY) {
		bodyHandler.move(deltaX, deltaY);
	}
	
	@Override
	public void changeToMovingState() {
		if (action != CharacterAction.RUN) {
			changeAction(CharacterAction.RUN);
		}
	}
	
	@Override
	public void changeToIdleState() {
		if (action != CharacterAction.IDLE && hasWeapon()) { // the idle animation has a weapon
			changeAction(CharacterAction.IDLE);
		}
	}
	
	@Override
	public String getUnitId() {
		return CutsceneHandler.CONTROLLED_UNIT_ID_PLAYER;
	}
	
	@Override
	public float getHealth() {
		return propertiesDataHandler.getHealthPercentual();
	}
	
	@Override
	public boolean isAlive() {
		return propertiesDataHandler.isAlive();
	}
	
	@Override
	public float getMana() {
		return propertiesDataHandler.getManaPercentual();
	}
	
	@Override
	public float getEndurance() {
		return propertiesDataHandler.getEndurancePercentual();
	}
	
	@Override
	public boolean isEnduranceLow() {
		return !propertiesDataHandler.hasEnoughEndurance(MIN_ENDURANCE_TO_START_BLOCK);
	}
	
	@Override
	public boolean isActionInCooldown() {
		return inputProcessor.isActionInCooldown();
	}
	
	@Override
	public float getActionCooldownTimerInPercent() {
		return inputProcessor.getActionCooldownTimerInPercent();
	}
	
	@Override
	public float getArmor() {
		return propertiesDataHandler.getArmorPercentual();
	}
	
	@Override
	public int getCoins() {
		return propertiesDataHandler.getCoins();
	}
	
	@Override
	public int getCoinsForHud() {
		return propertiesDataHandler.getCoinsForHud();
	}
	
	@Override
	public int getNormalKeys() {
		return itemDataHandler.getNumNormalKeys();
	}
	
	@Override
	public int getMetalIngots() {
		return propertiesDataHandler.getMetalIngots();
	}
	
	@Override
	public String getActiveAction() {
		return activeSpecialAction.name();
	}
	
	@Override
	public Array<String> getActionList() {
		return SpecialAction.getNamesAsList();
	}
	
	@Override
	public SpecialAction getActiveSpecialAction() {
		return activeSpecialAction;
	}
	
	@Override
	public void setActiveSpecialAction(SpecialAction specialAction) {
		activeSpecialAction = specialAction;
	}
	
	@Override
	public boolean isSpecialActionFeatherSelected() {
		return activeSpecialAction == SpecialAction.FEATHER;
	}
	
	/**
	 * Checks whether asamnir was stolen and the player has not yet gained a spare axe.
	 */
	public boolean hasWeapon() {
		return !GlobalValuesDataHandler.getInstance().getAsBoolean(GLOBAL_VALUE_KEY_ASAMNIR_STOLEN) //
				|| GlobalValuesDataHandler.getInstance().getAsBoolean(GLOBAL_VALUE_KEY_SPARE_WEAPON_GAINED);
	}
	
	@Override
	public void setDraggableObject(DraggableObject draggableObject) {
		this.draggableObject = draggableObject;
	}
	
	@Override
	public void beginContact(Contact contact) {
		bodyHandler.beginContact(contact);
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		bodyHandler.preSolve(contact);
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void takeDamage(float damage, AttackInfo attackInfo) {
		damage = 0; // TODO remove after tests
		
		AttackType attackType = attackInfo.getAttackType();
		damage *= difficulty.getDifficultyConfig().damageFactor; // apply a factor for the game difficulty to the damage
		
		if (isAlive()) {
			if (isBlocking() && attackInfo.canBeBlocked()) {
				if (attackInfo.shieldDamagedWhenBlocked()) {
					takeArmorDamage(damage * 0.33f);
				}
				
				if (attackInfo.canBeBlockedCompletely()) {
					// the attack can be completely blocked without taking damage - no shield hit state or animation is needed
					soundHandler.playSound(CharacterAction.SHIELD_HIT);
					return;
				}
				
				damage *= (1f - (weaponSkill.getSkillLevelConfig(WeaponSkillType.SHIELD).blockRateInPercent / 100f));
				
				// loose endurance for blocking the attack
				propertiesDataHandler.reduceEndurance(ENDURANCE_LOSS_FOR_BLOCKING_DAMAGE);
			}
			
			if (attackType == AttackType.CONTINUOUS_MAP_DAMAGE) {
				// turns the player character red if he is damaged by heat (from map damage)
				heatDamageTimer = HEAT_DAMAGE_TIME_IN_SECONDS;
			}
			
			boolean healthNotFullBeforeHit = propertiesDataHandler.getHealthPlusIncreasePercentual() < 1f - 1e-3f; // prevent rounding errors
			boolean healthBelowHalfBeforeHit = propertiesDataHandler.getHealthPlusIncreasePercentual() < 0.5f - 1e-3f; // prevent rounding errors
			propertiesDataHandler.takeDamage(damage);
			checkHealthBelowHalf(healthBelowHalfBeforeHit);
			checkHealthNotFull(healthNotFullBeforeHit);
			
			if (attackType == AttackType.SPELL) {
				handleSpellAttackHit(attackInfo);
			}
			
			if (!propertiesDataHandler.isAlive()) {
				die();
			}
			else {
				if (attackType == AttackType.LASER_BLASTER_BEAM) {
					return; // the laser blaster beam deals continuous damage and should not trigger a hit animation every time
				}
				
				if (isBlocking() && attackInfo.canBeBlocked()) {
					changeAction(CharacterAction.SHIELD_HIT);
				}
				else {
					changeAction(CharacterAction.HIT);
				}
			}
		}
	}
	
	private void checkHealthNotFull(boolean healthNotFullBeforeHit) {
		if (!healthNotFullBeforeHit && propertiesDataHandler.getHealthPlusIncreasePercentual() < 1f - 1e-3f) { // prevent rounding errors
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("HEALTH") //
					.setFloatValue(1f));
		}
	}
	
	private void checkHealthBelowHalf(boolean healthBelowHalfBeforeHit) {
		if (!healthBelowHalfBeforeHit && propertiesDataHandler.getHealthPlusIncreasePercentual() < 0.5f - 1e-3f) { // prevent rounding errors
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("HEALTH") //
					.setFloatValue(0.5f));
		}
	}
	
	private void takeArmorDamage(float damage) {
		propertiesDataHandler.reduceEnduranceForHitBlocking();
		boolean armorNotFullBeforeHit = propertiesDataHandler.getArmorPlusIncreasePercentual() < 1f - 1e-3f;
		boolean armorBelowHalfBeforeHit = propertiesDataHandler.getArmorPlusIncreasePercentual() < 0.5f - 1e-3f;
		propertiesDataHandler.takeArmorDamage(damage);
		checkArmorNotFull(armorNotFullBeforeHit);
		checkArmorBelowHalf(armorBelowHalfBeforeHit);
	}
	
	private void checkArmorNotFull(boolean armorNotFullBeforeHit) {
		if (!armorNotFullBeforeHit && propertiesDataHandler.getArmorPlusIncreasePercentual() < 1f - 1e-3f) { // prevent rounding errors
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("ARMOR") //
					.setFloatValue(1f));
		}
	}
	
	private void checkArmorBelowHalf(boolean armorBelowHalfBeforeHit) {
		if (!armorBelowHalfBeforeHit && propertiesDataHandler.getArmorPlusIncreasePercentual() < 0.5f - 1e-3f) { // prevent rounding errors
			EventHandler.getInstance().fireEvent(new EventConfig() //
					.setEventType(EventType.PLAYER_STATS_BELOW_THRESHOLD) //
					.setStringValue("ARMOR") //
					.setFloatValue(0.5f));
		}
	}
	
	private void handleSpellAttackHit(AttackInfo attackInfo) {
		if ("invert_controls".equals(attackInfo.getProperties().get("spellType"))) {
			invertedControlsTimer = Float.parseFloat(attackInfo.getProperties().get("spellDuration", "10f"));
		}
	}
	
	private void die() {
		propertiesDataHandler.resetIncreaseHealth(); // prevent reviving because the health still gets increased
		
		if (resurectionRuneCollectedAndForged()) {
			propertiesDataHandler.increaseHealthByHalf();
			GlobalValuesDataHandler.getInstance().put(RuneType.GLOBAL_VALUE_KEY_RUNE_KENAZ_FORGED, false);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.RUNE_USED).setStringValue(RUNE_KENAZ_ANIMATION_NAME));
			soundHandler.playSound("rune_kenaz_used");
		}
		else {
			soundHandler.playSound(CharacterAction.HIT);
			changeAction(CharacterAction.DIE);
			stopBackgroundMusic();
			GameUtil.runDelayed(() -> GameStateManager.getInstance().setGameOver(true), TIME_TILL_GAME_OVER_MENU);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.PLAYER_DIED));
		}
	}
	
	private void stopBackgroundMusic() {
		// stop the current music
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.STOP_BACKGROUND_MUSIC) //
				.setBooleanValue(true)); // fade out
		
		// clear the queue because the "fade out" parameter of the last event will otherwise start the next music in the queue
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CLEAR_BACKGROUND_MUSIC_QUEUE));
		
		// the game over music will be played by the GameScreen when the game over menu is shown
	}
	
	private boolean resurectionRuneCollectedAndForged() {
		GlobalValuesDataHandler globalValues = GlobalValuesDataHandler.getInstance();
		return RuneType.KENAZ.isCollected() && globalValues.getAsBoolean(RuneType.GLOBAL_VALUE_KEY_RUNE_KENAZ_FORGED);
	}
	
	@Override
	public boolean reflectProjectile(Projectile projectile) {
		if (projectile instanceof MagicWave) {
			if (isBlocking() && reflectionRuneCollected()) {
				Vector2 reflectedRotationVector = vectorFromAngle((projectile.getRotation() + 180f) % 360f);
				attackHandler.startAttack(ATTACK_NAME_REFLECT_MAGIC_WAVE, reflectedRotationVector);
				soundHandler.playSound(SOUND_REFLECT_PROJECTILE);
				return true;
			}
		}
		
		return false;
	}
	
	private Vector2 vectorFromAngle(float angleInDegrees) {
		return new Vector2((float) Math.cos(Math.toRadians(angleInDegrees)), (float) Math.sin(Math.toRadians(angleInDegrees)));
	}
	
	private boolean reflectionRuneCollected() {
		return RuneType.MANNAZ.isCollected();
	}
	
	@Override
	public void removeFromMap() {
		PhysicsWorld.getInstance().removeBodyWhenPossible(bodyHandler.body);
	}
	
	@Override
	public void pushByHit(Vector2 hitCenter, float force, float forceWhenBlocked, boolean blockAffected) {
		bodyHandler.pushByHit(hitCenter, force, forceWhenBlocked, blockAffected);
	}
	
	@Override
	public void freeze() {
		freezingTimer = FREEZING_TIME_IN_SECONDS;
	}
	
	protected float getFreezingTimer() {
		return freezingTimer;
	}
	
	protected boolean isFrozen() {
		return freezingTimer > 0;
	}
	
	protected float getHeatDamageTimer() {
		return heatDamageTimer;
	}
	
	protected boolean isDamagedByHeat() {
		return heatDamageTimer > 0;
	}
	
	protected boolean hasInvertedControls() {
		return invertedControlsTimer > 0;
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		float change;
		switch (event.eventType) {
			case PLAYER_RESPAWNED:
				// increase the health, armor and mana to minimum values if they are below the minimum values
				propertiesDataHandler.increaseStatsToMinimumAfterRespawn();
				break;
			case EVENT_OBJECT_TOUCHED:
				if (event.stringValue.equals(EventObject.EVENT_KEY_RESPAWN_CHECKPOINT)) {
					if (event.parameterObject != null && event.parameterObject instanceof EventObject) {
						EventObject respawnObject = (EventObject) event.parameterObject;
						propertiesDataHandler.setRespawnPoint(respawnObject.getEventObjectCenterPosition());
						propertiesDataHandler.setRespawnWithStartingStats(event.booleanValue);
					}
					GameStateManager.fireQuickSaveEvent();
				}
				break;
			case TAKE_PLAYERS_COINS:
				propertiesDataHandler.reduceCoins(event.intValue);
				break;
			case GIVE_COINS_TO_PLAYER:
				soundHandler.playSound(SOUND_SELL_OR_BUY_ITEM);
				propertiesDataHandler.increaseCoins(event.intValue);
				break;
			case TAKE_PLAYERS_METAL_INGOTS:
				propertiesDataHandler.reduceMetalIngots(event.intValue);
				break;
			case GIVE_METAL_INGOTS_TO_PLAYER:
				soundHandler.playSound(SOUND_SELL_OR_BUY_ITEM);
				propertiesDataHandler.increaseMetalIngots(event.intValue);
				break;
			case PLAYER_BUY_ITEM:
				Item item = ItemFactory.createItem(event.stringValue, 0f, 0f, new MapProperties());
				itemDataHandler.collectItem(item, true);
				break;
			case FAST_TRAVEL_TO_MAP_POSITION:
				FastTravelPointProperties fastTravelTargetPoint = fastTravelDataHandler.getFastTravelPropertiesById(event.stringValue);
				if (fastTravelTargetPoint.enabled) {
					setPosition(fastTravelTargetPoint.positionOnMapX, fastTravelTargetPoint.positionOnMapY);
				}
				break;
			case GIVE_ITEM_TO_PLAYER:
				// coordinates of 0,0 would be directly on the player, so we use -100,-100 to avoid the items from being picked up by the player again
				Item createdItem = ItemFactory.createItem(event.stringValue, -100f, -100f, new MapProperties());
				// the boolean value might suppress the sound when picking up the item (see GlobalEventExecutionType.RECEIVE_ITEM)
				itemDataHandler.collectItem(createdItem, !event.booleanValue);
				break;
			case SET_ITEM:
				String itemId = event.stringValue;
				itemDataHandler.addSpecialItem(itemId);
				break;
			case CHANGE_HEALTH:
				change = event.floatValue;
				if (change > 0) {
					propertiesDataHandler.increaseHealth(change);
				}
				else if (change < 0) {
					propertiesDataHandler.takeDamage(-change);
					if (!propertiesDataHandler.isAlive()) {
						die();
					}
				}
				break;
			case SET_HEALTH:
				propertiesDataHandler.setHealth(event.floatValue);
				break;
			case CHANGE_SHIELD:
				change = event.floatValue;
				if (change > 0) {
					propertiesDataHandler.increaseArmor(change);
				}
				else if (change < 0) {
					propertiesDataHandler.takeArmorDamage(-change);
				}
				break;
			case CHANGE_MANA:
				change = event.floatValue;
				if (change > 0) {
					propertiesDataHandler.increaseMana(change);
				}
				else if (change < 0) {
					useMana(-change);
				}
				break;
			case KILL_PLAYER:
				takeDamage(100, AttackInfo.dummy());
				break;
			case BEFORE_PERSIST_STATE:
				AnnotationUtil.executeAnnotatedMethods(BeforePersistState.class, this);
				break;
			case HOOKSHOT_ATTACK_STARTED:
				activeHookshot = (Hookshot) event.parameterObject;
				// connect to the hookshot by subscribing to its events
				event.parameterObject = bodyHandler.body;
				break;
			case HOOKSHOT_ATTACK_FINISHED:
				hookshotActive = false;
				activeHookshot = null;
				break;
			case SET_TOKENS_RELATIVE:
				int tokens = Math.max(0, propertiesDataHandler.getTokens(event.stringValue) + event.intValue);
				propertiesDataHandler.setTokens(event.stringValue, tokens);
				break;
			default:
				// do nothing, because this event type is not handled here
				break;
		}
	}
	
	@Override
	public void dispose() {
		soundHandler.dispose();
		PhysicsWorld.getInstance().removeContactListener(this);
		EventHandler.getInstance().removeEventListener(this);
	}
}
