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

import io.github.jevaengine.math.*;
import io.github.jevaengine.world.World;
import io.github.jevaengine.world.scene.*;
import io.github.jevaengine.world.scene.ISceneBuffer.ISceneBufferEffect;

import java.util.ArrayList;
import java.util.List;

public final class ControlledCamera implements ICamera {
	private static final float MIN_ZOOM = 0.0001F;
	private final ISceneBufferFactory m_sceneBufferFactory;
	private final List<ISceneBufferEffect> m_effects = new ArrayList<>();
	private Vector3F m_lookAtTile = new Vector3F();
	private float m_zoom = 1.0F;
	private World m_world;

	public ControlledCamera(ISceneBufferFactory sceneBufferFactory) {
		m_sceneBufferFactory = sceneBufferFactory;
	}

	@Override
	public void addEffect(ISceneBufferEffect e) {
		m_effects.add(e);
	}

	@Override
	public void removeEffect(ISceneBufferEffect e) {
		m_effects.remove(e);
	}

	public float getZoom() {
		return m_zoom;
	}

	public void setZoom(float zoom) {
		m_zoom = Math.max(MIN_ZOOM, zoom);
	}

	public void lookAt(Vector3F tileLocation) {
		m_lookAtTile = new Vector3F(tileLocation);
	}

	public Vector3F boundLocation(Vector3F v) {
		if (m_world == null)
			return new Vector3F();

		return new Vector3F(Math.min(m_world.getBounds().width - 1, Math.max(0, v.x)),
				Math.min(m_world.getBounds().height - 1, Math.max(0, v.y)),
				v.z);
	}

	public void move(Vector3F delta) {
		m_lookAtTile = boundLocation(m_lookAtTile.add(delta));
	}

	public Vector3F getLookAt() {
		return boundLocation(m_lookAtTile);
	}

	@Override
	public void attach(World world) {
		dettach();
		m_world = world;
	}

	@Override
	public final void dettach() {
		m_world = null;
	}

	private Rect2F getProjectedView(ISceneBuffer sceneBuffer, Rect2D viewBounds, float boundsDepth) {
		Vector2D depthFactor = sceneBuffer.translateScreenToWorld(new Vector3F(0, 0, boundsDepth)).round();

		//First we must project the view bound corners into world space. This allows us to determine which tiles are visible (those that are contained
		// by the viewBounds and thus those tiles that are contained within the projected view bounds. )
		Vector2F worldBounds[] = new Vector2F[]{sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x, viewBounds.y, boundsDepth)).difference(depthFactor),
				sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x + viewBounds.width, viewBounds.y, boundsDepth)).difference(depthFactor),
				sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x, viewBounds.y + viewBounds.height, boundsDepth)).difference(depthFactor),
				sceneBuffer.translateScreenToWorld(new Vector3F(viewBounds.x + viewBounds.width, viewBounds.y + viewBounds.height, boundsDepth)).difference(depthFactor)};

		/*
		 * Now that we have projected the view bounds into world space, we will construct a rect representing the projected view bounds. The correct rect
		 * will contain all points of the projected view bounds rectangle.
		 */
		float minX = Integer.MAX_VALUE;
		float maxX = Integer.MIN_VALUE;
		float minY = Integer.MAX_VALUE;
		float maxY = Integer.MIN_VALUE;

		for (Vector2F v : worldBounds) {
			minX = Math.min(minX, v.x);
			minY = Math.min(minY, v.y);
			maxX = Math.max(maxX, v.x);
			maxY = Math.max(maxY, v.y);
		}

		return new Rect2F(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public IImmutableSceneBuffer getScene(Rect2D bounds, float scale) {
		if (m_world == null)
			return new NullSceneBuffer();

		ISceneBuffer sceneBuffer = new ScaledSceneBuffer(scale * m_zoom, m_sceneBufferFactory.create());

		for (ISceneBufferEffect e : m_effects)
			sceneBuffer.addEffect(e);

		Vector3F lookat = boundLocation(m_lookAtTile);

		//We need to construct a new bounds, that is centered over out camera bounds.
		Vector2D lookatScreen = sceneBuffer.translateWorldToScreen(getLookAt());

		Rect2D centeredView = new Rect2D(lookatScreen.x - bounds.width / 2, lookatScreen.y - bounds.height / 2, bounds.width, bounds.height);

		Rect2F projectedView = getProjectedView(sceneBuffer, centeredView, getLookAt().z);

		sceneBuffer.translate(new Vector2D(-lookatScreen.x + bounds.width / 2, -lookatScreen.y + bounds.height / 2));

		m_world.fillScene(sceneBuffer, projectedView);

		return sceneBuffer;
	}
}
