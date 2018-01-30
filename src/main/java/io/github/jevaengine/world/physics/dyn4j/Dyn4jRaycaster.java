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
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.physics.RayCastIntersection;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.RaycastResult;

import java.util.ArrayList;
import java.util.List;

final class Dyn4jRaycaster {
	@Nullable
	public RayCastIntersection cast(Dyn4jWorld world, Vector2F start, Vector2F end, Body owner) {
		List<RaycastResult> results = new ArrayList<>();
		world.getWorld().raycast(Dyn4jUtil.unwrap(start), Dyn4jUtil.unwrap(end), null, true, false, true, results);

		if (!results.isEmpty()) {
			Filter filter = new Filter(owner, start);
			for (RaycastResult result : results) {
				if (filter.accept(result)) {
					Raycast r = result.getRaycast();
					return new RayCastIntersection(new Vector3F(Dyn4jUtil.wrap(r.getNormal()), 0), (float) r.getDistance());
				}
			}
		}

		return null;
	}

	private class Filter {
		private final Vector2F m_source;
		private final Body m_owner;

		public Filter(Body owner, Vector2F source) {
			m_owner = owner;
			m_source = source;
		}

		boolean accept(RaycastResult r) {

			final Object oAPhysicsBody = m_owner.getUserData();
			final Object oBPhysicsBody = r.getBody().getUserData();

			if (!(oAPhysicsBody instanceof Dyn4jBody && oBPhysicsBody instanceof Dyn4jBody))
				return false;

			Dyn4jBody a = (Dyn4jBody) oAPhysicsBody;
			Dyn4jBody b = (Dyn4jBody) oBPhysicsBody;

			return a.collidesWith(b) && b.collidesWith(a);
		}
	}
}
