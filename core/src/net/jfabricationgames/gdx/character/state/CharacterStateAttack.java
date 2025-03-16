package net.jfabricationgames.gdx.character.state;

import java.util.function.Supplier;

import com.badlogic.gdx.math.Vector2;

public interface CharacterStateAttack {
	
	public void abort();
	
	public boolean isExecuted();
	
	public default void setTargetPositionSupplier(Supplier<Vector2> targetPositionSupplier) {};
}
