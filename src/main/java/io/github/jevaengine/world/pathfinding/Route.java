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

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Route
{
	private ArrayList<Vector2F> m_path = new ArrayList<>();
	private final IRoutingRules m_rules;
	
	public Route(IRoutingRules rules, Vector2F ... path)
	{
		m_rules = rules;
		m_path.addAll(Arrays.asList(path));
	}
	
	public Route(IRoutingRules rules)
	{
		m_rules = rules;
	}
	
	public Route(Route src)
	{
		m_rules = src.m_rules;
		m_path.addAll(src.m_path);
	}
	
	public void truncate(int maxSteps)
	{
		if (m_path.size() <= maxSteps)
			return;

		m_path = new ArrayList<>(m_path.subList(0, maxSteps));
	}

	public int length()
	{
		return m_path.size();
	}

	@Nullable
	public Vector2F getCurrentTarget()
	{
		if (m_path.isEmpty())
			return null;

		return m_path.get(0);
	}

	public boolean nextTarget()
	{
		if (!m_path.isEmpty())
			m_path.remove(0);

		return !m_path.isEmpty();
	}

	public boolean hasNext()
	{
		return m_path.size() > 1;
	}
	
	public Vector2F peek(int ahead)
	{
		return m_path.get(ahead);
	}

	public void addWaypoint(Vector2F node)
	{
		m_path.add(node);
	}
	
	public void addWaypoints(Vector2F ... nodes)
	{
		for(Vector2F node : nodes)
			addWaypoint(node);
	}
	
	public int validate(Vector2F start, World world)
	{
		List<Vector2F> path = new ArrayList<>(m_path);
		
		if(path.isEmpty())
			return 0;
		
		if(!path.get(0).difference(start).isZero())
			path.add(0, start);
		
		List<Direction> directions = new ArrayList<>();
		
		for(int i = 0; i < m_path.size() - 1; i++)
		{
			Vector2F a = m_path.get(i);
			Vector2F b = m_path.get(i + 1);
			
			directions.add(Direction.fromVector(b.difference(a)));
		}
		
		int validSteps = 0;
		for(; validSteps < m_path.size() - 1; validSteps++)
		{
			Vector2F origin = m_path.get(validSteps);
			
			Direction movements[] = m_rules.getMovements(world, origin);
			
			if(Arrays.binarySearch(movements, directions.get(validSteps)) < 0)
				break;
		}
		
		return validSteps;
	}
}
