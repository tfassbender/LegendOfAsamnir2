package net.jfabricationgames.gdx.event;

import java.util.Objects;

public class EventConfig {
	
	public EventType eventType;
	public int intValue;
	public float floatValue;
	public boolean booleanValue;
	public String stringValue;
	
	public Object parameterObject;
	
	public EventConfig setEventType(EventType eventType) {
		this.eventType = eventType;
		return this;
	}
	
	public EventConfig setIntValue(int intValue) {
		this.intValue = intValue;
		return this;
	}
	
	public EventConfig setFloatValue(float floatValue) {
		this.floatValue = floatValue;
		return this;
	}
	
	public EventConfig setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		return this;
	}
	
	public EventConfig setStringValue(String stringValue) {
		this.stringValue = stringValue;
		return this;
	}
	
	public EventConfig setParameterObject(Object parameterObject) {
		this.parameterObject = parameterObject;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(booleanValue, eventType, floatValue, intValue, stringValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventConfig other = (EventConfig) obj;
		return booleanValue == other.booleanValue && eventType == other.eventType && Float.floatToIntBits(floatValue) == Float.floatToIntBits(other.floatValue) && intValue == other.intValue && Objects.equals(stringValue, other.stringValue);
	}
	
	public boolean equalsIgnoringDefaultConfigValues(EventConfig config) {
		if (this == config)
			return true;
		if (config == null)
			return false;
		if (eventType != config.eventType && config.eventType != null)
			return false;
		if (intValue != config.intValue && config.intValue != 0)
			return false;
		if (Float.floatToIntBits(floatValue) != Float.floatToIntBits(config.floatValue) && config.floatValue != 0f)
			return false;
		if (booleanValue != config.booleanValue && config.booleanValue != false)
			return false;
		if (stringValue == null) {
			if (config.stringValue != null)
				return false;
		}
		else if (!stringValue.equals(config.stringValue) && config.stringValue != null)
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "EventConfig [eventType=" + eventType + ", intValue=" + intValue + ", floatValue=" + floatValue + ", booleanValue=" + booleanValue + ", stringValue=" + stringValue + ", parameterObject=" + parameterObject + "]";
	}
	
	/**
	 * Creates a shallow copy of this {@link EventConfig} object.
	 */
	public EventConfig copy() {
		EventConfig copy = new EventConfig();
		copy.eventType = eventType;
		copy.intValue = intValue;
		copy.floatValue = floatValue;
		copy.booleanValue = booleanValue;
		copy.stringValue = stringValue;
		copy.parameterObject = parameterObject;
		return copy;
	}
}
