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
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.physics.IPhysicsBody;

import java.util.List;

public final class VelocityLimitSteeringDriver implements ISteeringDriver {
	private final SteeringBehaviorList m_behaviors = new SteeringBehaviorList();

	private final float m_maxSteerVelocity;

	@Nullable
	private IPhysicsBody m_target;

	public VelocityLimitSteeringDriver(float maxSteerVelocity) {
		m_maxSteerVelocity = maxSteerVelocity;
	}

	@Override
	public List<ISteeringBehavior> getBehaviors() {
		return m_behaviors;
	}

	public void attach(IPhysicsBody target) {
		m_target = target;
	}

	public void dettach() {
		m_target = null;
	}

	@Override
	public boolean isDriving() {
		if (m_target == null)
			return false;

		return !getSteerVelocity().isZero();
	}

	private Vector2F getSteerVelocity() {
		return m_behaviors.direct(m_target, new Vector2F()).multiply(m_maxSteerVelocity);
	}

	public void update(int deltaTime) {
		if (m_target == null)
			return;

		Vector2F steerVelocity = getSteerVelocity().difference(m_target.getLinearVelocity().getXy());

		if (steerVelocity.isZero())
			return;

		m_target.applyLinearImpulse(new Vector3F(steerVelocity.multiply(m_target.getMass())
				.add(steerVelocity.normalize().multiply(m_target.getWorld().getMaxFrictionForce())), 0).multiply(deltaTime / 1000.0F));

		m_target.setDirection(Direction.fromVector(steerVelocity));
	}
}
