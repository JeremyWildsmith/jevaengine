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
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.search.ISearchFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IImmutableEffectMap
{
	public LogicEffects getTileEffects(Vector2F location);
	public LogicEffects[] getTileEffects(ISearchFilter<LogicEffects> filter);
	
	public static class LogicEffects
	{
		private final Set<IEntity> m_obstructions = new HashSet<>();
		
		public LogicEffects() { }
		
		public LogicEffects(LogicEffects effects)
		{
			m_obstructions.addAll(effects.m_obstructions);
		}

		public LogicEffects(IEntity ... obstructions)
		{
			m_obstructions.addAll(Arrays.asList(obstructions));
		}

		public boolean isTraversable(IEntity subject)
		{
			for(IEntity e : m_obstructions)
			{
				if(e != subject && e.getBody().collidesWith(subject.getBody()))
					return false;
			}
			
			return true;
		}
		
		public float getSightEffect()
		{
			return 0;
		}
		
		public static LogicEffects merge(LogicEffects[] tiles)
		{
			LogicEffects effect = new LogicEffects();

			for (LogicEffects tile : tiles)
				effect = effect.overlay(tile);

			return effect;
		}

		public LogicEffects overlay(LogicEffects overlay)
		{
			LogicEffects newEffects = new LogicEffects();
			newEffects.m_obstructions.addAll(m_obstructions);
			newEffects.m_obstructions.addAll(overlay.m_obstructions);
			
			return newEffects;
		}
	}
}
