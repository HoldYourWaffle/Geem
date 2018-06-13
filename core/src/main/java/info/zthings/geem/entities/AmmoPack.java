package info.zthings.geem.entities;

import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class AmmoPack extends Entity {

	public AmmoPack() {
		super(GeemLoop.getRC().ammoPackModel);
		position = new Vector3(0, 0, -10); 
		model.transform.scale(.5F, .5F, .5F);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		model.transform.rotate(0, 1, 0, dt*150);
	}
	
}
