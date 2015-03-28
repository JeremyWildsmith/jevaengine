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

import io.github.jevaengine.math.Circle2F;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;
import io.github.jevaengine.world.physics.RayCastResults;

public final class AvoidanceBehavior implements ISteeringBehavior
{
	private final int RAYCAST_ITERATIONS = 3;
	private final float m_reactionDistance;
	
	public AvoidanceBehavior(float reactionDistance)
	{
		m_reactionDistance = reactionDistance;
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		if(currentDirection.isZero())
			return currentDirection;
		
		Circle2F bounds = subject.getAABB().getXy().getBoundingCircle();
		float reactionDistance = m_reactionDistance + bounds.radius;
		
		float angle = (float)Math.PI / (2 * RAYCAST_ITERATIONS);
		Vector2F travelDirection = currentDirection.normalize();
		
		for(int i = 0; i < RAYCAST_ITERATIONS; i++)
		{
			Vector2F rayDirectionLeft = travelDirection.rotate(-angle);
			Vector2F rayDirectionRight = travelDirection.rotate(angle);
			
			RayCastResults resultsLeft = subject.castRay(new Vector3F(rayDirectionLeft, 0), reactionDistance);
			RayCastResults resultsRight = subject.castRay(new Vector3F(rayDirectionRight, 0), reactionDistance);
			RayCastResults resultsStraight = subject.castRay(new Vector3F(travelDirection, 0), reactionDistance);
			
			if(resultsLeft != null && resultsRight != null && resultsStraight != null)
				return new Vector2F();
			else if(resultsLeft != null && resultsRight != null)
				break;
			else if(resultsLeft != null)
				travelDirection = travelDirection.rotate(angle);
			else if(resultsRight != null)
				travelDirection = travelDirection.rotate(-angle);
			else
				break;
		}
		
		return travelDirection;
	}
}
