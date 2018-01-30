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
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.physics.IPhysicsBody;
import io.github.jevaengine.world.physics.IPhysicsWorld;
import io.github.jevaengine.world.physics.IPhysicsWorldFactory;
import io.github.jevaengine.world.physics.PhysicsBodyDescription;

public final class Dyn4jDebuggerWorldFactory implements IPhysicsWorldFactory {
	@Override
	public IPhysicsWorld create(float maxSurfaceFrictionForceNewtonMeters) {
		final Dyn4jWorld world = new Dyn4jWorld(maxSurfaceFrictionForceNewtonMeters);

		final Dyn4jDebuggerWorld debugger = new Dyn4jDebuggerWorld(world.getWorld());

		return new IPhysicsWorld() {
			@Override
			public void update(int deltaTime) {
				debugger.gameLoop();
				world.update(deltaTime);
			}

			@Override
			public void setGravity(Vector2F gravity) {
				world.setGravity(gravity);
			}

			@Override
			public IPhysicsBody createBody(IEntity owner, PhysicsBodyDescription bodyDescription) {
				return world.createBody(owner, bodyDescription);
			}

			@Override
			public IPhysicsBody createBody(PhysicsBodyDescription bodyDescription) {
				return world.createBody(bodyDescription);
			}

			@Override
			public float getMaxFrictionForce() {
				return world.getMaxFrictionForce();
			}
		};
	}
}
