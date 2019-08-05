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
package io.github.jevaengine.ui;

import io.github.jevaengine.graphics.IImmutableGraphic;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.math.Vector2F;
import io.github.jevaengine.math.Vector3F;
import io.github.jevaengine.ui.style.ComponentStyle;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Nullable;
import io.github.jevaengine.util.Observers;
import io.github.jevaengine.world.scene.IImmutableSceneBuffer;
import io.github.jevaengine.world.scene.NullSceneBuffer;
import io.github.jevaengine.world.scene.camera.ICamera;
import io.github.jevaengine.world.scene.camera.NullCamera;

import java.awt.*;

public final class WorldView extends Control {
	public static final String COMPONENT_NAME = "worldView";

	private final int m_desiredWidth;
	private final int m_desiredHeight;
	private final boolean m_transparent;
	private final Observers m_observers = new Observers();
	private IImmutableGraphic m_frame;
	private ICamera m_camera = new NullCamera();

	private IImmutableSceneBuffer m_lastScene = new NullSceneBuffer();

	public WorldView(int desiredWidth, int desiredHeight) {
		super(COMPONENT_NAME);
		m_desiredWidth = desiredWidth;
		m_desiredHeight = desiredHeight;
		m_frame = new NullGraphic(desiredWidth, desiredHeight);
		m_transparent = false;
	}

	public WorldView(String instanceName, int desiredWidth, int desiredHeight, boolean transparent) {
		super(COMPONENT_NAME, instanceName);
		m_desiredWidth = desiredWidth;
		m_desiredHeight = desiredHeight;
		m_frame = new NullGraphic(desiredWidth, desiredHeight);
		m_transparent = transparent;
	}

	public WorldView(String instanceName, int desiredWidth, int desiredHeight) {
		this(instanceName, desiredWidth, desiredHeight, false);
	}

	public IObserverRegistry getObservers() {
		return m_observers;
	}

	@Override
	public Rect2D getBounds() {
		return m_frame.getBounds();
	}

	public void setBounds(Rect2D bounds) {
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(bounds.width, bounds.height);
	}

	public void setCamera(ICamera camera) {
		m_camera = camera;
	}

	public Vector2F translateScreenToWorld(Vector2F relativeLocation) {
		if (m_camera == null)
			return new Vector2F();

		return m_lastScene.translateScreenToWorld(new Vector3F(relativeLocation, m_camera.getLookAt().z), 1.0F);
	}

	public Vector2D translateWorldToScreen(Vector3F location) {
		if (m_camera == null)
			return new Vector2D();

		return m_lastScene.translateWorldToScreen(location.difference(new Vector3F(0, 0, m_camera.getLookAt().z))).difference(getAbsoluteLocation());
	}

	@Nullable
	public <T> T pick(Class<T> clazz, Vector2D location) {
		if (m_lastScene != null) {
			return m_lastScene.pick(clazz, location.x, location.y, 1.0F);
		} else
			return null;
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent) {
		InputMouseEvent relativeMouseEvent = new InputMouseEvent(mouseEvent);
		relativeMouseEvent.location = mouseEvent.location.difference(getAbsoluteLocation());

		m_observers.raise(IWorldViewInputObserver.class).mouseEvent(relativeMouseEvent);

		return true;
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent) {
		m_observers.raise(IWorldViewInputObserver.class).keyEvent(keyEvent);
		return true;
	}

	@Override
	public void update(int deltaTime) {
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
		m_frame.render(g, x, y, scale);

		if(!m_transparent) {
			g.setColor(Color.black);
			g.fillRect(x, y, getBounds().width, getBounds().height);
		}

		Shape oldClip = g.getClip();
		g.clipRect(x, y, getBounds().width, getBounds().height);

		m_lastScene = m_camera.getScene(getBounds(), scale);
		m_lastScene.render(g, x, y, scale, getBounds());

		g.setClip(oldClip);
	}

	@Override
	public void onStyleChanged() {
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_desiredWidth, m_desiredHeight);
	}

	public interface IWorldViewInputObserver {
		void mouseEvent(InputMouseEvent event);

		void keyEvent(InputKeyEvent event);
	}
}
