package net.jfabricationgames.gdx.data.handler.type;

public enum DataItemPropertyKeys {
	
	HEALTH("health"), //
	MANA("mana"), //
	VALUE("value"), // 
	ARMOR("armor"), //
	AMMO("ammo"), // 
	AMMO_TYPE("ammoType"), //
	
	HEALTH_ABSOLUTE("healthAbsolute"), //
	ARMOR_ABSOLUTE("armorAbsolute"), //
	MANA_ABSOLUTE("manaAbsolute"), //
	AMMO_ABSOLUTE("ammoAbsolute"); // 
	
	private final String propertyName;
	
	private DataItemPropertyKeys(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
}
