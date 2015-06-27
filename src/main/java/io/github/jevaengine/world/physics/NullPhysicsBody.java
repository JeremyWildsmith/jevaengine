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
package io.github.jevaengine.world.physics;

import io.github.jevaengine.math.Circle3F;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.NullObservers;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.entity.DefaultEntity;

public final class NullPhysicsBody implements IPhysicsBody
{
	@Override
	public boolean hasOwner()
	{
		return false;
	}

	@Override
	public DefaultEntity getOwner()
	{
		return null;
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}
	
	@Override
	public float getMass()
	{
		return 1;
	}

	@Override
	public Vector3F getLocation()
	{
		return new Vector3F();
	}

	@Override
	public Direction getDirection()
	{
		return Direction.Zero;
	}

	@Override
	public Vector3F getLinearVelocity()
	{
		return new Vector3F();
	}

	@Override
	public float getAngularVelocity()
	{
		return 0;
	}

	@Override
	@Nullable
	public RayCastIntersection castRay(Vector3F direction, float maxCast)
	{
		return null;
	}

	@Override
	public IObserverRegistry getObservers()
	{
		return new NullObservers();
	}

	@Override
	public void setLocation(Vector3F location) { }

	@Override
	public void setDirection(Direction direction) { }

	@Override
	public void applyLinearImpulse(Vector3F impulse) { }

	@Override
	public void applyAngularImpulse(float impulse) { }

	@Override
	public void applyForceToCenter(Vector3F force) { }

	@Override
	public void applyTorque(float torque) { }

	@Override
	public void setLinearVelocity(Vector3F velocity) { }

	@Override
	public void destory() { }

	@Override
	public IImmutablePhysicsWorld getWorld()
	{
		return new NullPhysicsWorld();
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}
	
	@Override
	public boolean collidesWith(IImmutablePhysicsBody subject)
	{
		return false;
	}
	
	@Override
	public Rect3F getAABB()
	{
		return new Rect3F();
	}	

	@Override
	public Circle3F getBoundingCircle()
	{
		return new Circle3F();
	}
}
