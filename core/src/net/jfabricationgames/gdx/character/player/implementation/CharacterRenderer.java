package net.jfabricationgames.gdx.character.player.implementation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.DummyAnimationDirector;
import net.jfabricationgames.gdx.animation.GrowingAnimationDirector;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.cutscene.action.CutsceneControlledUnit;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemType;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.texture.TextureLoader;

class CharacterRenderer {
	
	private static final String ANIMATION_DWARF_CONFIG_FILE = "config/animation/dwarf.json";
	private static final String TEXTURE_CONFIG_FILE_NAME = "config/dwarf/textures.json";
	
	private static final String ANIMATION_NAME_RUN_RIGHT_NO_AXE = "dwarf_run_right_no_axe";
	
	private static final Vector2 PHYSICS_BODY_POSITION_OFFSET = new Vector2(0f, -0.15f);
	private static final float DRAWING_DIRECTION_OFFSET = 0.1f;
	private static final Vector2 DARKNESS_GRADIENT_POSITION_OFFSET = new Vector2(0f, -0.6f);
	
	private static final Color FROZEN_COLOR = new Color(0.1f, 0.7f, 1f, 1f);
	private static final Color HEAT_DAMAGE_COLOR = new Color(0.6f, 0.3f, 0.3f, 1f);
	
	private static final Color COMPASS_ARROW_COLOR = new Color(202f / 255f, 43f / 255f, 1f / 255f, 1f);
	private static final String GLOBAL_VALUE_KEY_COMPASS_TARGET_UNIT_ID = "compass_target_unit_id";
	private static final String GLOBAL_VALUE_KEY_COMPASS_SEARCH_FOR_TRIFORCE_PIECES = "compass_search_for_triforce_pieces";
	
	private Dwarf player;
	
	private AnimationManager animationManager;
	
	protected AnimationDirector<TextureRegion> animation;
	private AnimationDirector<TextureRegion> invertedControlsAnimation;
	
	private TextureLoader textureLoader;
	protected TextureRegion idleDwarfSprite;
	protected TextureRegion idleDwarfNoAxeSprite;
	private TextureRegion blockSprite;
	private TextureRegion aimMarkerSprite;
	
	private GrowingAnimationDirector<TextureRegion> darknessAnimation;
	private boolean darknessFading;
	
	public CharacterRenderer(Dwarf player) {
		this.player = player;
		
		animationManager = AnimationManager.getInstance();
		animationManager.loadAnimations(ANIMATION_DWARF_CONFIG_FILE);
		
		textureLoader = new TextureLoader(TEXTURE_CONFIG_FILE_NAME);
		idleDwarfSprite = textureLoader.loadTexture("idle");
		idleDwarfNoAxeSprite = textureLoader.loadTexture("idle_no_axe");
		blockSprite = textureLoader.loadTexture("block");
		aimMarkerSprite = textureLoader.loadTexture("aim_marker");
		darknessAnimation = animationManager.getGrowingAnimationDirector("darkness_fade");
		
		animation = getAnimation();
		
		invertedControlsAnimation = animationManager.getTextureAnimationDirector("arcane_swirl_loop");
	}
	
	private AnimationDirector<TextureRegion> getAnimation() {
		if (player.action != CharacterAction.NONE && player.action != CharacterAction.BLOCK) {
			return getAnimation(player.action);
		}
		else {
			return new DummyAnimationDirector<TextureRegion>();
		}
	}
	private AnimationDirector<TextureRegion> getAnimation(CharacterAction action) {
		if (action == CharacterAction.RUN && !player.hasWeapon()) {
			return animationManager.getTextureAnimationDirector(ANIMATION_NAME_RUN_RIGHT_NO_AXE);
		}
		return animationManager.getTextureAnimationDirector(getAnimationName(action));
	}
	private String getAnimationName(CharacterAction action) {
		return action.getAnimationName();
	}
	
