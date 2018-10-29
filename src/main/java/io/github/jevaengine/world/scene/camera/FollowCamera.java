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
package io.github.jevaengine.world.scene.camera;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.entity.IEntity.IEntityWorldObserver;
import io.github.jevaengine.world.scene.IImmutableSceneBuffer;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;
import io.github.jevaengine.world.scene.ISceneBufferFactory;

import java.lang.ref.WeakReference;

public final class FollowCamera implements ICamera {
	private final ControlledCamera m_camera;
	private final EntityObserver m_observer = new EntityObserver();

	private WeakReference<IEntity> m_target;

	public FollowCamera(ISceneBufferFactory sceneBufferFactory) {
		m_camera = new ControlledCamera(sceneBufferFactory);
		m_target = new WeakReference<>(null);
	}

	public float getZoom() {
		return m_camera.getZoom();
	}

	public void setZoom(float zoom) {
		m_camera.setZoom(zoom);
	}

	@Override
	public void addEffect(ISceneBufferEffect e) {
		m_camera.addEffect(e);
	}

	@Override
	public void removeEffect(ISceneBufferEffect e) {
		m_camera.removeEffect(e);
	}

	public void setTarget(@Nullable IEntity target) {
		if (m_target.get() != null)
			m_target.get().getObservers().remove(m_observer);

		m_target = new WeakReference<>(target);

		if (target != null)
			target.getObservers().add(m_observer);
	}

	@Override
	public Vector3F getLookAt() {
		if (m_target.get() == null)
			return new Vector3F();
		else
			return m_target.get().getBody().getLocation();
	}

	@Override
	public IImmutableSceneBuffer getScene(Rect2D bounds, float scale) {
		Vector3F target = m_target.get() == null ? new Vector3F() : m_target.get().getBody().getLocation();
		m_camera.lookAt(target);

		return m_camera.getScene(bounds, scale);
	}

	@Override
	public void dettach() {
		m_camera.dettach();
	}

	@Override
	public void attach(World world) {
		m_camera.attach(world);
	}

	private class EntityObserver implements IEntityWorldObserver {
		@Override
		public void leaveWorld() {
			setTarget(null);
		}

		@Override
		public void enterWorld() {
		}
	}
}
