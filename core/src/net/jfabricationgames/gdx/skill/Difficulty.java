package net.jfabricationgames.gdx.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.skill.config.DifficultySkillConfig;

/**
 * The difficulty setting of the game (implemented as a skill to simply change the configuration if needed).
 */
public class Difficulty {
	
	private static final String CONFIG_PATH = "config/skill/difficulty.json";
	private static final String GLOBAL_VALUE_KEY_DIFFICULTY_LEVEL = "difficulty_level";
	private static final int DEFAULT_DIFFICULTY_LEVEL = 1; // normal difficulty
	
	private ObjectMap<String, DifficultySkillConfig> config;
	
	private Difficulty() {}
	
	@SuppressWarnings("unchecked")
	public static Difficulty loadDifficultyConfig() {
		FileHandle difficultyConfigFileHandle = Gdx.files.internal(CONFIG_PATH);
		
		Difficulty difficultySkill = new Difficulty();
		Json json = new Json();
		difficultySkill.config = json.fromJson(ObjectMap.class, DifficultySkillConfig.class, difficultyConfigFileHandle);
		
		return difficultySkill;
	}
	
	public DifficultySkillConfig getDifficultyConfig() {
		int difficultyLevelIndex = GlobalValuesDataHandler.getInstance().getAsInteger(GLOBAL_VALUE_KEY_DIFFICULTY_LEVEL, DEFAULT_DIFFICULTY_LEVEL);
		return config.get(String.valueOf(difficultyLevelIndex));
	}
	
	public static DifficultyLevel getDifficultyLevel() {
		int difficultyLevelIndex = GlobalValuesDataHandler.getInstance().getAsInteger(GLOBAL_VALUE_KEY_DIFFICULTY_LEVEL, DEFAULT_DIFFICULTY_LEVEL);
		switch (difficultyLevelIndex) {
			case 0:
				return DifficultyLevel.EASY;
			case 1:
				return DifficultyLevel.NORMAL;
			case 2:
				return DifficultyLevel.HARD;
			default:
				return DifficultyLevel.NORMAL;
		}
	}
	
	public static void setDifficultyLevel(DifficultyLevel level) {
		GlobalValuesDataHandler.getInstance().put(GLOBAL_VALUE_KEY_DIFFICULTY_LEVEL, String.valueOf(level.index));
	}
}
