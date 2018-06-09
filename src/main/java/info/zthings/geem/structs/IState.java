package info.zthings.geem.structs;

import com.badlogic.gdx.assets.AssetManager;

public interface IState {
	public abstract void create(AssetManager ass);
	public abstract void postLoad(AssetManager ass);
	public abstract void dispose();
	
	public abstract void update(float dt);
	public abstract void render(RenderContext rc);
	
	public abstract void pause();
	public abstract void resume();
	public abstract void resize(int width, int height);
	
	public abstract class AStateAdapter implements IState {
		public void create() {}
		public void dispose() {}
		
		public void update(float dt) {}
		public void render(RenderContext rc) {}
		
		public void pause() {}
		public void resume() {}
		public void resize(int width, int height) {}
	}

}
