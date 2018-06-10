package info.zthings.geem.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class Bullet {
	private final ModelInstance model;
	
	public Vector3 position;
	public final float speed, divX, divZ; //TODO accuracy stat?
	
	public Bullet(Ship shooter) {
		model = new ModelInstance(GeemLoop.rc.bulletModel);
		position = new Vector3(shooter.position.x, shooter.position.y, shooter.position.z);
		
		double ma = 3, a = (Math.random() * Math.toRadians(ma*2)) - Math.toRadians(ma);
		speed = shooter.baseSpeedZ * shooter.hp * 4F;
		divX = (float) (speed * Math.sin(a));
		divZ = (float) (speed * Math.cos(a));
		
		model.transform.rotate(0, 1, 0, (float)Math.toDegrees(a));
		GeemLoop.rc.ass.get("sfx/laser.wav", Sound.class).play(.3F);
	}
	
	public void update(float dt) {
		position.add(divX * dt, 0, divZ * dt);
		model.transform.setTranslation(position);
	}

	public void render(ResourceContext rc, Environment env) {
		rc.models.render(model, env);
	}
	
}
