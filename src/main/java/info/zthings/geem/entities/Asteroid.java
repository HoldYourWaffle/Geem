package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class Asteroid extends Entity {
	
	public Asteroid(float x, float z) {
		super(GeemLoop.rc.asteroidModels[(int)(Math.random()*29)]);
		this.position = new Vector3(x, .5F, z);
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model.transform.scale(.025F, .025F, .025F);
		super.update(Gdx.graphics.getDeltaTime(), null);
	}
	
	@Override
	public void update(float dt, PerspectiveCamera cam) {
		//super.update(dt, cam); I never move
	}

}
