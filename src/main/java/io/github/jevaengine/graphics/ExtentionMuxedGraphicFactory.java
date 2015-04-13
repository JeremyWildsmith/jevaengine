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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class ExtentionMuxedGraphicFactory implements IGraphicFactory
{
	private final Map<String, IGraphicFactory> m_factories = new HashMap<>();
	private final IGraphicFactory m_defaultGraphicFactory;
	
	public ExtentionMuxedGraphicFactory(IGraphicFactory defaultGraphicFactory)
	{
		m_defaultGraphicFactory = defaultGraphicFactory;
	}
	
	public void put(String extention, IGraphicFactory factory)
	{
		String ext = extention.startsWith(".") ? extention : "." + extention;
		m_factories.put(ext, factory);
	}
	
	@Override
	public IGraphic create(int width, int height)
	{
		return m_defaultGraphicFactory.create(width, height);
	}

	@Override
	public IImmutableGraphic create(URI name) throws GraphicConstructionException
	{
		for(Map.Entry<String, IGraphicFactory> e : m_factories.entrySet())
		{
			if(name.getPath().endsWith(e.getKey()))
				return e.getValue().create(name);
		}
		
		return m_defaultGraphicFactory.create(name);
	}
	
}
