package io.github.jevaengine.config.json;

import io.github.jevaengine.IAssetStreamFactory;
import io.github.jevaengine.IAssetStreamFactory.AssetStreamConstructionException;
import io.github.jevaengine.config.IConfigurationFactory;
import io.github.jevaengine.config.IImmutableVariable;
import io.github.jevaengine.config.IVariable;
import io.github.jevaengine.config.ValueSerializationException;
import io.github.jevaengine.util.ThreadSafe;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;

public final class JsonConfigurationFactory implements IConfigurationFactory
{
	private final IAssetStreamFactory m_assetFactory;
	
	@Inject
	public JsonConfigurationFactory(IAssetStreamFactory assetFactory)
	{
		m_assetFactory = assetFactory;
	}
	
	@Override
	@ThreadSafe
	public IImmutableVariable create(URI name) throws ConfigurationConstructionException
	{
		return createMutable(name);
	}
	
	@Override
	@ThreadSafe
	public IVariable createMutable(URI name) throws ConfigurationConstructionException
	{
		try
		{
			return JsonVariable.create(m_assetFactory.create(name));
		} catch (IOException | ValueSerializationException | AssetStreamConstructionException e) {
			throw new ConfigurationConstructionException(name, e);
		}
	}
}
