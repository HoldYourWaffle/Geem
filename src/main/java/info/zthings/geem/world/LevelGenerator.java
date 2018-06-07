package info.zthings.geem.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class LevelGenerator {
	private final float tightness;
	private final int ww;
	private final List<Vector2> gaps = new ArrayList<>();
	
	private Vector2 currentPos;
	
	public LevelGenerator(int ww, int tightness) {
		this.ww = ww;
		this.tightness = tightness;
		// tightness = wh / (120 * (wh / 1000f)) * 2;
		reset();
	}
	
	@Deprecated
	public void generate(int n) {
		for (int i = 0; i < n; i++)
			next();
	}
	
	@Deprecated
	public void reset() {
		gaps.clear();
		gaps.add(new Vector2(ww / 2f, 0));
		currentPos = gaps.get(0);
	}
	
	public void next() {
		double alpha, alphaMin, alphaMax, ydiv, xdiv;
		
		// TODO add deviation rule (further than 50% from middle? -> bias)
		ydiv = tightness;
		
		float roomLeft = currentPos.x, roomRight = ww - currentPos.x;
		
		alphaMin = Math.atan(ydiv / roomLeft);
		alphaMax = Math.PI - Math.atan(ydiv / roomRight);
		alpha = alphaMin + Math.random() * (alphaMax - alphaMin);
		xdiv = ydiv / -Math.tan(alpha);
		
		currentPos = currentPos.plus(xdiv, ydiv);
		gaps.add(currentPos.cpy());
	}
	
	public void debugRender(ShapeRenderer sr) {
		sr.begin();
		
		Vector2 prev = gaps.get(0);
		for (Vector2 gap : gaps) {
			sr.setShapeType(ShapeType.Line);
			sr.setColor(Color.GREEN);
			sr.line(prev, gap);
			
			sr.setColor(Color.PURPLE);
			sr.setShapeType(ShapeType.Filled);
			sr.circle(gap.x, gap.y, .3f);
			
			prev = gap;
		}
		
		sr.end();
	}
	
}
