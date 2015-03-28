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
package io.github.jevaengine.world.physics.jbox2d;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyType;

import java.util.NoSuchElementException;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

final class JBox2DUtil
{
	public static Vector2F wrap(Vec2 vec)
	{
		return new Vector2F(vec.x, vec.y);
	}
	
	public static Vec2 unwrap(Vector2F vec)
	{
		return new Vec2(vec.x, vec.y);
	}
	
	public static BodyType unwrap(PhysicsBodyType bodyType)
	{
		switch(bodyType)
		{
		case Dynamic:
			return BodyType.DYNAMIC;
		case Kinematic:
			return BodyType.KINEMATIC;
		case Static:
			return BodyType.STATIC;
		default:
			throw new NoSuchElementException();
		}
	}
}
