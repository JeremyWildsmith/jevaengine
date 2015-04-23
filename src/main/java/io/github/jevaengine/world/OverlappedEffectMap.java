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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OverlappedEffectMap implements IImmutableEffectMap
{
	private final List<IImmutableEffectMap> m_maps = new ArrayList<>();
	
	public OverlappedEffectMap(IImmutableEffectMap ... maps)
	{
		m_maps.addAll(Arrays.asList(maps));
	}
	
	public void add(IImmutableEffectMap e)
	{
		m_maps.add(e);
	}
	
	public void remove(IImmutableEffectMap e)
	{
		m_maps.remove(e);
	}

	@Override
	public LogicEffects getTileEffects(Vector2F location)
	{
		LogicEffects e = new LogicEffects();
		
		for(IImmutableEffectMap m : m_maps)
			e = e.overlay(m.getTileEffects(location));
		
		return e;
	}

	@Override
	public LogicEffects[] getTileEffects(ISearchFilter<LogicEffects> filter)
	{
		List<LogicEffects[]>  mapEffects = new ArrayList<>();
		int elementsToCopy = 0;
		
		for(IImmutableEffectMap m : m_maps)
		{
			LogicEffects[] effects = m.getTileEffects(filter);
			
			elementsToCopy += effects.length;
			
			if(effects.length > 0)
				mapEffects.add(effects);
		}
		
		LogicEffects[] buffer = new LogicEffects[elementsToCopy];
	
		for(LogicEffects[] e : mapEffects)
		{
			System.arraycopy(e, 0, buffer, buffer.length - elementsToCopy, e.length);
			elementsToCopy -= e.length;
		}
		
		return buffer;
	}
}
