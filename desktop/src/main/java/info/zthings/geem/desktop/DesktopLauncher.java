package info.zthings.geem.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import info.zthings.geem.main.GeemLoop;

public class DesktopLauncher {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		/*config.width = 563;
		config.height = 1000;
		config.y = 10;*/
		config.width = config.height = 400;
		config.height = 1000;
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		new LwjglApplication(new GeemLoop(), config);
	}
	
}
