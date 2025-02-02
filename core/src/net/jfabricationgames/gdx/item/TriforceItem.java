package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventListenerInstance;
import net.jfabricationgames.gdx.event.EventType;

/**
 * A special item for the global side-quest to find all parts of the triforce.
 */
public class TriforceItem extends Item {
	
	public static enum TriforceItemType {
		
		TRIFORCE_SVARTALFHEIM_MAIN_NORTH(TriforceItemLocation.SVARTALFHEIM), //
		TRIFORCE_SVARTALFHEIM_MAIN_SOUTH(TriforceItemLocation.SVARTALFHEIM), //
		TRIFORCE_SVARTALFHEIM_DWARVEN_CAVE_MAIN(TriforceItemLocation.SVARTALFHEIM), //
		TRIFORCE_SVARTALFHEIM_DWARVEN_CAVE_CASTLE(TriforceItemLocation.SVARTALFHEIM), //
		
		TRIFORCE_NIFLHEIM_MAIN_CHICKEN_SIDE_QUEST(TriforceItemLocation.NIFLHEIM), //
		TRIFORCE_NIFLHEIM_ICE_FIELDS(TriforceItemLocation.NIFLHEIM), //
		
		TRIFORCE_MUSPELHEIM_DESERT(TriforceItemLocation.MUSPELHEIM), //
		
		TRIFORCE_ASGARD_CASTLE(TriforceItemLocation.ASGARD), //
		TRIFORCE_ASGARD_FOREST(TriforceItemLocation.ASGARD); //
		
		public static TriforceItemType getByContainingName(String name) {
			for (TriforceItemType type : values()) {
				if (name.toUpperCase().contains(type.name())) {
					return type;
				}
			}
			throw new IllegalStateException("No triforce item name contained in search string: " + name);
		}
		
		public final TriforceItemLocation location;
		
		public final String globalValueKeyCollected;
		public final String globalValueKeyDelivered;
		
		private TriforceItemType(TriforceItemLocation location) {
			this.location = location;
			globalValueKeyCollected = "triforce_" + name().toLowerCase() + "_collected";
			globalValueKeyDelivered = "triforce_" + name().toLowerCase() + "_delivered";
		}
		
		public boolean isCollectedOrDelivered() {
			return GlobalValuesDataHandler.getInstance().getAsBoolean(globalValueKeyCollected) //
					|| GlobalValuesDataHandler.getInstance().getAsBoolean(globalValueKeyDelivered);
		}
	}
	
	/**
	 * The location of the item. 
	 * Needed to be able to tell the player where to find the remaining triforce parts in the dialog with the elf in the dwarven village.
	 */
	public static enum TriforceItemLocation {
		
		SVARTALFHEIM, //
		NIFLHEIM, //
		MUSPELHEIM, //
		HELHEIM, //
		ASGARD; //
	
	}
	
	public static int getNumCarriedTriforcePieces() {
		int numCarried = 0;
		for (TriforceItemType type : TriforceItemType.values()) {
			if (GlobalValuesDataHandler.getInstance().getAsBoolean(type.globalValueKeyCollected)) {
				numCarried++;
			}
		}
		return numCarried;
	}
	
	public static int getNumDeliveredTriforcePieces() {
		int numDelivered = 0;
		for (TriforceItemType type : TriforceItemType.values()) {
			if (GlobalValuesDataHandler.getInstance().getAsBoolean(type.globalValueKeyDelivered)) {
				numDelivered++;
			}
		}
		return numDelivered;
	}
	
	public static void deliverCarriedTriforcePieces() {
		for (TriforceItemType type : TriforceItemType.values()) {
			if (GlobalValuesDataHandler.getInstance().getAsBoolean(type.globalValueKeyCollected)) {
				GlobalValuesDataHandler.getInstance().put(type.globalValueKeyDelivered, true);
				GlobalValuesDataHandler.getInstance().put(type.globalValueKeyCollected, false);
			}
		}
	}
	
	public static int getNumTriforcePiecesLeftInLocation(TriforceItemLocation location) {
		int piecesLeft = 0;
		for (TriforceItemType type : TriforceItemType.values()) {
			if (type.location == location //
					&& !GlobalValuesDataHandler.getInstance().getAsBoolean(type.globalValueKeyCollected) //
					&& !GlobalValuesDataHandler.getInstance().getAsBoolean(type.globalValueKeyDelivered)) {
				piecesLeft++;
			}
		}
		
		return piecesLeft;
	}
	
	/**
	 * Create an {@link EventListener} that only listens to the event of delivering the triforce pieces, so the method is called.
	 */
	static {
		new EventListenerInstance() {
			
			@Override
			public void handleEvent(EventConfig event) {
				if (event.eventType == EventType.DELIVER_TRIFORCE_PIECES) {
					deliverCarriedTriforcePieces();
				}
			}
		};
	}
	
	private TriforceItemType type;
	
	public TriforceItem(String itemName, ItemTypeConfig typeConfig, Sprite sprite, AnimationDirector<TextureRegion> animation, MapProperties properties) {
		super(itemName, typeConfig, sprite, animation, properties);
		type = TriforceItemType.getByContainingName(itemName);
		sprite.setScale(Constants.WORLD_TO_SCREEN * 0.75f); // scale the items down a bit
	}
	
	@Override
	public void pickUp(boolean playSound) {
		super.pickUp(playSound);
		GlobalValuesDataHandler.getInstance().put(type.globalValueKeyCollected, true);
	}
}
