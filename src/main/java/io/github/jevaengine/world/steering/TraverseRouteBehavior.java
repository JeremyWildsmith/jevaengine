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
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.pathfinding.Route;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;

public class TraverseRouteBehavior implements ISteeringBehavior
{
	private final SeekBehavior m_seekBehavior;
	private final PointSubject m_seekTarget = new PointSubject(new Vector2F());
	private final Route m_route;
	
	public TraverseRouteBehavior(float influence, Route route, float arrivaleTolorance)
	{
		m_seekBehavior = new SeekBehavior(influence, arrivaleTolorance, m_seekTarget);
		m_route = new Route(route);
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		Vector2F currentTarget = m_route.getCurrentTarget();
		
		if(currentTarget == null)
			return new Vector2F();
		
		m_seekTarget.setLocation(currentTarget);
		
		Vector2F direction = m_seekBehavior.direct(subject, currentDirection);
		
		if(direction.isZero())
		{
			m_route.nextTarget();
			return direct(subject, currentDirection);
		}else
			return direction;
	}
}
