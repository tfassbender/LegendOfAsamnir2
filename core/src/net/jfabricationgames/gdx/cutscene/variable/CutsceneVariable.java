package net.jfabricationgames.gdx.cutscene.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.item.TriforceItem;
import net.jfabricationgames.gdx.item.TriforceItem.TriforceItemType;
import net.jfabricationgames.gdx.map.GameMapManager;

/**
 * A collection of variables that can be evaluated from cutscenes and replace placeholders in the text.
 */
public enum CutsceneVariable {
	
	//*********************************
	//*** triforce side quest
	//*********************************
	NUM_TRIFORCE_PIECES_CARRIED {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumCarriedTriforcePieces());
		}
	},
	NUM_TRIFORCE_PIECES_DELIVERED {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumDeliveredTriforcePieces());
		}
	},
	NUM_TRIFORCE_PIECES_TOTAL {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItemType.values().length);
		}
	},
	NUM_TRIFORCE_PIECES_LEFT_IN_SVARTALFHEIM {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumTriforcePiecesLeftInLocation(TriforceItem.TriforceItemLocation.SVARTALFHEIM));
		}
	},
	NUM_TRIFORCE_PIECES_LEFT_IN_NIFLHEIM {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumTriforcePiecesLeftInLocation(TriforceItem.TriforceItemLocation.NIFLHEIM));
		}
	},
	NUM_TRIFORCE_PIECES_LEFT_IN_MUSPELHEIM {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumTriforcePiecesLeftInLocation(TriforceItem.TriforceItemLocation.MUSPELHEIM));
		}
	},
	NUM_TRIFORCE_PIECES_LEFT_IN_HELHEIM {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumTriforcePiecesLeftInLocation(TriforceItem.TriforceItemLocation.HELHEIM));
		}
	},
	NUM_TRIFORCE_PIECES_LEFT_IN_ASGARD {
		
		@Override
		public String evaluate() {
			return Integer.toString(TriforceItem.getNumTriforcePiecesLeftInLocation(TriforceItem.TriforceItemLocation.ASGARD));
		}
	},
	
	//*********************************
	//*** chicken side quest
	//*********************************
	NUM_CHICKENS_MISSING {
		
		private static final int NUM_CHICKENS_TOTAL = 10;
		
		@Override
		public String evaluate() {
			// the map uses event objects with a unitId to determine the target area for the chicken side quest
			CutsceneControlledUnit lowerLeftEventObject = GameMapManager.getInstance().getMap().getUnitById("loa2_l2_niflheim_chicken_target_area_lower_left");
			CutsceneControlledUnit upperRightEventObject = GameMapManager.getInstance().getMap().getUnitById("loa2_l2_niflheim_chicken_target_area_upper_right");
			
			if (lowerLeftEventObject == null || upperRightEventObject == null) {
				Gdx.app.error(getClass().getSimpleName(), "The chicken target area is not set correctly. The chicken side quest can't be evaluated. Maybe this was requested from the wrong map?");
				return "10";
			}
			
			Vector2 lowerLeft = lowerLeftEventObject.getPosition();
			Vector2 upperRight = upperRightEventObject.getPosition();
			
			// count the chickens that are in the target area
			int numChickensCaught = 0;
			for (int i = 0; i < NUM_CHICKENS_TOTAL; i++) {
				CutsceneControlledUnit chicken = GameMapManager.getInstance().getMap().getUnitById("loa2_l2_niflheim_chicken_" + (i + 1));
				
				if (chicken == null) {
					Gdx.app.error(getClass().getSimpleName(), "The chicken with id 'loa2_l2_niflheim_chicken_" + (i + 1) + "' is not found in the map. The chicken side quest can't be evaluated correctly.");
					continue;
				}
				
				if (chicken.getPosition().x >= lowerLeft.x && chicken.getPosition().x <= upperRight.x //
						&& chicken.getPosition().y >= lowerLeft.y && chicken.getPosition().y <= upperRight.y) {
					numChickensCaught++;
				}
			}
			
			return Integer.toString(NUM_CHICKENS_TOTAL - numChickensCaught);
		}
	};
	
	public abstract String evaluate();
	
	public static boolean doesVariableExist(String variableName) {
		try {
			CutsceneVariable.valueOf(variableName);
			return true;
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}
}
