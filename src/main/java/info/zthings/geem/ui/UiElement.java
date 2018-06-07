package info.zthings.geem.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import info.zthings.geem.structs.RenderContext;

public interface UiElement {
	
	public abstract Vector2 getLocation();
	public abstract Rectangle getBounds();
	
	public abstract void setLocation(Vector2 vec);
	public abstract void setLocation(int x, int y);
		
	public abstract void update(float dt, Camera cam);
	public abstract void render(RenderContext rc);
	
}
