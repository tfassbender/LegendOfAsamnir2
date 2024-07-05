package net.jfabricationgames.gdx.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.skill.config.WeaponSkillConfig;
import net.jfabricationgames.gdx.skill.config.WeaponSkillLevelConfig;

public class WeaponSkill {
	
	private static final String CONFIG_PATH = "config/skill/weapons.json";
	private static final String GLOBAL_VALUE_KEY_SKILL_LEVEL_PREFIX = "weapon_skill_level__";
	
	private ObjectMap<String, WeaponSkillConfig> configs;
	
	private WeaponSkill() {}
	
	@SuppressWarnings("unchecked")
	public static WeaponSkill loadWeaponSkillFromConfig() {
		FileHandle attackConfigFileHandle = Gdx.files.internal(CONFIG_PATH);
		
		WeaponSkill weaponSkill = new WeaponSkill();
		Json json = new Json();
		weaponSkill.configs = json.fromJson(ObjectMap.class, WeaponSkillConfig.class, attackConfigFileHandle);
		
		return weaponSkill;
	}
	
	public WeaponSkillLevelConfig getSkillLevelConfig(WeaponSkillType skillType) {
		int skillLevel = getSkillLevel(skillType);
		return getSkillLevelConfig(skillType, skillLevel);
	}
	
	private WeaponSkillLevelConfig getSkillLevelConfig(WeaponSkillType skillType, int level) {
		return configs.get(skillType.name().toLowerCase()).skillLevels.get(String.valueOf(level));
	}
	
	public int getSkillLevel(WeaponSkillType skillType) {
		String globalValueKey = GLOBAL_VALUE_KEY_SKILL_LEVEL_PREFIX + skillType.name().toLowerCase();
		return GlobalValuesDataHandler.getInstance().getAsInteger(globalValueKey, 0);
	}
	
	public int getMaxSkillLevel(WeaponSkillType skillType) {
		return configs.get(skillType.name().toLowerCase()).maxLevel;
	}
	
	public void increaseSkillLevel(WeaponSkillType skillType) {
		String globalValueKey = GLOBAL_VALUE_KEY_SKILL_LEVEL_PREFIX + skillType.name().toLowerCase();
		int skillLevel = GlobalValuesDataHandler.getInstance().getAsInteger(globalValueKey, 0);
		if (skillLevel >= getMaxSkillLevel(skillType)) {
			throw new IllegalStateException("The skill level for skill type " + skillType + " is already at its maximum.");
		}
		
		GlobalValuesDataHandler.getInstance().put(globalValueKey, String.valueOf(skillLevel + 1));
	}
	
	public int getNextSkillLevelCost(WeaponSkillType type) {
		int level = getSkillLevel(type);
		if (level >= getMaxSkillLevel(type)) {
			return -1;
		}
		
		return getSkillLevelConfig(type, level + 1).cost;
	}
}
