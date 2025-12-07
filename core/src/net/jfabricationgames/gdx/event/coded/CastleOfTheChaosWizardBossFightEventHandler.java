package net.jfabricationgames.gdx.event.coded;

import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.condition.Condition;
import net.jfabricationgames.gdx.condition.ConditionType;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class CastleOfTheChaosWizardBossFightEventHandler extends CodedEventHandler {
	
	private static final String STATE_SWITCH_ID_UP = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_up";
	private static final String STATE_SWITCH_ID_RIGHT = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_right";
	private static final String STATE_SWITCH_ID_DOWN = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_down";
	private static final String STATE_SWITCH_ID_LEFT = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_left";
	
	private static final String UNIT_ID_PLAYER = "PLAYER";
	private static final String UNIT_ID_ARCHANGEL = "loa2_l5_castle_of_the_chaos_wizard__dellingr";
	
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_LOWER_LEFT = "cutscene_object__chaos_wizard_throne_room__area_lower_left";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_LEFT = "cutscene_object__chaos_wizard_throne_room__area_middle_left";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_UPPER_LEFT = "cutscene_object__chaos_wizard_throne_room__area_upper_left";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_TOP = "cutscene_object__chaos_wizard_throne_room__area_middle_top";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_UPPER_RIGHT = "cutscene_object__chaos_wizard_throne_room__area_upper_right";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_RIGHT = "cutscene_object__chaos_wizard_throne_room__area_middle_right";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_LOWER_RIGHT = "cutscene_object__chaos_wizard_throne_room__area_lower_right";
	private static final String CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_BOTTOM = "cutscene_object__chaos_wizard_throne_room__area_middle_bottom";
	
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_RIGHT = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__right";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM_RIGHT = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__bottom_right";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__bottom";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM_LEFT = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__bottom_left";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_LEFT = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__left";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_TOP_LEFT = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__top_left";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_TOP = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__top";
	private static final String CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_FINAL = "cutscene_object__chaos_wizard_throne_room__magic_pipe_position__final";
	
	private static final String CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_BOTTOM_RIGHT = "cutscene_object__chaos_wizard_throne_room__way_to_throne__bottom_right";
	private static final String CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_BOTTOM = "cutscene_object__chaos_wizard_throne_room__way_to_throne__bottom";
	private static final String CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_TOP_LEFT = "cutscene_object__chaos_wizard_throne_room__way_to_throne__top_left";
	private static final String CONFIG_OBJECT_UNIT_ID_THORIN_POSITION_AFTER_FIRST_BOSS = "cutscene_object__chaos_wizard_dialog__thorin_position__after_first_boss";
	
	private static final String CUTSCENE_FUNCTION_CALL_PARAMETER__AFTER_FIRST_BOSS_FIGHT_THORIN_POSITION = "loa2_l5_castle_of_the_chaos_wizard__throne_room__cutscene_after_first_boss_defeated__get_thorin_target_position";
	private static final String CUTSCENE_FUNCTION_CALL_PARAMETER__ARCHANGEL_DEFENSE_POSITION = "loa2_l5_castle_of_the_chaos_wizard__throne_room__get_archangel_defense_position";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_CENTER_ON = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_center_on";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_RIGHT_ON = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_right_on";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_LEFT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_left";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_RIGHT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_right";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_LEFT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_left_from_left";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTTOM = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_left_from_bottom";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTH = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_left_from_both";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTTOM = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_right_from_bottom";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_RIGHT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_right_from_right";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTH = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_bottom_right_from_both";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_LEFT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_left_from_left";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_TOP = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_left_from_top";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_BOTH = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_left_from_both";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_TOP = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_right_from_top";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_RIGHT = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_right_from_right";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_BOTH = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__pipes_top_right_from_both";
	
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_ENERGY_WALL_1 = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__energy_wall_1";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_ENERGY_WALL_2 = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__energy_wall_2";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_ENERGY_WALL_3 = "render_effect_layer__loa2_l5_castle_of_the_chaos_wizard__energy_wall_3";
	
	private static final String CONFIG_EVENT_STRING_ENABLE_MAGIC_PIPES = "loa2_l5_castle_of_the_chaos_wizard__enable_magic_pipes";
	private static final String CONFIG_EVENT_STRING_SET_MAGIC_PIPES_TO_DEFNSE_MODE = "loa2_l5_castle_of_the_chaos_wizard__throne_room__set_magic_pipes_to_defense_mode";
	private static final String GLOBAL_VALUE_KEY_MAGIC_PIPES_ENABLED = "loa2_l5_castle_of_the_chaos_wizard__magic_pipes_enabled";
	
	private Direction directionSwitchUp = Direction.UP;
	private Direction directionSwitchRight = Direction.RIGHT;
	private Direction directionSwitchDown = Direction.DOWN;
	private Direction directionSwitchLeft = Direction.LEFT;
	
	private GlobalValuesDataHandler globalValuesDataHandler;
	
	@Override
	public void handleEvent(EventConfig event) {
		if (globalValuesDataHandler == null) {
			// must be initialized lazyly - otherwise there would be a cyclic dependency at game start
			globalValuesDataHandler = GlobalValuesDataHandler.getInstance();
		}
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) && event.stringValue.equals(CONFIG_EVENT_STRING_ENABLE_MAGIC_PIPES)) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_ENABLED, true);
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_CENTER_ON, true);
			changeMagicPipesRenderEffectLayer();
		}
		
		if (EventType.CONFIG_GENERATED_EVENT.equals(event.eventType) && event.stringValue.equals(CONFIG_EVENT_STRING_SET_MAGIC_PIPES_TO_DEFNSE_MODE)) {
			setMultiStateSwitchesToArchAngelPosition();
			changeMagicPipesRenderEffectLayer();
		}
		
		if (EventType.MULTI_STATE_SWITCH_ACTION.equals(event.eventType)) {
			handleMultiStateSwitchAction(event);
			checkArchAngelDefenseModeEnd();
		}
		
		if (EventType.CUTSCENE_FUNCTION_CALL.equals(event.eventType)) {
			handleCutsceneFunctionCall(event);
		}
	}
	
	private void setMultiStateSwitchesToArchAngelPosition() {
		if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_LEFT)) {
			directionSwitchLeft = Direction.DOWN;
			directionSwitchDown = Direction.LEFT;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_LEFT)) {
			directionSwitchLeft = Direction.LEFT;
			directionSwitchUp = Direction.LEFT;
			directionSwitchDown = Direction.LEFT;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_LEFT)) {
			directionSwitchUp = Direction.LEFT;
			directionSwitchLeft = Direction.UP;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_TOP)) {
			directionSwitchUp = Direction.UP;
			directionSwitchLeft = Direction.UP;
			directionSwitchRight = Direction.UP;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_RIGHT)) {
			directionSwitchUp = Direction.RIGHT;
			directionSwitchRight = Direction.UP;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_RIGHT)) {
			directionSwitchRight = Direction.RIGHT;
			directionSwitchUp = Direction.RIGHT;
			directionSwitchDown = Direction.RIGHT;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_RIGHT)) {
			directionSwitchRight = Direction.DOWN;
			directionSwitchDown = Direction.RIGHT;
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_BOTTOM)) {
			directionSwitchDown = Direction.DOWN;
			directionSwitchLeft = Direction.DOWN;
			directionSwitchRight = Direction.DOWN;
		}
		
		updateMultiStateSwitchDirections();
	}
	
	private void updateMultiStateSwitchDirections() {
		// update the directions of the multi state switches, so the textures match the expected directions
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.MULTI_STATE_SWITCH_SET_STATE) //
				.setStringValue(STATE_SWITCH_ID_UP) //
				.setIntValue(directionSwitchUp.index));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.MULTI_STATE_SWITCH_SET_STATE) //
				.setStringValue(STATE_SWITCH_ID_RIGHT) //
				.setIntValue(directionSwitchRight.index));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.MULTI_STATE_SWITCH_SET_STATE) //
				.setStringValue(STATE_SWITCH_ID_DOWN) //
				.setIntValue(directionSwitchDown.index));
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.MULTI_STATE_SWITCH_SET_STATE) //
				.setStringValue(STATE_SWITCH_ID_LEFT) //
				.setIntValue(directionSwitchLeft.index));
	}
	
	private void handleMultiStateSwitchAction(EventConfig event) {
		boolean handled = false;
		switch (event.stringValue) {
			case STATE_SWITCH_ID_UP:
				directionSwitchUp = Direction.fromIndex(event.intValue);
				handled = true;
				break;
			case STATE_SWITCH_ID_RIGHT:
				directionSwitchRight = Direction.fromIndex(event.intValue);
				handled = true;
				break;
			case STATE_SWITCH_ID_DOWN:
				directionSwitchDown = Direction.fromIndex(event.intValue);
				handled = true;
				break;
			case STATE_SWITCH_ID_LEFT:
				directionSwitchLeft = Direction.fromIndex(event.intValue);
				handled = true;
				break;
		}
		
		if (handled && globalValuesDataHandler.getAsBoolean(GLOBAL_VALUE_KEY_MAGIC_PIPES_ENABLED)) {
			changeMagicPipesRenderEffectLayer();
		}
	}
	
	private void changeMagicPipesRenderEffectLayer() {
		turnOffAllMagicPipeEffects();
		
		// straight pipes
		if (directionSwitchUp == Direction.UP) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP, true);
		}
		if (directionSwitchRight == Direction.RIGHT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_RIGHT, true);
		}
		if (directionSwitchDown == Direction.DOWN) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM, true);
		}
		if (directionSwitchLeft == Direction.LEFT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_LEFT, true);
		}
		
		// top left corner
		if (directionSwitchUp == Direction.LEFT && directionSwitchLeft == Direction.UP) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_BOTH, true);
		}
		else if (directionSwitchUp == Direction.LEFT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_TOP, true);
		}
		else if (directionSwitchLeft == Direction.UP) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_LEFT, true);
		}
		
		// top right corner
		if (directionSwitchUp == Direction.RIGHT && directionSwitchRight == Direction.UP) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_BOTH, true);
		}
		else if (directionSwitchUp == Direction.RIGHT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_TOP, true);
		}
		else if (directionSwitchRight == Direction.UP) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_RIGHT, true);
		}
		
		// bottom left corner
		if (directionSwitchDown == Direction.LEFT && directionSwitchLeft == Direction.DOWN) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTH, true);
		}
		else if (directionSwitchDown == Direction.LEFT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTTOM, true);
		}
		else if (directionSwitchLeft == Direction.DOWN) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_LEFT, true);
		}
		
		// bottom right corner
		if (directionSwitchDown == Direction.RIGHT && directionSwitchRight == Direction.DOWN) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTH, true);
		}
		else if (directionSwitchDown == Direction.RIGHT) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTTOM, true);
		}
		else if (directionSwitchRight == Direction.DOWN) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_RIGHT, true);
		}
	}
	
	private void turnOffAllMagicPipeEffects() {
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_LEFT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_RIGHT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM, false);
		
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_LEFT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTTOM, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_LEFT_FROM_BOTH, false);
		
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTTOM, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_RIGHT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_BOTTOM_RIGHT_FROM_BOTH, false);
		
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_LEFT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_TOP, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_LEFT_FROM_BOTH, false);
		
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_TOP, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_RIGHT, false);
		globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_TOP_RIGHT_FROM_BOTH, false);
	}
	
	private void checkArchAngelDefenseModeEnd() {
		if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_LEFT)) {
			if (directionSwitchLeft != Direction.DOWN && directionSwitchDown != Direction.LEFT) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_LEFT)) {
			if (directionSwitchLeft != Direction.LEFT) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_LEFT)) {
			if (directionSwitchUp != Direction.LEFT && directionSwitchLeft != Direction.UP) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_TOP)) {
			if (directionSwitchUp != Direction.UP) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_RIGHT)) {
			if (directionSwitchUp != Direction.RIGHT && directionSwitchRight != Direction.UP) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_RIGHT)) {
			if (directionSwitchRight != Direction.RIGHT) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_RIGHT)) {
			if (directionSwitchRight != Direction.DOWN && directionSwitchDown != Direction.RIGHT) {
				endDefenseMode();
			}
		}
		else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_BOTTOM)) {
			if (directionSwitchDown != Direction.DOWN) {
				endDefenseMode();
			}
		}
	}
	
	private void endDefenseMode() {
		EventHandler.getInstance().fireEvent(new EventConfig() //
				.setEventType(EventType.CONFIG_GENERATED_EVENT) //
				.setStringValue("loa2_l5_castle_of_the_chaos_wizard__throne_room__archangel_end_defense_mode"));
	}
	
	private void handleCutsceneFunctionCall(EventConfig event) {
		if (CUTSCENE_FUNCTION_CALL_PARAMETER__AFTER_FIRST_BOSS_FIGHT_THORIN_POSITION.equals(event.stringValue)) {
			String targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_THORIN_POSITION_AFTER_FIRST_BOSS;
			if (isUnitInArea(UNIT_ID_PLAYER, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_LEFT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_BOTTOM;
			}
			else if (isUnitInArea(UNIT_ID_PLAYER, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_BOTTOM)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_BOTTOM_RIGHT;
			}
			else if (isUnitInArea(UNIT_ID_PLAYER, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_LEFT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_WAY_TO_THRONE_TOP_LEFT;
			}
			
			// set the target position (unit id) as answer of the function call
			event.stringValue = targetPositionUnitId;
		}
		else if (CUTSCENE_FUNCTION_CALL_PARAMETER__ARCHANGEL_DEFENSE_POSITION.equals(event.stringValue)) {
			String targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_TOP; // to have a fallback in case of errors
			if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_LEFT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM_LEFT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_LEFT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_LEFT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_LEFT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_TOP_LEFT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_TOP)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_TOP;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_UPPER_RIGHT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_RIGHT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_RIGHT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_RIGHT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_LOWER_RIGHT)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM_RIGHT;
			}
			else if (isUnitInArea(UNIT_ID_ARCHANGEL, CONFIG_OBJECT_UNIT_ID_AREA_MIDDLE_BOTTOM)) {
				targetPositionUnitId = CONFIG_OBJECT_UNIT_ID_MAGIC_PIPE_POSITION_BOTTOM;
			}
			
			// set the target position (unit id) as answer of the function call
			event.stringValue = targetPositionUnitId;
		}
	}
	
	private boolean isUnitInArea(String unitId, String areaConfigObjectUnitId) {
		Condition condition = new Condition();
		condition.parameters = new ObjectMap<>();
		condition.parameters.put("objectId", unitId);
		condition.parameters.put("targetAreaObjectId", areaConfigObjectUnitId);
		
		return ConditionType.OBJECT_IN_POSITION.check(condition);
	}
	
	private enum Direction {
		
		UP(0), //
		RIGHT(1), //
		DOWN(2), // 
		LEFT(3);
		
		private final int index;
		
		private Direction(int index) {
			this.index = index;
		}
		
		private static Direction fromIndex(int index) {
			for (Direction direction : values()) {
				if (direction.index == index) {
					return direction;
				}
			}
			return null;
		}
	}
}
