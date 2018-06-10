package info.zthings.geem.structs;

public interface IState {
	
	public abstract void create();//AssetManager ass);
	//public abstract void postLoad(AssetManager ass);
	public abstract void dispose();
	
	public abstract void update(float dt);
	public abstract void render(RenderContext rc);
	
	public abstract void pause();
	public abstract void resume();
	public abstract void resize(int width, int height);
	
}
