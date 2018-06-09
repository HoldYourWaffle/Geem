package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import info.zthings.geem.structs.RenderContext;

public /*abstract*/ class Ship {
	public final float modelScale;
	public final Vector3 position;
	public final BoundingBox bounds;
	
	private final ModelInstance model;
	
	private final int baseSpeedX, baseSpeedZ;
	private final float defence;
	public float hp = 1;
	
	public Ship(ModelInstance model, int speedX, int speedZ, float defence, float modelScale) {
		this.baseSpeedX = speedX;
		this.baseSpeedZ = speedZ;
		this.defence = defence;
		this.position = new Vector3(0, 0, 1);
		this.model = model;
		this.model.transform.scale(modelScale, modelScale, modelScale);
		this.modelScale = modelScale;
		
		bounds = new BoundingBox(); 
		model.calculateBoundingBox(bounds);
	}
	
	public boolean hit() {
		if (hp > 1) hp = 1;
		else hp -= 1 - defence;
		return hp <= 0;
	}
	
	public void update(float dt, PerspectiveCamera cam) {
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W))
			dz = dt*baseSpeedZ*hp;
		else if (Gdx.input.isKeyPressed(Keys.S))
			dz = dt*-baseSpeedZ*hp;
		
		int dx;
		if (Gdx.input.isKeyPressed(Keys.A)) dx = -1;
		else if (Gdx.input.isKeyPressed(Keys.D)) dx = 1;
		else dx = 0;
		
		//hp += (dt/10) * (hp > 1 ? (2 / hp * .2) : 1);
		if (hp > 2) hp = 2;
		
		position.add(-dx*baseSpeedX*hp*dt, 0, dz);
		cam.position.z += dz;
		
		if (position.x < -5) position.x = -5;
		else if (position.x > 5) position.x = 5;
		
		model.transform.setToTranslation(position);
		model.transform.scale(1.5F, 1.5F, 1.5F);
		model.transform.rotate(0, -dx*3, dx, 35);
	}
	
	public void render(RenderContext rc, Environment env, PerspectiveCamera cam) {
		rc.models.begin(cam);
		rc.models.render(model, env);
		rc.models.end();
	}
	
}
