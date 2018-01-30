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
package io.github.jevaengine.world.scene;

import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.world.entity.IEntity;
import io.github.jevaengine.world.scene.model.IImmutableSceneModel;

import java.awt.*;

/**
 * @author Jeremy
 */
public final class ScaledSceneBuffer implements ISceneBuffer {
	private final float m_scale;
	private final ISceneBuffer m_buffer;

	public ScaledSceneBuffer(float scale, ISceneBuffer buffer) {
		m_scale = scale;
		m_buffer = buffer;
	}

	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation, float scale) {
		return m_buffer.translateScreenToWorld(screenLocation, scale * m_scale);
	}

	@Override
	public Vector2F translateScreenToWorld(Vector3F screenLocation) {
		return m_buffer.translateScreenToWorld(screenLocation, m_scale);
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location, float scale) {
		return m_buffer.translateWorldToScreen(location, scale * m_scale);
	}

	@Override
	public Vector2D translateWorldToScreen(Vector3F location) {
		return m_buffer.translateWorldToScreen(location, m_scale);
	}

	@Override
	public <T> T pick(Class<T> clazz, int x, int y, float scale) {
		return m_buffer.pick(clazz, x, y, scale * m_scale);
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale, Rect2D bounds) {
		m_buffer.render(g, x, y, scale * m_scale, bounds);
	}

	@Override
	public void addModel(IImmutableSceneModel model, IEntity dispatcher, Vector3F location) {
		m_buffer.addModel(model, dispatcher, location);
	}

	@Override
	public void addModel(IImmutableSceneModel model, Vector3F location) {
		m_buffer.addModel(model, location);
	}

	@Override
	public void addEffect(ISceneBufferEffect e) {
		m_buffer.addEffect(e);
	}

	@Override
	public void reset() {
		m_buffer.reset();
	}

	@Override
	public void translate(Vector2D translation) {
		m_buffer.translate(translation);
	}
}
