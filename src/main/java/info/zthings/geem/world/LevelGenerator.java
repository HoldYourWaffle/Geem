package info.zthings.geem.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class LevelGenerator {
	private Vector2 currentPos = Vector2.Zero;
	private List<Vector2> gaps = new ArrayList<>();
	
	private float tightness = 1.5F, divTightness = 1, gapWidth = 1.5F;
	
	private final int worldWidth;
	
	public LevelGenerator(int ww) {
		this.worldWidth = ww;
		gaps.add(new Vector2(0, 0));
		next();
	}
	
	public void next() {
		double alpha, ydiv, xdiv;
		do {
			ydiv = tightness + Math.random() * divTightness;
			
			/*double alphamax = Math.PI - Math.atan(ydiv/Math.abs((-worldWidth/2-gapWidth-5) - currentPos.x)) - Math.atan(ydiv/Math.abs((worldWidth/2-gapWidth-5) - currentPos.x)),
					anglemin = 90;
			
			System.out.println((int)Math.toDegrees(alphamax));
			
			alpha = Math.min(Math.random() * alphamax, anglemin);*/
			
			alpha = Math.random()*Math.PI;
			xdiv = ydiv / Math.tan(alpha);
			
		} while (Math.abs(90 - (int)Math.toDegrees(alpha)) > 50 ||
				(currentPos.x + xdiv >= worldWidth/2-1 || currentPos.x + xdiv <= -worldWidth/2+2));
		
		System.out.println(currentPos.x + xdiv);
		System.out.println(-worldWidth/2-2);
		System.out.println();
		
		currentPos = currentPos.plus(xdiv, ydiv);
		gaps.add(new Vector2(currentPos));
	}
	
	public void render(ShapeRenderer sr) {
		sr.begin();
		
		Vector2 prev = Vector2.Zero;
		for (Vector2 gap : gaps) {
			sr.setColor(Color.WHITE);
			sr.line(gap.x-gapWidth, gap.y, gap.x+gapWidth, gap.y);
			
			sr.setColor(Color.BLUE);
			sr.line(prev, gap);
			
			/*sr.setColor(Color.RED);
			sr.line(gap.x-gapWidth, gap.y, gap.x-gapWidth-1, gap.y);
			sr.line(gap.x+gapWidth, gap.y, gap.x+gapWidth+1, gap.y);*/
			
			prev = gap;
		}
		
		sr.end();
	}
	
}
