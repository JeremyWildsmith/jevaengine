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
package io.github.jevaengine.world.pathfinding;

import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.EffectMap.TileEffects;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.search.RadialSearchFilter;
import java.util.ArrayList;
import java.util.List;

public final class ClearanceRoutingRules implements IRoutingRules
{
	private final IRoutingRules m_rules;
	private final float m_clearance;
	
	public ClearanceRoutingRules(IRoutingRules rules, float clearance)
	{
		m_rules = rules;
		m_clearance = clearance;
	}
	
	private boolean hasClearance(World world, Vector2F point)
	{
		TileEffects[] effects = world.getTileEffects(new RadialSearchFilter<TileEffects>(point, m_clearance));
		
		for(TileEffects e : effects)
		{
			if(!e.isTraversable())
				return false;
		}
		
		return true;
	}
	
	@Override
	public Direction[] getMovements(World world, SearchNode currentNode, Vector2D destination)
	{
		List<Direction> movements = new ArrayList<>();
		
		for(Direction d : m_rules.getMovements(world, currentNode, destination))
		{
			if(hasClearance(world, new Vector2F(currentNode.getLocation().add(d.getDirectionVector()))))
				movements.add(d);
		}
		
		return movements.toArray(new Direction[movements.size()]);
	}
}
