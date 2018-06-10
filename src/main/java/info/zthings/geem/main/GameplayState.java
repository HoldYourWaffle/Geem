package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Asteroid;
import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.world.DebugRenderer;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private OrthographicCamera camUi;
	
	private Environment env;
	private DebugRenderer debugRenderer;
	//private LevelGenerator lg = new LevelGenerator(10, 10);
	
	private BitmapFont fnt;
	private GlyphLayout glyphDied;
	private Music music;
	
	private final Ship ship;
	private List<Asteroid> obstacles = new ArrayList<>();
	private StarBox stars = new StarBox(5);
	
	private int score;
	private Timer timer;
	private volatile int time = -3;
	private boolean focus = true;
	
	public GameplayState(Ship ship) {
		this.ship = ship;
	}
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 3, -6);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		GeemLoop.rc.decals.setGroupStrategy(new CameraGroupStrategy(cam));
		
		camUi = new OrthographicCamera(1280, 720);
		camUi.position.set(1280/2, 720/2, 0);
		camUi.update();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -0.8f, -0.2f));
		
		fnt = GeemLoop.rc.fntVT323.generateFont(new FreeTypeFontParameter(69));
		glyphDied = new GlyphLayout(fnt, "YOU DIED");
		
		debugRenderer = new DebugRenderer();
		
		music = GeemLoop.rc.ass.get("music/ingame.wav");
		music.setLooping(true);
		
		timer = new Timer();
		timer.scheduleTask(new Task(()->time++), 1, 1);
		timer.start();
		
		ship.update(Gdx.graphics.getDeltaTime(), cam);
		stars.update(Gdx.graphics.getDeltaTime(), cam, ship);
		
		//for (int i = 0; i < 10; i++) nextGap();
	}
	
	/*private void nextGap() {
		ModelInstance mi = new ModelInstance(GeemLoop.rc.ass.get("astroid.g3db", Model.class));
		Vector2 vec = new Vector2(cam.position.z + 100, Math.random() * 10 - 5);
		mi.transform.setTranslation(vec.x, 0, vec.y);
		obstacles.add(new Astroid(mi, vec));
	}*/
	
	boolean debug = true;
	
	@Override
	public void update(float dt) {
		if (ship.hp <= 0 || (!debug && !focus)) return;
		
		if (time >= 0) {
			if (!music.isPlaying()) music.play();
			stars.update(dt, cam, ship);
			ship.update(dt, cam);
		}
		
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		debugRenderer.update(dt, cam);
		
		/*if (ship.position.z > gaps.get(1).getRight().y) { //y in Vector2 = z
			boolean passed = ship.position.x+ship.bounds.getWidth()*ship.modelScale/2 < gaps.get(1).getRight().x+gapWidth/2 &&
							 ship.position.x-ship.bounds.getWidth()*ship.modelScale/2 > gaps.get(1).getRight().x-gapWidth/2;
			
			if (!passed) {
				score--;
				//TO DO animation
				died = ship.hit();
				if (died) {
					GeemLoop.rc.ass.get("sfx/fail.wav", Sound.class).play();
					//TO DO continue
				} else GeemLoop.rc.ass.get("sfx/oof.wav", Sound.class).play(.8F);
			} else {
				score++;
				ship.hp += ship.hp < 1 ? .2F : .1F;
				GeemLoop.rc.ass.get("sfx/yeet.wav", Sound.class).play(.6F, Math.max(1, ship.hp), 0);
			}
			
			gaps.remove(0);
			nextGap();
		}*/
	}
	
	@Override
	public void render(RenderContext rc) {
		//debugRenderer.render(rc, cam);
		rc.sprites.setProjectionMatrix(camUi.combined);
		
		stars.render(rc, cam);
		
		rc.models.begin(cam);
		//gaps.forEach(box->rc.models.render(box.getLeft(), env));
		rc.models.end();
		
		if (ship.hp > 0) {
			ship.render(rc, env, cam);
			
			rc.sprites.begin();
			//time = 2;
			
			if (time >= 0) {
				fnt.setColor(Color.WHITE);
				fnt.draw(rc.sprites, "SPEED ", 10, 720);
				rc.sprites.draw(rc.ass.get("hpbar.png", Texture.class), 170, 690, 0, 0,
						(int) ((1280 - 170 - 10) / 2 * ship.hp), 20);
				fnt.draw(rc.sprites, "TIME  " + time, 10, 665);
				fnt.draw(rc.sprites, "SCORE " + score, 10, 610);
				if (time < 3) {
					fnt.setColor(Color.GREEN);
					fnt.draw(rc.sprites, "GO!", 1280/2-35, 720/2+150);
				}
			} else {
				fnt.setColor(time == -3 ? Color.RED : time == -2 ? Color.ORANGE : Color.GREEN);
				fnt.draw(rc.sprites, ""+Math.abs(time), 1280/2-10, 720/2+150);
			}
			
			rc.sprites.end();
		} else {
			rc.sprites.begin();
			fnt.draw(rc.sprites, glyphDied, 1280/2-glyphDied.width/2, 720/1.3F);
			rc.sprites.end();
		}
		
		ModelInstance mi = new ModelInstance(rc.asteroidModel);
		mi.transform.setToTranslation(ship.position);
		rc.models.begin(cam);
		rc.models.render(mi, env);
		rc.models.end();
	}
	
	
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
		fnt.dispose();
	}
	
	
	
	
	@Override public void pause() { focus = false; }
	@Override public void resume() { focus = true; }
	@Override public void resize(int width, int height) {}
}
