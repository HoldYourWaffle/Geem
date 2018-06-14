package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;
import info.zthings.geem.ui.Button;
import info.zthings.geem.ui.TextButton;

public class MainMenuState implements IState {
	//private Ship selectedShip = null;
	private ShipSelection selected = ShipSelection.NORMAL;
	private boolean stateShip = false, fading = false;
	
	private enum ShipSelection { NORMAL, SUB, UFO }
	
	private OrthographicCamera cam;
	private PerspectiveCamera cam3d;
	private List<Vector2> stars = new ArrayList<>(100);
	private Music music;
	
	private Button btnStart, btnBack, btnGo;
	private GlyphLayout glyphTitle, glyphCharacter;
	private ModelInstance miNormal, miSub, miUfo;
	
	public MainMenuState(boolean charScreen, Class<? extends Ship> ship) {
		this.stateShip = charScreen;
		
		if (ship.equals(Ship.ShipNormal.class)) selected = ShipSelection.NORMAL;
		else if (ship.equals(Ship.ShipSub.class)) selected = ShipSelection.SUB;
		else if (ship.equals(Ship.ShipUfo.class)) selected = ShipSelection.UFO;
	}
	
	@Override
	public void create() {
		cam = new OrthographicCamera(1280, 720);
		cam.position.set(1280/2, 720/2, 0);
		
		cam3d = new PerspectiveCamera(69, 1280, 720);
		cam3d.position.set(0, 0, -1);
		cam3d.lookAt(0, 0, 200);
		cam3d.near = 0.1f;
		cam3d.far = 100;
		cam3d.update();
		
		glyphTitle = new GlyphLayout(GeemLoop.getRC().fntTitle, "GEEM");
		glyphCharacter = new GlyphLayout(GeemLoop.getRC().fntTitle, "SELECT SHIP");
		
		music = GeemLoop.getRC().ass.get("music/circus.wav");
		music.setLooping(true);
		music.setVolume(.1f);
		music.play();
		
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
						GeemLoop.getLoop().setState(new GameplayState(makeShip()))
					), .25F);
					t.start();
				});
		
		btnBack = new Button("arrow", 50, GeemLoop.getRC().atlas.findRegion("button1").getRegionHeight()/2, ()->stateShip = false, false, false);
		btnBack.setSize(80, 80);
		
		miNormal = new ModelInstance(GeemLoop.getRC().shipNormalModel);
		miNormal.transform.rotate(0, 1, 0, 180);
		miSub = new ModelInstance(GeemLoop.getRC().shipSubModel);
		miSub.transform.setToScaling(.002F, .002F, .002F);
		miSub.transform.rotate(0, 1, 0, 70);
		miUfo = new ModelInstance(GeemLoop.getRC().shipUfoModel);
		miUfo.transform.setToScaling(.02F, .02F, .02F);
	}
	
	private Ship makeShip() {
		switch (selected) {
			case NORMAL: return new Ship.ShipNormal();
			case SUB: return new Ship.ShipSub();
			case UFO: return new Ship.ShipUfo();
			default: throw new AssertionError("bruh OwO");
		}
	}
	
	@Override
	public void update(float dt) {
		cam.update();
		
		if (stateShip) {
			btnBack.update(dt, cam);
			btnGo.update(dt, cam);
			
			if (Gdx.input.isTouched()) {
				Vector2 point = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).flatten();
				//System.out.println(point);
				Rectangle normal = new Rectangle(479, 210, 325, 225),
							sub = new Rectangle(830, 210, 325, 225),
							ufo = new Rectangle(125, 210, 325, 225);
				
				if (normal.contains(point)) selected = ShipSelection.NORMAL;
				else if (sub.contains(point)) selected = ShipSelection.SUB;
				else if (ufo.contains(point)) selected = ShipSelection.UFO;
			}
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
		rc.sprites.end();
		
		cam3d.position.set(0, 2, -3);
		cam3d.lookAt(0, 0, 10);
		cam3d.far = 10;
		cam3d.update();
		
		float d = -150*Gdx.graphics.getDeltaTime();
		int selBoxX;
		
		miNormal.transform.setTranslation(0, 1.3F, 0);
		miSub.transform.setTranslation(-2, 1.50F, 0);
		miUfo.transform.setTranslation(2, .75F, 0);
		
		switch (selected) {
			case NORMAL:
				miNormal.transform.rotate(0, 1, 0, d);
				selBoxX = 479;
				break;
			case SUB:
				miSub.transform.rotate(0, 1, 0, d);
				selBoxX = 830;
				break;
			case UFO:
				miUfo.transform.rotate(0, 1, 0, d/1.125F);
				selBoxX = 125;
				break;
			default: throw new AssertionError("gwut");
			
		}
		
		rc.models.begin(cam3d);
		rc.models.render(miNormal);
		rc.models.render(miSub);
		rc.models.render(miUfo);
		rc.models.end();
		
		rc.shapes.begin(ShapeType.Line);
		rc.shapes.setColor(Color.RED);
		rc.shapes.rect(selBoxX, 210, 325, 225);
		rc.shapes.setColor(Color.WHITE);
		rc.shapes.end();
	}
	
	@Override public void dispose() {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
	
}
