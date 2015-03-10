package io.github.jevaengine.graphics;

import io.github.jevaengine.util.ThreadSafe;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;

public final class CachedGraphicFactory implements IGraphicFactory
{
	private final HashMap<URI, WeakReference<IImmutableGraphic>> m_imageCache = new HashMap<>();
	private final IGraphicFactory m_graphicFactory;
	
	public CachedGraphicFactory(IGraphicFactory graphicFactory)
	{
		m_graphicFactory = graphicFactory;
	}
	
	@Override
	@ThreadSafe
	public IGraphic create(int width, int height)
	{
		return m_graphicFactory.create(width, height);
	}

	@Override
	@ThreadSafe
	public IImmutableGraphic create(URI name) throws GraphicConstructionException
	{
		synchronized (m_imageCache)
		{
			IImmutableGraphic img = (m_imageCache.containsKey(name) ? m_imageCache.get(name).get() : null);
			
			if (img == null)
			{
				img = m_graphicFactory.create(name);
				m_imageCache.put(name, new WeakReference<IImmutableGraphic>(img));
			}
			
			return img;
		}
	}
}
