package info.zthings.geem.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;

import info.zthings.geem.structs.RenderContext;

public class DebugRenderer implements Disposable {
	private Model gridModel, axesModel;
	private ModelInstance gridInstance, axesInstance;
	
	public DebugRenderer() {
		final float GRID_W = 20,
				GRID_D = 20,
				GRID_STEP = .5f,
				y = .1f;
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
		builder.setColor(1, 1, 1, .5f);
		for (float x = -GRID_W; x <= GRID_W; x += GRID_STEP) {
			builder.line(x, 0, 0, x, 0, GRID_D);
			for (float z = 0; z <= GRID_D; z += GRID_STEP)
				builder.line(x, 0, z, GRID_W, 0, z);
		}
		
	
		gridModel = modelBuilder.end();
		gridInstance = new ModelInstance(gridModel);
		
		modelBuilder.begin();
		builder = modelBuilder.part("axes", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
		builder.setColor(Color.RED);
		builder.line(0, y, 0, GRID_D, y, 0);
		
		builder.setColor(Color.GREEN);
		builder.line(0, y, 0, 0, y, GRID_D-4);
		axesModel = modelBuilder.end();
		axesInstance = new ModelInstance(axesModel);
	}
	
	@Override
	public void dispose() {
		gridModel.dispose();
		axesModel.dispose();
	}

	public void update(float dt, PerspectiveCamera cam) {
		axesInstance.transform.set(cam.position.x, 0, cam.position.z+4, 0, 0, 0, 0);
		gridInstance.transform.set((int)cam.position.x, 0, (int)cam.position.z, 0, 0, 0, 0);
	}

	public void render(RenderContext rc, PerspectiveCamera cam) {
		rc.models.begin(cam);
		rc.models.render(gridInstance);
		rc.models.render(axesInstance);
		rc.models.end();
	}
}
