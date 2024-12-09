package net.jfabricationgames.gdx.cutscene.variable;

import net.jfabricationgames.gdx.item.TriforceItem;
import net.jfabricationgames.gdx.item.TriforceItem.TriforceItemType;

/**
 * A collection of variables that can be evaluated from cutscenes and replace placeholders in the text.
 */
public enum CutsceneVariable {
	
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
