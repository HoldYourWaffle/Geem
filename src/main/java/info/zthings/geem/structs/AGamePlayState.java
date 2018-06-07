package info.zthings.geem.structs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;

import info.zthings.geem.main.GeemLoop;

public abstract class AGamePlayState implements IState {
	public static final float cameraFar = 100F;
	private PerspectiveCamera cam;
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = cameraFar;
		cam.update();
		
		GeemLoop.rc.decals.setGroupStrategy(new CameraGroupStrategy(cam));
	}
	
	@Override
	public void dispose() {
		GeemLoop.rc.decals.setGroupStrategy(null);
	}
	
}
