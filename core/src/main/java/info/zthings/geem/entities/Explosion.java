package info.zthings.geem.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class Explosion {
	private Decal decal;
	private final Vector3 position;
	
	private float frame;
	private int prevRender = -1;
	public boolean destroyed;
	
	private final boolean flipX, flipY;
	private final float rot;
	
	public Explosion(Vector3 position) {
		this.position = position;
		flipX = Math.random() > .5;
		flipY = Math.random() > .5;
		rot = (float) (Math.random() * 360);
		update(0);
	}
	
	public void update(float dt) {
		assert !destroyed;
		
		frame += dt * 90;
		if ((int)Math.floor(frame) > prevRender) {
			prevRender = (int)Math.floor(frame);
			
			if (prevRender >= GeemLoop.getRC().explosionFrames.length) {
				destroyed = true;
				return;
			}
			
			TextureRegion tex = new TextureRegion(GeemLoop.getRC().explosionFrames[prevRender]);
			tex.flip(flipX, flipY);
			
			decal = Decal.newDecal(tex, true);
			decal.rotateZ(rot);
			decal.setScale(.1F);
			decal.translate(position);
		}
	}
	
	public void render(ResourceContext rc) {
		rc.decals.add(decal);
	}
	
}
