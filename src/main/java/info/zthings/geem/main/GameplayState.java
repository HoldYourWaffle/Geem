package info.zthings.geem.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.world.DebugRenderer;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private DebugRenderer debugRender;
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 2.5f, -4);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		
		debugRender = new DebugRenderer();
	}

	@Override
	public void update(float dt) {
		cam.update();
		
		if (Gdx.input.isKeyPressed(Keys.UP)) cam.position.z += 2*dt;
		cam.lookAt(0, 0, 200);
		
		debugRender.update(dt, cam);
	}

	@Override
	public void render(RenderContext rc) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		debugRender.render(rc, cam);
	}
	
	

	@Override
	public void dispose() {
		debugRender.dispose();
	}
	
	
	
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
	
}
