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
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;

import java.awt.*;

public class ValueGuage extends Control implements IRenderable {
	public static final String COMPONENT_NAME = "valueGuage";
	private final Rect2D m_bounds;
	private float m_value;
	private IImmutableGraphic m_fill = new NullGraphic();
	private IImmutableGraphic m_frame = new NullGraphic();

	public ValueGuage(Rect2D bounds) {
		super(COMPONENT_NAME);
		m_bounds = new Rect2D(bounds);
	}

	public ValueGuage(String instanceName, Rect2D bounds) {
		super(COMPONENT_NAME, instanceName);
		m_bounds = bounds;
	}

	public float getValue() {
		return m_value;
	}

	public void setValue(float value) {
		m_value = value;
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale) {
		m_frame.render(g, x, y, scale);
		Rect2D frameBounds = m_frame.getBounds();
		Rect2D fillBounds = m_fill.getBounds();

		int offsetX = (frameBounds.width - fillBounds.width) / 2;
		int offsetY = (frameBounds.height - fillBounds.height) / 2;

		Shape oldClip = g.getClip();
		Rect2D fillRect = m_fill.getBounds();
		fillRect.width *= m_value;
		g.setClip(new Rectangle(x + offsetX, y + offsetY, fillRect.width, fillRect.height));
		m_fill.render(g, x + offsetX, y + offsetY, scale);
		g.setClip(oldClip);
	}

	@Override
	public Rect2D getBounds() {
		return m_fill.getBounds();
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

	@Override
	protected void onStyleChanged() {
		m_fill = getComponentStyle().getStateStyle(ComponentState.Activated).createFrame(m_bounds.height, m_bounds.height);
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_fill.getBounds().width, m_fill.getBounds().height);
	}
}
