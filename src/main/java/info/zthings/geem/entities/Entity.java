package info.zthings.geem.entities;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import info.zthings.geem.structs.ResourceContext;

public abstract class Entity {
	protected final ModelInstance model;
	private BoundingBox bb = new BoundingBox(), bbc;
	
	public Vector3 position;
	
	public Entity(Model model) {
		this.model = new ModelInstance(model);
		this.model.calculateBoundingBox(bb);
	}
	
	public BoundingBox getCurrentBounds() {
		return bbc;
	}
	
	public void update(float dt, PerspectiveCamera cam) {
		model.transform.setTranslation(position);
		bbc = bb.mul2(model.transform);
	}
	
	public void render(ResourceContext rc, Environment env) {
		rc.models.render(model, env);
	}
	
}
