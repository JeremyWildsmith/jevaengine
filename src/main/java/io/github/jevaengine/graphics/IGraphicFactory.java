package io.github.jevaengine.graphics;

import io.github.jevaengine.util.ThreadSafe;

import java.net.URI;

public interface IGraphicFactory
{
	@ThreadSafe
	IGraphic create(int width, int height);

	@ThreadSafe
	IImmutableGraphic create(URI name) throws GraphicConstructionException;
	
	public static final class GraphicConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public GraphicConstructionException(URI assetName, Exception cause) {
			super("Error constructing graphic " + assetName.toString(), cause);
		}
	}
}
