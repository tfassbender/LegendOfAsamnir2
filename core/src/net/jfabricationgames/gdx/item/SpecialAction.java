package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;

public enum SpecialAction implements ItemSpecialAction {
	
	BOW(0, 0f, 25f, 1f, "special_action_available__bow", "special_action_quick_select_enabled__bow"), //
	BOMB(1, 0f, 35f, 1f, "special_action_available__bomb", "special_action_quick_select_enabled__bomb"), //
	BOOMERANG(2, 5f, 25f, 1f, "special_action_available__boomerang", "special_action_quick_select_enabled__boomerang"), //
	WAND(3, 10f, 25f, 1f, "special_action_available__wand", "special_action_quick_select_enabled__wand"), //
	FEATHER(4, 0f, 0f, 0.65f, "special_action_available__feather", "special_action_quick_select_enabled__feather"), //
	LANTERN(5, 10f, 0f, 0.65f, "special_action_available__lantern", "special_action_quick_select_enabled__lantern"), //
	ROPE(6, 0f, 25f, 0.2f, "special_action_available__rope", "special_action_quick_select_enabled__rope"), //
	ICE_PICK(7, 0f, 25f, 1f, "special_action_available__ice_pick", "special_action_quick_select_enabled__ice_pick"), //
	HOOKSHOT(8, 0f, 25f, 1f, "special_action_available__hookshot", "special_action_quick_select_enabled__hookshot");
	
	public static SpecialAction findByNameIgnoringCase(String specialAction) {
		for (SpecialAction action : values()) {
			if (action.name().equalsIgnoreCase(specialAction)) {
				return action;
			}
		}
		return null;
	}
	
	public static SpecialAction getByContainingName(String itemName) {
		for (SpecialAction action : values()) {
			if (itemName.toUpperCase().contains(action.name())) {
				return action;
			}
		}
		throw new IllegalStateException("No special action name contained in search string: " + itemName);
	}
	
	public static Array<String> getNamesAsList() {
		Array<String> names = new Array<>(values().length);
		
		for (SpecialAction action : values()) {
			names.add(action.name().toLowerCase());
		}
		
		return names;
	}
	
	public static SpecialAction getNextSpecialAction(SpecialAction activeSpecialAction, int delta) {
		int searchedIndex = (activeSpecialAction.indexInMenu + delta + values().length) % values().length;
		for (SpecialAction action : values()) {
			if (action.indexInMenu == searchedIndex) {
				return action;
			}
		}
		throw new IllegalStateException("No special action found.");
	}
	
	public static Array<SpecialAction> asList() {
		Array<SpecialAction> actions = new Array<>(values().length);
		
		for (SpecialAction action : values()) {
			actions.add(action);
		}
		
		return actions;
	}
	
	public final int indexInMenu;
	public final float manaCost;
	public final float enduranceCost;
	public final String actionAvailableGlobalValueKey;
	public final String actionQuickSelectEnabledGlobalValueKey;
	
	private final float textureScaleFactor;
	
	private SpecialAction(int indexInMenu, float manaCost, float enduranceCost, float textureScaleFactor, String actionAvailableGlobalValueKey, String quickSelectEnabledGlobalValueKey) {
		this.indexInMenu = indexInMenu;
		this.manaCost = manaCost;
		this.enduranceCost = enduranceCost;
		this.textureScaleFactor = textureScaleFactor;
		this.actionAvailableGlobalValueKey = actionAvailableGlobalValueKey;
		this.actionQuickSelectEnabledGlobalValueKey = quickSelectEnabledGlobalValueKey;
	}
	
	public boolean canBeUsed() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(actionAvailableGlobalValueKey);
	}
	
	public boolean isQuickSelectionEnabled() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(actionQuickSelectEnabledGlobalValueKey);
	}
	
	@Override
	public float getScaleFactor() {
		return textureScaleFactor;
	}
	
	@Override
	public String getActionAvailableGlobalValueKey() {
		return actionAvailableGlobalValueKey;
	}
}
