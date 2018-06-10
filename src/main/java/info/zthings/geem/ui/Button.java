package info.zthings.geem.ui;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.structs.ResourceContext;

public class Button implements UiElement {
	private Vector2 pos;
	private int width, height;
	private GlyphLayout text;
	
	private BtnState state = BtnState.NORMAL;
	private Runnable callback;
	private final TextureRegion btnNormal, btnHover, btnClick;
	private final BitmapFont fnt;
	private Color textColor;
	
	public Button(String txt, BitmapFont fnt, Color txtColor, String atlasName, TextureAtlas tex, int x, int y, Runnable callback, Consumer<TextureRegion> modder) {
		pos = new Vector2(x, y);
		
		this.fnt = fnt;
		this.callback = callback;
		
		setText(txt);
		setTextColor(txtColor);
		
		btnNormal = tex.findRegion(atlasName + 1);
		btnHover =  tex.findRegion(atlasName + 2);
		btnClick =  tex.findRegion(atlasName + 3);
		
		modder.accept(btnNormal);
		modder.accept(btnHover);
		modder.accept(btnClick);
		
		width = btnNormal.getRegionWidth();
		height = btnNormal.getRegionHeight();
	}
	
	private enum BtnState { NORMAL, HOVER, CLICK; }
	
	@Override
	public void update(float dt, Camera cam) {
		if (getBounds().contains(cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).flatten())) { //hover
			if (Gdx.input.isTouched()) state = BtnState.CLICK; //hover + click
			else if (state == BtnState.CLICK) { //not touched but in click state -> released
				callback.run();
			} else state = BtnState.HOVER; //never touched but hovered
			
			state = Gdx.input.isTouched() ? BtnState.CLICK : BtnState.HOVER;
		} else state = BtnState.NORMAL;
	}
	
	@Override
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
		
		rc.sprites.begin();
		rc.sprites.setColor(1, 1, 1, rc.sprites.getColor().a);
		rc.sprites.draw(r, pos.x, pos.y, width, height);
		fnt.setColor(textColor);
		fnt.draw(rc.sprites, text, pos.x + width/2 - text.width/2, pos.y + height/2 + text.height/2);
		rc.sprites.end();
	}
	
	
	
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle(pos.x, pos.y, width, height);
	}
	
	@Override
	public Vector2 getLocation() {
		return pos;
	}
	
	@Override
	public void setLocation(Vector2 vec) {
		pos = vec;
	}
	
	@Override
	public void setLocation(int x, int y) {
		setLocation(new Vector2(x, y));
	}
	
	public void setText(String txt) {
		text = new GlyphLayout(fnt, txt);
	}
	
	public void setCallback(Runnable r) {
		callback = r;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
	
	public void setSize(int w, int h) {
		width = w;
		height = h;
	}
	
}
