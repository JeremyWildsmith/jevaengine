/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
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
