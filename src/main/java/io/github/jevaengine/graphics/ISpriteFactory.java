package io.github.jevaengine.graphics;

import io.github.jevaengine.util.ThreadSafe;

import java.net.URI;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultSpriteFactory.class)
public interface ISpriteFactory
{
	@ThreadSafe
	Sprite create(URI path) throws SpriteConstructionException;
	
	public static final class SpriteConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public SpriteConstructionException(URI assetName, Exception cause) {
			super("Error constructing sprite " + assetName.toString(), cause);
		}
	}
}
