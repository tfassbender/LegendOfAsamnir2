package net.jfabricationgames.gdx.event.coded;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventType;

public class CastleOfTheChaosWizardMagicPipesEventHandler extends CodedEventHandler {
	
	private static final String STATE_SWITCH_ID_UP = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_up";
	private static final String STATE_SWITCH_ID_RIGHT = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_right";
	private static final String STATE_SWITCH_ID_DOWN = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_down";
	private static final String STATE_SWITCH_ID_LEFT = "loa2_l5_castle_of_the_chaos_wizard__magic_pipe_switch_left";
	
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
		
		// TODO delete the first event handler after tests
		if (EventType.EVENT_OBJECT_TOUCHED.equals(event.eventType) && event.stringValue.equals("loa2_l5_castle_of_the_chaos_wizard__throne_room__entering")) {
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_ENABLED, true);
			globalValuesDataHandler.put(GLOBAL_VALUE_KEY_MAGIC_PIPES_RENDER_EFFECT_LAYER_PIPES_CENTER_ON, true);
			changeMagicPipesRenderEffectLayer();
		}
		
		if (EventType.MULTI_STATE_SWITCH_ACTION.equals(event.eventType)) {
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
