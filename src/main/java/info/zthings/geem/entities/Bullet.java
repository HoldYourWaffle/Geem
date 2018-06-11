package info.zthings.geem.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class Bullet extends Entity {
	public final float speed, divX, divZ; //TODO accuracy stat?
	
	public Bullet(Ship shooter) {
		super(GeemLoop.rc.bulletModel);
		position = new Vector3(shooter.position.x, shooter.getGunY(), shooter.position.z+1);
		
		double ma = 3, a = (Math.random() * Math.toRadians(ma*2)) - Math.toRadians(ma);
		speed = shooter.baseSpeedZ * 4F; //TODO slow down with fuel
		divX = (float) (speed * Math.sin(a));
		divZ = (float) (speed * Math.cos(a));
		
		model.transform.rotate(0, 1, 0, (float)Math.toDegrees(a));
		GeemLoop.rc.ass.get("sfx/laser.wav", Sound.class).play(.3F);
	}
	
	@Override
	public void update(float dt, PerspectiveCamera cam) {
		super.update(dt, cam);
		position.add(divX * dt, 0, divZ * dt);
	}
	
}
