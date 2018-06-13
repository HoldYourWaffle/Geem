package info.zthings.geem.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class Bullet extends Entity {
	public final float speed, divX, divZ;
	
	public Bullet(Ship shooter) {
		super(GeemLoop.getRC().bulletModel);
		position = new Vector3(shooter.position.x, shooter.getGunY(), shooter.position.z+1);
		
		double a = (Math.random() * Math.toRadians(shooter.accuracy*2)) - Math.toRadians(shooter.accuracy);
		speed = shooter.baseSpeedZ * 4F;
		divX = (float) (speed * Math.sin(a));
		divZ = (float) (speed * Math.cos(a));
		
		model.transform.rotate(0, 1, 0, (float)Math.toDegrees(a));
		GeemLoop.getRC().ass.get("sfx/laser.wav", Sound.class).play(.3F);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		position.add(divX * dt, 0, divZ * dt);
	}
	
}
