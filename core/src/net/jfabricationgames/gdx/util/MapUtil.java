package net.jfabricationgames.gdx.util;

import java.util.Optional;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class MapUtil {
	
	private MapUtil() {}
	
	public static String mapPropertiesToString(MapProperties properties, boolean includePosition) {
		return SerializationUtil.serializeMapProperties(properties, includePosition);
	}
	
	public static MapProperties createMapPropertiesFromString(String jsonConfig) {
		MapProperties mapProperties = new MapProperties();
		if (jsonConfig == null) {
			return mapProperties;
		}
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		ObjectMap<String, String> properties = json.fromJson(ObjectMap.class, String.class, jsonConfig);
		
		for (ObjectMap.Entry<String, String> property : properties) {
			mapProperties.put(property.key, property.value);
		}
		
		return mapProperties;
	}
	
	public static Optional<String> getMapPropertyConfigValue(MapProperties properties, String propertyKey) {
		String configMapProperty = properties.get(propertyKey, String.class);
		return Optional.ofNullable(configMapProperty);
	}
	
	public static Optional<Float> getMapPropertyConfigValueAsFloat(MapProperties properties, String propertyKey) {
		Optional<String> configMapProperty = getMapPropertyConfigValue(properties, propertyKey);
		if (configMapProperty.isPresent()) {
			try {
				return Optional.of(Float.parseFloat(configMapProperty.get()));
			}
			catch (NumberFormatException e) {
				throw new IllegalStateException("The map property '" + propertyKey + "' is not a valid float: '" + configMapProperty + "'", e);
			}
		}
		
		return Optional.empty();
	}
}
