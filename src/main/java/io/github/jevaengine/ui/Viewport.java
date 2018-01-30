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
import io.github.jevaengine.graphics.IRenderable;
import io.github.jevaengine.graphics.NullGraphic;
import io.github.jevaengine.graphics.NullRenderable;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;

import java.awt.*;

public final class Viewport extends Control {
	public static final String COMPONENT_NAME = "viewport";

	private final int m_desiredWidth;
	private final int m_desiredHeight;

	private IRenderable m_view;
	private IImmutableGraphic m_frame;

	public Viewport(int desiredWidth, int desiredHeight) {
		super(COMPONENT_NAME);
		m_view = new NullRenderable();
		m_frame = new NullGraphic(desiredWidth, desiredHeight);
		m_desiredWidth = desiredWidth;
		m_desiredHeight = desiredHeight;
	}

	public Viewport(String instanceName, int desiredWidth, int desiredHeight) {
		super(COMPONENT_NAME, instanceName);
		m_view = new NullRenderable();
		m_frame = new NullGraphic(desiredWidth, desiredHeight);
		m_desiredWidth = desiredWidth;
		m_desiredHeight = desiredHeight;
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
		m_frame.render(g, x, y, scale);
		m_view.render(g, x, y, scale);
	}

	public void setView(IRenderable view) {
		m_view = view;
	}

	@Override
	public Rect2D getBounds() {
		return m_frame.getBounds();
	}

	@Override
	public void onStyleChanged() {
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_desiredWidth, m_desiredHeight);
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent) {
		return false;
	}

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent) {
		return false;
	}

	@Override
	public void update(int deltaTime) {
	}

}
