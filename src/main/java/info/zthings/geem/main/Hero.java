package info.zthings.geem.main;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

public abstract class Hero {
	public final int speedX, speedZ, defence;
	
	public Hero(int speedX, int speedZ, int defence) {
		this.speedX = speedX;
		this.speedZ = speedZ;
		this.defence = defence;
	}
	
	public abstract Decal getDecal();
}
