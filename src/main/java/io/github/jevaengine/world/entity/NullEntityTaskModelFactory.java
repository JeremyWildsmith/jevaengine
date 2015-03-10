package io.github.jevaengine.world.entity;

public class NullEntityTaskModelFactory implements IEntityTaskModelFactory
{
	@Override
	public IEntityTaskModel create(IEntity host)
	{
		return new NullEntityTaskModel();
	}
}
