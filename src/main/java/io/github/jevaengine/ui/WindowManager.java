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

import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.joystick.InputMouseEvent.MouseEventType;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.util.MutableProcessList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class WindowManager {
	private final List<Window> m_windows = new MutableProcessList<>();
	private final List<Window> m_effectiveOrderBuffer = new MutableProcessList<>();

	private final Vector2D m_resolution;

	public WindowManager(Vector2D resolution) {
		m_resolution = resolution;
	}

	protected Window[] getWindows() {
		return m_windows.toArray(new Window[m_windows.size()]);
	}

	public final void addWindow(Window window) {
		if (m_windows.contains(window))
			throw new DuplicateWindowException();

		window.setManager(this);

		m_windows.add(window);
		setFocusedWindow(window);
	}

	public final void removeWindow(Window window) {
		if (!m_windows.contains(window))
			throw new NoSuchWindowException();

		window.setManager(null);
		m_windows.remove(window);
		m_effectiveOrderBuffer.remove(window);
	}

	public void setFocusedWindow(Window window) {
		if (m_effectiveOrderBuffer.contains(window)) {
			if (m_effectiveOrderBuffer.get(0) != window) {
				m_effectiveOrderBuffer.get(0).clearFocus();
				m_effectiveOrderBuffer.remove(window);
				m_effectiveOrderBuffer.add(0, window);
				window.setFocus();
			}
		}
	}

	public void centerWindow(Window window) {
		if (!m_windows.contains(window))
			return;

		window.setLocation(new Vector2D((m_resolution.x - window.getBounds().width) / 2,
				(m_resolution.y - window.getBounds().height) / 2));
	}

	private void initEffectiveOrderBuffer() {
		List<Window> topMost = new ArrayList<>();

		Window top = null;

		if(m_effectiveOrderBuffer != null && !m_effectiveOrderBuffer.isEmpty())
			top = m_effectiveOrderBuffer.get(0);

		m_effectiveOrderBuffer.clear();
		for (Window w : m_windows) {
			if(w == top)
				continue;

			if (w.isTopMost())
				topMost.add(w);
			else
				m_effectiveOrderBuffer.add(w);
		}
		m_effectiveOrderBuffer.addAll(0, topMost);

		if(top != null)
			m_effectiveOrderBuffer.add(0, top);

		m_windows.clear();
		m_windows.addAll(m_effectiveOrderBuffer);
	}

	private List<Window> getRenderOrder(boolean reversed) {
		List<Window> order = new ArrayList<>();

		//Render topmost last
		for (int i = m_effectiveOrderBuffer.size() - 1; i >= 0; i--) {
			if (!m_effectiveOrderBuffer.get(i).isTopMost() && m_effectiveOrderBuffer.get(i).isVisible())
				order.add(m_effectiveOrderBuffer.get(i));
		}

		for (int i = m_effectiveOrderBuffer.size() - 1; i >= 0; i--) {
			if (m_effectiveOrderBuffer.get(i).isTopMost() && m_effectiveOrderBuffer.get(i).isVisible())
				order.add(m_effectiveOrderBuffer.get(i));
		}

		if(reversed)
			Collections.reverse(order);

		return order;
	}

	public void onMouseEvent(InputMouseEvent mouseEvent) {
		Window moveToTop = null;

		initEffectiveOrderBuffer();

		Window topWindow = m_effectiveOrderBuffer.size() > 0 ? m_effectiveOrderBuffer.get(0) : null;

		for (Window window : getRenderOrder(true)) {
			if (window.isVisible()) {
				Vector2D relativePoint = mouseEvent.location.difference(window.getLocation());
				Vector2D topRelativePoint = mouseEvent.location.difference(topWindow.getLocation());

				if (window.getBounds().contains(relativePoint)) {
					if (window.isFocusable() && mouseEvent.type == MouseEventType.MousePressed && moveToTop == null)
						moveToTop = topWindow = window;

					if (mouseEvent.isDragging && window.isMovable() && window == topWindow) {
						if (!window.onMouseEvent(mouseEvent))
							window.setLocation(window.getLocation().add(mouseEvent.delta));
					} else {
						if (window == topWindow)
							window.onMouseEvent(mouseEvent);
						else if (mouseEvent.type == MouseEventType.MouseMoved)
							window.onMouseEvent(mouseEvent);
					}
				}
			}
		}

		if (moveToTop != null && m_windows.contains(moveToTop))
			setFocusedWindow(moveToTop);
	}

	public boolean onKeyEvent(InputKeyEvent keyEvent) {
		if (!m_effectiveOrderBuffer.isEmpty())
			m_effectiveOrderBuffer.get(0).onKeyEvent(keyEvent);

		return true;
	}

	public void render(Graphics2D g, int x, int y, float fScale) {
		initEffectiveOrderBuffer();
		//Render topmost last
		for (Window w : getRenderOrder(false)) {
			w.render(g, x + w.getLocation().x, y + w.getLocation().y, fScale);
		}
	}

	public void update(int deltaTime) {
		for (Window window : m_windows)
			window.update(deltaTime);
	}

	public Vector2D getResolution() {
		return new Vector2D(m_resolution);
	}
}
