package net.jfabricationgames.gdx.projectile;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.attack.AttackConfig;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyShape;
import net.jfabricationgames.gdx.texture.TextureConfig;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class FrostGiantAxe extends Projectile {
	
	public FrostGiantAxe(ProjectileTypeConfig typeConfig, AttackConfig attackConfig, Sprite sprite, ProjectileMap gameMap) {
		super(typeConfig, attackConfig, sprite, gameMap);
		setImageOffset(0f, 0f);
		unitId = "frost_giant_axe"; // needed to find the axe on the map (so the giant can pick it up after throwing)
	}
	
	@Override
	protected PhysicsBodyProperties createShapePhysicsBodyProperties() {
		return new PhysicsBodyProperties().setPhysicsBodyShape(PhysicsBodyShape.CIRCLE).setRadius(0.4f);
	}
	
	@Override
	protected void startBodyLinearDamping() {
		super.startBodyLinearDamping();
		changeSpriteAfterLanding();
	}
	
	@Override
	protected void stopProjectileAfterRangeExceeds() {
		super.stopProjectileAfterRangeExceeds();
		changeSpriteAfterLanding();
	}
	
	private void changeSpriteAfterLanding() {
		TextureConfig textureConfig = new TextureConfig();
		textureConfig.atlas = "packed/demo/demo.atlas";
		textureConfig.texture = "frost_giant_axe_landed";
		TextureLoader textureLoader = new TextureLoader(textureConfig);
		TextureRegion axeLandedTexture = textureLoader.loadDefaultTexture();
		
		Sprite axeLandedSprite = new Sprite(axeLandedTexture);
		axeLandedSprite.setX(axeLandedSprite.getWidth() * 0.5f);
		axeLandedSprite.setY(axeLandedSprite.getHeight() * 0.5f);
		axeLandedSprite.setScale(Constants.WORLD_TO_SCREEN);
		
		this.sprite = axeLandedSprite;
	}
}
