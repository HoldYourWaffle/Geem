package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.structs.RenderContext;

public class Ship {
	public final int speedX, speedZ, defence;
	public final Vector3 position;
	private final ModelInstance model;
	
	public Ship(ModelInstance model, int speedX, int speedZ, int defence) {
		this.speedX = speedX;
		this.speedZ = speedZ;
		this.defence = defence;
		this.model = model;
		this.position = new Vector3(0, 1, 1);
	}
	
	public void update(float dt, PerspectiveCamera cam) {
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W))
			dz = dt*speedZ;
		else if (Gdx.input.isKeyPressed(Keys.S))
			dz = dt*-speedZ;
		
		position.add(0, 0, dz);
		cam.position.z += dz;
		
		model.transform.setToTranslation(position);
		
		int dx;
		if (Gdx.input.isKeyPressed(Keys.A)) dx = -1;
		else if (Gdx.input.isKeyPressed(Keys.D)) dx = 1;
		else dx = 0;
		
		position.add(-dx*speedX*dt, 0, 0);
		
		if (position.x < -5) position.x = -5;
		else if (position.x > 5) position.x = 5;
		
		model.transform.scale(1.5F, 1.5F, 1.5F);
		model.transform.rotate(0, -dx*3, dx, 35);
	}
	
	public void render(RenderContext rc, PerspectiveCamera cam) {
		rc.models.begin(cam);
		rc.models.render(model);
		rc.models.end();
	}
	
}
