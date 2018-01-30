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
package io.github.jevaengine.util;

import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<X, Y> {
	private Map<X, Y> m_map;

	public MapBuilder() {
		m_map = new HashMap<X, Y>();
	}

	public MapBuilder(Map<X, Y> map) {
		m_map = map;
	}

	public MapBuilder<X, Y> a(X x, Y y) {
		m_map = new HashMap<>(m_map);
		m_map.put(x, y);

		return new MapBuilder<>(m_map);
	}

	public Map<X, Y> get() {
		return m_map;
	}
}
