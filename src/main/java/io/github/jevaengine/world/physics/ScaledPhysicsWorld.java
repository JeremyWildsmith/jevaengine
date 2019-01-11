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
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.entity.IEntity;

/**
 * @author Jeremy
 */
public final class ScaledPhysicsWorld extends ImmutableScaledPhysicsWorld implements IPhysicsWorld {
	private final IPhysicsWorld m_world;

	public ScaledPhysicsWorld(IPhysicsWorld world, float scale) {
		super(world, scale);
		m_world = world;
	}

	@Override
	public void update(int deltaTime) {
		m_world.update(deltaTime);
	}

	@Override
	public void setGravity(Vector2F gravity) {
		m_world.setGravity(gravity);
	}

	@Override
	public IPhysicsBody createBody(IEntity owner, PhysicsBodyDescription bodyDescription) {
		PhysicsBodyDescription d = new PhysicsBodyDescription(bodyDescription);
		d.shape.aabb.x *= m_scale;
		d.shape.aabb.y *= m_scale;
		d.shape.aabb.z *= m_scale;
		d.shape.aabb.width *= m_scale;
		d.shape.aabb.height *= m_scale;
		d.shape.aabb.depth *= m_scale;

		d.density *= m_scale;
		d.friction *= m_scale;

		return new ScaledPhysicsBody(m_world.createBody(owner, d));
	}

	@Override
	public IPhysicsBody createBody(PhysicsBodyDescription bodyDescription) {
		PhysicsBodyDescription d = new PhysicsBodyDescription(bodyDescription);
		d.shape.aabb.x *= m_scale;
		d.shape.aabb.y *= m_scale;
		d.shape.aabb.z *= m_scale;
		d.shape.aabb.width *= m_scale;
		d.shape.aabb.height *= m_scale;
		d.shape.aabb.depth *= m_scale;

		d.density *= m_scale;
		d.friction *= m_scale;

		return new ScaledPhysicsBody(m_world.createBody(d));
	}

	@Override
	public float getMaxFrictionForce() {
		return m_world.getMaxFrictionForce();
	}

	public class ImmutableScaledPhysicsBody implements IImmutablePhysicsBody {
		protected IImmutablePhysicsBody m_body;
		protected Observers m_observers = new Observers();

		public ImmutableScaledPhysicsBody(IImmutablePhysicsBody body) {
			m_body = body;
			body.getObservers().add(new ScaledPhysicsBodyContactObserverRelay());
		}

		@Override
		public IImmutablePhysicsWorld getWorld() {
			return ScaledPhysicsWorld.this;
		}

		@Override
		public boolean hasOwner() {
			return m_body.hasOwner();
		}

		@Override
		public IEntity getOwner() {
			return m_body.getOwner();
		}

		@Override
		public boolean isStatic() {
			return m_body.isStatic();
		}

		@Override
		public boolean isSensor() {
			return m_body.isSensor();
		}

		@Override
		public boolean isCollidable() {
			return m_body.isCollidable();
		}

		@Override
		public boolean collidesWith(IImmutablePhysicsBody subject) {
			return m_body.collidesWith(subject);
		}

		@Override
		public Circle3F getBoundingCircle() {
			Circle3F bounding = m_body.getBoundingCircle();

			bounding.x /= m_scale;
			bounding.y /= m_scale;
			bounding.z /= m_scale;
			bounding.radius /= m_scale;

			return bounding;
		}

		@Override
		public Rect3F getAABB() {
			Rect3F aabb = m_body.getAABB();

			aabb.x /= m_scale;
			aabb.y /= m_scale;
			aabb.z /= m_scale;
			aabb.width /= m_scale;
			aabb.height /= m_scale;
			aabb.depth /= m_scale;

			return aabb;
		}

		@Override
		public float getMass() {
			return m_body.getMass() / m_scale;
		}

		@Override
		public Vector3F getLocation() {
			Vector3F location = m_body.getLocation();

			location.x /= m_scale;
			location.y /= m_scale;
			location.z /= m_scale;

			return location;
		}

		@Override
		public Direction getDirection() {
			return m_body.getDirection();
		}

		@Override
		public Vector3F getLinearVelocity() {
			return m_body.getLinearVelocity().multiply(1.0F / m_scale);
		}

		@Override
		public float getAngularVelocity() {
			return m_body.getAngularVelocity() / m_scale;
		}

		@Override
		public RayCastIntersection castRay(Vector3F direction, float maxCast) {
			return m_body.castRay(direction, maxCast * m_scale);
		}

		@Override
		public IObserverRegistry getObservers() {
			return m_observers;
		}

		public final class ScaledPhysicsBodyContactObserverRelay implements IPhysicsBodyContactObserver, IPhysicsBodyOrientationObserver {
			@Override
			public void onBeginContact(IImmutablePhysicsBody other) {
				m_observers.raise(IPhysicsBodyContactObserver.class).onBeginContact(other);
			}

			@Override
			public void onEndContact(IImmutablePhysicsBody other) {
				m_observers.raise(IPhysicsBodyContactObserver.class).onEndContact(other);
			}

			@Override
			public void locationSet() {
				m_observers.raise(IPhysicsBodyOrientationObserver.class).locationSet();
			}

			@Override
			public void directionSet() {
				m_observers.raise(IPhysicsBodyOrientationObserver.class).directionSet();
			}
		}
	}

	public final class ScaledPhysicsBody extends ImmutableScaledPhysicsBody implements IPhysicsBody {
		private final IPhysicsBody m_body;

		public ScaledPhysicsBody(IPhysicsBody body) {
			super(body);
			m_body = body;
		}

		@Override
		public void setLocation(Vector3F location) {
			m_body.setLocation(location.multiply(m_scale));
		}

		@Override
		public void setDirection(Direction direction) {
			m_body.setDirection(direction);
		}

		@Override
		public void applyLinearImpulse(Vector3F impulse) {
			m_body.applyLinearImpulse(impulse.multiply(m_scale));
		}

		@Override
		public void applyAngularImpulse(float impulse) {
			m_body.applyAngularImpulse(impulse * m_scale);
		}

		@Override
		public void applyForceToCenter(Vector3F force) {
			m_body.applyForceToCenter(force.multiply(m_scale));
		}

		@Override
		public void applyTorque(float torque) {
			m_body.applyTorque(torque * m_scale);
		}

		@Override
		public void setLinearVelocity(Vector3F velocity) {
			m_body.setLinearVelocity(velocity.multiply(m_scale));
		}

		@Override
		public void destory() {
			m_body.destory();
		}

		@Override
		public void setCollidable(boolean isCollidable) {
			m_body.setCollidable(isCollidable);
		}
	}
}
