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
import io.github.jevaengine.joystick.InputMouseEvent.MouseEventType;
import io.github.jevaengine.math.Rect2D;

import java.awt.*;

public final class Checkbox extends Control {
	public static final String COMPONENT_NAME = "checkbox";
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	private boolean m_value = false;

	private IImmutableGraphic m_frame = new NullGraphic();

	public Checkbox(boolean value) {
		super(COMPONENT_NAME);
		m_value = value;
	}

	public Checkbox(String instanceName, boolean value) {
		super(COMPONENT_NAME, instanceName);
		m_value = value;
	}

	public Checkbox() {
		super(COMPONENT_NAME);
		m_value = false;
	}

	public boolean getValue() {
		return m_value;
	}

	public void setValue(boolean value) {
		m_value = value;
	}

	@Override
	public Rect2D getBounds() {
		return m_frame.getBounds();
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
		m_frame.render(g, x, y, scale);
		g.setColor(Color.white);

		Rect2D bounds = getBounds();

		if (m_value)
			g.fillRect(x + 2, y + 2, bounds.width - 4, bounds.height - 4);
	}

	public boolean onMouseEvent(InputMouseEvent mouseEvent) {
		if (mouseEvent.type == MouseEventType.MouseClicked)
			m_value = !m_value;

		return true;
	}

	public boolean onKeyEvent(InputKeyEvent keyEvent) {
		return false;
	}

	@Override
	public void update(int deltaTime) {
	}

	@Override
	public void onStyleChanged() {
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(WIDTH, HEIGHT);
	}
}
