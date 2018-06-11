package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public abstract class Ship extends Entity {
	public final Vector3 scale;
	protected final Quaternion rotXZ, rotY;
	
	public final int baseSpeedX, baseSpeedZ, turnAngle;
	private final float defence;
	public float hp = 1;
	
	public Ship(Model model, int speedX, int speedZ, float defence, float modelScale, int turnAngle) {
		super(model);
		this.baseSpeedX = speedX;
		this.baseSpeedZ = speedZ;
		this.defence = defence;
		this.position = new Vector3(0, 1, 0);
		//this.model.transform.scale(modelScale, modelScale, modelScale);
		this.scale = new Vector3(modelScale, modelScale, modelScale);
		this.rotXZ = new Quaternion();
		this.rotY = new Quaternion();
		this.turnAngle = turnAngle;
	}
	
	public boolean hit() {
		if (hp > 1) hp = 1;
		else hp -= 1 - defence;
		return hp <= 0;
	}
	
	private boolean debug = false;
	
	@Override
	public void update(float dt, PerspectiveCamera cam) {
		super.update(dt, cam);
		
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W) || !debug)
			dz = dt*baseSpeedZ*hp;
		else if (Gdx.input.isKeyPressed(Keys.S))
			dz = dt*-baseSpeedZ*hp;
		
		int dx;
		if (Gdx.input.isKeyPressed(Keys.A)) dx = -1;
		else if (Gdx.input.isKeyPressed(Keys.D)) dx = 1;
		else dx = 0;
		
		if (hp > 2) hp = 2;
		
		position.add(-dx*baseSpeedX*hp*dt, 0, dz);
		cam.position.z += dz; //TODO move cam control to state update
		
		rotXZ.setFromAxis(Math.abs(dx/3F), 0, dx/2F, turnAngle);
		rotXZ.mul(rotY);
		model.transform.set(position, rotXZ.nor(), scale);
	}
	
	public float getGunY() {
		return position.y;
	}
	
	public static class ShipNormal extends Ship {
		public ShipNormal() {
			super(GeemLoop.rc.shipNormalModel, 6, 20, .5F, 1.5F, 30);
		}
	}
	
	public static class ShipUfo extends Ship {
		
		public ShipUfo() {
			super(GeemLoop.rc.shipUfoModel, 6, 15, .5F, .025F, 15);
			position.y -= .5F;
		}
		
		private float a = 0;
		
		@Override
		public void update(float dt, PerspectiveCamera cam) {
			a += dt * 500;
			rotY.setFromAxis(0, 1, 0, a);
			super.update(dt, cam);
		}
		
		@Override
		public float getGunY() {
			return position.y + .5F;
		}
		
	}
	
}
