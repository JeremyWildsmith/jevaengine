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
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.entity.IEntity;

public final class NonparticipantPhysicsBody implements IPhysicsBody
{
	private final IEntity m_owner;
	private final Rect3F m_aabb;
	private Vector3F m_location = new Vector3F();
	private Direction m_direction = Direction.Zero;

	private final Observers m_observers = new Observers();
	
	public NonparticipantPhysicsBody(IEntity owner, Rect3F aabb)
	{
		m_owner = owner;
		m_aabb = new Rect3F(aabb);
	}

	public NonparticipantPhysicsBody(IEntity owner)
	{
		m_owner = owner;
		m_aabb = new Rect3F();
	}
	
	public NonparticipantPhysicsBody()
	{
		m_owner = null;
		m_aabb = new Rect3F();
	}
	
	@Override
	public void destory() { }
	
	@Override
	public IImmutablePhysicsWorld getWorld()
	{
		return new NullPhysicsWorld();
	}

	@Override
	public boolean hasOwner()
	{
		return m_owner != null;
	}
	
	@Override
	public IEntity getOwner()
	{
		return m_owner;
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}
	
	@Override
	public float getMass()
	{
		return 1.0F;
	}
	
	@Override
	public Vector3F getLocation()
	{
		return new Vector3F(m_location);
	}

	@Override
	public Direction getDirection()
	{
		return m_direction;
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
		return m_observers;
	}

	@Override
	public void setLocation(Vector3F location)
	{
		m_location = new Vector3F(location);
		m_observers.raise(IPhysicsBodyOrientationObserver.class).locationSet();
	}

	@Override
	public void setDirection(Direction direction)
	{
		m_direction = direction;
		m_observers.raise(IPhysicsBodyOrientationObserver.class).directionSet();
	}
	
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
		return m_aabb.add(m_location);
	}

	@Override
	public Circle3F getBoundingCircle()
	{
		return new Circle3F(m_location.x, m_location.y, m_location.z, Math.max(Math.max(m_aabb.width, m_aabb.height), m_aabb.depth));
	}

	@Override
	public void setCollidable(boolean isCollidable) { }
}
