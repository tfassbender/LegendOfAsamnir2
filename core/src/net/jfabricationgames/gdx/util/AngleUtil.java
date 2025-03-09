package net.jfabricationgames.gdx.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Helper methods for angle calculations.
 */
public class AngleUtil {
	
	private AngleUtil() {}
	
	public static enum AngleDirection {
		LEFT, //
		RIGHT, //
		UP, //
		DOWN, //
	}
	
	public static AngleDirection getDirection(Vector2 startPoint, Vector2 endPoint) {
		float angle = getAngle(startPoint, endPoint);
		return getDirection(angle);
	}
	
	public static float getAngle(Vector2 startPoint, Vector2 endPoint) {
		return (float) (Math.toDegrees(Math.atan2(endPoint.x - startPoint.x, endPoint.y - startPoint.y)) + 720) % 360;
	}
	
	public static AngleDirection getDirection(float angle) {
		if (angle >= 45 && angle < 135) {
			return AngleDirection.RIGHT;
		}
		else if (angle >= 135 && angle < 225) {
			return AngleDirection.DOWN;
		}
		else if (angle >= 225 && angle < 315) {
			return AngleDirection.LEFT;
		}
		else {
			return AngleDirection.UP;
		}
	}
}
