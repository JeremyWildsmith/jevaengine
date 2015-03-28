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

import io.github.jevaengine.graphics.IFont;
import io.github.jevaengine.joystick.InputKeyEvent;
import io.github.jevaengine.joystick.InputMouseEvent;
import io.github.jevaengine.math.Rect2D;

import java.awt.Graphics2D;

public final class Label extends Control
{
	public static final String COMPONENT_NAME = "label";
	private String m_text;

	public Label(String text)
	{
		super(COMPONENT_NAME);
		m_text = text;
	}

	public Label(String instanceName, String text)
	{
		super(COMPONENT_NAME, instanceName);
		m_text = text;
	}
	
	public Label()
	{
		super(COMPONENT_NAME);
		m_text = "";
	}

	public String getText()
	{
		return m_text;
	}

	@Override
	public Rect2D getBounds()
	{
		IFont font = getComponentStyle().getStateStyle(ComponentState.Default).getFont();	
		return font.getTextBounds(m_text, 1.0F);
	}
	
	public void setText(String text)
	{
		m_text = text;
	}

	@Override
	public void render(Graphics2D g, int x, int y, float scale)
	{
		IFont font = getComponentStyle().getStateStyle(ComponentState.Default).getFont();
		font.drawText(g, x, y, scale, m_text);
	}

	public boolean onMouseEvent(InputMouseEvent mouseEvent)
	{
		return false;
	}

	public boolean onKeyEvent(InputKeyEvent keyEvent)
	{
		return false;
	}

	@Override
	public void update(int deltaTime) { }
}
