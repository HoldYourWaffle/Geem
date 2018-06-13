package info.zthings.geem.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class Button {
	protected Vector2 pos;
	protected int width, height;
	
	private BtnState state = BtnState.NORMAL;
	private Runnable callback;
	private final TextureRegion btnNormal, btnHover, btnClick;
	
	public Button(String atlasName, int x, int y, Runnable callback, boolean flipX, boolean flipY) {
		pos = new Vector2(x, y);
		this.callback = callback;
		
		btnNormal = new TextureRegion(GeemLoop.getRC().atlas.findRegion(atlasName + 1));
		btnNormal.flip(flipX, flipY);
		btnHover = new TextureRegion(GeemLoop.getRC().atlas.findRegion(atlasName + 2));
		btnHover.flip(flipX, flipY);
		btnClick = new TextureRegion(GeemLoop.getRC().atlas.findRegion(atlasName + 3));
		btnClick.flip(flipX, flipY);
		
		width = btnNormal.getRegionWidth();
		height = btnNormal.getRegionHeight();
	}
	
	private enum BtnState { NORMAL, HOVER, CLICK; }
	
	public void update(float dt, Camera cam) {
		if (new Rectangle(pos.x, pos.y, width, height).contains(cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).flatten())) { //hover
			if (Gdx.input.isTouched()) state = BtnState.CLICK; //hover + click
			else if (state == BtnState.CLICK) { //not touched but in click state -> released
				if (callback != null) callback.run();
			} else state = BtnState.HOVER; //never touched but hovered
			
			state = Gdx.input.isTouched() ? BtnState.CLICK : BtnState.HOVER;
		} else state = BtnState.NORMAL;
	}
	
	public void render(ResourceContext rc) {
		TextureRegion r;
		switch (state) {
			case CLICK:
				r = btnClick;
				break;
			case HOVER:
				r = btnHover;
				break;
			case NORMAL:
				r = btnNormal;
				break;
			default: throw new AssertionError("Undefined state");
		}
		
		//rc.sprites.begin();
		rc.sprites.setColor(1, 1, 1, rc.sprites.getColor().a);
		rc.sprites.draw(r, pos.x, pos.y, width, height);
		
		//rc.sprites.end();
	}
	
	
	public boolean isClicked() {
		return state == BtnState.CLICK;
	}
	
	public void setLocation(int x, int y) {
		pos.set(x, y);
	}
	
	public void setSize(int w, int h) {
		width = w;
		height = h;
	}
	
}