	public void changeAnimation() {
		animation = getAnimation();
		animation.resetStateTime();
	}
	
	public void startDarknessFade() {
		darknessFading = true;
	}
	
	public void processDarknessFadingAnimation(float delta) {
		if (darknessFading) {
			darknessAnimation.increaseStateTime(delta);
		}
	}
	
	public void drawDwarf(SpriteBatch batch) {
		TextureRegion frame;
		if (player.action == CharacterAction.NONE) {
			if (player.hasWeapon()) {
				frame = idleDwarfSprite;
			}
			else {
				frame = idleDwarfNoAxeSprite;
			}
		}
		else if (player.action == CharacterAction.BLOCK) {
			frame = blockSprite;
		}
		else {
			frame = animation.getKeyFrame();
		}
		
		if (!drawingDirectionEqualsTextureDirection(frame)) {
			frame.flip(true, false);
		}
		
		Color color = new Color(batch.getColor()); // getColor() returns a reference to the color, so it has to be copied
		setSpriteColor(batch);
		
		//use null as offset parameter to not create a new empty vector every time
		draw(batch, frame, 0, 0, frame.getRegionWidth(), frame.getRegionHeight());
		
		batch.setColor(color); // reset to the original color
		
		drawEffects(batch);
	}
	
	private void setSpriteColor(SpriteBatch batch) {
		if (player.isFrozen()) {
			// blend in the frozen color by setting the oppacity
			calculateAndSetColor(batch, player.getFreezingTimer(), Dwarf.FREEZING_TIME_IN_SECONDS, FROZEN_COLOR);
		}
		else if (player.isDamagedByHeat()) {
			// blend in the heat damage color by setting the oppacity
			calculateAndSetColor(batch, player.getHeatDamageTimer(), Dwarf.HEAT_DAMAGE_TIME_IN_SECONDS, HEAT_DAMAGE_COLOR);
		}
	}
	
	private void calculateAndSetColor(SpriteBatch batch, float timer, float totalTime, Color color) {
		float progress = 1f;
		float colorGradientTime = 1f; // time to blend in and out
		
		if (timer > totalTime - colorGradientTime) {
			progress = ((totalTime / colorGradientTime) - timer);
		}
		else if (timer < colorGradientTime) {
			progress = timer;
		}
		
		color = color.cpy().lerp(Color.WHITE, 1f - progress); // linear interpolation to blend the colors (WHITE is the default color for a sprite batch)
		
		batch.setColor(color);
	}
	
	private boolean drawingDirectionEqualsTextureDirection(TextureRegion frame) {
		return player.movementHandler.isDrawDirectionRight() != frame.isFlipX();
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame, float offsetX, float offsetY, float width, float height) {
		float originX = 0.5f * width + PHYSICS_BODY_POSITION_OFFSET.x * width;
		float originY = 0.5f * height + PHYSICS_BODY_POSITION_OFFSET.y * height;
		float x = player.bodyHandler.body.getPosition().x - originX;
		float y = player.bodyHandler.body.getPosition().y - originY;
		x += offsetX;
		y += offsetY;
		x += getDrawingDirectionOffset();
		
		batch.draw(frame, // textureRegion
				x, y, // x, y
				originX, originY, //originX, originY
				width, height, // width, height
				Constants.WORLD_TO_SCREEN, // scaleX
				Constants.WORLD_TO_SCREEN, // scaleY
				0.0f); // rotation
	}
	
	private void drawEffects(SpriteBatch batch) {
		if (player.hasInvertedControls()) {
			invertedControlsAnimation.increaseStateTime(Gdx.graphics.getDeltaTime());
			TextureRegion region = invertedControlsAnimation.getKeyFrame();
			invertedControlsAnimation.getSpriteConfig() //
					.setX((player.bodyHandler.body.getPosition().x - region.getRegionWidth() * 0.5f + PHYSICS_BODY_POSITION_OFFSET.x)) //
					.setY((player.bodyHandler.body.getPosition().y - region.getRegionHeight() * 0.5f + PHYSICS_BODY_POSITION_OFFSET.y));
			invertedControlsAnimation.draw(batch);
		}
	}
	
