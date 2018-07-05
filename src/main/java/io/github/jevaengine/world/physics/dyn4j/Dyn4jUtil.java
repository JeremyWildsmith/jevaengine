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
package io.github.jevaengine.world.physics.dyn4j;

import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.world.physics.PhysicsBodyDescription.PhysicsBodyType;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

import java.util.NoSuchElementException;
import org.dyn4j.geometry.MassType;

final class Dyn4jUtil {
	public static Vector2F wrap(Vector2 vec) {
		return new Vector2F((float) vec.x, (float) vec.y);
	}

	public static Vector2 unwrap(Vector2F vec) {
		return new Vector2(vec.x, vec.y);
	}

	public static MassType unwrap(PhysicsBodyType bodyType) {
		switch (bodyType) {
			case Dynamic:
				return MassType.NORMAL;
			case Static:
				return MassType.INFINITE;
			default:
				throw new NoSuchElementException();
		}
	}
}
