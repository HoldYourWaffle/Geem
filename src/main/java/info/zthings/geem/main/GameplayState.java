package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Asteroid;
import info.zthings.geem.entities.Bullet;
import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;
import info.zthings.geem.world.DebugRenderer;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private OrthographicCamera camUi;
	
	private Environment env;
	private DebugRenderer debugRenderer;
	
	private BitmapFont fnt;
	private GlyphLayout glyphDied;
	private Music music;
	
	private final Ship ship;
	private List<Asteroid> obstacles = new ArrayList<>();
	private List<Bullet> bullets = new ArrayList<>();
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
	
	boolean debug = false;
	
	@Override
	public void update(float dt) {
		if (debug) time = 4;
		if (ship.hp <= 0 || (!debug && !focus)) return;
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE) && time >= 0) //NOW fix bullet starting point
			bullets.add(new Bullet(ship));
		
		bullets.forEach(b->{
			b.update(dt, cam);
			//obstacles.removeIf(a->a.getCurrentBounds().intersects(b.getCurrentBounds())); NOW shoot asteroids (+ sfx & explosion)
		});
		bullets.removeIf(b->b.position.z - ship.position.z > 100);
		obstacles.forEach(a->a.update(dt, cam));
		obstacles.removeIf(a->a.position.z < cam.position.z);
		
		//bullets.clear();
		
		stars.update(dt, cam, ship);
		ship.update(dt, cam);
		
		if (Math.random() < .3) {
			obstacles.add(new Asteroid((float)(80*Math.random() - 40), ship.position.z + 120));
		}
		if (time >= 0 && !music.isPlaying()) music.play();
		
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		debugRenderer.update(dt, cam);
	}
	
	@Override
	public void render(ResourceContext rc) {
		//debugRenderer.render(rc, cam);
		rc.sprites.setProjectionMatrix(camUi.combined);
		
		stars.render(rc, cam);
		rc.models.begin(cam);
		bullets.forEach(b->b.render(rc, env));
		obstacles.forEach(a->a.render(rc, env));
		
		if (ship.hp > 0) {
			ship.render(rc, env);
			rc.models.end();
			
			rc.sprites.begin();
			
			if (time >= 0) {
				fnt.setColor(Color.WHITE);
				fnt.draw(rc.sprites, "SPEED ", 10, 720);
				rc.sprites.draw(rc.atlas.findRegion("hpbar"), 170, 690, (int) ((1280 - 170 - 10) / 2 * ship.hp), 20);
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
			rc.models.end();
			
			rc.sprites.begin();
			fnt.draw(rc.sprites, glyphDied, 1280/2-glyphDied.width/2, 720/1.3F);
			rc.sprites.end();
		}
	}
	
	
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
		fnt.dispose();
	}
	
	
	
	long timerDelay;
	
	@Override
	public void resume() {
		timer.delay(TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - timerDelay);
	    timer.start();
		focus = true;
	}
	
	@Override
	public void pause() {
		timerDelay = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
		timer.stop();
		focus = false;
	}
	
	
	@Override public void resize(int width, int height) {}
}
