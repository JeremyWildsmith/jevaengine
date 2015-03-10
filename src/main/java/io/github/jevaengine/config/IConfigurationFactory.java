package io.github.jevaengine.config;

import io.github.jevaengine.util.ThreadSafe;

import java.net.URI;

public interface IConfigurationFactory
{
	@ThreadSafe
	IVariable createMutable(URI name) throws ConfigurationConstructionException;

	@ThreadSafe
	IImmutableVariable create(URI name) throws ConfigurationConstructionException;
	
	public static final class ConfigurationConstructionException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ConfigurationConstructionException(URI assetName,
				Exception cause) {
			super("Error constructing configuration " + assetName.toString(), cause);
		}
	}
}
