package info.zthings.geem;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import info.zthings.geem.main.GeemLoop;

public class AndroidLauncher extends AndroidApplication {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//config.useGL30 = true;
		initialize(new GeemLoop(), config);
		
		
	}
	
}
