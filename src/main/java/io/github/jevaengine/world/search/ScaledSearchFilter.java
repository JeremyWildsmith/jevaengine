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
package io.github.jevaengine.world.search;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2F;

public final class ScaledSearchFilter<T> implements ISearchFilter<T>
{
	private final ISearchFilter<T> m_searchFilter;
	private final float m_scale;
	
	public ScaledSearchFilter(ISearchFilter<T> searchFilter, float scale)
	{
		m_searchFilter = searchFilter;
		m_scale = scale;
	}
	
	@Override
	public Rect2D getSearchBounds()
	{
		Rect2D bounds = m_searchFilter.getSearchBounds();
		
		return new Rect2D((int)Math.round(m_scale * bounds.x),
							(int)Math.round(m_scale * bounds.y),
							(int)Math.round(m_scale * bounds.width),
							(int)Math.round(m_scale * bounds.height));
	}

	@Override
	public boolean shouldInclude(Vector2F location)
	{
		Vector2F scaled = new Vector2F(location.x * m_scale, location.y * m_scale);
		
		return m_searchFilter.shouldInclude(scaled);
	}

	@Override
	public T filter(T item)
	{
		return m_searchFilter.filter(item);
	}
}
