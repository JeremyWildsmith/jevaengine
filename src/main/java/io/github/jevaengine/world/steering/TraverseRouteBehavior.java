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
package io.github.jevaengine.world.steering;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.pathfinding.Route;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

public class TraverseRouteBehavior implements ISteeringBehavior
{
	private final SeekBehavior m_seekBehavior;
	private final PointSubject m_seekTarget = new PointSubject(new Vector2F());
	private final Route m_route;
	private final float m_arrivalTolorance;
	private final float m_waypointTolorance;
	
	public TraverseRouteBehavior(float influence, Route route, float arrivaleTolorance, float waypointTolorance)
	{
		m_seekBehavior = new SeekBehavior(influence, m_seekTarget);
		m_route = new Route(route);
		m_arrivalTolorance = arrivaleTolorance;
		m_waypointTolorance = waypointTolorance;
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{		
		//Attempt to validate path
		if(subject.getOwner() != null)
		{
			if(m_route.validate(subject.getLocation().getXy(), subject.getOwner().getWorld(), 1) == 0)
				return currentDirection;
		}
		
		Vector2F currentTarget = m_route.getCurrentTarget();
		
		if(currentTarget == null)
			return currentDirection;
		
		float tolorance = m_route.hasNext() ? m_waypointTolorance : m_arrivalTolorance;
		if(currentTarget.difference(subject.getLocation().getXy()).getLength() < tolorance)
		{
			m_route.nextTarget();
			return direct(subject, currentDirection);
		}else
		{
			m_seekTarget.setLocation(currentTarget);
			return m_seekBehavior.direct(subject, currentDirection);
		}
	}
}
