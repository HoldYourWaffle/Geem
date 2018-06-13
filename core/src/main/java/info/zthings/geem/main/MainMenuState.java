package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;
import info.zthings.geem.ui.Button;
import info.zthings.geem.ui.TextButton;

public class MainMenuState implements IState {
	private Ship selectedShip = null;
	
	private boolean stateShip = false, fading = false;
	
	private List<Vector2> stars = new ArrayList<>(100);
	
	private Music music;
	
	private Button btnStart, btnBack, btnGo;
	private GlyphLayout glyphTitle, glyphCharacter;
	
	private OrthographicCamera cam;
	
	public MainMenuState(boolean charScreen) {//, Class<? extends Ship> ship) {
		this.stateShip = charScreen;
		
	}

	@Override
	public void create() {
		cam = new OrthographicCamera(1280, 720);
		cam.position.set(1280/2, 720/2, 0);
		
		glyphTitle = new GlyphLayout(GeemLoop.getRC().fntTitle, "GEEM");
		glyphCharacter = new GlyphLayout(GeemLoop.getRC().fntTitle, "SELECT SHIP");
		
		music = GeemLoop.getRC().ass.get("music/circus.wav");
		music.setLooping(true);
		music.setVolume(.1f);
		music.play();
		
		selectedShip = new Ship.ShipNormal();
		
		btnStart = new TextButton("Start", "button",
				1280/2 - GeemLoop.getRC().atlas.findRegion("button1").getRegionWidth()/2, 720/2 - GeemLoop.getRC().atlas.findRegion("button1").getRegionHeight(),
				false, false, ()->stateShip = true);
		
		btnGo = new TextButton("Go!", "button",
				1280/2 - GeemLoop.getRC().atlas.findRegion("button1").getRegionWidth()/2, GeemLoop.getRC().atlas.findRegion("button1").getRegionHeight()/2,
				false, false, ()->{
					fading = true;
					music.stop();
					Timer t = new Timer();
					t.scheduleTask(new Task(()->
						GeemLoop.getLoop().setState(new GameplayState(selectedShip))
					), .25F);
					t.start();
				});
		
		btnBack = new Button("arrow", 50, GeemLoop.getRC().atlas.findRegion("button1").getRegionHeight()/2, ()->stateShip = false, false, false);
		btnBack.setSize(80, 80);
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
		rc.sprites.begin();
		btnStart.render(rc);
		rc.fntTitle.draw(rc.sprites, glyphTitle, 1280/2 - glyphTitle.width/2, 720 - glyphTitle.height);
		rc.sprites.end();
	}
	
	private void renderShipSelect(ResourceContext rc) {
		rc.sprites.begin();
		btnBack.render(rc);
		btnGo.render(rc);
		rc.fntTitle.draw(rc.sprites, glyphCharacter, 1280/2 - glyphCharacter.width/2, 720 - glyphCharacter.height);
		//rc.sprites.setColor(1, 1, 1, .4f);
		rc.sprites.end();
	}
	
	@Override public void dispose() {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
	
}
