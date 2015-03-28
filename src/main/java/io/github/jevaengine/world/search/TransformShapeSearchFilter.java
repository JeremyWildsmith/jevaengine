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

import java.awt.Rectangle;
import java.awt.Shape;

import io.github.jevaengine.math.Matrix2X2;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2F;

public class TransformShapeSearchFilter<T> implements ISearchFilter<T>
{

	private Shape m_shape;

	private Matrix2X2 m_transform;

	public TransformShapeSearchFilter(Matrix2X2 transform, Shape shape)
	{
		m_transform = transform;
		m_shape = shape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#getSearchBounds()
	 */
	@Override
	public Rect2D getSearchBounds()
	{
		Rectangle bounds = m_shape.getBounds();

		Matrix2X2 inverse = m_transform.inverse();

		Vector2F tl = inverse.dot(new Vector2F(bounds.x, bounds.y));
		Vector2F tr = inverse.dot(new Vector2F(bounds.x + bounds.width, bounds.y));
		Vector2F bl = inverse.dot(new Vector2F(bounds.x, bounds.y + bounds.height));
		Vector2F br = inverse.dot(new Vector2F(bounds.x + bounds.width, bounds.y + bounds.height));

		return new Rect2D((int) (tl.x), (int) (tr.y), (int) (br.x - tl.x), (int) (bl.y - tr.y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.jeremywildsmith.jevaengine.world.ISearchFilter#shouldInclude(jeva.math.Vector2F)
	 */
	@Override
	public boolean shouldInclude(Vector2F location)
	{
		Vector2F transformedLocation = m_transform.dot(location);

		return m_shape.contains(transformedLocation.x, transformedLocation.y);
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
