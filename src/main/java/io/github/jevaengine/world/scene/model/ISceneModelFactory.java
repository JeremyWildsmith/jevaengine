package io.github.jevaengine.world.scene.model;

import java.net.URI;

public interface ISceneModelFactory
{
	ISceneModel create(URI name) throws SceneModelConstructionException;

	public static final class SceneModelConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public SceneModelConstructionException(URI assetName, Exception cause)
		{
			super("Error constructing scene model " + assetName.toString(), cause);
		}
	}
}