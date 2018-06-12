package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class Asteroid extends Entity {
	public boolean hard;
	
	public Asteroid(float x, float z) {
		super(GeemLoop.getRC().asteroidModels[(int)(Math.random()*29)]);
		this.position = new Vector3(x, .5F, z);
		
		float rc = .0000125F; //TODO test
		hard = Math.random() < rc * (z - 2000);
		
		if (hard) System.out.println(z);
		
		model.materials.get(0).set(ColorAttribute.createDiffuse(hard ? Color.FIREBRICK : Color.DARK_GRAY));
		model.transform.scale(.025F, .025F, .025F);
		super.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void update(float dt) {
		//super.update(dt, cam); I never move
	}
	
	public boolean hit() {
		if (hard) {
			model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
			hard = false;
			return false;
		} else {
			destroyed = true;
			return true;
		}
	}

}
