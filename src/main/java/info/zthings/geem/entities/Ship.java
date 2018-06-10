package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public abstract class Ship {
	public final Vector3 position, scale;
	protected final Quaternion rotXZ, rotY;
	
	private final ModelInstance model;
	
	public final int baseSpeedX, baseSpeedZ;
	private final float defence;
	public float hp = 1;
	
	public Ship(ModelInstance model, int speedX, int speedZ, float defence, float modelScale) {
		this.baseSpeedX = speedX;
		this.baseSpeedZ = speedZ;
		this.defence = defence;
		this.position = new Vector3(0, 1, 0);
		this.model = model;
		//this.model.transform.scale(modelScale, modelScale, modelScale);
		this.scale = new Vector3(modelScale, modelScale, modelScale);
		this.rotXZ = new Quaternion();
		this.rotY = new Quaternion();
	}
	
	public boolean hit() {
		if (hp > 1) hp = 1;
		else hp -= 1 - defence;
		return hp <= 0;
	}
	
	private boolean debug = false;
	
	public void update(float dt, PerspectiveCamera cam) {
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W) || !debug)
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
		
		if (position.x < -6) position.x = -6;
		else if (position.x > 6) position.x = 6;
		
		//System.out.println(dx);
		//dx = -1;
		rotXZ.setFromAxis(Math.abs(dx/3F), 0, dx/2F, 30);
		rotXZ.mul(rotY);
		model.transform.set(position, rotXZ.nor(), scale);
		//model.transform.setScale(modelScale, modelScale, modelScale);
	}
	
	public void render(ResourceContext rc, Environment env, PerspectiveCamera cam) {
		rc.models.begin(cam);
		rc.models.render(model, env);
		rc.models.end();
	}
	
	
	
	public static class ShipNormal extends Ship {
		public ShipNormal() {
			super(new ModelInstance(GeemLoop.rc.shipNormalModel), 6, 20, .5F, 1.5F);
		}
	}
	
	public static class ShipUfo extends Ship {
		
		public ShipUfo() {
			super(new ModelInstance(GeemLoop.rc.shipUfoModel), 6, 15, .5F, .025F);
			position.y -= .5F;
		}
		
		private float a = 0;
		
		@Override
		public void update(float dt, PerspectiveCamera cam) {
			a += dt * 100;
			rotY.setFromAxis(0, 1, 0, a);
			super.update(dt, cam);
			
			//System.out.println(rotation.y);
		}
	}
	
}
