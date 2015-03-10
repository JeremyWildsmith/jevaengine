package io.github.jevaengine;

import io.github.jevaengine.util.ThreadSafe;

import java.io.InputStream;
import java.net.URI;

public interface IAssetStreamFactory
{
	@ThreadSafe
	InputStream create(URI name) throws AssetStreamConstructionException;
	
	public static final class AssetStreamConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public AssetStreamConstructionException(URI assetName, Exception cause)
		{
			super("Error constructing asset stream " + assetName.toString(), cause);
		}	
	}
}
