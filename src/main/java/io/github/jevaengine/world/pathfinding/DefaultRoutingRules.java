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
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.World;

import java.util.ArrayList;

public class DefaultRoutingRules implements IRoutingRules
{
	private Direction[] m_allowedMovements;
	
	public DefaultRoutingRules(Direction[] allowedMovements)
	{
		m_allowedMovements = allowedMovements;
	}

	@Override
	public Direction[] getMovements(World world, SearchNode currentNode, @Nullable Vector2D destination)
	{
		ArrayList<Direction> m_directions = new ArrayList<Direction>();

		for (Direction dir : m_allowedMovements)
		{
			if (world.getTileEffects((currentNode.getLocation(dir))).isTraversable() && !currentNode.isIneffective(dir))
			{
				// So sorry for these if statements...
				if (!dir.isDiagonal())
					m_directions.add(dir);
				else if (world.getTileEffects(currentNode.getLocation(Direction.fromVector(new Vector2F(dir.getDirectionVector().x, 0)))).isTraversable() &&
							world.getTileEffects(currentNode.getLocation(Direction.fromVector(new Vector2F(0, dir.getDirectionVector().y)))).isTraversable())
					m_directions.add(dir);
			} else if (destination != null && destination.difference(currentNode.getLocation(dir)).isZero())
				return new Direction[0]; //Nowhere to go to arrive at destination...
		}

		return m_directions.toArray(new Direction[m_directions.size()]);
	}

}
