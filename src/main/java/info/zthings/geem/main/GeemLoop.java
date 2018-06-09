package info.zthings.geem.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;

public class GeemLoop implements ApplicationListener {
	public static RenderContext rc;
	private IState state;
	private boolean posted;
	private Viewport vp;
	
	@Override
	public void create() {
		vp = new StretchViewport(1280, 720);
		vp.apply();
		
		rc = new RenderContext(new ModelBatch(), new DecalBatch(null), new SpriteBatch(), new ShapeRenderer());
		//setState(new MainMenuState());
		setState(new GameplayState());
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		if (!rc.ass.update()) {
			rc.sprites.begin();
			rc.sprites.draw(rc.loading, 0, 0);
			rc.sprites.end();
			return;
		} else if (!posted) {
			state.postLoad(rc.ass);
			posted = true;
			return;
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
			Gdx.app.exit();
		
		state.update(Gdx.graphics.getDeltaTime());
		state.render(rc);
		
		rc.sprites.resetProjectionMatrix();
		rc.sprites.begin();
		if (Gdx.graphics.getFramesPerSecond() < 60) rc.fnt.setColor(Color.RED);
		else rc.fnt.setColor(Color.WHITE);
		rc.fnt.draw(rc.sprites, "FPS: " + Gdx.graphics.getFramesPerSecond() + ", dt: " + Gdx.graphics.getDeltaTime(), 0, rc.fnt.getLineHeight());
		rc.sprites.end();
	}
	
	@Override
	public void dispose() {
		state.dispose();
		rc.dispose();
	}
	
	public void setState(IState state) {
		posted = false;
		if (this.state != null) this.state.dispose();
		this.state = state;
		this.state.create(rc.ass);
	}
	
	@Override
	public void resize(int width, int height) {
		if (state != null)
			state.resize(width, height);
	}
	
	@Override
	public void pause() {
		if (state != null)
			state.pause();
	}
	
	@Override
	public void resume() {
		if (state != null)
			state.resume();
	}
	
}
