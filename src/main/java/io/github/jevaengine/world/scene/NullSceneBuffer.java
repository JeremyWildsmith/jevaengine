package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;

import java.awt.Graphics2D;

public final class NullSceneBuffer implements ISceneBuffer
{

	@Override
	public void render(Graphics2D g, int x, int y, float scale) { }

	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation, float scale)
	{
		return new Vector2F();
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location, float scale)
	{
		return new Vector2D();
	}
	
	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation)
	{
		return translateScreenToWorld(screenLocation, 1.0F);
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location)
	{
		return new Vector2D();
	}

	@Override
	public <T> T pick(Class<T> clazz, int x, int y, float scale)
	{
		return null;
	}

	@Override
	public void addModel(IImmutableSceneModel model, IEntity dispatcher, Vector3F location) { }

	@Override
	public void addModel(IImmutableSceneModel model, Vector3F location) { }

	@Override
	public void reset() { }

	@Override
	public void translate(Vector2D translation) { }
}
