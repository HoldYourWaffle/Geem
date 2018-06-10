package info.zthings.geem.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;
import info.zthings.geem.structs.ResourceContext;

public class Bullet {
	private final ModelInstance model;
	
	public Vector3 position;
	public final float speed;
	
	public Bullet(Ship shooter) {
		this.model = new ModelInstance(GeemLoop.rc.bulletModel);
		position = new Vector3(shooter.position.x, shooter.position.y, shooter.position.z);
		this.speed = shooter.baseSpeedZ * shooter.hp * 5F;
		GeemLoop.rc.ass.get("sfx/laser.wav", Sound.class).play(.3F);
	}
	
	public void update(float dt) {
		position.add(0, 0, speed * dt);
		model.transform.setTranslation(position);
	}

	public void render(ResourceContext rc, PerspectiveCamera cam, Environment env) {
		rc.models.render(model, env);
	}
	
}
