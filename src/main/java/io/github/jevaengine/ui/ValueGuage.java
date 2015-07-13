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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public class ValueGuage extends Control implements IRenderable
{
	public static final String COMPONENT_NAME = "valueGuage";
	
	private final IImmutableGraphic m_fill;
	
	private float m_value;

	private IImmutableGraphic m_frame;
	
	public ValueGuage(IImmutableGraphic fill)
	{
		super(COMPONENT_NAME);
		
		m_fill = fill;
		m_frame = new NullGraphic(fill.getBounds().width, fill.getBounds().height);
	}
	
	public ValueGuage(String instanceName, IImmutableGraphic fill)
	{
		super(COMPONENT_NAME, instanceName);
		m_fill = fill;
	}

	public void setValue(float value)
	{
		m_value = value;
	}

	public float getValue()
	{
		return m_value;
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		m_frame.render(g, x, y, scale);
		Shape oldClip = g.getClip();
		
		Rect2D fillRect = m_fill.getBounds();
		fillRect.width *= m_value;
		g.setClip(new Rectangle(x, y, fillRect.width, fillRect.height));
		m_fill.render(g, x, y, scale);
		g.setClip(oldClip);
	}

	@Override
	public Rect2D getBounds()
	{
		return m_fill.getBounds();
	}

	@Override
	public boolean onMouseEvent(InputMouseEvent mouseEvent) { return false; }

	@Override
	public boolean onKeyEvent(InputKeyEvent keyEvent) { return false; }

	@Override
	public void update(int deltaTime) { }

	@Override
	protected void onStyleChanged()
	{
		m_frame = getComponentStyle().getStateStyle(ComponentState.Default).createFrame(m_fill.getBounds().width, m_fill.getBounds().height);
	}
}
