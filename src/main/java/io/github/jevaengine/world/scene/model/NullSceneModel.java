package io.github.jevaengine.world.scene.model;

import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.world.Direction;

import java.util.ArrayList;
import java.util.List;

public final class NullSceneModel implements ISceneModel
{
	@Override
	public void dispose() { }
	
	public NullSceneModel clone()
	{
		return new NullSceneModel();
	}
	
	@Override
	public List<ISceneModelComponent> getComponents()
	{
		return new ArrayList<>();
	}
	
	@Override
	public Rect3F getAABB()
	{
		return new Rect3F();
	}

	@Override
	public void update(int deltaTime) { }

	@Override
	public Direction getDirection()
	{
		return Direction.Zero;
	}

	@Override
	public void setDirection(Direction direction) { }
}
