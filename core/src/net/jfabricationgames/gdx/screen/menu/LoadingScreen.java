package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox.TextureType;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class LoadingScreen extends MenuScreen<LoadingScreen> {
	
	private static final String ASSET_GROUP_NAME_LOADING_SCREEN = "loading_screen";
	private static final String TEXTURE_CONFIG = "config/menu/loading_screen/loading_screen_textures.json";
	
	private MenuBox bannerTitle;
	private MenuBox bannerSubTitle;
	
	private AssetGroupManager assetManager;
	private Runnable afterFinishedLoading;
	
	private AnimationDirector<TextureRegion> dwarfAnimation;
	private AnimationDirector<TextureRegion> chaosWizardAnimation;
	private TextureRegion backgroundImage;
	
	public LoadingScreen(Runnable afterFinishedLoading) {
		this.afterFinishedLoading = afterFinishedLoading;
		assetManager = AssetGroupManager.getInstance();
		dwarfAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_run_right");
		chaosWizardAnimation = AnimationManager.getInstance().getTextureAnimationDirector("elite_mage_idle");
		
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		backgroundImage = textureLoader.loadTexture("background");
		
		createComponents();
	}
	
	private void createComponents() {
		bannerTitle = new MenuBox(10, 2, TextureType.BIG_BANNER);
		bannerSubTitle = new MenuBox(8, 2, TextureType.BIG_BANNER_LOW);
	}
	
	@Override
	public void showMenu() {
		ScreenManager.getInstance().setScreen(this);
	}
	
	@Override
	protected String getAssetGroupName() {
		return ASSET_GROUP_NAME_LOADING_SCREEN;
	}
	
	@Override
	protected String getInputContextName() {
		return null;
	}
	
	@Override
	public void setFocusTo(String stateName, String leavingState) {}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		setProjectionMatrixBeforeRendering();
		
		dwarfAnimation.increaseStateTime(delta);
		chaosWizardAnimation.increaseStateTime(delta);
		
		batch.begin();
		drawBackground();
		drawBanners();
		drawLoadingBar();
		batch.end();
		
		drawTexts();
		
		if (assetManager.finishedLoading()) {
			afterFinishedLoading.run();
		}
	}
	
	private void drawBackground() {
		batch.draw(backgroundImage, -80, 10, 1360, 700);
	}
	
	private void drawBanners() {
		bannerTitle.draw(batch, 145, 655, 900, 230);
		bannerSubTitle.draw(batch, 200, 605, 800, 180);
	}
	
	private void drawLoadingBar() {
		float progress = assetManager.getProgress();
		float positionX = 150f + 730f * progress;
		float positionY = 120f;
		
		TextureRegion dwarf = dwarfAnimation.getKeyFrame();
		batch.draw(dwarf, positionX, positionY, 80, 80);
		
		TextureRegion chaosWizard = chaosWizardAnimation.getKeyFrame();
		batch.draw(chaosWizard, 950, positionY, 150, 80);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.4f);
		screenTextWriter.drawText("Legend of Asamnir 2", 265, 790);
		
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(1f);
		screenTextWriter.drawText("Rise of the Chaos Wizard", 300, 709);
		
		screenTextWriter.setScale(1f);
		screenTextWriter.drawText("Loading...", 495, 88);
	}
}
