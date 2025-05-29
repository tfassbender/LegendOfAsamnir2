package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.data.handler.type.DataItem;
import net.jfabricationgames.gdx.data.properties.KeyItemProperties;
import net.jfabricationgames.gdx.data.state.BeforeAddStatefulObject;
import net.jfabricationgames.gdx.data.state.MapObjectState;
import net.jfabricationgames.gdx.data.state.StatefulMapObject;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;
import net.jfabricationgames.gdx.util.MapUtil;
import net.jfabricationgames.gdx.util.SerializationUtil;

public class Item implements StatefulMapObject, CutsceneControlledUnit, DataItem {
	
	protected static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("item");
	
	protected static ItemTypeConfig defaultTypeConfig;
	
	protected AnimationDirector<TextureRegion> animation;
	protected Sprite sprite;
	protected MapProperties properties;
	protected Body body;
	
	protected ItemMap itemMap;
	protected ItemTextBox itemTextBox;
	
	@MapObjectState
	protected final String itemName;
	protected ItemTypeConfig typeConfig;
	protected String pickUpSoundName;
	
	@MapObjectState
	protected boolean picked;
	@MapObjectState
	private Vector2 position;
	@MapObjectState
	private String mapProperties;
	
	private Runnable onRemoveFromMap;
	
	public Item(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		this.itemName = itemName;
		this.typeConfig = typeConfig;
		this.sprite = sprite;
		this.animation = animation;
		this.properties = properties;
		
		if (animation == null && sprite == null) {
			Gdx.app.error(getClass().getSimpleName(), "Neither an animation nor a sprite was set for this item.");
		}
		
		if (sprite != null) {
			sprite.setScale(Constants.WORLD_TO_SCREEN * typeConfig.textureScale);
		}
		
		readTypeConfig();
	}
	
	protected void readTypeConfig() {
		pickUpSoundName = typeConfig.pickUpSound;
		if (pickUpSoundName == null && defaultTypeConfig != null) {
			pickUpSoundName = defaultTypeConfig.pickUpSound;
		}
		
		if (typeConfig.defaultMapProperties != null) {
			for (Entry<String, String> entry : typeConfig.defaultMapProperties.entries()) {
				if (!properties.containsKey(entry.key)) {
					properties.put(entry.key, entry.value);
				}
			}
		}
	}
	
	protected void createPhysicsBody(float x, float y) {
		PhysicsBodyProperties properties = new PhysicsBodyProperties() //
				.setType(BodyType.StaticBody) //
				.setX(x) //
				.setY(y) //
				.setSensor(false) //
				.setRadius(typeConfig.physicsObjectRadius) //
				.setCollisionType(PhysicsCollisionType.ITEM);
		body = PhysicsBodyCreator.createCircularBody(properties);
		body.setUserData(this);
	}
	
	public void setItemMap(ItemMap itemMap) {
		this.itemMap = itemMap;
	}
	
	public void setItemTextBox(ItemTextBox itemTextBox) {
		this.itemTextBox = itemTextBox;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
	public ItemType getType() {
		return typeConfig.type;
	}
	
	@BeforeAddStatefulObject
	public void updateMapProperties() {
		if (!isConfiguredInMap()) {
			mapProperties = SerializationUtil.serializeMapProperties(properties, false);
		}
	}
	
	@Override
	public String getMapObjectId() {
		if (!isConfiguredInMap()) {
			if (isSpecialItem()) {
				return getSpecialItemValue();
			}
			else if (itemName.equals("key")) {
				return "key_" + getStatefullObjectId();
			}
		}
		
		return StatefulMapObject.getMapObjectId(properties);
	}
	
	private Object getStatefullObjectId() {
		if (!properties.containsKey(StatefulMapObject.MAP_PROPERTIES_KEY_STATEFULL_OBJECT_ID)) {
			properties.put(StatefulMapObject.MAP_PROPERTIES_KEY_STATEFULL_OBJECT_ID, MapObjectDataHandler.getInstance().getUniqueObjectCount());
		}
		return properties.get(StatefulMapObject.MAP_PROPERTIES_KEY_STATEFULL_OBJECT_ID);
	}
	
	private boolean isSpecialItem() {
		return KeyItemProperties.isSpecialKey(getKeyProperties());
	}
	
	private String getSpecialItemValue() {
		if (!isSpecialItem()) {
			return null;
		}
		
		String specialKeyProperties = getSpecialKeyPropertiesAsString();
		return specialKeyProperties.replace("\\n", "_").replace(" ", "_").replace(":", "_");
	}
	
	@Override
	public boolean isConfiguredInMap() {
		return StatefulMapObject.getMapObjectId(properties) != null;
	}
	
	@Override
	public void applyState(ObjectMap<String, String> state) {
		if (Boolean.parseBoolean(state.get("picked"))) {
			picked = true;
			removeFromMap();
		}
	}
	
	public void draw(float delta, SpriteBatch batch) {
		if (animation != null) {
			animation.increaseStateTime(delta);
			animation.draw(batch);
		}
		else if (sprite != null) {
			sprite.draw(batch);
		}
	}
	
	@Override
	public boolean canBePicked() {
		return true;
	}
	
	@Override
	public void pickUp(boolean playSound) {
		picked = true;
		if (playSound) {
			playPickUpSound();
		}
		setGlobalValue();
		removeFromMap();
		
		MapObjectDataHandler.getInstance().addStatefulMapObject(this);
		
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ITEM_PICKED_UP).setStringValue(itemName));
	}
	
	private void playPickUpSound() {
		if (pickUpSoundName != null) {
			soundSet.playSound(pickUpSoundName);
		}
	}
	
	private void setGlobalValue() {
		if (typeConfig.globalValue != null) {
			GlobalValuesDataHandler.getInstance().put(typeConfig.globalValue, true);
		}
	}
	
	public void removeFromMap() {
		itemMap.removeItem(this, body);
		body = null;// set the body to null to avoid strange errors in native Box2D methods
		
		if (onRemoveFromMap != null) {
			onRemoveFromMap.run();
		}
	}
	
	public void setOnRemoveFromMap(Runnable onRemoveFromMap) {
		this.onRemoveFromMap = onRemoveFromMap;
	}
	
	public boolean isSpecialKey() {
		ObjectMap<String, String> keyProperties = getKeyProperties();
		if (keyProperties.size == 0 || (keyProperties.size == 1 && keyProperties.containsKey(KeyItemProperties.COMMON_REQUIRED_PROPERTY))) {
			return false;
		}
		
		return true;
	}
	
	public String getSpecialKeyPropertiesAsString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : getKeyProperties().entries()) {
			String propertyKey = entry.key.substring(KeyItemProperties.KEY_PROPERTY_PREFIX.length());
			String propertyValue = entry.value;
			sb.append(propertyKey).append(':').append(' ').append(propertyValue).append('\n');
		}
		return sb.substring(0, sb.length() - 1);
	}
	
	@Override
	public ObjectMap<String, String> getKeyProperties() {
		return KeyItemProperties.getKeyProperties(properties);
	}
	
	@Override
	public boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
	
	@Override
	public <T> T getProperty(String property, Class<T> clazz) {
		return properties.get(property, clazz);
	}
	
	@Override
	public String getItemName() {
		return itemName;
	}
	
	@Override
	public String getUnitId() {
		return properties.get(CutsceneControlledUnit.MAP_PROPERTIES_KEY_UNIT_ID, String.class);
	}
	
	@Override
	public String toString() {
		return "Item [name=" + itemName + "; properties=" + MapUtil.mapPropertiesToString(properties, true) + "]";
	}
}
