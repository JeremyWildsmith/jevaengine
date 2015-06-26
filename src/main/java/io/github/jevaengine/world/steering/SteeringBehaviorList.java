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
import io.github.jevaengine.world.physics.IImmutablePhysicsBody;
import java.util.ArrayList;
import java.util.Arrays;

public final class SteeringBehaviorList extends ArrayList<ISteeringBehavior> implements ISteeringBehavior
{
	private static final long serialVersionUID = 1L;

	public SteeringBehaviorList(ISteeringBehavior ... behaviors)
	{
		super(Arrays.asList(behaviors));
	}
	
	@Override
	public Vector2F direct(IImmutablePhysicsBody subject, Vector2F currentDirection)
	{
		Vector2F steerDirection = new Vector2F();
		
		for(ISteeringBehavior b : this)
			steerDirection = b.direct(subject, steerDirection);
		
		return steerDirection.isZero() ? steerDirection : steerDirection.normalize();
	}
}
