package info.zthings.geem.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import info.zthings.geem.world.LevelGenerator;

public class GeemLoop implements ApplicationListener {
	private ShapeRenderer sr;
	private SpriteBatch sb;
	private BitmapFont bf;
	
	private OrthographicCamera cam;
	private Viewport view;
	
	private LevelGenerator lg;
	
	private final int ww = 10, wh = 25;
	
	@Override
	public void create() {
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);
		
		lg = new LevelGenerator(ww, wh);
		sb = new SpriteBatch();
		bf = new BitmapFont();
		bf.getData().setScale(3f);
		
		cam = new OrthographicCamera();
		view = new StretchViewport(ww, wh, cam);
		
		cam.position.set(ww/2f-.1f, wh/2f-.1f, 0);
		view.apply();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		
		sr.setProjectionMatrix(cam.combined);
		sb.setProjectionMatrix(cam.combined);
		
		sr.begin();
		
		sr.setColor(Color.BLACK);
		for (int x = 0; x < ww; x++) 
			sr.line(x, 0, x, wh);
		
		for (int y = 0; y < wh; y++) {
			if (y % 2 == 0) sr.setColor(Color.GOLD);
			sr.line(0, y, ww, y);
			sr.setColor(Color.BLACK);
		}
		
		sr.setColor(Color.RED);
		sr.line(0, 0, ww, 0);		
		sr.setColor(Color.BLUE);
		sr.line(0, 0, 0, wh);
		
		sr.end();
		
		lg.render(sr);
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) lg.next();
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.R)) lg.reset();
		if (Gdx.input.isKeyJustPressed(Keys.G)) lg.generate(12);
	}
	
	
	@Override
	public void dispose() {
		sr.dispose();
		sb.dispose();
		bf.dispose();
	}
	
	
	
	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
}
