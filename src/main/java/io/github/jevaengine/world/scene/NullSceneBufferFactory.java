package io.github.jevaengine.world.scene;

public final class NullSceneBufferFactory implements ISceneBufferFactory
{
	@Override
	public ISceneBuffer create()
	{
		return new NullSceneBuffer();
	}
}
