package info.zthings.geem.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public abstract class Ship extends Entity {
	public final Vector3 scale;
	protected final Quaternion rotXZ, rotY;
	
	public final int baseSpeedX, baseSpeedZ, turnAngle, defence;
	public int hp = 100;
	public float fuel = 100;
	
	public Ship(Model model, int speedX, int speedZ, int defence, float modelScale, int turnAngle) {
		super(model);
		this.baseSpeedX = speedX;
		this.baseSpeedZ = speedZ;
		this.defence = defence;
		this.position = new Vector3(0, 1, 0);
		this.scale = new Vector3(modelScale, modelScale, modelScale);
		this.rotXZ = new Quaternion();
		this.rotY = new Quaternion();
		this.turnAngle = turnAngle;
	}
	
	public boolean hit() {
		hp -= 100 - defence;
		return hp <= 0;
	}
	
	public int hitsLeft() {
		return (int)Math.ceil(hp / (100-defence));
	}
	
	private boolean debug = false;
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		//fuel = 1;
		
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W) || !debug)
			dz = baseSpeedZ;
		else if (Gdx.input.isKeyPressed(Keys.S))
			dz = -baseSpeedZ;
		
		float ff = fuel < 50 ? .3F + .7F/50F * fuel : 1;
		dz *= dt * ff;
		fuel -= dz / 15F;
		
		if (fuel < -.25F) { //TODO sfx?
			hp = 0;
		}
		
		int dx;
		if (Gdx.input.isKeyPressed(Keys.A)) dx = -1;
		else if (Gdx.input.isKeyPressed(Keys.D)) dx = 1;
		else dx = 0;
		
		position.add(-dx*baseSpeedX*dt*ff, 0, dz);
		
		rotXZ.setFromAxis(Math.abs(dx/3F), 0, dx/2F, turnAngle);
		rotXZ.mul(rotY);
		model.transform.set(position, rotXZ.nor(), scale);
	}
	
	public float getGunY() {
		return position.y;
	}
	
	public static class ShipNormal extends Ship {
		public ShipNormal() {
			super(GeemLoop.rc.shipNormalModel, 6, 20, 90, 1.5F, 30);
		}
	}
	
	public static class ShipUfo extends Ship {
		
		public ShipUfo() {
			super(GeemLoop.rc.shipUfoModel, 6, 15, 90, .025F, 15);
			position.y -= .5F;
		}
		
		private float a = 0;
		
		@Override
		public void update(float dt) {
			a += dt * 500;
			rotY.setFromAxis(0, 1, 0, a);
			super.update(dt);
		}
		
		@Override
		public float getGunY() {
			return position.y + .5F;
		}
		
	}
	
}
