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
import java.util.NoSuchElementException;

public class BinarySearchFilter<T> implements ISearchFilter<T>
{

	enum Operation
	{

		Or,

		And,

		Nand,

		Nor,

		Xor
	}

	private Operation m_operation;

	private ISearchFilter<T> m_filterA;

	private ISearchFilter<T> m_filterB;

	public BinarySearchFilter(ISearchFilter<T> a, ISearchFilter<T> b, Operation operation)
	{
		m_operation = operation;
		m_filterA = a;
		m_filterB = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#getSearchBounds()
	 */
	@Override
	public Rect2D getSearchBounds()
	{
		Rect2D boundsA = m_filterA.getSearchBounds();
		Rect2D boundsB = m_filterB.getSearchBounds();

		return new Rect2D(Math.min(boundsA.x, boundsB.x), Math.min(boundsA.y, boundsB.y), Math.max(boundsA.width, boundsB.width), Math.max(boundsA.height, boundsB.height));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#shouldInclude(jeva.math.Vector2F)
	 */
	@Override
	public final boolean shouldInclude(Vector2F location)
	{
		switch (m_operation)
		{
			case And:
				return m_filterA.shouldInclude(location) && m_filterB.shouldInclude(location);
			case Or:
				return m_filterA.shouldInclude(location) || m_filterB.shouldInclude(location);
			case Nand:
				return !(m_filterA.shouldInclude(location) && m_filterB.shouldInclude(location));
			case Nor:
				return !(m_filterA.shouldInclude(location) || m_filterB.shouldInclude(location));
			case Xor:
				return (m_filterA.shouldInclude(location) ^ m_filterB.shouldInclude(location));
			default:
				throw new NoSuchElementException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#filter(java.lang.Object)
	 */
	@Override
	public T filter(T item)
	{
		return item;
	}

}
