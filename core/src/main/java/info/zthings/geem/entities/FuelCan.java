package info.zthings.geem.entities;

import com.badlogic.gdx.math.Vector3;

import info.zthings.geem.main.GeemLoop;

public class FuelCan extends Entity {

	public FuelCan() {
		super(GeemLoop.rc.fuelModel);
		position = new Vector3(0, 0, 0); 
		model.transform.scale(.15F, .15F, .15F);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		model.transform.rotate(0, 1, 0, 150*dt);
	}
	
}
