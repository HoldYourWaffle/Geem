package info.zthings.geem.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.world.DebugRenderer;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private DebugRenderer debugRender;
	
	private AssetManager ass;
	private Model shipModel;
	private Ship ship;
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 3, -4);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		
		debugRender = new DebugRenderer();
		ass = new AssetManager();
		
		ass.load("ships/ship.g3db", Model.class);
		ass.finishLoading();
		
		shipModel = ass.get("ships/ship.g3db", Model.class);
		ship = new Ship(new ModelInstance(shipModel), 3, 8, 1);
	}

	@Override
	public void update(float dt) {
		ship.update(dt, cam);
		
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		debugRender.update(dt, cam);
	}
	
	@Override
	public void render(RenderContext rc) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		debugRender.render(rc, cam);
		ship.render(rc, cam);
	}
	
	

	@Override
	public void dispose() {
		debugRender.dispose();
		ass.dispose();
	}
	
	
	
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
	
}
