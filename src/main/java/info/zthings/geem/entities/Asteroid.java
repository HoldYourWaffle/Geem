package info.zthings.geem.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class Asteroid extends Entity { //TODO base entity class
	
	public Asteroid(float x, float z) {
		super(GeemLoop.rc.asteroidModels[(int)(Math.random()*29)]);
		this.position = new Vector3(x, .5F, z);
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model.transform.scale(.025F, .025F, .025F);
	}
	
	@Override
	public void update(float dt, PerspectiveCamera cam) {
		super.update(dt, cam);
	}

}
