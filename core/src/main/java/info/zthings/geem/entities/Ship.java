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
	
	public final int baseSpeedX, baseSpeedZ, turnAngle, defence, accuracy; //accuracy = max deviation (bigger is worse)
	public int hp = 100, ammo = 10;
	public float fuel = 100;
	
	public boolean movingLeft, movingRight;
	
	public Ship(Model model, int speedX, int speedZ, int defence, int accuracy, float modelScale, int turnAngle) {
		super(model);
		this.baseSpeedX = speedX;
		this.baseSpeedZ = speedZ;
		this.defence = defence;
		this.accuracy = accuracy;
		this.position = new Vector3(0, 1, 0);
		this.scale = new Vector3(modelScale, modelScale, modelScale);
		this.rotXZ = new Quaternion();
		this.rotY = new Quaternion();
		this.turnAngle = turnAngle;
	}
	
	public boolean hit(boolean hard) {
		hp -= (100 - defence) * (hard ? 2 : 1);
		return hp <= 0;
	}
	
	public int hitsLeft() {
		return (int)Math.ceil(hp / (100-defence));
	}
	
	private boolean debug = false;
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		//TODO REMOVE COMMENTS FOR CHEATS
		//fuel = 100;
		//hp = 100;
		
		float dz = 0;
		if (Gdx.input.isKeyPressed(Keys.W) || !debug)
			dz = baseSpeedZ;
		
		float ff = fuel < 50 ? .3F + .7F/50F * fuel : 1;
		dz *= dt * ff;
		fuel -= dz / 15F;
		
		if (fuel < -.25F)
			hp = 0;
		
		int dx;
		if (Gdx.input.isKeyPressed(Keys.A) || movingLeft) dx = -1;
		else if (Gdx.input.isKeyPressed(Keys.D) || movingRight) dx = 1;
		else dx = 0;
		
		position.add(-dx*baseSpeedX*dt*ff, 0, dz);
		
		rotXZ.setFromAxis(Math.abs(dx/3F), 0, dx/2F, turnAngle);
		rotXZ.mul(rotY);
		model.transform.set(position, rotXZ.nor(), scale);
	}
	
	public float getGunY() {
		return position.y;
	}
	
	//TODO balance (ship stats)
	public static class ShipNormal extends Ship {
		public ShipNormal() {
			super(GeemLoop.getRC().shipNormalModel, 6, 20, 80, 3, 1.5F, 30); //speedLinksRechts, speedForward, defence, accuracy (laatste 2 gwn laten)
			
			/*
			 * Accuracy is de maximale angle dat ie kan afwijken (dus hoger is slechter)
			 * Als je gehit wordt doet ie 100 - defence damage, dus als je 90 defence hebt doet ie maar 10 damage
			 */
		}
	}
		
	//TODO balance (ship stats)
	public static class ShipSub extends Ship {
		public ShipSub() {
			super(GeemLoop.getRC().shipSubModel, 2, 10, 90, 1, 0.0045F, 10); //speedLinksRechts, speedForward, defence, accuracy (laatste 2 gwn laten)
			rotY.setFromAxis(0, 1, 0, -90);
			
			/*
			 * Accuracy is de maximale angle dat ie kan afwijken (dus hoger is slechter)
			 * Als je gehit wordt doet ie 100 - defence damage, dus als je 90 defence hebt doet ie maar 10 damage
			 */
		}
	}
	
	//TODO balance (ship stats)
	public static class ShipUfo extends Ship {
		
		public ShipUfo() {
			super(GeemLoop.getRC().shipUfoModel, 10, 30, 40, 6, .025F, 15); //speedLinksRechts, speedForward, defence, accuracy (laatste 2 gwn laten)
			
			/*
			 * Accuracy is de maximale angle dat ie kan afwijken (dus hoger is slechter)
			 * Als je gehit wordt doet ie 100 - defence damage, dus als je 90 defence hebt doet ie maar 10 damage
			 */
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
