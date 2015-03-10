package io.github.jevaengine.world;

import io.github.jevaengine.IInitializationProgressMonitor;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultWorldFactory.class)
public interface IWorldFactory
{
	World create(URI name, float tileWidthMeters, float tileHeightMeters, IInitializationProgressMonitor progressMonitor) throws WorldConstructionException;

	public static final class WorldConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public WorldConstructionException(URI assetName, Exception cause) {
			super("Error constructing world " + assetName.toString(), cause);
		}
	}
	
	public static final class NullWorldFactory implements IWorldFactory
	{

		@Override
		public World create(URI name, float tileWidthMeters,
				float tileHeightMeters,
				IInitializationProgressMonitor progressMonitor)
				throws WorldConstructionException
		{
			throw new WorldConstructionException(name, new NullWorldFactoryCannotConstructWorldException());
		}
		
		public static final class NullWorldFactoryCannotConstructWorldException extends Exception
		{
			private static final long serialVersionUID = 1L;

			private NullWorldFactoryCannotConstructWorldException() { }
		}
		
	}
}