	private float getDrawingDirectionOffset() {
		if (player.movementHandler.isDrawDirectionRight()) {
			return DRAWING_DIRECTION_OFFSET;
		}
		else {
			return -DRAWING_DIRECTION_OFFSET;
		}
	}
	
	public void drawAimMarker(SpriteBatch batch) {
		final float aimMarkerDistanceFactor = 0.5f;
		final float aimMarkerOffsetY = -0.1f;
		Vector2 aimMarkerOffset = player.movementHandler.getMovingDirection().getNormalizedDirectionVector().scl(aimMarkerDistanceFactor).add(0, aimMarkerOffsetY);
		final float aimMarkerSize = 5f;
		draw(batch, aimMarkerSprite, aimMarkerOffset, aimMarkerSize, aimMarkerSize);
	}
	
	private void draw(SpriteBatch batch, TextureRegion frame, Vector2 offset, float width, float height) {
		if (offset != null) {
			draw(batch, frame, offset.x, offset.y, width, height);
		}
		else {
			draw(batch, frame, 0, 0, width, height);
		}
	}
	
	public void drawCompassMarker(ShapeRenderer shapeRenderer) {
		GameMapManager.getInstance().getMap().getUnitById(ANIMATION_DWARF_CONFIG_FILE);
		
		final float aimMarkerDistanceFactor = 0.8f;
		
		Vector2 compassTargetPosition = getCompassTargetPosition();
		if (compassTargetPosition == null) {
			// no target position available, so do not draw the compass marker
			return;
		}
		
		Vector2 normalizedDirection = compassTargetPosition.sub(player.bodyHandler.body.getPosition()).nor();
		Vector2 aimMarkerOffset = normalizedDirection.cpy().scl(aimMarkerDistanceFactor);
		
		final float arrowSize = 0.2f;
		final float arrowBaseWidth = 0.2f;
		shapeRenderer.setColor(COMPASS_ARROW_COLOR);
		drawArrow(shapeRenderer, //
				player.bodyHandler.body.getPosition().x + aimMarkerOffset.x, //
				player.bodyHandler.body.getPosition().y + aimMarkerOffset.y, //
				normalizedDirection.angleRad(), //
				arrowSize, //
				arrowBaseWidth);
		
		// opposite direction (to make it look like a compass needle)
		shapeRenderer.setColor(Color.WHITE);
		drawArrow(shapeRenderer, //
				player.bodyHandler.body.getPosition().x + aimMarkerOffset.x, //
				player.bodyHandler.body.getPosition().y + aimMarkerOffset.y, //
				normalizedDirection.angleRad(), //
				-arrowSize, //
				arrowBaseWidth);
	}
	
	private Vector2 getCompassTargetPosition() {
		// check if a global value is configured that defines the target unit for the compass
		String compassTargetUnitId = GlobalValuesDataHandler.getInstance().get(GLOBAL_VALUE_KEY_COMPASS_TARGET_UNIT_ID);
		if (compassTargetUnitId != null && !compassTargetUnitId.isEmpty()) {
			CutsceneControlledUnit unit = GameMapManager.getInstance().getMap().getUnitById(compassTargetUnitId);
			if (unit != null) {
				return unit.getPosition();
			}
		}
		
		// if no target unit is configured, but the compass can search for triforce pieces, target the nearest triforce piece
		if (GlobalValuesDataHandler.getInstance().getAsBoolean(GLOBAL_VALUE_KEY_COMPASS_SEARCH_FOR_TRIFORCE_PIECES)) {
			Array<Item> items = GameMapManager.getInstance().getMap().getItemsInMap();
			ArrayIterator<Item> iterator = items.iterator();
			while (iterator.hasNext()) {
				Item item = iterator.next();
				if (item.getType() != ItemType.TRIFORCE) {
					iterator.remove(); // remove all items that are not triforce pieces
				}
			}
			
			if (!items.isEmpty()) {
				// sort the items by distance to the player
				items.sort((item1, item2) -> {
					float distance1 = item1.getPosition().dst(player.bodyHandler.body.getPosition());
					float distance2 = item2.getPosition().dst(player.bodyHandler.body.getPosition());
					return Float.compare(distance1, distance2);
				});
				return items.first().getPosition(); // return the position of the nearest triforce piece
			}
		}
		
		return null; // nothing to target
	}
	
