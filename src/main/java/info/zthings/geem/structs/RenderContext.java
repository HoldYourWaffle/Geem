package info.zthings.geem.structs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public class RenderContext implements Disposable {
	public final AssetManager ass;
	public final Texture loading;
	
	public final ModelBatch models;
	public final DecalBatch decals;
	public final SpriteBatch sprites;
	public final ShapeRenderer shapes;
	
	public final BitmapFont fnt;
	public final FreeTypeFontGenerator fntOswald, fntVT323;
	
	public RenderContext(ModelBatch mb, DecalBatch db, SpriteBatch sb, ShapeRenderer sr) {
		this.models = mb;
		this.sprites = sb;
		this.decals = db;
		this.shapes = sr;
		
		this.fnt = new BitmapFont();
		this.fntOswald = new FreeTypeFontGenerator(Gdx.files.internal("fonts/oswald.ttf"));
		this.fntVT323 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vt323.ttf"));
		
		this.ass = new AssetManager();
		this.loading = new Texture("loading.png");
	}

	@Override
	public void dispose() {
		models.dispose();
		decals.dispose();
		sprites.dispose();
		shapes.dispose();
		ass.dispose();
		
		loading.dispose();
		
		fnt.dispose();
		fntOswald.dispose();
		fntVT323.dispose();
	}
	
}
