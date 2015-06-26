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
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.physics.RayCastResults;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

final class JBox2DRaycaster
{
	private Fixture m_owner;
	private Vector2F m_normal;
	private float m_distance;
	private boolean m_intersected = false;

	@Nullable
	public RayCastResults cast(JBox2DWorld world, Vector2F start, Vector2F end, Fixture owner)
	{
		m_owner = owner;
		m_normal = new Vector2F();
		m_distance = Float.MAX_VALUE;
		m_intersected = false;
		world.getWorld().raycast(new CallbackHandler(start), JBox2DUtil.unwrap(start), JBox2DUtil.unwrap(end));
		
		if(!m_intersected)
			return null;
		else
			return new RayCastResults(new Vector3F(m_normal, 0), m_distance);
	}
	
	private class CallbackHandler implements RayCastCallback
	{
		private final Vector2F m_source;
		
		public CallbackHandler(Vector2F source)
		{
			m_source = source;
		}
		
		@Override
		public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction)
		{
			
			final Object oAPhysicsBody = m_owner.getBody().m_userData;
			final Object oBPhysicsBody = fixture.getBody().m_userData;
		
			if(!(oAPhysicsBody instanceof JBox2DBody && oBPhysicsBody instanceof JBox2DBody))
				return 1.0F;

			JBox2DBody a = (JBox2DBody)oAPhysicsBody;
			JBox2DBody b = (JBox2DBody)oBPhysicsBody;
			
			float distance = m_source.difference(JBox2DUtil.wrap(point)).getLength();
			
			if(distance <= m_distance && a.collidesWith(b) && b.collidesWith(a))
			{
				m_distance = distance;
				m_normal = JBox2DUtil.wrap(normal);
				m_intersected = true;
			}
			
			return 1.0F;
		}
		
	}
}
