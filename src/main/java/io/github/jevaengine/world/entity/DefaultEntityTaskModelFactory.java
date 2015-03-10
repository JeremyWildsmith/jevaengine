package io.github.jevaengine.world.entity;

public class DefaultEntityTaskModelFactory implements IEntityTaskModelFactory
{
	@Override
	public IEntityTaskModel create(IEntity host)
	{
		return new DefaultEntityTaskModel(host);
	}

}
