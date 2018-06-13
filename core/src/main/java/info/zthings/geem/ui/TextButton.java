package info.zthings.geem.ui;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class TextButton extends Button {
	private GlyphLayout text;
	
	public TextButton(String text, String atlasName, int x, int y, boolean flipX, boolean flipY, Runnable callback) {
		super(atlasName, x, y, callback, flipX, flipY);
		this.text = new GlyphLayout(GeemLoop.getRC().fntBtn, text);
	}
	
	@Override
	public void render(ResourceContext rc) {
		super.render(rc);
		
		//rc.sprites.begin();
		rc.fntBtn.draw(rc.sprites, text, pos.x + width/2 - text.width/2, pos.y + height/2 + text.height/2);
		//rc.sprites.end();
	}
	
}
