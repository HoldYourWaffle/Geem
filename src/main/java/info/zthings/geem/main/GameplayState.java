package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.IState;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.world.DebugRenderer;
import info.zthings.geem.world.LevelGenerator;

public class GameplayState implements IState {
	private PerspectiveCamera cam;
	private Environment env;
	private DebugRenderer debugRender;
	private LevelGenerator lg = new LevelGenerator(10, 10);
	
	private BitmapFont fnt;
	private GlyphLayout glyphDied;
	
	private Model shipModel, box;
	private Ship ship;
	
	private List<Pair<ModelInstance, Vector2>> gaps = new ArrayList<>();
	private final int gapWidth = 4;
	
	private boolean died = false;
	
	@Override
	public void create(AssetManager ass) {
		cam = new PerspectiveCamera(69, 1280, 720);
		cam.position.set(0, 3, -4);
		cam.lookAt(0, 0, 200);
		cam.near = 0.1f;
		cam.far = 100;
		cam.update();
		
		env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
        fnt = GeemLoop.rc.fntVT323.generateFont(new FreeTypeFontParameter(69));
        glyphDied = new GlyphLayout(fnt, "YOU DIED");
        
		debugRender = new DebugRenderer();
		ass.load("ships/ship.g3db", Model.class);
		
		ModelBuilder boxbuilder = new ModelBuilder();
		box = boxbuilder.createBox(gapWidth, .5F, .5F, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		
		for (int i = 0; i < 10; i++) nextGap();
	}
	
	private void nextGap() {
		ModelInstance gap = new ModelInstance(box);
		Vector2 vec = lg.next();
		vec.add(-5, 0);
		vec.set(0, vec.y);
		//vec.set(0, 10);
		
		gap.transform.setTranslation(vec.x, 0, vec.y);
		gaps.add(Pair.of(gap, vec));
	}
	
	@Override
	public void postLoad(AssetManager ass) {
		shipModel = ass.get("ships/ship.g3db", Model.class);
		ship = new Ship(new ModelInstance(shipModel), 3, 8, .5F, 1.5F);
	}

	@Override
	public void update(float dt) {
		if (died) {
			return;
		}
		
		ship.update(dt, cam);
		
		cam.lookAt(0, 0, cam.position.z+200);
		cam.update();
		debugRender.update(dt, cam);
		
		if (ship.position.z > gaps.get(1).getRight().y) { //y in Vector2 = z
			boolean passed = ship.position.x+ship.bounds.getWidth()*ship.modelScale/2 < gaps.get(1).getRight().x+gapWidth/2 &&
							 ship.position.x-ship.bounds.getWidth()*ship.modelScale/2 > gaps.get(1).getRight().x-gapWidth/2;
			
			if (!passed) {
				//TODO play oof
				//TODO animation
				died = ship.hit();
				System.out.println(ship.hp);
				
				if (died) {
					//TODO play fail
				}
			}
			
			gaps.remove(0);
		}
	}
	
	@Override
	public void render(RenderContext rc) {
		debugRender.render(rc, cam);

		rc.models.begin(cam);
		gaps.forEach(box->rc.models.render(box.getLeft(), env));
		rc.models.end();
		
		if (!died) ship.render(rc, env, cam);
		else {
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
