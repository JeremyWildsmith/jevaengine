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
package io.github.jevaengine.world;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.search.ISearchFilter;
import io.github.jevaengine.world.search.TranslatedSearchFilter;

public final class TranslatedEffectMap implements IImmutableEffectMap {
	private final IImmutableEffectMap m_effectMap;
	private final Vector2F m_offset;

	public TranslatedEffectMap(IImmutableEffectMap effectMap, Vector2F offset) {
		m_effectMap = effectMap;
		m_offset = offset;
	}

	@Override
	public LogicEffects getTileEffects(Vector2F location) {
		return m_effectMap.getTileEffects(location.difference(m_offset));
	}

	@Override
	public LogicEffects[] getTileEffects(ISearchFilter<LogicEffects> filter) {
		return m_effectMap.getTileEffects(new TranslatedSearchFilter<>(filter, new Vector2F(m_offset)));
	}
}
