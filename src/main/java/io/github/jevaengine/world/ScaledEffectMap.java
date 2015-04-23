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
import io.github.jevaengine.world.search.ScaledSearchFilter;

public final class ScaledEffectMap implements IEffectMap
{
	private final IEffectMap m_effectMap;
	private final float m_scale;

	public ScaledEffectMap(IEffectMap effectMap, float scale)
	{
		m_effectMap = effectMap;
		m_scale = scale;
	}
	
	private Vector2F scale(Vector2F v)
	{
		return new Vector2F(m_scale * v.x, m_scale * v.y);
	}
	
	@Override
	public void clear()
	{
		m_effectMap.clear();
	}

	@Override
	public LogicEffects getTileEffects(Vector2F location)
	{
		return m_effectMap.getTileEffects(scale(location));
	}

	@Override
	public LogicEffects[] getTileEffects(ISearchFilter<LogicEffects> filter)
	{
		return m_effectMap.getTileEffects(new ScaledSearchFilter<>(filter, m_scale));
	}

	@Override
	public void applyOverlayEffects(ISearchFilter<LogicEffects> filter, LogicEffects overlay)
	{
		m_effectMap.applyOverlayEffects(new ScaledSearchFilter<>(filter, m_scale), overlay);
	}
}
