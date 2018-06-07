package info.zthings.geem.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.structs.GameMode;
import info.zthings.geem.structs.IState.AStateAdapter;
import info.zthings.geem.structs.RenderContext;
import info.zthings.geem.ui.Button;

public class MainMenuState extends AStateAdapter {
	private GameMode selectedMode = null;
	private Hero selectedCharacter = null;
	
	private boolean renderMain, renderCharacter, renderSpLoad, renderMpWait;
	private int dx, dy;
	
	private Texture background, button;
	private BitmapFont btnFnt, titleFnt;
	private Button infinite, race;
	private GlyphLayout titleGlyph;
	
	private OrthographicCamera cam;
	
	@Override
	public void create() {
		cam = new OrthographicCamera(1280, 720);
		cam.position.set(1280/2, 720 + 360, 0);
		
		background = new Texture("menu/background.png");
		button = new Texture("menu/button.png");
		
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.borderWidth = 2;
		ftfp.size = 38;
		btnFnt = GeemLoop.rc.getFontGenerator().generateFont(ftfp);
		ftfp.size = 138;
		titleFnt = GeemLoop.rc.getFontGenerator().generateFont(ftfp);
		
		renderMain = true;
		
		int s = 2000;
		infinite = new Button("Infinite", btnFnt, Color.BLACK, button, 1280/2 - button.getWidth() - 100, (int)(720 * 1.5) - button.getHeight()/4, ()->{
			selectedMode = GameMode.INFINITE;
			dx = s;
			renderCharacter = true;
		});
		
		race = new Button("Race", btnFnt, Color.BLACK, button, 1280/2 + 100, (int)(720 * 1.5) - button.getHeight()/4, ()->{
			selectedMode = GameMode.RACE;
			dx = s;
			renderCharacter = true;
		});
		
		titleGlyph = new GlyphLayout(titleFnt, "GEEM");
	}
	
	@Override
	public void update(float dt) {
		if (dx != 0) {
			cam.position.add(dx*dt, 0, 0);
			if ((int)cam.position.x > 1920) {
				cam.position.x = 1920;
				dx = 0;
				renderMain = false;
			}
		} else if (dy != 0) {
			cam.position.add(0, dy*dt, 0);
			//TODO stoppage
		}
		
		cam.update();
		GeemLoop.rc.update(cam);
		
		infinite.update(dt, cam);
		race.update(dt, cam);
	}
	
	@Override
	public void render(RenderContext rc) {
		rc.sprites.begin();
		Vector3 sz = cam.unproject(new Vector3(0, 719, 0));
		Vector2 zero = new Vector2(sz.x, sz.y);
		rc.sprites.draw(background, zero.x, zero.y);
		rc.sprites.end();

		if (renderMain) renderMain(rc);
		if (renderCharacter) renderCharacter(rc);
		if (renderSpLoad) renderLsp(rc);
		if (renderMpWait) renderWmp(rc);
		
		//=======DEBUG
		rc.sprites.resetProjectionMatrix();
		rc.sprites.begin();
		rc.fnt.draw(rc.sprites, (int)(cam.position.x) + " , " + (int)(cam.position.y), 0, rc.fnt.getLineHeight()*2);
		Vector3 m = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		rc.fnt.draw(rc.sprites, (int)(m.x) + " , " + (int)(m.y), 0, rc.fnt.getLineHeight()*3);
		rc.sprites.end();
		//=======DEBUG
	}
	
	private void renderMain(RenderContext rc) {
		infinite.render(rc);
		race.render(rc);
		System.out.println("KEKEKE");
		rc.sprites.begin();
		titleFnt.draw(rc.sprites, titleGlyph, 640 - titleGlyph.width/2, 1440 - titleGlyph.height);
		rc.sprites.end();
	}
	
	private void renderCharacter(RenderContext rc) {
		rc.shapes.begin(ShapeType.Filled);
		rc.shapes.rect(1500, 750, 800, 680);
		rc.shapes.end();
	}
	
	private void renderWmp(RenderContext rc) {
		//STUB empty method
		
	}

	private void renderLsp(RenderContext rc) {
		//STUB empty method
		
	}
	
	
	
	@Override
	public void dispose() {
		background.dispose();
		button.dispose();
		btnFnt.dispose();
		titleFnt.dispose();
	}
	
}
