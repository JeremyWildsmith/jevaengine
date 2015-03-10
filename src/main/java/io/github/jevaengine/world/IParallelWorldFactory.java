package io.github.jevaengine.world;

import io.github.jevaengine.IInitializationMonitor;
import io.github.jevaengine.world.IWorldFactory.WorldConstructionException;

import java.net.URI;

public interface IParallelWorldFactory
{
	void create(final URI name, final float tileWidthMeters, final float tileHeightMeters, final IInitializationMonitor<World, WorldConstructionException> monitor);	
}
