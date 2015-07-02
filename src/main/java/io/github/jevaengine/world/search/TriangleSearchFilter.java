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

import io.github.jevaengine.math.Matrix2X2;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2F;

public class TriangleSearchFilter<T> implements ISearchFilter<T>
{

	private Matrix2X2 m_worldToBarycentric;

	private Vector2F[] m_vertice;

	public TriangleSearchFilter(Vector2F a, Vector2F b, Vector2F c)
	{

		m_vertice = new Vector2F[]
		{ a, b, c };

		m_worldToBarycentric = new Matrix2X2(m_vertice[2].x - m_vertice[0].x, m_vertice[1].x - m_vertice[0].y, m_vertice[2].y - m_vertice[0].y, m_vertice[1].y - m_vertice[0].y).inverse();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#getSearchBounds()
	 */
	@Override
	public final Rect2D getSearchBounds()
	{
		float xMin = Math.min(Math.min(m_vertice[0].x, m_vertice[1].x), m_vertice[2].x);
		float xMax = Math.max(Math.max(m_vertice[0].x, m_vertice[1].x), m_vertice[2].x);

		float yMin = Math.min(Math.min(m_vertice[0].y, m_vertice[1].y), m_vertice[2].y);
		float yMax = Math.max(Math.max(m_vertice[0].y, m_vertice[1].y), m_vertice[2].y);

		return new Rect2D((int) Math.floor(xMin), (int) Math.floor(yMin), (int) Math.ceil(xMax - xMin), (int) Math.ceil(yMax - yMin));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#shouldInclude(jeva.math.Vector2F)
	 */
	@Override
	public final boolean shouldInclude(Vector2F location)
	{
		Vector2F v = m_worldToBarycentric.dot(location.difference(m_vertice[0]));

		return (v.x + v.y <= 1.0F && v.x >= 0.0F && v.y >= 0.0F);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#filter(java.lang.Object)
	 */
	@Override
	public boolean filter(T o)
	{
		return true;
	}
}
