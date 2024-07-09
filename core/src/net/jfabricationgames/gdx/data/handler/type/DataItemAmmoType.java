package net.jfabricationgames.gdx.data.handler.type;

import net.jfabricationgames.gdx.item.ItemAmmoType;

public enum DataItemAmmoType {
	
	ARROW, //
	BOMB; //
	
	public static DataItemAmmoType getByNameIgnoreCase(String name) {
		for (DataItemAmmoType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		throw new IllegalStateException("Unknown ItemAmmoType name: " + name);
	}
	
	public static DataItemAmmoType getByItemAmmoType(ItemAmmoType itemAmmoType) {
		switch (itemAmmoType) {
			case ARROW:
				return ARROW;
			case BOMB:
				return BOMB;
			default:
				throw new IllegalStateException("Unexpected ItemAmmoType: " + itemAmmoType);
		}
	}
}
