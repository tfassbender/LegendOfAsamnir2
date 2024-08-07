package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.hud.StatsCharacter;

public class HeadsUpDisplay implements Disposable {
	
	private StatusBar statusBar;
	private BossStatusBar bossStatusBar;
	private OnScreenInfoRenderer onScreenInfoRenderer;
	private OnScreenAnimationRenderer onScreenRuneRenderer;
	private OnScreenTextBox onScreenText;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(float hudSceneWidth, float hudSceneHeight, OrthographicCamera camera, StatsCharacter character) {
		statusBar = new StatusBar(camera, character, hudSceneWidth, hudSceneHeight);
		bossStatusBar = new BossStatusBar(camera, hudSceneWidth, hudSceneHeight);
		onScreenInfoRenderer = new OnScreenInfoRenderer(camera, character, hudSceneWidth, hudSceneHeight);
		onScreenRuneRenderer = new OnScreenAnimationRenderer(camera, hudSceneWidth, hudSceneHeight);
		onScreenText = OnScreenTextBox.createInstance(camera, hudSceneWidth, hudSceneHeight);
		worldEdge = new WorldEdge(camera);
	}
	
	public void render(float delta) {
		bossStatusBar.render(delta);
		onScreenText.render(delta);//render the screen text before the player's status bar, to not overdraw the status bar, when using a black background
		statusBar.render(delta);
		onScreenInfoRenderer.render(delta);
		onScreenRuneRenderer.render(delta);
		worldEdge.render(delta);
	}
	
	@Override
	public void dispose() {
		statusBar.dispose();
		bossStatusBar.dispose();
		onScreenInfoRenderer.dispose();
		onScreenText.dispose();
		worldEdge.dispose();
	}
}
