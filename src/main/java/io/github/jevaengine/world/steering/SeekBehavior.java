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

public final class SeekBehavior implements ISteeringBehavior {
	private final float m_arrivalTolorance;
	private final float m_influence;
	private final ISteeringSubject m_target;
	private final IImmutablePhysicsBody m_subject;

	public SeekBehavior(IImmutablePhysicsBody subject, float influence, float arrivalTolorance, ISteeringSubject target) {
		m_arrivalTolorance = arrivalTolorance;
		m_influence = influence;
		m_target = target;
		m_subject = subject;
	}

	@Override
	public Vector2F direct() {
		Vector2F deltaFromDestination = m_target.getLocation().difference(m_subject.getLocation().getXy());

		if (deltaFromDestination.getLength() < m_arrivalTolorance)
			return new Vector2F();

		return deltaFromDestination.normalize().multiply(m_influence);
	}
}
