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

import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.EffectMap;
import io.github.jevaengine.world.IEffectMap.TileEffects;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.search.RectangleSearchFilter;

import java.util.ArrayList;
import java.util.List;

public class EntityRoutingRules implements IRoutingRules
{
	private final Direction[] m_allowedMovements;
	private final IEntity m_subject;
	
	public EntityRoutingRules(IEntity subject, Direction[] allowedMovements)
	{
		m_subject = subject;
		m_allowedMovements = allowedMovements;
	}

	private boolean hasClearance(World world, Vector2D point)
	{
		Rect2F bounds = m_subject.getBody().getAABB().getXy();
		bounds.x = (float)Math.floor(bounds.x + point.x - m_subject.getBody().getLocation().x);
		bounds.y = (float)Math.floor(bounds.y + point.y - m_subject.getBody().getLocation().y);
		bounds.width = (float)Math.ceil(bounds.width);
		bounds.height = (float)Math.ceil(bounds.height);
		
		TileEffects[] effects = world.getTileEffects(new RectangleSearchFilter<EffectMap.TileEffects>(bounds));
		
		for(EffectMap.TileEffects e : effects)
		{
			if(!e.isTraversable(m_subject))
				return false;
		}
		
		return true;
	}
	
	@Override
	public Direction[] getMovements(World world, Vector2F origin)
	{
		List<Direction> m_directions = new ArrayList<>();
		SearchNode currentNode = new SearchNode(null, Direction.Zero, origin.round());
		
		for (Direction dir : m_allowedMovements)
		{
			if (hasClearance(world, currentNode.getLocation(dir)))
			{
				// So sorry for these if statements...
				if (!dir.isDiagonal())
					m_directions.add(dir);
				else if (hasClearance(world, currentNode.getLocation(Direction.fromVector(new Vector2F(dir.getDirectionVector().x, 0)))) &&
									hasClearance(world, currentNode.getLocation(Direction.fromVector(new Vector2F(0, dir.getDirectionVector().y)))))
					m_directions.add(dir);
			}
		}

		return m_directions.toArray(new Direction[m_directions.size()]);
	}

}
