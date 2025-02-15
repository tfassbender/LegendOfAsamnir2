package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;

public class Hookshot extends Boomerang {
	
	public Hookshot(ProjectileTypeConfig typeConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, sprite, gameMap);
	}
	
	@Override
	protected void rotateProjectile(float delta) {
		// TODO only rotate if not fixed to an object
		super.rotateProjectile(delta);
	}
	
	@Override
	protected void startProjectile(Vector2 direction) {
		super.startProjectile(direction);
		
		// TODO connect to the player with a joint (maybe using events)
		
		// change traversable objects to sensors here - the hookshot cannot collide with them anyway, since it's a projectile
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR));
	}
	
	@Override
	protected void removeAfterMovedBackToPlayer() {
		super.removeAfterMovedBackToPlayer();
		
		// tells the player that the attack is finished so the player is able to move again
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.HOOKSHOT_ATTACK_FINISHED));
		
		// change traversable objects back to solid bodies after the hookshot finishes
		EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.TRAVERSABLE_OBJECT_CHANGE_BODY_TO_SENSOR));
	}
}