	private void drawArrow(ShapeRenderer shapeRenderer, float x, float y, float angle, float size, float baseWidth) {
		// Calculate direction vector
		float tipX = x + (float) Math.cos(angle) * size;
		float tipY = y + (float) Math.sin(angle) * size;
		
		// Perpendicular vector for the base
		float perpAngle = angle + (float) Math.PI / 2f;
		float halfBase = baseWidth / 2f;
		
		float base1X = x + (float) Math.cos(perpAngle) * halfBase;
		float base1Y = y + (float) Math.sin(perpAngle) * halfBase;
		
		float base2X = x - (float) Math.cos(perpAngle) * halfBase;
		float base2Y = y - (float) Math.sin(perpAngle) * halfBase;
		
		shapeRenderer.triangle(tipX, tipY, base1X, base1Y, base2X, base2Y);
	}
	
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer) {
		batch.begin();
		draw(batch, darknessAnimation.getKeyFrame(), //
				DARKNESS_GRADIENT_POSITION_OFFSET.x, //
				// the texture seems to move while the animation is playing, which causes a gap between the texture and the black overlay underneath
				// the state time with a factor of -2f is used as a workarround against this
				DARKNESS_GRADIENT_POSITION_OFFSET.y - darknessAnimation.getStateTime() * 2f, // 
				darknessAnimation.getWidth(), darknessAnimation.getHeight());
		batch.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		
		float x = player.bodyHandler.body.getPosition().x;
		float y = player.bodyHandler.body.getPosition().y;
		
		final float LEF_RIGHT_OFFSET_X = 0.2f;
		final float UP_OFFSET_Y = 0.25f;
		final float DOWN_OFFSET_Y = 0.5f;
		
		//left
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f + LEF_RIGHT_OFFSET_X, //
				y + Constants.SCENE_HEIGHT, //
				-Constants.SCENE_WIDTH, //
				-Constants.SCENE_HEIGHT * 2f);
		
		//right
		shapeRenderer.rect(x + darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f - LEF_RIGHT_OFFSET_X, //
				y + Constants.SCENE_HEIGHT, //
				Constants.SCENE_WIDTH, //
				-Constants.SCENE_HEIGHT * 2f);
		
		//up
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f, //
				y + darknessAnimation.getHeight() * Constants.WORLD_TO_SCREEN * 0.5f + UP_OFFSET_Y, //
				Constants.SCENE_WIDTH * 2f, //
				Constants.SCENE_HEIGHT);
		
		//down
		shapeRenderer.rect(x - darknessAnimation.getWidth() * Constants.WORLD_TO_SCREEN * 0.5f, //
				y - darknessAnimation.getHeight() * Constants.WORLD_TO_SCREEN * 0.5f + DOWN_OFFSET_Y, //
				Constants.SCENE_WIDTH * 2f, //
				-Constants.SCENE_HEIGHT);
		
		shapeRenderer.end();
		
		if (darknessAnimation.isAnimationFinished()) {
			darknessAnimation.resetStateTime();
			darknessFading = false;
			
			//set the global value for lantern used, to not render the darkness anymore
			GlobalValuesDataHandler.getInstance().put(Constants.GLOBAL_VALUE_KEY_LANTERN_USED, true);
		}
	}
}
