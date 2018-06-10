package info.zthings.geem.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.entities.Ship;
import info.zthings.geem.structs.RenderContext;

public class StarBox {
	private final TextureRegion texStar;
	private List<Decal> stars = new ArrayList<>(100);
	private int radius; //TODO final
	
	public StarBox(int radius) {
		texStar = GeemLoop.rc.ass.get("star.png", Texture.class).asRegion();
		this.radius = radius;
	}
	
	public void update(float dt, PerspectiveCamera cam, Ship s) {
		if (stars.size() < 700) {
			for (int i = 0; i < 4; i++) {
				Decal d = Decal.newDecal(texStar);
				radius = 20;
				double a = Math.random() * Math.PI * 2,
					   min = 3.5,
					   L = min + Math.random() * (radius - min);
				
				
				float x = (float) (L * Math.cos(a)),
					  y = (float) (L * Math.sin(a));
				Vector3 vec = new Vector3(x, y, cam.position.z + 35);
				
				d.setPosition(vec);
				d.setScale(.01F);
				stars.add(d);
			}
		}
		
		System.out.println(stars.size());
		
		stars.removeIf(d->d.getPosition().z < cam.position.z);
	}
	
	public void render(RenderContext rc, PerspectiveCamera cam) {
		stars.forEach(rc.decals::add);
		rc.decals.flush();
	}
	
}