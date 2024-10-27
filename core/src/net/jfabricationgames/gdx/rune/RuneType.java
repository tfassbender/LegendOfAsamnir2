package net.jfabricationgames.gdx.rune;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;

public enum RuneType {
	
	OTHALA(0, "rune_collected__othala", "OTHALA - carry and use items"), //
	GEBO(1, "rune_collected__gebo", "GEBO - sacrifice gold to buy supplies"), //
	ANSUZ(2, "rune_collected__ansuz", "ANSUZ - solve puzzles"), //
	MANNAZ(3, "rune_collected__mannaz", "MANNAZ - reflect magical attacks"), //
	ALGIZ(4, "rune_collected__algiz", "ALGIZ - see hidden paths"), //
	RAIDHO(5, "rune_collected__raidho", "RAIDHO - travel between the nine worlds"), //
	HAGALAZ(6, "rune_collected__hagalaz", "HAGALAZ - gain supreme strength"), //
	KENAZ(7, "rune_collected__kenaz", "KENAZ - revive after death"), //
	LAGUZ(8, "rune_collected__laguz", "LAGUZ - access the castle of the chaos wizard"); //
	
	public static final String GLOBAL_VALUE_KEY_RUNE_HAGALAZ_FORGED = "rune_forged__hagalaz";
	public static final String RUNE_HAGALAZ_DESCRIPTION_POSTFIX_FORGED = " (reforge after use)";
	public static final String RUNE_HAGALAZ_DESCRIPTION_POSTFIX_UNFORGED = " (needs to be reforged)";
	
	public static Array<String> getNamesAsList() {
		Array<String> names = new Array<>(values().length);
		
		for (RuneType rune : values()) {
			names.add(rune.name().toLowerCase());
		}
		
		return names;
	}
	
	public static RuneType getByOrder(int order) {
		for (RuneType type : values()) {
			if (type.order == order) {
				return type;
			}
		}
		throw new IllegalStateException("No rune found for searched order: " + order);
	}
	
	public static RuneType getByContainingName(String runeName) {
		for (RuneType type : values()) {
			if (runeName.toUpperCase().contains(type.name())) {
				return type;
			}
		}
		throw new IllegalStateException("No rune name contained in search string: " + runeName);
	}
	
	public final int order;
	public final String description;
	public final String globalValueKeyCollected;
	
	private RuneType(int order, String globalValueKey, String description) {
		this.order = order;
		this.globalValueKeyCollected = globalValueKey;
		this.description = description;
	}
	
	public boolean isCollected() {
		return GlobalValuesDataHandler.getInstance().getAsBoolean(globalValueKeyCollected);
	}
}