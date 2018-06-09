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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.GameMode;
import info.zthings.geem.structs.IState.AStateAdapter;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.ui.Button;

public class MainMenuState extends AStateAdapter {
	private GameMode selectedMode = null;
	private Ship selectedShip = null;
	
	private State state = State.MAIN;
	private enum State { MAIN, CHARACTER, LOADSP, WAITMP }
	
	private List<Vector2> stars = new ArrayList<>(100);
	private float spawnTimer;
	
	private TextureAtlas textures;
	private TextureRegion texInfinite, texRace;
	
	private BitmapFont btnFnt, titleFnt;
	private Music music;
	
	private Button btnInfinite, btnRace, btnBack;
	private GlyphLayout glyphTitle, glyphCharacter;
	
	private OrthographicCamera cam;
	
	@Override
	public void create() {
		cam = new OrthographicCamera(1280, 720);
		cam.position.set(1280/2, 720/2, 0);
		
		textures = new TextureAtlas("mainmenu.atlas");
		texInfinite = textures.findRegion("infinity");
		texRace = textures.findRegion("race");
		
		music = Gdx.audio.newMusic(Gdx.files.internal("music/circus.wav"));
		music.setLooping(true);
		music.setVolume(.1f);
		music.play();
		
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.borderWidth = 2;
		ftfp.size = 128;
		titleFnt = GeemLoop.rc.fntOswald.generateFont(ftfp);
		
		ftfp.borderWidth = 0;
		ftfp.color = Color.BLACK;
		ftfp.size = 38;
		btnFnt = GeemLoop.rc.fntVT323.generateFont(ftfp);
		
		
		
		int bp = 100, by = 720/2 - textures.findRegion("button1").getRegionHeight();
		
		btnInfinite = new Button("Infinite", btnFnt, Color.BLACK, "button", textures, 1280/2 - textures.findRegion("button1").getRegionWidth() - bp, by, ()->{
			selectedMode = GameMode.INFINITE;
			state = State.CHARACTER;
		}, t->{});
		
		btnRace = new Button("Race", btnFnt, Color.BLACK, "button", textures, 1280/2 + bp, by, ()->{
			selectedMode = GameMode.RACE;
			state = State.CHARACTER;
		}, t->{});
		
		btnBack = new Button("", btnFnt, Color.BLACK, "arrow", textures, 50, 50, ()->{
			selectedMode = null;
			state = State.MAIN;
		}, t->t.flip(true, false));
		btnBack.setSize(50, 50);
		
		glyphTitle = new GlyphLayout(titleFnt, "GEEM");
		glyphCharacter = new GlyphLayout(titleFnt, "SELECT SHIP");
	}
	
	@Override
	public void update(float dt) {
		cam.update();
		GeemLoop.rc.update(cam);
		
		switch (state) {
			case MAIN:
				btnInfinite.update(dt, cam);
				btnRace.update(dt, cam);
				break;
				
			case CHARACTER:
				btnBack.update(dt, cam);
				break;
				
			case LOADSP:
				btnBack.update(dt, cam);
				break;
				
			case WAITMP:
				btnBack.update(dt, cam);
				break;
				
			default: throw new AssertionError("wot");
		}
		
		if (spawnTimer > 10 && stars.size() < 250) {
			stars.add(new Vector2(0, Math.random() * 720));
			spawnTimer = 0;
		} else spawnTimer += 1000*dt;
		
		stars.forEach(vec->vec.add(500 * dt, 0));
		stars.removeIf(vec->vec.x > 1300);
	}
	
	@Override
	public void render(RenderContext rc) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		rc.shapes.begin(ShapeType.Filled);
		stars.forEach(vec->rc.shapes.circle(vec.x, vec.y, 1));
		rc.shapes.end();
		
		switch (state) {
			case MAIN:
				renderMain(rc);
				break;
				
			case CHARACTER:
				renderCharacter(rc);
				btnBack.render(rc);
				break;
				
			case LOADSP:
				renderLsp(rc);
				btnBack.render(rc);
				break;
				
			case WAITMP:
				renderWmp(rc);
				btnBack.render(rc);
				break;
				
			default: throw new AssertionError("wot");
		}
	}
	
	private void renderMain(RenderContext rc) {
		btnInfinite.render(rc);
		btnRace.render(rc);
		
		rc.sprites.begin();
		titleFnt.draw(rc.sprites, glyphTitle, 1280/2 - glyphTitle.width/2, 720 - glyphTitle.height);
		rc.sprites.end();
	}
	
	private void renderCharacter(RenderContext rc) {
		rc.sprites.begin();
		titleFnt.draw(rc.sprites, glyphCharacter, 1280/2 - glyphCharacter.width/2, 720 - glyphCharacter.height);
		rc.sprites.setColor(1, 1, 1, .4f);
		rc.sprites.draw(selectedMode == GameMode.INFINITE ? texInfinite : texRace, 100, 100);
		rc.sprites.end();
	}
	
	private void renderWmp(RenderContext rc) {
		//STUB empty method
		
	}

	private void renderLsp(RenderContext rc) {
		//STUB empty method
		
	}
	
	
	
	@Override
	public void dispose() {
		btnFnt.dispose();
		titleFnt.dispose();
		music.dispose();
		textures.dispose();
	}
	
}
