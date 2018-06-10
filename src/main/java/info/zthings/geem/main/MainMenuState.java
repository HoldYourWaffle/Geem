package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;
import info.zthings.geem.ui.Button;

public class MainMenuState implements IState {
	private Ship selectedShip = null;
	
	private boolean stateShip = false, fading = false;
	
	private List<Vector2> stars = new ArrayList<>(100);
	
	private TextureAtlas textures;
	
	private BitmapFont fntBtn, fntTitle;
	private Music music;
	
	private Button btnStart, btnBack, btnGo;
	private GlyphLayout glyphTitle, glyphCharacter;
	
	private OrthographicCamera cam;
	
	@Override
	public void create() {
		cam = new OrthographicCamera(1280, 720);
		cam.position.set(1280/2, 720/2, 0);
		
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.borderWidth = 2;
		ftfp.size = 128;
		fntTitle = GeemLoop.rc.fntOswald.generateFont(ftfp);
		
		ftfp.borderWidth = 0;
		ftfp.color = Color.BLACK;
		ftfp.size = 38;
		fntBtn = GeemLoop.rc.fntVT323.generateFont(ftfp);
		
		glyphTitle = new GlyphLayout(fntTitle, "GEEM");
		glyphCharacter = new GlyphLayout(fntTitle, "SELECT SHIP");
		
		textures = GeemLoop.rc.ass.get("mainmenu.atlas"); //TODO update atlas
		
		music = GeemLoop.rc.ass.get("music/circus.wav");
		music.setLooping(true);
		music.setVolume(.1f);
		music.play();
		
		selectedShip = new Ship.ShipNormal();
		
		btnStart = new Button("Start", fntBtn, Color.BLACK, "button", textures,
				1280/2 - textures.findRegion("button1").getRegionWidth()/2, 720/2 - textures.findRegion("button1").getRegionHeight(),
				()->stateShip = true, t->{});
		
		btnGo = new Button("Go!", fntBtn, Color.BLACK, "button", textures,
				1280/2 - textures.findRegion("button1").getRegionWidth()/2, textures.findRegion("button1").getRegionHeight()/2,
				()->{
					fading = true;
					music.stop();
					Timer t = new Timer();
					t.scheduleTask(new Task(()->{
						((GeemLoop)Gdx.app.getApplicationListener()).setState(new GameplayState(selectedShip));
					}), .25F);
					t.start();
				}, t->{});
		
		btnBack = new Button("", fntBtn, Color.BLACK, "arrow", textures, 50, textures.findRegion("button1").getRegionHeight()/2, ()->stateShip = false, t->t.flip(true, false));
		btnBack.setSize(50, 50);
	}
	
	@Override
	public void update(float dt) {
		cam.update();
		
		if (stateShip) {
			btnBack.update(dt, cam);
			btnGo.update(dt, cam);
		} else btnStart.update(dt, cam);
		
		if (stars.size() < 300) {
			stars.add(new Vector2(0, Math.random() * 720));
			stars.add(new Vector2(0, Math.random() * 720));
		}
		
		stars.forEach(vec->vec.add(500 * dt, 0));
		stars.removeIf(vec->vec.x > 1300);
	}
	
	@Override
	public void render(ResourceContext rc) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		rc.shapes.setProjectionMatrix(cam.combined);
		rc.sprites.setProjectionMatrix(cam.combined);
		
		if (fading) return;
		
		rc.shapes.begin(ShapeType.Filled);
		stars.forEach(vec->rc.shapes.circle(vec.x, vec.y, 1));
		rc.shapes.end();
		
		if (stateShip) renderShipSelect(rc);
		else renderMain(rc);
	}
	
	private void renderMain(ResourceContext rc) {
		btnStart.render(rc);
		rc.sprites.begin();
		fntTitle.draw(rc.sprites, glyphTitle, 1280/2 - glyphTitle.width/2, 720 - glyphTitle.height);
		rc.sprites.end();
	}
	
	private void renderShipSelect(ResourceContext rc) {
		btnBack.render(rc);
		btnGo.render(rc);
		
		rc.sprites.begin();
		fntTitle.draw(rc.sprites, glyphCharacter, 1280/2 - glyphCharacter.width/2, 720 - glyphCharacter.height);
		//rc.sprites.setColor(1, 1, 1, .4f);
		rc.sprites.end();
	}
	
	@Override
	public void dispose() {
		System.out.println("Dispose");
		music.dispose();
		fntBtn.dispose();
		fntTitle.dispose();
	}
	
	
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
	
}
