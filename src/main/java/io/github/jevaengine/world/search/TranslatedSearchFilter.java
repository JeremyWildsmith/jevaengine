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

public final class TranslatedSearchFilter<T> implements ISearchFilter<T> {
	private final ISearchFilter<T> m_searchFilter;
	private final Vector2F m_offset;

	public TranslatedSearchFilter(ISearchFilter<T> searchFilter, Vector2F offset) {
		m_searchFilter = searchFilter;
		m_offset = new Vector2F(offset);
	}

	@Override
	public Rect2D getSearchBounds() {
		Rect2D bounds = m_searchFilter.getSearchBounds();

		bounds.x -= m_offset.x;
		bounds.y -= m_offset.y;

		return bounds;
	}

	@Override
	public boolean shouldInclude(Vector2F location) {
		return m_searchFilter.shouldInclude(location.difference(m_offset));
	}

	@Override
	public boolean filter(T item) {
		return m_searchFilter.filter(item);
	}
}
