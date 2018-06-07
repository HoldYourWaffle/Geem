package info.zthings.geem.structs;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public class RenderContext implements Disposable {
	public final ModelBatch models;
	public final DecalBatch decals;
	public final SpriteBatch sprites;
	public final ShapeRenderer shapes;
	
	public final BitmapFont fnt;
	
	private final FreeTypeFontGenerator ftfg;
	private final Map<Integer, BitmapFont> fontCache;
	
	public RenderContext(ModelBatch mb, DecalBatch db, SpriteBatch sb, ShapeRenderer sr) {
		this.models = mb;
		this.sprites = sb;
		this.decals = db;
		this.shapes = sr;
		this.fnt = new BitmapFont();
		
		this.fontCache = new HashMap<>();
		this.ftfg = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Medium.ttf"));
	}
	
	public void update(Camera cam) {
		//models.setCamera(cam);
		sprites.setProjectionMatrix(cam.combined);
		shapes.setProjectionMatrix(cam.combined);
	}
	
	public FreeTypeFontGenerator getFontGenerator() {
		return ftfg;
	}
	
	public BitmapFont getFont(int size) {
		if (fontCache.containsKey(size)) return fontCache.get(size);
		
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.size = size;
		return ftfg.generateFont(ftfp);
	}

	@Override
	public void dispose() {
		models.dispose();
		decals.dispose();
		sprites.dispose();
		shapes.dispose();
		fnt.dispose();
		fontCache.forEach((s, f)->f.dispose());
		ftfg.dispose();
	}
	
}
