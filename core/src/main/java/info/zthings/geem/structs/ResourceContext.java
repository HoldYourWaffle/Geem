package info.zthings.geem.structs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public class ResourceContext implements Disposable {
	public final AssetManager ass;
	
	public final ModelBatch models;
	public final DecalBatch decals;
	public final SpriteBatch sprites;
	public final ShapeRenderer shapes;
	
	public final BitmapFont fntDefault, fntBtn, fntTitle, fntUi;
	public final FreeTypeFontGenerator fntGenOswald, fntGenVT323;
	
	public TextureAtlas atlas;
	
	public Model shipNormalModel, shipUfoModel, bulletModel, fuelModel;
	public Model[] asteroidModels = new Model[30];
	public TextureRegion[] explosionFrames = new TextureRegion[48];
	
	private int highscore;
	private GlyphLayout glyphHighscore;
	
	public ResourceContext(ModelBatch mb, DecalBatch db, SpriteBatch sb, ShapeRenderer sr) {
		this.models = mb;
		this.sprites = sb;
		this.decals = db;
		this.shapes = sr;
		
		this.fntDefault = new BitmapFont();
		this.fntGenOswald = new FreeTypeFontGenerator(Gdx.files.internal("fonts/oswald.ttf"));
		this.fntGenVT323 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vt323.ttf"));
		
		ModelBuilder bb = new ModelBuilder();
		bulletModel = bb.createBox(.1F, .1F, 2F, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		
		this.ass = new AssetManager();
		ass.load("ships/normal.g3db", Model.class);
		ass.load("ships/ufo/ufo.g3db", Model.class);
		//ass.load("ships/pirate.g3db", Model.class);
		
		ass.load("models/canister.g3db", Model.class);
		for (int i = 1; i <= 30; i++)
			ass.load("models/asteroids/asteroid"+i+".g3dj", Model.class);
		
		ass.load("music/ingame.wav", Music.class);
		ass.load("music/circus.wav", Music.class);
		ass.load("sfx/beep.wav", Music.class);
		
		ass.load("sfx/oof.wav", Sound.class);
		ass.load("sfx/fail.wav", Sound.class);
		ass.load("sfx/fuel.wav", Sound.class);
		ass.load("sfx/laser.wav", Sound.class);
		ass.load("sfx/biem.wav", Sound.class);
		ass.load("sfx/click.wav", Sound.class);
		
		ass.load("sprites.atlas", TextureAtlas.class);
		
		fntUi = fntGenVT323.generateFont(new FreeTypeFontParameter(69));
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.borderWidth = 2;
		ftfp.size = 128;
		fntTitle = fntGenOswald.generateFont(ftfp);
		
		ftfp.borderWidth = 0;
		ftfp.color = Color.BLACK;
		ftfp.size = 38;
		fntBtn = fntGenVT323.generateFont(ftfp);
		
		highscore = Gdx.app.getPreferences("geem-highscore").getInteger("highscore");
		glyphHighscore = new GlyphLayout(fntUi, "HIGHSCORE: " + highscore);
	}
	
	public boolean updateAss() {
		if (ass.update()) {
			this.shipNormalModel = ass.get("ships/normal.g3db");
			this.shipUfoModel = ass.get("ships/ufo/ufo.g3db");
			this.fuelModel = ass.get("models/canister.g3db");
			
			for (int i = 1; i <= 30; i++)
				this.asteroidModels[i-1] = ass.get("models/asteroids/asteroid"+i+".g3dj");
			
			atlas = ass.get("sprites.atlas");
			for (int i = 0; i <= 47; i++)
				this.explosionFrames[i] = atlas.findRegion("explosion"+i);
			return true;
		} else return false;
	}
	
	public GlyphLayout getHighscoreGlyph() {
		return glyphHighscore;
	}
	
	public int getHighscore() {
		return highscore;
	}
	
	public void updateHighscore(int score) {
		if (score > highscore) {
			highscore = score;
			glyphHighscore = new GlyphLayout(fntUi, "HIGHSCORE: " + highscore);
			Gdx.app.getPreferences("geem-highscore").putInteger("highscore", highscore);
			Gdx.app.getPreferences("geem-highscore").flush();
		}
	}
	
	@Override
	public void dispose() {
		ass.dispose();
		
		models.dispose();
		decals.dispose();
		sprites.dispose();
		shapes.dispose();
		
		bulletModel.dispose();
		
		fntDefault.dispose();
		fntGenOswald.dispose();
		fntGenVT323.dispose();
		
		fntUi.dispose();
		fntBtn.dispose();
		fntTitle.dispose();
	}
	
}
