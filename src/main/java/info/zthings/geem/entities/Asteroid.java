package info.zthings.geem.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector2;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class Asteroid {
	private ModelInstance model;
	public Vector2 position;
	
	public Asteroid(Vector2 position) {
		model = new ModelInstance(GeemLoop.rc.asteroidModels[(int)(Math.random()*29)]);
		this.position = position;
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model.transform.scale(.025F, .025F, .025F);
	}
	
	public void update(float dt) {
		model.transform.setTranslation(position.x, .5F, position.y);
	}

	public void render(ResourceContext rc, Environment env) {
		rc.models.render(model, env);
	}
	
}
