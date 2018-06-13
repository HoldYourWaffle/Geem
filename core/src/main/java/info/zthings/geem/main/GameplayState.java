package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import info.zthings.geem.entities.Asteroid;
import info.zthings.geem.entities.Bullet;
import info.zthings.geem.entities.Explosion;
import info.zthings.geem.entities.FuelCan;
import info.zthings.geem.entities.Ship;
import info.zthings.geem.entities.StarBox;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.ResourceContext;
import info.zthings.geem.ui.Button;
import info.zthings.geem.ui.TextButton;
import info.zthings.geem.world.DebugRenderer;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private OrthographicCamera camUi;
	
	private Environment env;
	private Music music, beep;
	private DebugRenderer debugRenderer;
	
	private TextButton btnRestart;
	private Button btnLeft, btnRight, btnFire;
	private GlyphLayout glyphDied, glyphScore;
	
	private final Ship ship;
	private StarBox stars = new StarBox(20);
	private final FuelCan fuelcan;
	private List<Asteroid> obstacles = new ArrayList<>();
	private List<Bullet> bullets = new ArrayList<>();
	private List<Explosion> explosions = new ArrayList<>();
	
	private int kills;
	private Timer timer;
	private volatile int time = -3;
	private boolean focus = true, fireJustClicked;
	
	private final int xb = 7;
	
	public GameplayState(Ship ship) {
		this.ship = ship;
		this.fuelcan = new FuelCan();
	}
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 3, -6);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		GeemLoop.getRC().decals.setGroupStrategy(new CameraGroupStrategy(cam));
		
		camUi = new OrthographicCamera(1280, 720);
		camUi.position.set(1280/2, 720/2, 0);
		camUi.update();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -0.8f, -0.2f));
		
		debugRenderer = new DebugRenderer();
		glyphDied = new GlyphLayout(GeemLoop.getRC().fntUi, "YOU DIED");
		
		music = GeemLoop.getRC().ass.get("music/ingame.wav");
		music.setLooping(true);
		beep = GeemLoop.getRC().ass.get("sfx/beep.wav");
		beep.setLooping(true);
		
		timer = new Timer();
		timer.scheduleTask(new Task(this::onSecond), 1, 1);
		timer.start();
		
		btnRestart = new TextButton("Restart", "button",
				1280/2 - GeemLoop.getRC().atlas.findRegion("button1").getRegionWidth()/2, 720/2 - GeemLoop.getRC().atlas.findRegion("button1").getRegionHeight()*2,
				false, false, ()->GeemLoop.getLoop().setState(new MainMenuState(true)));
		
		btnLeft = new Button("control", 0, 100, null, true, false);
		btnRight = new Button("control", 0, 100, null, false, false);
		btnFire = new Button("pew", 0, 100, null, false, false);
		
		ship.update(Gdx.graphics.getDeltaTime());
		stars.update(Gdx.graphics.getDeltaTime(), cam, ship);
	}
	
	private void onSecond() {
		time++;
		
		double r = Math.random(), chance = ((60 - ship.fuel)/100F); //TODO balance (fuel)
		System.out.println(chance * 100);
		if (ship.fuel < 60 && (chance > r || ship.fuel < 25)
				&& fuelcan.position.z < cam.position.z) {
			fuelcan.position.set((float)Math.random() * 2*xb - xb, ship.position.y + .3F, ship.position.z + 100);
		}
	}
	
	boolean debug = false;
	
	@Override
	public void update(float dt) {
		if (debug) time = 4;
		
		fuelcan.update(dt);
		
		bullets.forEach(b->b.update(dt));
		bullets.removeIf(b->b.position.z - ship.position.z > 100 || b.destroyed);
		
		explosions.forEach(e->e.update(dt));
		explosions.removeIf(e->e.destroyed);
		
		obstacles.forEach(a->a.update(dt));
		obstacles.removeIf(a->a.position.z < cam.position.z || a.destroyed);
		
		if (ship.hp <= 0 || (!debug && !focus)) {
			if (ship.hp <= 0)
				btnRestart.update(dt, camUi);
			return;
		}
		
		btnLeft.update(dt, camUi);
		btnRight.update(dt, camUi);
		btnFire.update(dt, camUi);
		
		if (time == 0) ship.fuel = 100;
		
		if (time >= 0 && !music.isPlaying())
			music.play();
		
		if (ship.fuel < 25) {
			if (!beep.isPlaying())
				beep.play();
		} else beep.stop();
		
		ship.movingLeft = btnLeft.isClicked();
		ship.movingRight = btnRight.isClicked();
		
		stars.update(dt, cam, ship);
		ship.update(dt);
		
		if (ship.position.x < -xb) ship.position.x = -xb;
		else if (ship.position.x > xb) ship.position.x = xb;
		
		final Vector3 vecBuf = new Vector3();
		for (Asteroid a : obstacles) {
			if (a.position.x < -xb || a.position.x > xb) continue;
			
			if (a.getCurrentBounds().intersects(fuelcan.getCurrentBounds())) {
				a.destroyed = true;
				continue;
			}
			
			if (a.getCurrentBounds().intersects(ship.getCurrentBounds())) {
				explosions.add(new Explosion(ship.position.add2(0, 0, -.1F)));
				a.destroyed = true;
				kills--;
				if (!ship.hit(a.hard)) //!died from hit
					GeemLoop.getRC().ass.get("sfx/oof.wav", Sound.class).play(.8F);
				continue;
			} else for (Bullet b : bullets) {
				vecBuf.set(b.position.x, a.getCurrentBounds().getCenterY(), b.position.z);				
				if (a.getCurrentBounds().contains(vecBuf)) {
					b.destroyed = true;
					float dist = a.position.z - ship.position.z;
					GeemLoop.getRC().ass.get("sfx/biem.wav", Sound.class).play((dist < 60 ? 1 - dist / 60 : 0) * .8F);
					if (a.hit()) { //asteroid dead
						explosions.add(new Explosion(a.position));
						kills++;
						continue;
					}
				}
			}
		}
		
		if (ship.getCurrentBounds().intersects(fuelcan.getCurrentBounds())) {
			fuelcan.position.set(0, 0, 0);
			ship.fuel = 100;
			GeemLoop.getRC().ass.get("sfx/fuel.wav", Sound.class).play();
		}
		
		boolean fire = false;
		if (btnFire.isClicked() && !fireJustClicked) {
			fire = true;
			fireJustClicked = true;
		} else if (!btnFire.isClicked()) fireJustClicked = false;
		
		if ((Gdx.input.isKeyJustPressed(Keys.SPACE) || fire) && time >= 0) {
			if (ship.ammo > 0) {
				bullets.add(new Bullet(ship));
				ship.ammo--;
			} else GeemLoop.getRC().ass.get("sfx/click.wav", Sound.class).play();
		}
		
		if (Math.random() < .2 + (.8 / 6000) * ship.position.z) { //TODO balance (asteroid density)
			Asteroid a = new Asteroid((float)(80*Math.random() - 40), ship.position.z + 120);
			if (obstacles.stream().noneMatch(ac->ac.getCurrentBounds().intersects(a.getCurrentBounds())))
				obstacles.add(a);
		}
		
		cam.position.z = ship.position.z - 6;
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		//debugRenderer.update(dt, cam);
		
		
		if (ship.hp <= 0) {
			GeemLoop.getRC().updateHighscore(getScore());
			glyphScore = new GlyphLayout(GeemLoop.getRC().fntUi, "SCORE: " + getScore());
			timer.stop();
			beep.stop();
			music.stop();
			GeemLoop.getRC().ass.get("sfx/fail.wav", Sound.class).play(.8F);
		}
	}
	
	private int getScore() {
		return (int) (kills + ship.position.z / 100);
	}
	
	@Override
	public void render(ResourceContext rc) {
		//debugRenderer.render(rc, cam);
		rc.sprites.setProjectionMatrix(camUi.combined);
		
		rc.models.begin(cam);
		fuelcan.render(rc, env);
		bullets.forEach(b->b.render(rc, env));
		obstacles.forEach(a->a.render(rc, env));
		
		if (ship.hp > 0) {
			ship.render(rc, env);
			rc.models.end();
			
			rc.sprites.begin();
			if (time >= 0) {
				switch (ship.hitsLeft()) {
					case 0: throw new AssertionError("I should be dead");
					case 1: rc.fntUi.setColor(Color.RED); break;
					case 2: rc.fntUi.setColor(Color.ORANGE); break;
					default: rc.fntUi.setColor(Color.GREEN);
				}
				rc.fntUi.draw(rc.sprites, "HP " + ship.hp + "%", 10, 720);
				
				if (ship.fuel < 25) rc.fntUi.setColor(Color.RED);
				else if (ship.fuel < 50) rc.fntUi.setColor(Color.ORANGE);
				else rc.fntUi.setColor(Color.GREEN);
				rc.fntUi.draw(rc.sprites, "FUEL " + (int)Math.ceil(ship.fuel) + "%", 10, 665);
				
				if (ship.ammo == 0) rc.fntUi.setColor(Color.RED);
				else if (ship.ammo <= 3) rc.fntUi.setColor(Color.ORANGE);
				else rc.fntUi.setColor(Color.GREEN);
				rc.fntUi.draw(rc.sprites, "AMMO " + ship.ammo, 10, 610);
				
				rc.fntUi.setColor(Color.WHITE);
				rc.fntUi.draw(rc.sprites, "TIME  " + time, 10, 555);
				
				rc.fntUi.setColor(getScore() > rc.getHighscore() ? Color.GOLD : Color.WHITE);
				rc.fntUi.draw(rc.sprites, "SCORE " + getScore(), 10, 500);
				
				
				if (time < 3) {
					rc.fntUi.setColor(Color.GREEN);
					rc.fntUi.draw(rc.sprites, "GO!", 1280/2-35, 720/2+150);
				}
			} else {
				rc.fntUi.setColor(time == -3 ? Color.RED : time == -2 ? Color.ORANGE : Color.GREEN);
				rc.fntUi.draw(rc.sprites, ""+Math.abs(time), 1280/2-10, 720/2+150);
			}
			
			rc.sprites.end();
		} else {
			rc.models.end();
			
			rc.sprites.begin();
			btnRestart.render(rc);
			rc.fntUi.setColor(Color.WHITE);
			rc.fntUi.draw(rc.sprites, glyphDied, 1280/2-glyphDied.width/2, 720/1.3F);
			
			rc.fntUi.draw(rc.sprites, glyphScore, 1280/2-glyphScore.width/2, 440);
			rc.fntUi.draw(rc.sprites, rc.getHighscoreGlyph(), 1280/2-rc.getHighscoreGlyph().width/2, 380);
			
			rc.sprites.end();
		}
		
		stars.render(rc, cam);
		explosions.forEach(e->e.render(rc));
		rc.decals.flush();
		
		rc.sprites.begin();
		rc.sprites.setColor(1, 1, 1, .5F);
		
		float f = 1.2F;
		btnLeft.render(rc);
		btnLeft.setLocation(75, 75);
		btnLeft.setSize((int)(127*f), (int)(58*f));
		
		btnRight.render(rc);
		btnRight.setLocation(300, 75);
		btnRight.setSize((int)(127*f), (int)(58*f));
		
		btnFire.render(rc);
		btnFire.setLocation(1280 - 200, 50);
		
		rc.sprites.setColor(Color.WHITE);
		rc.sprites.end();
	}
	
	
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
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
