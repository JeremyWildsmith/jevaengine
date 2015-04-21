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

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.search.ISearchFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IEffectMap
{
	public void clear();
	
	public TileEffects getTileEffects(Vector2D location);

	public TileEffects[] getTileEffects(ISearchFilter<TileEffects> filter);
	
	public void applyOverlayEffects(ISearchFilter<TileEffects> filter, TileEffects overlay);
	
	public void applyOverlayEffects(Vector2D location, TileEffects value);
	
	public void overlay(EffectMap overlay, Vector2D offset);

	public void overlay(EffectMap overlay);

	public static class TileEffects
	{
		private final Set<IEntity> m_obstructions = new HashSet<>();
		
		public TileEffects() { }
		
		public TileEffects(TileEffects effects)
		{
			m_obstructions.addAll(effects.m_obstructions);
		}

		public TileEffects(IEntity ... obstructions)
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
		
		public static TileEffects merge(TileEffects[] tiles)
		{
			TileEffects effect = new TileEffects();

			for (TileEffects tile : tiles)
				effect = effect.overlay(tile);

			return effect;
		}

		public TileEffects overlay(TileEffects overlay)
		{
			TileEffects newEffects = new TileEffects();
			newEffects.m_obstructions.addAll(m_obstructions);
			newEffects.m_obstructions.addAll(overlay.m_obstructions);
			
			return newEffects;
		}
	}
}
