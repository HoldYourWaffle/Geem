package info.zthings.geem.structs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
	
	public final BitmapFont fntDefault;
	public final FreeTypeFontGenerator fntOswald, fntVT323;
	
	public Model shipNormalModel, asteroidModel, shipUfoModel, bulletModel;
	
	public ResourceContext(ModelBatch mb, DecalBatch db, SpriteBatch sb, ShapeRenderer sr) {
		this.models = mb;
		this.sprites = sb;
		this.decals = db;
		this.shapes = sr;
		
		this.fntDefault = new BitmapFont();
		this.fntOswald = new FreeTypeFontGenerator(Gdx.files.internal("fonts/oswald.ttf"));
		this.fntVT323 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vt323.ttf"));
		
		ModelBuilder bb = new ModelBuilder();
		bulletModel = bb.createBox(.1F, .1F, 2F, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		
		this.ass = new AssetManager();
		ass.load("ships/normal.g3db", Model.class);
		ass.load("ships/ufo/ufo.g3db", Model.class);
		//ass.load("ships/pirate.g3db", Model.class);
				
		ass.load("asteroid/asteroid.g3db", Model.class);
		
		ass.load("music/ingame.wav", Music.class);
		ass.load("music/circus.wav", Music.class);
		ass.load("sfx/oof.wav", Sound.class);
		ass.load("sfx/fail.wav", Sound.class);
		ass.load("sfx/yeet.wav", Sound.class);
		ass.load("sfx/laser.wav", Sound.class);
		
		ass.load("hpbar.png", Texture.class);
		ass.load("star.png", Texture.class);
		ass.load("mainmenu.atlas", TextureAtlas.class);
	}
	
	public boolean updateAss() {
		if (ass.update()) {
			this.shipNormalModel = ass.get("ships/normal.g3db", Model.class);
			this.shipUfoModel = ass.get("ships/ufo/ufo.g3db", Model.class);
			//this.shipPirateModel = ass.get("ships/pirate.g3db", Model.class);
			
			this.asteroidModel = ass.get("asteroid/asteroid.g3db", Model.class);
			
			return true;
		} else return false;
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
		fntOswald.dispose();
		fntVT323.dispose();
	}
	
}
