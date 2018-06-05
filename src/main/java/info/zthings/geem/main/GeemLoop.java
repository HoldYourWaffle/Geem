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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import info.zthings.geem.world.LevelGenerator;

public class GeemLoop implements ApplicationListener {
	private ShapeRenderer sr;
	private SpriteBatch sb;
	private BitmapFont bf;
	
	private OrthographicCamera cam;
	private Viewport view;
	
	private LevelGenerator lg = new LevelGenerator(10);
	
	@Override
	public void create() {
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);
		
		sb = new SpriteBatch();
		bf = new BitmapFont();
		bf.getData().setScale(3f);
		
		cam = new OrthographicCamera();
		view = new FitViewport(10, 25, cam);
		
		//cam.rotate(180);
		cam.position.set(0, 12.5F, 0);
		view.apply();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(0, .5F, 0, 1);
		
		sr.setProjectionMatrix(cam.combined);
		sb.setProjectionMatrix(cam.combined);
		
		sr.begin();
		sr.setColor(Color.BLACK);
		for (int x = -5; x < 5; x++)
			sr.line(x, 0, x, 25);
		for (int y = 0; y < 25; y++)
			sr.line(-5, y, 5, y);
		sr.end();
		
		lg.render(sr);
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) lg.next();
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			lg = new LevelGenerator(10);
			for (int i=0; i<11; i++) lg.next();
		}
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
