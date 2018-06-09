package info.zthings.geem.structs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

public class Ship {
	public final int speedX, speedZ, defence;
	public final Texture tex;
	public final Vector3 position;
	
	public Ship(Texture tex, int speedX, int speedZ, int defence) {
		this.tex = tex;
		this.speedX = speedX;
		this.speedZ = speedZ;
		this.defence = defence;
		this.position = new Vector3(Vector3.Zero);
	}
	
	public void setPosition(Vector3 pos) {
		this.position.set(pos);
	}
	
	public void setPosition(int x, int y, int z) {
		this.position.set(x, y, z);
	}
	
	public void render(DecalBatch db) {
		//TODO render hero
		//db.add(decal);
	}
	
}
