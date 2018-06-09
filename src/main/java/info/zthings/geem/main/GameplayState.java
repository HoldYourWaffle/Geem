package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.GameMode;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.world.DebugRenderer;
import info.zthings.geem.world.LevelGenerator;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private OrthographicCamera camUi;
	private Environment env;
	private DebugRenderer debugRender;
	private LevelGenerator lg = new LevelGenerator(10, 10);
	
	private BitmapFont fnt;
	private GlyphLayout glyphDied;
	
	private Model shipModel, box;
	private Ship ship;
	
	private List<Astroid> obstacles = new ArrayList<>();
	private final int gapWidth = 4;
	
	private boolean died = false;
	
	private final GameMode mode;
	private int score;
	
	private Timer timer;
	private volatile int time;
	
	public GameplayState(GameMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void create(AssetManager ass) {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 3, -5);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		
		camUi = new OrthographicCamera(1280, 720);
		camUi.position.set(1280/2, 720/2, 0);
		camUi.update();
		
		env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -0.8f, -0.2f));
		
        fnt = GeemLoop.rc.fntVT323.generateFont(new FreeTypeFontParameter(69));
        glyphDied = new GlyphLayout(fnt, "YOU DIED");
        
		debugRender = new DebugRenderer();
		ass.load("ships/ship.g3db", Model.class);
		ass.load("hpbar.png", Texture.class);
		ass.load("sfx/oof.wav", Sound.class);
		ass.load("sfx/fail.wav", Sound.class);
		ass.load("sfx/yeet.wav", Sound.class);
		ass.load("music/ingame.mp3", Music.class);
		
		ModelBuilder boxbuilder = new ModelBuilder();
		box = boxbuilder.createBox(gapWidth, .5F, .5F, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		
		for (int i = 0; i < 10; i++) nextGap();
	}
	
	private void nextGap() {
		ModelInstance gap = new ModelInstance(box);
		Vector2 vec = lg.next();
		vec.add(-5, 0);
		//vec.set(0, vec.y);
		//vec.set(0, 10);
		
		gap.transform.setTranslation(vec.x, 0, vec.y);
		astroid.add(new Astroid(GeemLoop.rc.ass.get("astroid.g3db", Model.class), cam.position.z + 100));
	}
	
	@Override
	public void postLoad(AssetManager ass) {
		shipModel = ass.get("ships/ship.g3db", Model.class);
		ship = new Ship(new ModelInstance(shipModel), 3, 10, .5F, 1.5F);
		
		Music m = ass.get("music/ingame.mp3", Music.class);
		m.setVolume(.25F);
		m.setLooping(true);
		m.play();
		
		timer = new Timer();
		timer.scheduleTask(new Task(()->time++), 0, 1);
		timer.start();
	}

	@Override
	public void update(float dt) {
		if (died) return;
		
		ship.update(dt, cam);
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		debugRender.update(dt, cam);
		
		if (ship.position.z > gaps.get(1).getRight().y) { //y in Vector2 = z
			boolean passed = ship.position.x+ship.bounds.getWidth()*ship.modelScale/2 < gaps.get(1).getRight().x+gapWidth/2 &&
							 ship.position.x-ship.bounds.getWidth()*ship.modelScale/2 > gaps.get(1).getRight().x-gapWidth/2;
			
			if (!passed) {
				score--;
				//TODO animation
				died = ship.hit();
				if (died) {
					GeemLoop.rc.ass.get("sfx/fail.wav", Sound.class).play();
					//TODO continue
				} else GeemLoop.rc.ass.get("sfx/oof.wav", Sound.class).play(.8F);
			} else {
				score++;
				ship.hp += ship.hp < 1 ? .2F : .1F;
				GeemLoop.rc.ass.get("sfx/yeet.wav", Sound.class).play(.6F, Math.max(1, ship.hp), 0);
			}
			
			gaps.remove(0);
			nextGap();
		}
	}
	
	@Override
	public void render(RenderContext rc) {
		debugRender.render(rc, cam);
		rc.sprites.setProjectionMatrix(camUi.combined);
		//rc.shapes.setProjectionMatrix(camUi.combined);

		rc.models.begin(cam);
		gaps.forEach(box->rc.models.render(box.getLeft(), env));
		rc.models.end();
		
		if (!died) {
			ship.render(rc, env, cam);
			
			rc.sprites.begin();
			fnt.draw(rc.sprites, "SPEED ", 10, 720);
			fnt.draw(rc.sprites, "TIME  "+time, 10, 665);
			fnt.draw(rc.sprites, "" + String.valueOf(score/(float)time).substring(0, 3), 225 + (int)Math.max(25 * (int)Math.log10(score), 25 * (int)Math.log10(time)), 638);
			if (mode == GameMode.INFINITE)
				fnt.draw(rc.sprites, "SCORE "+score, 10, 610);
			
			rc.sprites.draw(rc.ass.get("hpbar.png", Texture.class), 170, 690, 0, 0, (int)((1280-170-10)/2 * ship.hp), 20);
			rc.sprites.end();
		} else {
			rc.sprites.begin();
			fnt.draw(rc.sprites, glyphDied, 1280/2-glyphDied.width/2, 720/1.3F);
			rc.sprites.end();
		}
	}
	
	

	@Override
	public void dispose() {
		debugRender.dispose();
		box.dispose();
	}
	
	
	
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void resize(int width, int height) {}
}
