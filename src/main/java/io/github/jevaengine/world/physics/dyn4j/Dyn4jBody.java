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

import io.github.jevaengine.math.Circle3F;
import io.github.jevaengine.math.Rect2F;
import io.github.jevaengine.math.Rect3F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.Direction;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.physics.*;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Dyn4jBody implements IPhysicsBody {
	private final Body m_body;
	private final Fixture m_fixture;
	private final IEntity m_owner;
	private final Observers m_observers = new Observers();
	private final Set<Class<?>> m_collisionExceptions = new HashSet<>();
	private Dyn4jWorld m_world;
	private float m_depth = 0.0F;
	private boolean m_isCollidable = true;

	private Direction m_direction = Direction.XYPlus;

	private NonparticipantPhysicsBody m_dummyBody = null;

	private final boolean m_isSensor;

	private Rect3F m_aabb = null;

	public Dyn4jBody(Dyn4jWorld world, Body body, Fixture fixture, boolean isSensor, Rect2F aabb, @Nullable IEntity owner, Class<?>... collisionExceptions) {
		m_body = body;
		m_fixture = fixture;
		m_owner = owner;
		m_body.setUserData(this);
		m_world = world;
		m_collisionExceptions.addAll(Arrays.asList(collisionExceptions));
		m_isSensor = isSensor;
	}

	void beginContact(Dyn4jBody other) {
		if(m_dummyBody == null)
			m_observers.raise(IPhysicsBodyContactObserver.class).onBeginContact(other);
	}

	void endContact(Dyn4jBody other) {
		if(m_dummyBody == null)
			m_observers.raise(IPhysicsBodyContactObserver.class).onEndContact(other);
	}

	protected boolean isDisabled() {
		return m_dummyBody != null;
	}

	@Override
	public boolean isSensor() {
		return m_isSensor;
	}

	protected void disable() {
		if(isDisabled() || m_world == null)
			return;

		if (m_world != null) {
			NonparticipantPhysicsBody dummy = new NonparticipantPhysicsBody();
			dummy.setCollidable(m_isCollidable);
			dummy.setDirection(m_direction);
			dummy.setLocation(this.getLocation());
			m_dummyBody = dummy;
			m_world.m_physicsWorld.removeBody(m_body);
		}
	}

	protected void enable() {
		if(!isDisabled() || m_world == null)
			return;

		m_world.m_physicsWorld.addBody(m_body);
		m_body.setAsleep(true);

		Direction dir = m_dummyBody.getDirection();
		Vector3F loc = m_dummyBody.getLocation();

		m_dummyBody = null;

		setLocation(loc);
		setDirection(dir);
	}

	@Override
	public void destory() {
		m_observers.clear();
		m_world.m_physicsWorld.removeBody(m_body);
		m_world = null;
	}

	@Override
	public Dyn4jWorld getWorld() {
		return m_world;
	}

	@Override
	@Nullable
	public RayCastIntersection castRay(Vector3F direction, float maxCast) {

		if (m_dummyBody != null || direction.isZero() || m_world == null)
			return null;

		Vector3F startPoint = getLocation().add(direction.normalize().multiply((float) m_fixture.getShape().getRadius()));
		Vector3F endPoint = startPoint.add(direction.normalize().multiply(maxCast));

		return new Dyn4jRaycaster().cast(m_world, startPoint.getXy(), endPoint.getXy(), m_body);
	}

	@Override
	public IObserverRegistry getObservers() {
		return m_observers;
	}

	@Override
	public boolean hasOwner() {
		return m_owner != null;
	}

	@Override
	public IEntity getOwner() {
		return m_owner;
	}

	@Override
	public boolean isStatic() {
		if(m_dummyBody != null) {
			return m_dummyBody.isStatic();
		}

		return m_body.getMass().isInfinite();
	}

	@Override
	public boolean isCollidable() {
		return !(m_fixture.isSensor() || !m_isCollidable);
	}

	@Override
	public void setCollidable(boolean isCollidable) {
		m_isCollidable = isCollidable;
	}

	@Override
	public boolean collidesWith(IImmutablePhysicsBody body) {
		if (m_fixture.isSensor() || !m_isCollidable)
			return false;

		return body.hasOwner() ? !m_collisionExceptions.contains(body.getOwner().getClass()) : true;
	}

	@Override
	public Circle3F getBoundingCircle() {
		return new Circle3F(0, 0, 0, (float) m_fixture.getShape().getRadius());
	}

	@Override
	public Rect3F getAABB() {
		if(m_aabb == null) {
			m_aabb = new Rect3F();
			AABB b2aabb = m_fixture.getShape().createAABB();

			m_aabb.x = (float) b2aabb.getMinX();
			m_aabb.y = (float) b2aabb.getMinY();
			m_aabb.width = (float) (b2aabb.getMaxX() - b2aabb.getMinX());
			m_aabb.height = (float) (b2aabb.getMaxY() - b2aabb.getMinY());
		}

		return m_aabb.add(getLocation());
	}

	@Override
	public float getMass() {
		return (float) m_body.getMass().getMass();
	}

	@Override
	public Vector3F getLocation() {
		if(m_dummyBody != null)
			return m_dummyBody.getLocation();

		return new Vector3F(Dyn4jUtil.wrap(m_body.getTransform().getTranslation()), m_depth);
	}

	@Override
	public void setLocation(Vector3F location) {
		if (m_dummyBody != null) {
			m_dummyBody.setLocation(location);
		} else {
			Transform t = m_body.getTransform();
			t.translate(new Vector2(location.x - t.getTranslationX(), location.y - t.getTranslationY()));

			m_body.setTransform(t);
			m_depth = location.z;
		}
		m_observers.raise(IPhysicsBodyOrientationObserver.class).locationSet();
	}

	@Override
	public Direction getDirection() {
		if(m_dummyBody != null) {
			return m_dummyBody.getDirection();
		} else
			return m_direction;
	}

	@Override
	public void setDirection(Direction direction) {
		if (m_dummyBody != null) {
			m_dummyBody.setDirection(direction);
		} else {
			m_direction = direction;
		}

		m_observers.raise(IPhysicsBodyOrientationObserver.class).directionSet();
	}

	@Override
	public Vector3F getLinearVelocity() {
		return new Vector3F(Dyn4jUtil.wrap(m_body.getLinearVelocity()), 0);
	}

	@Override
	public void setLinearVelocity(Vector3F velocity) {
		if (m_dummyBody == null) {
			if (!velocity.isZero())
				m_body.setAsleep(false);
			m_body.setLinearVelocity(Dyn4jUtil.unwrap(velocity.getXy()));
		}
	}

	@Override
	public float getAngularVelocity() {
		return (float) m_body.getAngularVelocity();
	}

	@Override
	public void applyLinearImpulse(Vector3F impulse) {
		if (m_dummyBody == null) {
			m_body.applyImpulse(Dyn4jUtil.unwrap(impulse.getXy()));
		}
	}

	@Override
	public void applyAngularImpulse(float impulse) {
		if (m_dummyBody == null) {
			m_body.applyImpulse(impulse);
		}
	}

	@Override
	public void applyForceToCenter(Vector3F force) {
		if (m_dummyBody == null) {
			m_body.applyForce(Dyn4jUtil.unwrap(force.getXy()));
		}
	}

	@Override
	public void applyTorque(float torque) {
		if (m_dummyBody == null) {
			m_body.applyTorque(torque);
		}
	}
}
