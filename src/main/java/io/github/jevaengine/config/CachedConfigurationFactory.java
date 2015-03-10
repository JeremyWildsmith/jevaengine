package io.github.jevaengine.config;

import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.ThreadSafe;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

public final class CachedConfigurationFactory implements IConfigurationFactory
{
	private HashMap<URI, SoftReference<IImmutableVariable>> m_varCache = new HashMap<>();
	
	private final IConfigurationFactory m_configurationFactory;
	
	@Inject
	public CachedConfigurationFactory(IConfigurationFactory configurationFactory)
	{
		m_configurationFactory = configurationFactory;
	}
	
	@ThreadSafe
	@Nullable
	private IImmutableVariable findCachedConfiguration(URI formal)
	{
		synchronized(m_varCache)
		{
			Iterator<Map.Entry<URI, SoftReference<IImmutableVariable>>> it = m_varCache.entrySet().iterator();
	
			while(it.hasNext())
			{
				Map.Entry<URI, SoftReference<IImmutableVariable>> entry = it.next();
				
				if(entry.getValue().get() != null)
				{
					if(entry.getKey().equals(formal))
						return entry.getValue().get();
				}else
					it.remove();
			}
		
			return null;
		}
	}

	@Override
	@ThreadSafe
	public IImmutableVariable create(URI name) throws ConfigurationConstructionException
	{
		synchronized(m_varCache)
		{
			IImmutableVariable cacheVar = findCachedConfiguration(name);
			
			if(cacheVar != null)
				return cacheVar;
			else
			{
				IImmutableVariable loadVar = createMutable(name);

				m_varCache.put(name, new SoftReference<IImmutableVariable>(loadVar));
	
				return loadVar;
			}
		}
	}
	
	@Override
	@ThreadSafe
	public IVariable createMutable(URI name) throws ConfigurationConstructionException
	{
		return m_configurationFactory.createMutable(name);
	}	
}
