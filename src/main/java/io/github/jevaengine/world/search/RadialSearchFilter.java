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

public class RadialSearchFilter<T> implements ISearchFilter<T> {
	private Vector2F m_center;
	private float m_radius;

	public RadialSearchFilter(Vector2F center, float radius) {
		m_center = center;
		m_radius = radius;
	}

	@Override
	public Rect2D getSearchBounds() {
		return new Rect2D((int) Math.floor(m_center.x - m_radius),
				(int) Math.floor(m_center.y - m_radius),
				(int) Math.ceil(m_radius * 2),
				(int) Math.ceil(m_radius * 2));
	}

	@Override
	public boolean shouldInclude(Vector2F location) {
		return location.difference(m_center).getLength() <= m_radius;
	}

	@Override
	public boolean filter(T item) {
		return true;
	}

}
