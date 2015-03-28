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
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.entity.IEntity;


public interface IImmutablePhysicsBody
{
	IImmutablePhysicsWorld getWorld();
	
	boolean hasOwner();
	IEntity getOwner();
	
	boolean isStatic();
	boolean isCollidable();
	
	Circle3F getBoundingCircle();
	Rect3F getAABB();
	float getMass();
	Vector3F getLocation();
	Direction getDirection();
	Vector3F getLinearVelocity();	
	float getAngularVelocity();
	
	float getFriction();
	
	RayCastResults castRay(Vector3F direction, float maxCast);

	IObserverRegistry getObservers();
}
