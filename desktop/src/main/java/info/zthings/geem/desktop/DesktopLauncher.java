package info.zthings.geem.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import info.zthings.geem.main.GeemLoop;

public class DesktopLauncher {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		/*config.width = 1280;
		config.height = 720;*/
		config.width = 400;
		config.height = 1000;
		config.y = 10;
		
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		new LwjglApplication(new GeemLoop(), config);
	}
	
}
