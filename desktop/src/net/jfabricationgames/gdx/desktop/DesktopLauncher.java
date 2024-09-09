package net.jfabricationgames.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.desktop.log.LogConfiguration;

public class DesktopLauncher {
	
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = createApplicationConfiguration();
		Game game = Game.createInstance(() -> configureLog());
		new LwjglApplication(game, config);
	}
	
	private static LwjglApplicationConfiguration createApplicationConfiguration() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Legend of Asamnir 2 - Rise of the Chaos Wizard";
		config.width = 1200;
		config.height = 800;
		config.vSyncEnabled = true;
		config.fullscreen = true;
		return config;
	}
	
	private static void configureLog() {
		new LogConfiguration().configureLog();
	}
}
