package info.zthings.geem.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import info.zthings.geem.entities.Ship.ShipSub;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;

public class GeemLoop implements ApplicationListener {
	private ResourceContext rc;
	
	private IState state;
	private Viewport vp;
	private Texture loading;
	
	@Override
	public void create() {
		vp = new StretchViewport(1280, 720);
		vp.apply();
		
		loading = new Texture("loading.png");
		rc = new ResourceContext(new ModelBatch(), new DecalBatch(null), new SpriteBatch(), new ShapeRenderer());
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		if (!rc.updateAss()) {
			rc.sprites.begin();
			rc.sprites.draw(loading, 0, 0);
			rc.sprites.end();
			return;
		} else if (state == null) {
			//setState(new MainMenuState(false));
			
			//TODO hier van ship wisselen
			//setState(new GameplayState(new ShipNormal()));
			//setState(new GameplayState(new ShipUfo()));
			setState(new GameplayState(new ShipSub()));
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
			Gdx.app.exit();
		
		state.update(Gdx.graphics.getDeltaTime());
		state.render(rc);
		
		rc.sprites.resetProjectionMatrix();
		rc.sprites.begin();
		if (Gdx.graphics.getFramesPerSecond() < 60) rc.fntDefault.setColor(Color.RED);
		else rc.fntDefault.setColor(Color.WHITE);
		rc.fntDefault.draw(rc.sprites, "FPS: " + Gdx.graphics.getFramesPerSecond() + ", dt: " + Gdx.graphics.getDeltaTime(), 0, rc.fntDefault.getLineHeight());
		rc.sprites.end();
	}
	
	public static GeemLoop getLoop() {
		return ((GeemLoop)Gdx.app.getApplicationListener());
	}
	
	public static ResourceContext getRC() {
		return getLoop().rc;
	}
	
	@Override
	public void dispose() {
		loading.dispose();
		state.dispose();
		rc.dispose();
	}
	
	public void setState(IState state) {
		if (this.state != null) this.state.dispose();
		this.state = state;
		this.state.create();
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
